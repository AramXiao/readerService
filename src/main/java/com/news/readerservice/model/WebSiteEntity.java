package com.news.readerservice.model;

import com.news.readerservice.inter.NewEntityJsoupMapper;
import com.news.readerservice.inter.NewEntityMapper;

import java.util.List;

public class WebSiteEntity {

    private String id;
    private String websiteName;
    private String websiteUrl;
    private String pageLinksCssSelect;
    private String newsTableCssSelect;
    private String newsTagRegex;

    private NewEntityMapper mapper;
    private NewEntityJsoupMapper docMapper;

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

    public String getPageLinksCssSelect() {
        return pageLinksCssSelect;
    }

    public void setPageLinksCssSelect(String pageLinksCssSelect) {
        this.pageLinksCssSelect = pageLinksCssSelect;
    }

    public String getNewsTableCssSelect() {
        return newsTableCssSelect;
    }

    public void setNewsTableCssSelect(String newsTableCssSelect) {
        this.newsTableCssSelect = newsTableCssSelect;
    }

    public String getNewsTagRegex() {
        return newsTagRegex;
    }

    public NewEntityMapper getMapper() {
        return mapper;
    }

    public void setMapper(NewEntityMapper mapper) {
        this.mapper = mapper;
    }

    public void setNewsTagRegex(String newsTagRegex) {
        this.newsTagRegex = newsTagRegex;
    }


    public NewEntityJsoupMapper getDocMapper() {
        return docMapper;
    }

    public void setDocMapper(NewEntityJsoupMapper docMapper) {
        this.docMapper = docMapper;
    }

    @Override
    public String toString() {
        return "WebSiteEntity{" +
                "id='" + id + '\'' +
                ", websiteName='" + websiteName + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", pageLinksCssSelect='" + pageLinksCssSelect + '\'' +
                ", newsTableCssSelect='" + newsTableCssSelect + '\'' +
                ", newsTagRegex='" + newsTagRegex + '\'' +
                ", mapper=" + mapper +
                ", docMapper=" + docMapper +
                ", newsEntityList=" + newsEntityList +
                '}';
    }
}
