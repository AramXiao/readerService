package com.news.readerservice.thread;

import com.news.readerservice.model.EmailParam;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.service.EmailService;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.util.concurrent.BlockingDeque;

public class CheckEmailTaskThread implements Runnable{

    private Logger LOG = Logger.getLogger(CheckEmailTaskThread.class);
    private EmailService emailService;
    public CheckEmailTaskThread(EmailService emailService){
        this.emailService = emailService;
    }
    @Override
    public void run() {

        LOG.info("开始查询发送邮件队列.....");
        BlockingDeque<NewsEntity> newsEntityList = emailService.getQueue();
        LOG.info("newsEntityList size--->"+newsEntityList.size());
        while(!newsEntityList.isEmpty()){

//            if(!newsEntityList.isEmpty()){
                NewsEntity news =  newsEntityList.removeFirst();
                LOG.info("发送邮件实体内容==>"+news);
                EmailParam emailParam = new EmailParam();
                emailParam.getEmailData().put("item",news);
                //此处to数组输入多个值，即可实现批量发送
                String [] to={"xyh.giant@gmail.com","390526405@qq.com"};
                try {
                    emailService.thymeleafEmail(news.getTitle(), emailParam);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }
    }
}
