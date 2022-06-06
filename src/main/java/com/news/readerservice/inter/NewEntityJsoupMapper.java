package com.news.readerservice.inter;

import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import org.jsoup.nodes.Document;

import java.util.List;

public interface NewEntityJsoupMapper {


    List<NewsEntity> mapToList(Document doc, String pageUrl, WebSiteEntity webSiteEntity);
}
