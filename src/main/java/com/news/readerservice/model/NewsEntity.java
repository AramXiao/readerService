package com.news.readerservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Document(collection = "newsData")
public class NewsEntity implements Comparable<NewsEntity>{
    @Id
    String id;
    String webSiteUrl;
    String webSiteName;
    String pageUrl;
    String url;
    String title;
    String date;

    public NewsEntity(){}

    public NewsEntity(String url, String title, String date, String website){
        this.url = url;
        this.title = title;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public String getWebSiteName() {
        return webSiteName;
    }

    public void setWebSiteName(String webSiteName) {
        this.webSiteName = webSiteName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(NewsEntity o) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(this.getDate());
            Date date2 = sdf.parse(o.getDate());
            if(date1.after(date2)){
                return 1;
            }else{
                return -1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 0;
    }

    @Override
    public String toString() {
        return "NewsEntity{" +
                "webSiteUrl='" + webSiteUrl + '\'' +
                ", webSiteName='" + webSiteName + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
