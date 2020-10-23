package com.news.readerservice.dao;

import com.news.readerservice.model.NewsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsEntityRepository extends MongoRepository<NewsEntity, String> {
}
