package com.acfun;


import com.acfun.service.BlockKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jack on 15/5/12.
 */

@ComponentScan("com.acfun")
@SpringBootApplication
@Slf4j
public class Application extends SpringBootServletInitializer {

  @Autowired
  private BlockKeywordService blockKeywordService;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  //  @PostConstruct
  void importBlockKeyword() throws IOException {
    log.info("屏蔽词导入");
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/jack/Downloads/弹幕屏蔽词20150629.txt")));
    String s;
    int num = 0;
    while ((s = br.readLine()) != null) {
      if (!StringUtils.isEmpty(s)) {
        blockKeywordService.save(s);
        num++;
      }
    }
    log.info("屏蔽词导入完成,num:{}", num);
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }
}
