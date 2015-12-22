package com.acfun.repository;

import com.acfun.model.DanmuModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by jack on 15/6/29.
 */
public interface DanmuRepository extends MongoRepository<DanmuModel, String> {

  List<DanmuModel> findByTest(int test);
}
