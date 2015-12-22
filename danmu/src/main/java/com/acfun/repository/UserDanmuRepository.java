package com.acfun.repository;

import com.acfun.model.UserDanmuModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by jack on 15/6/29.
 */
public interface UserDanmuRepository extends MongoRepository<UserDanmuModel, String> {

  List<UserDanmuModel> findByVideoIdOrderByIdDesc(String videoId);
}
