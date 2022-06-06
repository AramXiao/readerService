package com.news.readerservice.service;

import com.news.readerservice.inter.CrawlService;
import com.news.readerservice.model.EmailParam;
import com.news.readerservice.model.NewsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmailTask implements  Runnable{

    private final Logger LOG = LoggerFactory.getLogger(EmailTask.class);

    private CrawlService crawlService;
    private EmailService emailService;

    public EmailTask(CrawlService crawlService, EmailService emailService){
        this.crawlService = crawlService;
        this.emailService = emailService;
    }

    @Override
    public void run() {

        LOG.info("开始线程" + Thread.currentThread().getName());

        LOG.info("开始处理网站-->"+this.crawlService.getWebSiteEntity().toString());
        try {
            EmailParam emailParam = new EmailParam();

            List<NewsEntity> dataList =  crawlService.checkLastestNews();
//            LOG.info("new dataList-->"+dataList);

            for(NewsEntity news : dataList){
                emailService.getQueue().add(news);
                emailParam.getEmailData().put("item",news);
                //此处to数组输入多个值，即可实现批量发送
                String [] to={"xyh.giant@gmail.com","390526405@qq.com"};
                emailService.thymeleafEmail(news.getTitle(), emailParam);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LOG.info("结束线程" + Thread.currentThread().getName());
    }
}
