package com.acfun.model;

/**
 * Created by jack on 15/6/30.
 */
public class WsResultModel {

  private String type;
  private Object data;

  public WsResultModel() {
  }

  public WsResultModel(String type) {
    this.type = type;
  }

  public WsResultModel(String type, Object data) {
    this.type = type;
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

}
