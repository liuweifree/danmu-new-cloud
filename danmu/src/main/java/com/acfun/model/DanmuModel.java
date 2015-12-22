package com.acfun.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by jack on 15/6/29.
 */
@Document(collection = "intranet_danmaku")
public class DanmuModel {

  @Field("_id")
  private String id;
  private String color;
  private String msg;
  private Boolean isBlocked = false;
  private Integer test = 0;//0普通弹幕；1测试弹幕
  private Date created = new Date();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Boolean getIsBlocked() {
    return isBlocked;
  }

  public void setIsBlocked(Boolean isBlocked) {
    this.isBlocked = isBlocked;
  }

  public Integer getTest() {
    return test;
  }

  public void setTest(Integer test) {
    this.test = test;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
}
