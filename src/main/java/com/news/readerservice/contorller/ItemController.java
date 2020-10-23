package com.news.readerservice.contorller;

import com.news.readerservice.model.EmailParam;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.model.WebSiteEntity;
import com.news.readerservice.service.EmailService;
import com.news.readerservice.service.NewsEntityService;
import com.news.readerservice.service.TaskExcutorService;
import com.news.readerservice.service.ZhuHaiCrawlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/test")
@RestController
public class ItemController {

    private final Logger LOG = LoggerFactory.getLogger(ItemController.class);

    @Value("${spring.mail.username}")
    private String from;

    @Value("${templatePath}")
    private String templatePath;

    @Autowired
    private EmailService emailService;

    @Autowired
    NewsEntityService newsEntityService;

    @Autowired
    TaskExcutorService taskExcutorService;



    @PostMapping(value="/email")
    public void testEmail(){
        this.emailTask2();
    }

//    @Scheduled(cron="0 0/1 * * * ?")
    public void emailTask(){
        try {
            EmailParam emailParam = new EmailParam();

            ZhuHaiCrawlService zhuHaiCrawlService = new ZhuHaiCrawlService(newsEntityService);
            WebSiteEntity websiteEntity = new WebSiteEntity("珠海房产", "http://zjj.zhuhai.gov.cn/zwgk/jfwj/index.html");
            zhuHaiCrawlService.setWebSiteEntity(websiteEntity);

            List<NewsEntity> dataList =  zhuHaiCrawlService.checkLastestNews();
            LOG.info("new dataList-->"+dataList);
            for(NewsEntity news : dataList){
                emailParam.getEmailData().put("item",news);
                //此处to数组输入多个值，即可实现批量发送
                String [] to={"xyh.giant@gmail.com","390526405@qq.com"};
                emailService.thymeleafEmail(from, to, news.getTitle(), templatePath, emailParam);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void emailTask2(){
        taskExcutorService.runTask();
    }
}
