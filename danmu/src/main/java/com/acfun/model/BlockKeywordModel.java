package com.acfun.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by jack on 15/7/03.
 */
@Document(collection = "block_keyword")
public class BlockKeywordModel {

  @Field("_id")
  private String id;

  private String word;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }
}
