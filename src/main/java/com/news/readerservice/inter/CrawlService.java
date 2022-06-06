package com.news.readerservice.inter;

import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;

import java.util.List;

public interface CrawlService {
    List<NewsEntity> checkLastestNews();

    List<NewsEntity> initNewsToDB();

    WebSiteEntity getWebSiteEntity();
}
