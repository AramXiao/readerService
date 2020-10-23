package com.news.readerservice;

import com.alibaba.fastjson.JSON;
import com.news.readerservice.model.NewsEntity;
import com.news.readerservice.service.NewsEntityService;
import com.news.readerservice.service.ZhuHaiCrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories("com.news.readerservice.dao.**")
@EnableScheduling
public class ReaderserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReaderserviceApplication.class, args);

    }

}
