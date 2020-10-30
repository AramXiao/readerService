package com.news.readerservice.service;

import com.gargoylesoftware.htmlunit.html.Html;
import com.news.readerservice.inter.CrawlService;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class GenericCrawlService implements CrawlService {

    public WebSiteEntity webSiteEntity;
    public NewsEntityService newsEntityService;



    public GenericCrawlService(NewsEntityService newsEntityService){
        this.newsEntityService = newsEntityService;
    }

    public void setWebSiteEntity(WebSiteEntity webSiteEntity){
        this.webSiteEntity = webSiteEntity;
    }

    public WebSiteEntity getWebSiteEntity(){
        return this.webSiteEntity;
    }

    public Logger LOG = Logger.getLogger(GenericCrawlService.class);

    public void crawlPageUrlLink(List<String> destArray, String url, String websiteName, String pageCssSelect){

        Document doc = null;
        try {
            doc = HttpClientUtil.getHtmlPageResponseAsDocument(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Elements els =  doc.select(pageCssSelect);
        if(els.isEmpty()){
            LOG.info("NO elements find!");
            return ;
        }

        Iterator<Element> elIter = els.iterator();

        while(elIter.hasNext()){
            Element link = elIter.next();
            LOG.info("links =" +els);

            String urlStr = link.attr("href").toString();
            if(!destArray.contains(StringUtils.trim(urlStr))){
                destArray.add(urlStr);
            }


        }

        LOG.info("destArray===>"+destArray);

    }

    public List<NewsEntity> defaultCrawlNews(){
        List<String> urlList = new ArrayList<>();
        urlList.add(webSiteEntity.getWebsiteUrl());
        return this.crawlNews(urlList, this.webSiteEntity.getWebsiteName(), this.webSiteEntity.getNewsTableCssSelect(), this.webSiteEntity.getNewsTagRegex());
    }

    public List<NewsEntity> crawlNews(List<String> urlArray, String websiteName, String newsDivRegex, String newsRegex){
        LOG.info("processing crawlNews");
        HttpClient httpClient = HttpClientUtil.createDefaultClient();
        List<NewsEntity> newsEntityList = new ArrayList<>();
        LOG.info("Process for " + websiteName);
        for(String url : urlArray){
            String convertUrl = "";
            if(url.indexOf("http")<0){
                convertUrl = HtmlUtil.getBaseUrl(webSiteEntity.getWebsiteUrl()) + "/" + url;
            }else{
                convertUrl = url;
            }
            String webContent = HttpClientUtil.crawl(websiteName, httpClient, convertUrl, Consts.UTF_8.toString());
            Document doc = Jsoup.parse(webContent);
            Element el = doc.selectFirst(newsDivRegex);

            if(el!=null){
                String tableData = el.html();
                List<NewsEntity> resultList = HtmlUtil.crawlTableToNewsEnttiy(tableData, convertUrl, this.webSiteEntity);
                LOG.info("crawl url:" + convertUrl);
                LOG.info("resultlist-->"+resultList);
                newsEntityList.addAll(resultList);
            }



        }

        return newsEntityList;

    }

    public List<NewsEntity> initNewsToDB(){

        List<String> urlArray = new ArrayList<>();
        this.crawlPageUrlLink(urlArray, this.webSiteEntity.getWebsiteUrl(), this.webSiteEntity.getWebsiteName(), this.getWebSiteEntity().getPageDivCssSelect());
        List<NewsEntity> newsList = this.crawlNews(urlArray, this.webSiteEntity.getWebsiteName(), this.webSiteEntity.getNewsTableCssSelect(), this.webSiteEntity.getNewsTagRegex());

        LOG.info("saveNews-->" + newsList);

        newsEntityService.saveNews(newsList);


        return newsList;
    }

    public List<NewsEntity> checkLastestNews(){
        LOG.info("processing checkLastestNews");
        List<String> pageLinks = new ArrayList<>();
        List<NewsEntity> insertDBList = new ArrayList<>();

        //如果第一次爬取，数据需要全部入库，调用initNewsToDB();
        if(!newsEntityService.checkBywebSiteName(this.webSiteEntity.getWebsiteName())){
            this.initNewsToDB();
        }else{

            this.crawlPageUrlLink(pageLinks, this.webSiteEntity.getWebsiteUrl(), this.webSiteEntity.getWebsiteName(), this.webSiteEntity.getPageDivCssSelect());

            Collections.sort(pageLinks, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int pageNum1 = 0;
                    if(o1.indexOf("_")>-1){
                        pageNum1 = HtmlUtil.getPageNum(o1);
                    }else{
                        pageNum1 = 0;
                    }
                    int pageNum2 = 0;
                    if(o2.indexOf("_")>-1){
                        pageNum2 = HtmlUtil.getPageNum(o2);
                    }else{
                        pageNum2 = 0;
                    }
                    return pageNum1>pageNum2?1:-1;
                }
            });

            Iterator<String> iter = pageLinks.iterator();

            boolean stopCheck = false;
            while(!stopCheck && iter.hasNext()){
                String pageLink = iter.next();
                List<String> crawlUrls = new ArrayList<>();
                crawlUrls.add(pageLink);
                //get first page data
                List<NewsEntity> newsEntityList = this.crawlNews(crawlUrls, this.webSiteEntity.getWebsiteName(), this.webSiteEntity.getNewsTableCssSelect(), this.webSiteEntity.getNewsTagRegex());



                for(NewsEntity news: newsEntityList){
                    if(!newsEntityService.checkIfExist(news)){
                        //News not exist in DB, add to insertDB list
                        insertDBList.add(news);
                    }else{
                        stopCheck = true;
                    }
                }

                if(stopCheck){
                    break;
                }

            }

            LOG.info("saveNews-->"+insertDBList);

            //save db
            newsEntityService.saveNews(insertDBList);
        }

        return insertDBList;


    }
}
