package com.news.readerservice;

import com.news.readerservice.inter.NewEntityMapper;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.service.GenericCrawlService;
import com.news.readerservice.service.NewsEntityService;
import com.news.readerservice.service.ZhuHaiGovService;
import com.news.readerservice.utils.HtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@SpringBootTest
class ReaderserviceApplicationTests {

    @Autowired
    NewsEntityService newsEntityService;


    @Test
    void contextLoads() {

    }

    @Test
    void TestZhuHaiGovService(){
        ZhuHaiGovService zhuHaiGovService = new ZhuHaiGovService(newsEntityService);
        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海政府信息公开——十问十答","http://zjj.zhuhai.gov.cn/gkmlpt/index#153");

        zhuHaiGovService.setWebSiteEntity(webSiteEntity);

        zhuHaiGovService.checkLastestNews();
    }

    @Test
    void TestZhuHaiZhengxie(){
        GenericCrawlService  cralService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海政协——提案选登","http://www.zhzx.gov.cn/tagz/taxd/");
        webSiteEntity.setPageDivCssSelect(".dede_pages>a[href]");
        webSiteEntity.setNewsTableCssSelect(".list");
        webSiteEntity.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a href=\"([^\"]*?)\"[^>]*?title=\"([^\"]*?)\">[^<]*?</a>[^<]*?</li>");
        webSiteEntity.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setUrl(HtmlUtil.convertLink(webSiteEntity.getWebsiteUrl(), StringUtils.trim(m.group(2))));
                    newsEntity.setTitle(StringUtils.trim(m.group(3)));
                    newsEntity.setDate(StringUtils.trim(m.group(1)));
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setPageUrl(pageUrl);
                    resultList.add(newsEntity);
                }
                return resultList;
            }
        });
        cralService.setWebSiteEntity(webSiteEntity);

        cralService.checkLastestNews();
    }



}
