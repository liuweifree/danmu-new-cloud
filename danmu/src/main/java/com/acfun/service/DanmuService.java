package com.acfun.service;

import com.acfun.model.DanmuModel;
import com.acfun.repository.DanmuRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jack on 15/7/17.
 */
@Service
@EnableScheduling
@Slf4j
public class DanmuService {

  private static final String TYPE_DANMU_VISITOR = "danmu_visitor";
  public static final int MAX_CHAR_LEN = 40;

  @Autowired
  private SessionService sessionService;
  @Autowired
  private DanmuRepository danmuRepository;
  @Autowired
  private VideoService videoService;

  @Autowired
  private DanmuThreadService danmuThreadService;

  @Value("${danmu.user.save.enabled}")
  private boolean danmuUserIsSave = true;
  private boolean testIsOpen = false;

  private BlockingQueue<DanmuModel> receiveDanmuQueue = new LinkedBlockingQueue<>(10000);//弹幕接收队列
  //private BlockingQueue<DanmuModel> sendDanmuQueue = new LinkedBlockingQueue<>(10000);//弹幕发送队列
  //private BlockingQueue<String> sendExpressionQueue = new LinkedBlockingQueue<>(10000);//表情发送队列
  private DanmuModel currentDanmuModel;


  public boolean add(String msg, String color) {
    return add(msg, color, true);
  }

  public boolean add(String msg, String color, boolean isSave) {
    DanmuModel danmuModel = new DanmuModel();
    danmuModel.setId(UUID.randomUUID().toString());
    danmuModel.setMsg(msg);
    danmuModel.setColor(color);
    danmuModel.setTest(isSave ? 0 : 1);

    if (videoService.isDelayEnable()) {
      sessionService.sendAdminMsg("danmuAdd", danmuModel);
    }

    //保存数据库
    if (isSave && danmuUserIsSave) {
      danmuRepository.save(danmuModel);
    }

    try {
      //不需要保存的弹幕为测试弹幕
      if (testIsOpen != isSave) {
        receiveDanmuQueue.put(danmuModel);
      }
      return true;
    } catch (InterruptedException e) {
      log.error("弹幕添加到队列出错，{}", e + "");
      return false;
    }
  }

  public boolean sendExpression(String expression) {

      danmuThreadService.putExpressionQueue(expression);

      return true;

  }
/**
  @PostConstruct
  void initSendThread() {
    new Thread(() -> {
      while (true) {
        try {
          DanmuModel danmuModel = sendDanmuQueue.take();
          log.info("danmu,create:{},send:{}", danmuModel.getCreated(), LocalDateTime.now());
          danmuModel.setColor((danmuModel.getColor() + "").replace("#", "0x"));
          //发送到播放器
          boolean isTest = danmuModel.getTest() == 1 ? true : false;
          if (isTest == testIsOpen) {
            sessionService.sendFirstPlayer(TYPE_DANMU_VISITOR, danmuModel);
          }
        } catch (InterruptedException e) {
          log.error("发送弹幕队列读取被打断", e);
        }
      }
    }).start();

    new Thread(() -> {
      Random random = new Random();
      while (true) {
        try {
          String expression = sendExpressionQueue.take();
          //发送到播放器
          if (!testIsOpen) {
            sessionService.sendFirstPlayer("expression", expression);
          }
          Thread.sleep(1500 + random.nextInt(3000));
        } catch (InterruptedException e) {
          log.error("发送表情队列读取被打断", e);
        }
      }
    }).start();
  }
**/
  @Scheduled(initialDelay = 0, fixedDelay = 10)
  public void initThread() {
    while (true) {
      try {
        if (currentDanmuModel == null) {
          currentDanmuModel = receiveDanmuQueue.take();
        }
        if (currentDanmuModel != null
            && (!videoService.isDelayEnable() ||
            (currentDanmuModel.getCreated() != null
                && currentDanmuModel.getCreated().getTime() + videoService.getDelaySecond() * 1000 <= new Date().getTime()))) {
          //如果弹幕没屏蔽，添加到发送队列
          if (!currentDanmuModel.getIsBlocked()) {
            //sendDanmuQueue.put(currentDanmuModel);
            danmuThreadService.putDanmuQueue(currentDanmuModel);
            if (videoService.isDelayEnable()) {
              sessionService.sendAdminMsg("danmuSent", currentDanmuModel.getId());
            }
          } else {
            log.info("该弹幕已经屏蔽不发送，{}", JSON.toJSONString(currentDanmuModel));
          }
          currentDanmuModel = null;
        } else {
          break;
        }
      } catch (InterruptedException e) {
        log.error("接收弹幕队列读取被打断", e);
        break;
      }
    }
  }

  private synchronized boolean setDanmuBlockStatus(String danmuId, boolean isBlocked) {
    if (currentDanmuModel != null && currentDanmuModel.getId().equals(danmuId)) {
      currentDanmuModel.setIsBlocked(isBlocked);
      return true;
    }
    Iterator<DanmuModel> iterator = receiveDanmuQueue.iterator();
    while (iterator.hasNext()) {
      DanmuModel danmuModel = iterator.next();
      if (danmuModel.getId().equals(danmuId)) {
        danmuModel.setIsBlocked(isBlocked);
        return true;
      }
    }
    return false;
  }

  public boolean addBlockDanmuId(String danmuId) {
    return setDanmuBlockStatus(danmuId, true);
  }

  public boolean removeBlockDanmuId(String danmuId) {
    return setDanmuBlockStatus(danmuId, false);
  }

  public List<DanmuModel> getDanmuList() {
    List<DanmuModel> danmuModelList = new ArrayList<>();
    if (currentDanmuModel != null) {
      danmuModelList.add(currentDanmuModel);
    }
    danmuModelList.addAll(receiveDanmuQueue);
    return danmuModelList;
  }

  public String filterEmoji(String s) {
    if (StringUtils.isEmpty(s)) {
      return s;
    }
    return s.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "").replaceAll("\\s+", " ").trim();
  }

  public boolean isTestIsOpen() {
    return testIsOpen;
  }

  public void setTestIsOpen(boolean testIsOpen) {
    this.testIsOpen = testIsOpen;
    currentDanmuModel = null;
    danmuThreadService.queueClear();
    //sendDanmuQueue.clear();
    //sendExpressionQueue.clear();
  }

}
