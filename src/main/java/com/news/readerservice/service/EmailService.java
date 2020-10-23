package com.news.readerservice.service;

import com.news.readerservice.model.EmailParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Value("#{'${mail.to}'.split(',')}")
    private String[] toList;

    @Value("${templatePath}")
    private String templatePath;


    public void thymeleafEmail(String subject, EmailParam emailParam) throws MessagingException {
        this.thymeleafEmail(this.from,this.toList,subject,this.templatePath,emailParam);
    }


    public void thymeleafEmail(String subject, String template, EmailParam emailParam) throws MessagingException {
        this.thymeleafEmail(this.from,this.toList,subject,template,emailParam);
    }

    /**
     * html模板邮件
     * @param from 发件人
     * @param to 收件人
     * @param subject 邮件主题
     * @param emailParam 给模板的参数
     * @param template html模板路径(相对路径)  Thymeleaf的默认配置期望所有HTML文件都放在 **resources/templates ** 目录下，以.html扩展名结尾。
     * @throws MessagingException

    */
    public void thymeleafEmail(String from, String[] to, String subject, String template, EmailParam emailParam) throws MessagingException {
        LOG.info("from-->"+from);
        LOG.info("to-->"+to);
        MimeMessage mimeMessage =javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        // 利用 Thymeleaf 模板构建 html 文本
        Context ctx = new Context();
        // 给模板的参数的上下文
        ctx.setVariable("emailParam", emailParam);
        // 执行模板引擎，执行模板引擎需要传入模板名、上下文对象
        // Thymeleaf的默认配置期望所有HTML文件都放在 **resources/templates ** 目录下，以.html扩展名结尾。
        // String emailText = templateEngine.process("email/templates", ctx);
        String emailText = templateEngine.process(template, ctx);
        mimeMessageHelper.setText(emailText, true);
        // FileSystemResource logoImage= new FileSystemResource("D:\\image\\logo.jpg");
        //绝对路径
//        FileSystemResource logoImage = new FileSystemResource(imgPath);
        //相对路径，项目的resources路径下
        //ClassPathResource logoImage = new ClassPathResource("static/image/logonew.png");
        // 添加附件,第一个参数表示添加到 Email 中附件的名称，第二个参数是图片资源
        //一般图片调用这个方法
//        mimeMessageHelper.addInline("logoImage", logoImage);
        //一般文件附件调用这个方法
//		mimeMessageHelper.addAttachment("logoImage", resource);
        javaMailSender.send(mimeMessage);
    }
}
