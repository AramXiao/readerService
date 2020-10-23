package com.news.readerservice.service;

import com.news.readerservice.dao.NewsEntityRepository;
import com.news.readerservice.model.NewsEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsEntityService {

    @Autowired
    private NewsEntityRepository newsEntityRepository;

    private Logger LOG = Logger.getLogger(NewsEntityService.class);

    public List<NewsEntity> saveNews(List<NewsEntity> newsEntityList){
        return newsEntityRepository.saveAll(newsEntityList);
    }

    public boolean checkIfExist(NewsEntity newsEntity){
        Example<NewsEntity> example = Example.of(newsEntity);
        Optional<NewsEntity> optional = newsEntityRepository.findOne(example);

        return optional.isPresent()?true:false;
    }

    public boolean checkBywebSiteName(String name){
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setWebSiteName(name);
        Example<NewsEntity> example = Example.of(newsEntity);
        long Count = newsEntityRepository.count(example);
        return Count<0?false:true;
    }

    public void save(NewsEntity newsEntity){
        newsEntityRepository.save(newsEntity);
    }



}
