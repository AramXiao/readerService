package com.news.readerservice;

import com.gargoylesoftware.htmlunit.javascript.host.canvas.ext.WEBGL_compressed_texture_s3tc;
import com.news.readerservice.inter.CrawlService;
import com.news.readerservice.inter.NewEntityJsoupMapper;
import com.news.readerservice.inter.NewEntityMapper;
import com.news.readerservice.model.EmailParam;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.service.*;
import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.StringUtil;
import com.shapesecurity.salvation2.Values.Hash;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@SpringBootTest
class ReaderserviceApplicationTests {

    @Autowired
    NewsEntityService newsEntityService;

    @Autowired
    EmailService emailService;


    @Test
    void contextLoads() {

    }

//    @Test
//    void TestZhuHaiGovService(){
//        ZhuHaiGovService zhuHaiGovService = new ZhuHaiGovService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海政府信息公开——十问十答","http://zjj.zhuhai.gov.cn/gkmlpt/index#153");
//
//        zhuHaiGovService.setWebSiteEntity(webSiteEntity);
//
//        zhuHaiGovService.checkLastestNews();
//    }
//
//    @Test
//    void TestZhuHaiZhengxie(){
//        GenericCrawlService  cralService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海政协——提案选登","http://www.zhzx.gov.cn/tagz/taxd/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list");
//        webSiteEntity.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a href=\"([^\"]*?)\"[^>]*?title=\"([^\"]*?)\">[^<]*?</a>[^<]*?</li>");
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setUrl(HtmlUtil.convertLink(webSiteEntity.getWebsiteUrl(), StringUtils.trim(m.group(2))));
//                    newsEntity.setTitle(StringUtils.trim(m.group(3)));
//                    newsEntity.setDate(StringUtils.trim(m.group(1)));
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setPageUrl(pageUrl);
//                    resultList.add(newsEntity);
//                }
//                return resultList;
//            }
//        });
//        cralService.setWebSiteEntity(webSiteEntity);
//
//        cralService.checkLastestNews();
//    }
//
//    @Test
//    void testZhuHaiZhuJianMarket(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市住建局——房地产市场管理","http://zjj.zhuhai.gov.cn/ywxx/fdcscgl/index.html");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span><a href=\"([^\"]*?)\"[\\s]*?title=\"([^\"]*?)\"[^>]*?>[^<]*?</a></li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setUrl(HtmlUtil.convertLink(webSiteEntity.getWebsiteUrl(), StringUtils.trim(m.group(2))));
//                    newsEntity.setTitle(StringUtils.trim(m.group(3)));
//                    newsEntity.setDate(StringUtils.trim(m.group(1)));
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    resultList.add(newsEntity);
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//
//    }
//
//
//    @Test
//    void testFangDiChanJiaoYiPlatForm(){
//        PlatFormPreSaleService crawlService = new PlatFormPreSaleService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("房地产交易监管平台——预售公示", "http://113.106.103.37/presalelist?keywords=presale&tabkey=all&searchcode=");
//        webSiteEntity.setPageLinksCssSelect(".pub-list");
//        webSiteEntity.setDocMapper(new NewEntityJsoupMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Document doc, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                Elements es = doc.select(".pub-list .house-info");
//
//                for(Element e : es){
//                    NewsEntity newsEntity = new NewsEntity();
//                    Element link = e.selectFirst("a[href]");
//                    Element houseInfo = e.selectFirst(".house-info-main>table>tbody>tr");
//
//                    String linkStr = link.attr("href");
//                    newsEntity.setTitle(StringUtils.trim(link.text()));
//                    newsEntity.setUrl(linkStr);
//                    newsEntity.setDate(null);
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    if(houseInfo!=null){
//                        newsEntity.setDetailInfo("楼盘信息："+houseInfo.text().replaceAll("^[\\s]*$", ","));
//                    }
//
//                    resultList.add(newsEntity);
//                }
//
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//
//    }
//
//
//    @Test
//    void testHousePricePublicShow(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("房地产交易监管平台——商品房价格公示表","http://zjj.zhuhai.gov.cn/ywxx/ywgsgg/spfjgbags/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity entity = new NewsEntity();
//                    entity.setDate(StringUtils.trim(m.group(1)));
//                    entity.setUrl(StringUtils.trim(m.group(2)));
//                    entity.setTitle(StringUtils.trim(m.group(3)));
//                    entity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    entity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    entity.setPageUrl(pageUrl);
//                    resultList.add(entity);
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }
//
//
//    @Test
//    void testZhuHaiBuild(){
//        ZhuHaiCityBuildService crawlService = new ZhuHaiCityBuildService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市住建局——施工许可","http://219.131.222.110:8899/cbms/zhzgj/toPermitSearchsCbmsWZAction.action");
//        webSiteEntity.setNewsTableCssSelect(".tablecon>.tbl");
//        webSiteEntity.setDocMapper(new NewEntityJsoupMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Document doc, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                Element el = doc.selectFirst(".tablecon>.tbl");
//
//                Elements records = el.select("tr");
//
//                for(int i=1; i<records.size(); i++){
//                    Element record = records.get(i);
//                    Elements tds = record.select("td");
//                    NewsEntity entity = new NewsEntity();
//                    entity.setTitle(tds.get(2).text());
//                    entity.setUrl("http://219.131.222.110:8899/cbms/zhzgj/"+tds.get(2).selectFirst("a[href]").attr("href"));
//                    entity.setPageUrl(pageUrl);
//                    entity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    entity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("施工许可证号:"+tds.get(1).text());
//                    sb.append(",工程名称："+tds.get(2).text());
//                    sb.append(",建设单位："+tds.get(3).text());
//                    sb.append(",施工单位："+tds.get(4).text());
//                    sb.append(",监理单位："+tds.get(5).text());
//                    sb.append("工程地址："+tds.get(6).text());
//                    entity.setDetailInfo(sb.toString());
//                    resultList.add(entity);
//                }
//
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//
//    }
//
//    @Test
//    void crawlPlaceSale(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市自然资源局——土地招拍挂","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/tdzpg/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setUrl(m.group(2));
//                    newsEntity.setTitle(m.group(3));
//                    newsEntity.setDate(m.group(1));
//                    resultList.add(newsEntity);
//
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }
//
//    @Test
//    void crawlPlaceSaleResult(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市自然资源局——土地招拍挂结果","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/tdcrjg/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setUrl(m.group(2));
//                    newsEntity.setTitle(m.group(3));
//                    newsEntity.setDate(m.group(1));
//                    resultList.add(newsEntity);
//
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }
//
//    @Test
//    void crawlPlanConfirm(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市自然资源局——规划条件核实","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/ghtjhs/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setUrl(m.group(2));
//                    newsEntity.setTitle(m.group(3));
//                    newsEntity.setDate(m.group(1));
//                    resultList.add(newsEntity);
//
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }
//
//    @Test
//    void crawlPlanConstruct(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海市自然资源局——规划编制业务","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/ghbzyw/");
//        webSiteEntity.setPageLinksCssSelect(".dede_pages>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".list01");
//        webSiteEntity.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setUrl(m.group(2));
//                    newsEntity.setTitle(m.group(3));
//                    newsEntity.setDate(m.group(1));
//                    resultList.add(newsEntity);
//
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }

//    @Test
//    void crawlPlanConstruct(){
//        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
//        WebSiteEntity webSiteEntity = new WebSiteEntity("香洲区项目审批","http://www.zhxz.gov.cn/xxgk/jsxm/xmsp/");
//        webSiteEntity.setPageLinksCssSelect(".page>a[href]");
//        webSiteEntity.setNewsTableCssSelect(".ins-con");
//        webSiteEntity.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");
//
//        webSiteEntity.setMapper(new NewEntityMapper() {
//            @Override
//            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
//                List<NewsEntity> resultList = new ArrayList<>();
//                while(m.find()){
//                    NewsEntity newsEntity = new NewsEntity();
//                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
//                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
//                    newsEntity.setPageUrl(pageUrl);
//                    newsEntity.setUrl(m.group(1));
//                    newsEntity.setTitle(m.group(2));
//                    newsEntity.setDate(m.group(3));
//                    resultList.add(newsEntity);
//
//                }
//                return resultList;
//            }
//        });
//
//        crawlService.setWebSiteEntity(webSiteEntity);
//        crawlService.checkLastestNews();
//    }


    /*@Test
    void crawlPlanConstruct(){

        GenericCrawlService doumenPlanService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity27 = new WebSiteEntity("斗门规划计划","http://www.doumen.gov.cn/zhsdmqrmzfmhwz/zwgk/ghjh/");
        webSiteEntity27.setPageLinksCssSelect(".page_num a[href]");
        webSiteEntity27.setNewsTableCssSelect(".con-right");
        webSiteEntity27.setNewsTagRegex("<div[^>]*?>[^<]*<div[^>]*?>[^<]*<a[\\s]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*</div>[^<]*<table[^>]*?>[^<]*<tbody>[^<]*<tr>[^<]*<td[^>]*>发布时间：([^<])*");

        webSiteEntity27.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(StringUtils.trim(m.group(1)));
                    newsEntity.setTitle(StringUtils.trim(m.group(2)));
//                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        doumenPlanService.setWebSiteEntity(webSiteEntity27);
        EmailTask doumenPlanEmailTask = new EmailTask(doumenPlanService, emailService);
        doumenPlanEmailTask.run();
    }*/

    @Test
    void crawlPlanConstruct(){
        GdInvestService gdInvestService = new GdInvestService(newsEntityService);
        WebSiteEntity webSiteEntity28 = new WebSiteEntity("广东省投资项目在线审批监管平台","https://www.gdtz.gov.cn/tybm/apply3!searchMore3.action?isCity=&actionCityId=");
        webSiteEntity28.setPageLinksCssSelect(".badoo a[href]");
        webSiteEntity28.setNewsTableCssSelect(".tab-a");
        webSiteEntity28.setNewsTagRegex("<tr>[^<]*<td>[^<]*</td>[^<]*<td>[^<]*?<div[^>]*?>[^<]*<a[\\s]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*</div></td>[^<]*?<td>[^<]*?<div[^>]*?>[^<]*?</div></td>[^<]*?<td>[^<]*?</td>[^<]*?<td>[^<]*?<div[^>]*?>([^<]*?)</div>[^<]*?</td>[^<]*?</tr>");

        webSiteEntity28.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity28.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity28.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(StringUtils.trim(m.group(1)));
                    newsEntity.setTitle(StringUtils.trim(m.group(2)));
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);
                }
                return resultList;
            }
        });

        gdInvestService.setWebSiteEntity(webSiteEntity28);
        EmailTask gdInvestEmailTask = new EmailTask(gdInvestService, emailService);
        gdInvestEmailTask.run();
    }



}
