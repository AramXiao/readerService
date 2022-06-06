package com.news.readerservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.news.readerservice.inter.CrawlService;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.utils.HttpClientUtil;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ZhuHaiGovService implements CrawlService {

    public Logger LOG = Logger.getLogger(GenericCrawlService.class);
    public WebSiteEntity webSiteEntity;
    public NewsEntityService newsEntityService;

    public ZhuHaiGovService(NewsEntityService newsEntityService){
        this.newsEntityService = newsEntityService;
    }


    public void setWebSiteEntity(WebSiteEntity webSiteEntity){
        this.webSiteEntity = webSiteEntity;
    }

    public WebSiteEntity getWebSiteEntity(){
        return this.webSiteEntity;
    }



    public Integer getMaxPageNum(String url){

        Integer pageNum = 0;
        Document doc = null;
        LOG.info("crawl for " + url);
        try {
            doc = HttpClientUtil.getHtmlPageResponseAsDocument(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Elements el = doc.select(".pagination a[data-page]");

        for(Element e: el){
            Integer pageN = Integer.parseInt(e.attr("data-page"));
            if(pageNum < pageN){
                pageNum = pageN;
            }
        }

//        LOG.info("pageNum-->"+pageNum);


        return pageNum;
    }

    public List<NewsEntity> crawlNews(String url, int pageNum, int modNum, String websiteName){
        LOG.info("crawl for " + websiteName);
        LOG.info("crawl for url " + url);

        List<NewsEntity> dataList = new ArrayList<>();

        HttpClient httpClient = HttpClientUtil.createDefaultClient();
        String data = HttpClientUtil.crawl(websiteName, httpClient, url, Consts.UTF_8.toString());

        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray jsonArray = JSON.parseArray(jsonObject.getString("articles"));


        int start = pageNum%modNum==0?modNum:pageNum%modNum;
        int offset = (start-1)*20;
        int limit = start * 20;

        for(int i=offset; i<limit && i<jsonArray.size(); i++){
            JSONObject article = jsonArray.getJSONObject(i);
            NewsEntity news = new NewsEntity();

            news.setUrl(article.getString("url"));
            news.setWebSiteName(webSiteEntity.getWebsiteName());
            news.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
            news.setTitle(article.getString("title"));
            news.setDate(article.getString("created_at"));
            news.setPageUrl(url);
            dataList.add(news);
        }

        return dataList;
    }

    public String getBaseUrl(String url){
        String base = "";
        if(url.indexOf("/")>-1){
            base = url.substring(0, url.lastIndexOf("/"));
        }

        LOG.info("baseurl ==>"+base);
        return base;
    }

    public Integer getRealPage(int page){
        return Integer.valueOf((int)Math.ceil(page/5.0));
    }


    public static void main(String[] args){

        System.out.println(Math.ceil(1.0/5));
    }

    @Override
    public List<NewsEntity> checkLastestNews() {

        List<NewsEntity> articlesAll = new ArrayList<>();
        Integer maxPage = this.getMaxPageNum(webSiteEntity.getWebsiteUrl());

        if(!newsEntityService.checkBywebSiteName(this.webSiteEntity.getWebsiteName())) {
            this.initNewsToDB();
        }else{

            boolean stopCheck = false;
            String baseUrl = this.getBaseUrl(webSiteEntity.getWebsiteUrl());

            for(int i=0; i<maxPage; i++){
                String url = baseUrl + "/api/all/153?page="+getRealPage(i+1)+"&sid=756016";
                List<NewsEntity> articles = this.crawlNews(url, i+1, 5, webSiteEntity.getWebsiteName());


                for(NewsEntity article: articles){
                    if(!newsEntityService.checkIfExist(article)){
                        newsEntityService.save(article);
                        articlesAll.add(article);
                    }else{
                        stopCheck = true;
                    }

                }

                if(stopCheck){
                    break;
                }
            }

        }

        LOG.info("datalist-->"+articlesAll);

        return articlesAll;
    }

    @Override
    public List<NewsEntity> initNewsToDB() {
        Integer maxPage = this.getMaxPageNum(webSiteEntity.getWebsiteUrl());
        String baseUrl = this.getBaseUrl(webSiteEntity.getWebsiteUrl());
        List<NewsEntity> articles = new ArrayList<>();
        for(int i=1; i<maxPage; i++){
            String url = baseUrl + "/gkmlpt/api/all/153?page="+getRealPage(i+1)+"&sid=756016";
            articles.addAll(this.crawlNews(url, i, 5, webSiteEntity.getWebsiteName()));
        }

        newsEntityService.saveNews(articles);

        return articles;
    }
}
