package com.news.readerservice.service;

import com.news.readerservice.inter.CrawlService;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.HttpClientUtil;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

//@Service
public class ZhuHaiCrawlService implements CrawlService {

    public WebSiteEntity webSiteEntity;
    public NewsEntityService newsEntityService;

    public ZhuHaiCrawlService(NewsEntityService newsEntityService){
//        this.webSiteEntity = new WebSiteEntity("珠海房产", "http://zjj.zhuhai.gov.cn/zwgk/jfwj/index.html");
        this.newsEntityService = newsEntityService;
    }

    public void setWebSiteEntity(WebSiteEntity webSiteEntity){
        this.webSiteEntity = webSiteEntity;
    }

    public WebSiteEntity getWebSiteEntity(){
        return this.webSiteEntity;
    }

    public Logger LOG = Logger.getLogger(ZhuHaiCrawlService.class);

    public void crawlPageUrlLink(List<String> destArray, String url, String websiteName){
        HttpClient httpClient = HttpClientUtil.createMultiThreadClient(400, 60, 5000, 9000);
        String webContent = HttpClientUtil.crawl(websiteName, httpClient, url, Consts.UTF_8.toString());

        Document doc = Jsoup.parse(webContent);
        Elements els =  doc.getElementsByClass("dede_pages");
        if(els.isEmpty()){
            LOG.info("NO elements find for dede_pages");
            return ;
        }

        Iterator<Element> elIter = els.iterator();

        while(elIter.hasNext()){
            Element e = elIter.next();
            Elements links = e.select("a[href]");
            LOG.info("links =" +links);

            for(Element link : links){
                String urlStr = link.attr("href").toString();
                if(!destArray.contains(urlStr)){
                    destArray.add(urlStr);
                }
            }


        }

        LOG.info("destArray===>"+destArray);

    }

    public List<NewsEntity> defaultCrawlNews(){
        List<String> urlList = new ArrayList<>();
        urlList.add(webSiteEntity.getWebsiteUrl());
        return this.crawlNews(urlList, this.webSiteEntity.getWebsiteName());
    }

    public List<NewsEntity> crawlNews(List<String> urlArray, String websiteName){
        LOG.info("processing crawlNews");
        HttpClient httpClient = HttpClientUtil.createDefaultClient();
        List<NewsEntity> newsEntityList = new ArrayList<>();
        LOG.info("Process for " + websiteName);
        for(String url : urlArray){
            String webContent = HttpClientUtil.crawl(websiteName, httpClient, url, Consts.UTF_8.toString());
            Document doc = Jsoup.parse(webContent);
            Elements els = doc.select("div.article_con .list01").select("ul > li");

            for(Element el: els){
                NewsEntity news = new NewsEntity();
                news.setDate(el.selectFirst("span").text());
                news.setUrl(el.selectFirst("a[href]").attr("href"));
                news.setTitle(el.selectFirst("a[href]").attr("title"));
                news.setWebSiteName(this.webSiteEntity.getWebsiteName());
                news.setWebSiteUrl(this.webSiteEntity.getWebsiteUrl());
                news.setPageUrl(url);
                newsEntityList.add(news);
            }

        }

        return newsEntityList;

    }

    public List<NewsEntity> initNewsToDB(){

        List<String> urlArray = new ArrayList<>();
        this.crawlPageUrlLink(urlArray, this.webSiteEntity.getWebsiteUrl(), this.webSiteEntity.getWebsiteName());
        List<NewsEntity> newsList = this.crawlNews(urlArray, this.webSiteEntity.getWebsiteName());


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

            this.crawlPageUrlLink(pageLinks, this.webSiteEntity.getWebsiteUrl(), this.webSiteEntity.getWebsiteName());

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
                List<NewsEntity> newsEntityList = this.crawlNews(crawlUrls, this.webSiteEntity.getWebsiteName());



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

            //save db
            newsEntityService.saveNews(insertDBList);
        }

        return insertDBList;


    }

}
