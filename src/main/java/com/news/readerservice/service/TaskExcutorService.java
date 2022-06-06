package com.news.readerservice.service;

import com.news.readerservice.inter.NewEntityJsoupMapper;
import com.news.readerservice.inter.NewEntityMapper;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.thread.CheckEmailTaskThread;
import com.news.readerservice.utils.HtmlUtil;
import com.news.readerservice.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TaskExcutorService {

    private final Logger LOG = LoggerFactory.getLogger(TaskExcutorService.class);

    private ThreadPoolExecutor executor;
    @Autowired
    NewsEntityService newsEntityService;
    @Autowired
    private EmailService emailService;

    private Thread t;

    public TaskExcutorService(){
        this.executor = new ThreadPoolExecutor(3, 5, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }


    public void runDailyTask(){
        ZhuHaiCrawlService zhuHaiZhuFangCrawlService = new ZhuHaiCrawlService(newsEntityService);
        WebSiteEntity zhuHaiZFEntity = new WebSiteEntity("珠海市住建局住房法规", "http://zjj.zhuhai.gov.cn/zwgk/zcfg/zffg/");
        zhuHaiZhuFangCrawlService.setWebSiteEntity(zhuHaiZFEntity);

        EmailTask zhuHaiZFTask = new EmailTask(zhuHaiZhuFangCrawlService, emailService);
        executor.execute(zhuHaiZFTask);

        ZhuHaiGovService zhuHaiGovService = new ZhuHaiGovService(newsEntityService);
        WebSiteEntity webSiteEntity = new WebSiteEntity("珠海政府信息公开——十问十答","http://zjj.zhuhai.gov.cn/gkmlpt/index#153");
        zhuHaiGovService.setWebSiteEntity(webSiteEntity);

        EmailTask zhuHaiGovTask = new EmailTask(zhuHaiGovService, emailService);
        executor.execute(zhuHaiGovTask);

        GenericCrawlService  cralService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity1 = new WebSiteEntity("珠海政协——提案选登","http://www.zhzx.gov.cn/tagz/taxd/");
        webSiteEntity1.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity1.setNewsTableCssSelect(".list");
        webSiteEntity1.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a href=\"([^\"]*?)\"[^>]*?title=\"([^\"]*?)\">[^<]*?</a>[^<]*?</li>");
        webSiteEntity1.setMapper(new NewEntityMapper() {
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
        cralService.setWebSiteEntity(webSiteEntity1);

        EmailTask zhuHaiZhexieTask = new EmailTask(cralService, emailService);
        executor.execute(zhuHaiZhexieTask);


        GenericCrawlService zhzxCrawlService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity2 = new WebSiteEntity("珠海市住建局——房地产市场管理","http://zjj.zhuhai.gov.cn/ywxx/fdcscgl/index.html");
        webSiteEntity2.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity2.setNewsTableCssSelect(".list01");
        webSiteEntity2.setNewsTagRegex("<li><span>([^<]*?)</span><a href=\"([^\"]*?)\"[\\s]*?title=\"([^\"]*?)\"[^>]*?>[^<]*?</a></li>");

        webSiteEntity2.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setUrl(HtmlUtil.convertLink(webSiteEntity.getWebsiteUrl(), StringUtils.trim(m.group(2))));
                    newsEntity.setTitle(StringUtils.trim(m.group(3)));
                    newsEntity.setDate(StringUtils.trim(m.group(1)));
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    resultList.add(newsEntity);
                }
                return resultList;
            }
        });

        zhzxCrawlService.setWebSiteEntity(webSiteEntity2);
        EmailTask zhzxCrawlTask = new EmailTask(zhzxCrawlService, emailService);
        executor.execute(zhzxCrawlTask);


        GenericCrawlService crawlService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity10 = new WebSiteEntity("香洲区项目审批","http://www.zhxz.gov.cn/xxgk/jsxm/xmsp/");
        webSiteEntity10.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity10.setNewsTableCssSelect(".ins-con");
        webSiteEntity10.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity10.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        crawlService.setWebSiteEntity(webSiteEntity10);
        EmailTask xiangZhouCrawlServiceEmailTask = new EmailTask(crawlService, emailService);
        executor.execute(xiangZhouCrawlServiceEmailTask);



        GenericCrawlService xiangZhouPublishCrawlService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity11 = new WebSiteEntity("香洲区新闻发布会","http://www.zhxz.gov.cn/xxgk/xzzx/xwfbh/");
        webSiteEntity11.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity11.setNewsTableCssSelect(".ins-con");
        webSiteEntity11.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity11.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        xiangZhouPublishCrawlService.setWebSiteEntity(webSiteEntity11);
        EmailTask xiangZhouPublishEmailTask = new EmailTask(xiangZhouPublishCrawlService, emailService);
        executor.execute(xiangZhouPublishEmailTask);


        GenericCrawlService xiangZhouWorkReportService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity12 = new WebSiteEntity("香洲区政府工作报告","http://www.zhxz.gov.cn/xxgk/zfgzgb/index.html");
        webSiteEntity12.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity12.setNewsTableCssSelect(".ins-con");
        webSiteEntity12.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity12.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        xiangZhouWorkReportService.setWebSiteEntity(webSiteEntity12);
        EmailTask xiangZhouWorkReportEmailTask = new EmailTask(xiangZhouWorkReportService, emailService);
        executor.execute(xiangZhouWorkReportEmailTask);




        GenericCrawlService xiangZhouGovDocService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity13 = new WebSiteEntity("香洲区政府红头文件","http://www.zhxz.gov.cn/xxgk/gkml/qzfgb/zfgb/index.html");
        webSiteEntity13.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity13.setNewsTableCssSelect(".mess-list-lBox");
        webSiteEntity13.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity13.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        xiangZhouGovDocService.setWebSiteEntity(webSiteEntity13);
        EmailTask xiangZhouGovDocEmailTask = new EmailTask(xiangZhouGovDocService, emailService);
        executor.execute(xiangZhouGovDocEmailTask);



        GenericCrawlService xiangZhouLowGovDocService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity14 = new WebSiteEntity("香洲区法规公文","http://www.zhxz.gov.cn/xxgk/fggw/gfxwj/");
        webSiteEntity14.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity14.setNewsTableCssSelect(".ins-con");
        webSiteEntity14.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity14.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        xiangZhouLowGovDocService.setWebSiteEntity(webSiteEntity14);
        EmailTask xiangZhouLowGovDocEmailTask = new EmailTask(xiangZhouLowGovDocService, emailService);
        executor.execute(xiangZhouLowGovDocEmailTask);


        GenericCrawlService xiangZhouOldChangeService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity15 = new WebSiteEntity("香洲区房屋征收补偿信息公开","http://www.zhxz.gov.cn/zdlyxxgk2016/fwzsbcxxgk/gytdsfwzsxmxx/");
        webSiteEntity15.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity15.setNewsTableCssSelect(".ins-con");
        webSiteEntity15.setNewsTagRegex("<li>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity15.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        xiangZhouOldChangeService.setWebSiteEntity(webSiteEntity15);
        EmailTask xiangZhouOldChangeEmailTask = new EmailTask(xiangZhouOldChangeService, emailService);
        executor.execute(xiangZhouOldChangeEmailTask);

        GenericCrawlService zhuHaiGovPrePublicService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity16 = new WebSiteEntity("珠海政府征地预公告","http://www.zhuhai.gov.cn/xw/ztjj/zhszdlyxxgkzl/zdxxgk/pchddxzxmydmc/zdyggxx/");
        webSiteEntity16.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity16.setNewsTableCssSelect(".ins-con");
        webSiteEntity16.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity16.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        zhuHaiGovPrePublicService.setWebSiteEntity(webSiteEntity16);
        EmailTask zhuHaiGovPrePublicServiceEamilTask = new EmailTask(zhuHaiGovPrePublicService, emailService);
        executor.execute(zhuHaiGovPrePublicServiceEamilTask);




        GenericCrawlService zhuHaiGovPlacePublicService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity17 = new WebSiteEntity("珠海政府征地公告","http://www.zhuhai.gov.cn/xw/ztjj/zhszdlyxxgkzl/zdxxgk/pchddxzxmydmc/zdggxx/");
        webSiteEntity17.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity17.setNewsTableCssSelect(".list");
        webSiteEntity17.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity17.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        zhuHaiGovPlacePublicService.setWebSiteEntity(webSiteEntity17);
        EmailTask zhuHaiGovPlacePublicEamilTask = new EmailTask(zhuHaiGovPlacePublicService, emailService);
        executor.execute(zhuHaiGovPlacePublicEamilTask);



        GenericCrawlService zhuHaiGovTravelMarketService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity18 = new WebSiteEntity("珠海政府旅游市场接待人次","http://www.zhuhai.gov.cn/xw/ztjj/zhszdlyxxgkzl/lyscjgzfxxgk/cyzl/");
        webSiteEntity18.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity18.setNewsTableCssSelect(".list");
        webSiteEntity18.setNewsTagRegex("<li>[^<]*?<span>([^<]*?)</span>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity18.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(StringUtils.trim(m.group(2)));
                    newsEntity.setTitle(StringUtils.trim(m.group(3)));
                    newsEntity.setDate(StringUtils.trim(m.group(1)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        zhuHaiGovTravelMarketService.setWebSiteEntity(webSiteEntity18);
        EmailTask zhuHaiGovTravelMarketEmailTask = new EmailTask(zhuHaiGovTravelMarketService, emailService);
        executor.execute(zhuHaiGovTravelMarketEmailTask);




        GenericCrawlService jinwanAuditService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity19 = new WebSiteEntity("金湾区项目审批+重大项目","http://www.jinwan.gov.cn/zwzx/jsxm/xmsp/");
        webSiteEntity19.setPageLinksCssSelect(".page>a[href]");
        webSiteEntity19.setNewsTableCssSelect(".list");
        webSiteEntity19.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity19.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanAuditService.setWebSiteEntity(webSiteEntity19);
        EmailTask jinwanAuditEmailTask = new EmailTask(jinwanAuditService, emailService);
        executor.execute(jinwanAuditEmailTask);




        GenericCrawlService jinwanNewsService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity20 = new WebSiteEntity("金湾区新闻资讯（官方媒体的）","http://www.jinwan.gov.cn/jwxw/mtgz/");
        webSiteEntity20.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity20.setNewsTableCssSelect(".right");
        webSiteEntity20.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity20.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanNewsService.setWebSiteEntity(webSiteEntity20);
        EmailTask jinwanNewsEmailTask = new EmailTask(jinwanNewsService, emailService);
        executor.execute(jinwanNewsEmailTask);



        GenericCrawlService jinwanImportantItemService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity21 = new WebSiteEntity("金湾五公开重点项目","http://www.jinwan.gov.cn/zwzx/wgk/zxgk/");
        webSiteEntity21.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity21.setNewsTableCssSelect(".right");
        webSiteEntity21.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity21.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanImportantItemService.setWebSiteEntity(webSiteEntity21);
        EmailTask jinwanImportantItemEmailTask = new EmailTask(jinwanImportantItemService, emailService);
        executor.execute(jinwanImportantItemEmailTask);


        GenericCrawlService jinwanResultPublicService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity22 = new WebSiteEntity("金湾区五公开结果","http://www.jinwan.gov.cn/zwzx/wgk/jggk/");
        webSiteEntity22.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity22.setNewsTableCssSelect(".right");
        webSiteEntity22.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity22.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanResultPublicService.setWebSiteEntity(webSiteEntity22);
        EmailTask jinwanResultPublicEmailTask = new EmailTask(jinwanResultPublicService, emailService);
        executor.execute(jinwanResultPublicEmailTask);




        GenericCrawlService jinwanPlanService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity23 = new WebSiteEntity("金湾区规划计划","http://www.jinwan.gov.cn/zwzx/ghjh/");
        webSiteEntity23.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity23.setNewsTableCssSelect(".right");
        webSiteEntity23.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity23.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanPlanService.setWebSiteEntity(webSiteEntity23);
        EmailTask jinwanPlanServiceEmailTask = new EmailTask(jinwanPlanService, emailService);
        executor.execute(jinwanPlanServiceEmailTask);





        GenericCrawlService jinwanEconomicService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity24 = new WebSiteEntity("金湾区经济运行简况","http://www.jinwan.gov.cn/zwzx/sjfb/");
        webSiteEntity24.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity24.setNewsTableCssSelect(".right");
        webSiteEntity24.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity24.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanEconomicService.setWebSiteEntity(webSiteEntity24);
        EmailTask jinwanEconomicEmailTask = new EmailTask(jinwanEconomicService, emailService);
        executor.execute(jinwanEconomicEmailTask);




        GenericCrawlService jinwanPurposeService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity25 = new WebSiteEntity("金湾区提案建议","http://www.jinwan.gov.cn/zwzx/jytablgz/");
        webSiteEntity25.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity25.setNewsTableCssSelect(".right");
        webSiteEntity25.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity25.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanPurposeService.setWebSiteEntity(webSiteEntity25);
        EmailTask jinwanPurposeServiceEmailTask = new EmailTask(jinwanPurposeService, emailService);
        executor.execute(jinwanPurposeServiceEmailTask);




        GenericCrawlService jinwanSurveyService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity26 = new WebSiteEntity("金湾区调查征集","http://www.jinwan.gov.cn/zwzx/dczj/");
        webSiteEntity26.setPageLinksCssSelect(".pager1>a[href]");
        webSiteEntity26.setNewsTableCssSelect("#NewsList");
        webSiteEntity26.setNewsTagRegex("<li[^>]*?>[^<]*?<a[^>]*?href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a>[^<]*?<span>([^<]*?)</span>[^<]*?</li>");

        webSiteEntity26.setMapper(new NewEntityMapper() {
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
                    newsEntity.setDate(StringUtils.trim(m.group(3)));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        jinwanSurveyService.setWebSiteEntity(webSiteEntity26);
        EmailTask jinwanSurveyEmailTask = new EmailTask(jinwanSurveyService, emailService);
        executor.execute(jinwanSurveyEmailTask);



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
        executor.execute(doumenPlanEmailTask);


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
        executor.execute(gdInvestEmailTask);

    }




    public  void runTask(){

        CheckEmailTaskThread threadTask = new CheckEmailTaskThread(emailService);
        t = new Thread(threadTask);
        t.start();

        ZhuHaiCrawlService zhuHaiCrawlService = new ZhuHaiCrawlService(newsEntityService);
        WebSiteEntity websiteEntity = new WebSiteEntity("珠海市住建局局发文件", "http://zjj.zhuhai.gov.cn/zwgk/jfwj/index.html");
        zhuHaiCrawlService.setWebSiteEntity(websiteEntity);


        EmailTask task = new EmailTask(zhuHaiCrawlService, emailService);
        executor.execute(task);


        PlatFormPreSaleService platFormPreSaleService = new PlatFormPreSaleService(newsEntityService);
        WebSiteEntity webSiteEntity3 = new WebSiteEntity("房地产交易监管平台——预售公示", "http://113.106.103.37/presalelist?keywords=presale&tabkey=all&searchcode=");
        webSiteEntity3.setPageLinksCssSelect(".pub-list");
        webSiteEntity3.setDocMapper(new NewEntityJsoupMapper() {
            @Override
            public List<NewsEntity> mapToList(Document doc, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                Elements es = doc.select(".pub-list .house-info");

                for(Element e : es){
                    NewsEntity newsEntity = new NewsEntity();
                    Element link = e.selectFirst("a[href]");
                    Element houseInfo = e.selectFirst(".house-info-main>table>tbody>tr");

                    String linkStr = link.attr("href");
                    newsEntity.setTitle(StringUtils.trim(link.text()));
                    newsEntity.setUrl(HtmlUtil.convertLink(webSiteEntity.getWebsiteUrl(), linkStr));
                    newsEntity.setDate(null);
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());



                    if(houseInfo!=null){
                        newsEntity.setDetailInfo("楼盘信息："+houseInfo.text().replaceAll("^[\\s]*$", ","));
                    }

                    resultList.add(newsEntity);
                }

                return resultList;
            }
        });

        platFormPreSaleService.setWebSiteEntity(webSiteEntity3);
        EmailTask platFormPreSaleTask = new EmailTask(platFormPreSaleService, emailService);
        executor.execute(platFormPreSaleTask);


        GenericCrawlService hourseTradePlaformService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity4 = new WebSiteEntity("房地产交易监管平台——商品房价格公示表","http://zjj.zhuhai.gov.cn/ywxx/ywgsgg/spfjgbags/");
        webSiteEntity4.setPageLinksCssSelect(".dede_pages>a[href]");

        webSiteEntity4.setNewsTableCssSelect(".list01");
        webSiteEntity4.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");
        webSiteEntity4.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity entity = new NewsEntity();
                    entity.setDate(StringUtils.trim(m.group(1)));
                    entity.setUrl(StringUtils.trim(m.group(2)));
                    entity.setTitle(StringUtils.trim(m.group(3)));
                    entity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    entity.setWebSiteName(webSiteEntity.getWebsiteName());
                    entity.setPageUrl(pageUrl);
                    resultList.add(entity);
                }
                return resultList;
            }
        });

        hourseTradePlaformService.setWebSiteEntity(webSiteEntity4);
        EmailTask hourseTradePlaformTask = new EmailTask(hourseTradePlaformService, emailService);
        executor.execute(hourseTradePlaformTask);


        ZhuHaiCityBuildService zhuHaiCityBuildService = new ZhuHaiCityBuildService(newsEntityService);
        WebSiteEntity webSiteEntity5 = new WebSiteEntity("珠海市住建局——施工许可","http://219.131.222.110:8899/cbms/zhzgj/toPermitSearchsCbmsWZAction.action");
        webSiteEntity5.setNewsTableCssSelect(".tablecon>.tbl");
        webSiteEntity5.setDocMapper(new NewEntityJsoupMapper() {
            @Override
            public List<NewsEntity> mapToList(Document doc, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                Element el = doc.selectFirst(".tablecon>.tbl");

                Elements records = el.select("tr");

                for(int i=1; i<records.size(); i++){
                    Element record = records.get(i);
                    Elements tds = record.select("td");
                    NewsEntity entity = new NewsEntity();
                    entity.setTitle(tds.get(2).text());
                    entity.setUrl("http://219.131.222.110:8899/cbms/zhzgj/"+tds.get(2).selectFirst("a[href]").attr("href"));
                    entity.setPageUrl(pageUrl);
                    entity.setWebSiteName(webSiteEntity.getWebsiteName());
                    entity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    StringBuilder sb = new StringBuilder();
                    sb.append("施工许可证号:"+tds.get(1).text());
                    sb.append(",工程名称："+tds.get(2).text());
                    sb.append(",建设单位："+tds.get(3).text());
                    sb.append(",施工单位："+tds.get(4).text());
                    sb.append(",监理单位："+tds.get(5).text());
                    sb.append("工程地址："+tds.get(6).text());
                    entity.setDetailInfo(sb.toString());
                    resultList.add(entity);
                }

                return resultList;
            }
        });

        zhuHaiCityBuildService.setWebSiteEntity(webSiteEntity5);
        EmailTask zhuHaiCityBuildTask = new EmailTask(zhuHaiCityBuildService, emailService);
        executor.execute(zhuHaiCityBuildTask);



        GenericCrawlService resourceUniomService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity6 = new WebSiteEntity("珠海市自然资源局——土地招拍挂","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/tdzpg/");
        webSiteEntity6.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity6.setNewsTableCssSelect(".list01");
        webSiteEntity6.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity6.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        resourceUniomService.setWebSiteEntity(webSiteEntity6);
        EmailTask resourceUniomTask = new EmailTask(resourceUniomService, emailService);
        executor.execute(resourceUniomTask);



        GenericCrawlService resourceSaleResultService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity7 = new WebSiteEntity("珠海市自然资源局——土地招拍挂结果","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/tdcrjg/");
        webSiteEntity7.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity7.setNewsTableCssSelect(".list01");
        webSiteEntity7.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity7.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        resourceSaleResultService.setWebSiteEntity(webSiteEntity7);
        EmailTask resourceSaleResultTask = new EmailTask(resourceSaleResultService, emailService);
        executor.execute(resourceSaleResultTask);





        GenericCrawlService crawlPlanConfirmService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity8 = new WebSiteEntity("珠海市自然资源局——规划条件核实","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/ghtjhs/");
        webSiteEntity8.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity8.setNewsTableCssSelect(".list01");
        webSiteEntity8.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity8.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        crawlPlanConfirmService.setWebSiteEntity(webSiteEntity8);
        EmailTask crawlPlanConfirmTask = new EmailTask(crawlPlanConfirmService, emailService);
        executor.execute(crawlPlanConfirmTask);


        GenericCrawlService crawlPlanConstructService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity9 = new WebSiteEntity("珠海市自然资源局——规划编制业务","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/ghbzyw/");
        webSiteEntity9.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity9.setNewsTableCssSelect(".list01");
        webSiteEntity9.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity9.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        crawlPlanConstructService.setWebSiteEntity(webSiteEntity9);
        EmailTask crawlPlanConstructTask = new EmailTask(crawlPlanConstructService, emailService);
        executor.execute(crawlPlanConstructTask);



        GenericCrawlService crawlPlanAuditService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity10 = new WebSiteEntity("珠海市发改局——项目审批结果","http://fgj.zhuhai.gov.cn/zwfw/xmspjg/index.html");
        webSiteEntity10.setPageLinksCssSelect("#pagesize>a[href]");
        webSiteEntity10.setNewsTableCssSelect(".list");
        webSiteEntity10.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity10.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        crawlPlanAuditService.setWebSiteEntity(webSiteEntity10);
        EmailTask crawlPlanAuditTask = new EmailTask(crawlPlanAuditService, emailService);
        executor.execute(crawlPlanAuditTask);






        TradeCenterPlaceService tradeCenterPlaceService = new TradeCenterPlaceService(newsEntityService);
        WebSiteEntity webSiteEntity11 = new WebSiteEntity("珠海市公共资源交易中心拍地","http://ggzy.zhuhai.gov.cn/exchangeinfo/landexchange/tdjygg/index.jhtml");
        webSiteEntity11.setPageLinksCssSelect(".pagesite>div>a[href]");
        webSiteEntity11.setNewsTableCssSelect(".rl-box-right");
        webSiteEntity11.setNewsTagRegex("(<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)<span[^>]*>([^<]*?)</span>[^<]*?</a>[^<]*?</li>|<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a><a[^<]*?>.*?</a>[^<]*?<span[^>]*>([^<]*?)</span>[^<]*?</li>)");

        webSiteEntity11.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    if(StringUtil.isNotBlank(m.group(2))){
                        newsEntity.setUrl(m.group(2));
                        newsEntity.setTitle(m.group(3));
                        newsEntity.setDate(m.group(4));
                    }else{
                        newsEntity.setUrl(m.group(5));
                        newsEntity.setTitle(m.group(6));
                        newsEntity.setDate(m.group(7));
                    }
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        tradeCenterPlaceService.setWebSiteEntity(webSiteEntity11);
        EmailTask tradeCenterPlaceTask = new EmailTask(tradeCenterPlaceService, emailService);
        executor.execute(tradeCenterPlaceTask);



        TradeCenterPlaceService tradeCenterPlaceResultService = new TradeCenterPlaceService(newsEntityService);
        WebSiteEntity webSiteEntity12 = new WebSiteEntity("珠海市公共资源交易中心拍地结果","http://ggzy.zhuhai.gov.cn/exchangeinfo/landexchange/tdjggs/index.jhtml");
        webSiteEntity12.setPageLinksCssSelect(".pagesite>div>a[href]");
        webSiteEntity12.setNewsTableCssSelect(".rl-box-right");
        webSiteEntity12.setNewsTagRegex("<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)<span[^>]*>([^<]*?)</span>[^<]*?</a>[^<]*?</li>");
        //webSiteEntity12.setNewsTagRegex("(<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)<span[^>]*>([^<]*?)</span>[^<]*?</a>[^<]*?</li>|<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a><a[^<]*?>.*?</a>[^<]*?<span[^>]*>([^<]*?)</span>[^<]*?</li>)");

        webSiteEntity12.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(1));
                    newsEntity.setTitle(m.group(2));
                    newsEntity.setDate(m.group(3));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        tradeCenterPlaceResultService.setWebSiteEntity(webSiteEntity12);
        EmailTask tradeCenterPlaceResultTask = new EmailTask(tradeCenterPlaceResultService, emailService);
        executor.execute(tradeCenterPlaceResultTask);


        GenericCrawlService crawlPublicEngineerService = new GenericCrawlService(newsEntityService);
        WebSiteEntity webSiteEntity13 = new WebSiteEntity("珠海市自然资源局——建筑工程类公示","http://zrzyj.zhuhai.gov.cn/zwgk/gggs/jzgclgs/");
        webSiteEntity13.setPageLinksCssSelect(".dede_pages>a[href]");
        webSiteEntity13.setNewsTableCssSelect(".list01");
        webSiteEntity13.setNewsTagRegex("<li><span>([^<]*?)</span>[^<]*?<a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a>[^<]*?</li>");

        webSiteEntity13.setMapper(new NewEntityMapper() {
            @Override
            public List<NewsEntity> mapToList(Matcher m, String pageUrl, WebSiteEntity webSiteEntity) {
                List<NewsEntity> resultList = new ArrayList<>();
                while(m.find()){
                    NewsEntity newsEntity = new NewsEntity();
                    newsEntity.setWebSiteUrl(webSiteEntity.getWebsiteUrl());
                    newsEntity.setWebSiteName(webSiteEntity.getWebsiteName());
                    newsEntity.setPageUrl(pageUrl);
                    newsEntity.setUrl(m.group(2));
                    newsEntity.setTitle(m.group(3));
                    newsEntity.setDate(m.group(1));
                    resultList.add(newsEntity);

                }
                return resultList;
            }
        });

        crawlPublicEngineerService.setWebSiteEntity(webSiteEntity13);
        EmailTask crawlPublicEngineerTask = new EmailTask(crawlPublicEngineerService, emailService);
        executor.execute(crawlPublicEngineerTask);






    }


//    public static void main(String[] args){
//        String text = "<li><a href=\"http://ggzy.zhuhai.gov.cn:80/exchangeinfo/landexchange/tdjygg/216028.jhtml\" target=\"_blank\" title=\"国有建设用地使用权网上挂牌出让公告（坪岚路西侧二类居住、文化设施）（交易序号：20250）\">\n" +
//                "国有建设用地使用权网上挂牌出让公告（坪岚路西侧二类居住、文化设施）（交易序号：20250）\t\t\t\t\t\t\t\t\t</a><a href=\"https://202.105.183.119:9041/ZTK\" target=\"_blank\"><img src=\"/r/cms/www/default/image/jsbm.jpg\"></a>\n" +
//                "\t\t\t\t\t\t\t<span style=\"float: right;font-size: 14px;line-height: 36px;color: #333333;\">2020-12-29</span></li>";
//        String text2 = "<li><a href=\"http://ggzy.zhuhai.gov.cn:80/exchangeinfo/landexchange/tdjygg/215695.jhtml\" target=\"_blank\" title=\"国有建设用地使用权网上挂牌出让公告（富山）（交易序号：20248）\">\n" +
//                "国有建设用地使用权网上挂牌出让公告（富山）（交易序号：20248）\t\t\t\t\t\t\t<span style=\"float: right;font-size: 14px;line-height: 36px;color: #333333;\">2020-12-25</span></a></li>";
//        Pattern p = Pattern.compile("(<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)<span[^>]*>([^<]*?)</span>[^<]*?</a>[^<]*?</li>|<li><a[\\s]*?href=\"([^\"]*?)\"[^>]*>([^<]*?)</a><a[^<]*?>.*?</a>[^<]*?<span[^>]*>([^<]*?)</span>[^<]*?</li>)", Pattern.CASE_INSENSITIVE);
//
//        Matcher m = p.matcher(text);
//
//        System.out.println("start");
//        if(m.find()){
//
//            System.out.println(m.groupCount());
//            System.out.println("group(0)-->"+m.group(0));
//            System.out.println("group(1)--->"+m.group(1));
//            System.out.println("group(2)-->"+m.group(2));
//            System.out.println("group(3)-->"+m.group(3));
//            System.out.println("group(4)-->"+m.group(4));
//            System.out.println("group(5)-->"+m.group(5));
//            System.out.println("group(6)-->"+m.group(6));
//            System.out.println("group(7)-->"+m.group(7));
//        }else{
//            System.out.println("not find");
//        }
//    }

}


