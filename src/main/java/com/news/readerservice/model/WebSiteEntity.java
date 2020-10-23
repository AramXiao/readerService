package com.news.readerservice.model;

import java.util.List;

public class WebSiteEntity {

    private String id;
    private String websiteName;
    private String websiteUrl;
    private List<NewsEntity> newsEntityList;

    public WebSiteEntity(String wesiteName, String websiteUrl){
        this.websiteName = wesiteName;
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }


    public List<NewsEntity> getNewsEntityList() {
        return newsEntityList;
    }

    public void setNewsEntityList(List<NewsEntity> newsEntityList) {
        this.newsEntityList = newsEntityList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
