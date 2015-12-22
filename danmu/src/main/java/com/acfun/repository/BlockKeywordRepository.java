package com.acfun.repository;

import com.acfun.model.BlockKeywordModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jack on 15/7/3.
 */
public interface BlockKeywordRepository extends MongoRepository<BlockKeywordModel, String> {

  BlockKeywordModel findByWord(String word);

}
