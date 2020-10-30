package com.news.readerservice.inter;

import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;

import java.util.List;
import java.util.regex.Matcher;

public interface NewEntityMapper {

    List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity);
}
