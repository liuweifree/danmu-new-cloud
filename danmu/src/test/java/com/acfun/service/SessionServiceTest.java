package com.acfun.service;

import com.acfun.model.DanmuModel;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by jack on 15/7/28.
 */
@Slf4j
public class SessionServiceTest {


  @Test
  public void testName() throws Exception {

    ObjectMapper mapper = new ObjectMapper();

    DanmuModel danmuModel = new DanmuModel();
    danmuModel.setMsg("hah哈哈");

    JsonGenerator g= mapper.getFactory().createGenerator(System.out);
    g.writeObject(danmuModel);

    log.info(JSON.toJSONString(LocalDateTime.now()));
    log.info(JSON.toJSONStringWithDateFormat(LocalDateTime.now(), "yyyy-MM-dd"));

  }
}