package com.acfun.service;

import com.acfun.model.DanmuModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by liuwei on 15/12/3.
 */
@Service
@Slf4j
public class DanmuThreadService {

    private static BlockingQueue<DanmuModel> sendDanmuQueue = new LinkedBlockingQueue<>(10000);//弹幕发送队列
    private static BlockingQueue<String> sendExpressionQueue = new LinkedBlockingQueue<>(10000);//表情发送队列
    private boolean testIsOpen = false;

    @Autowired
    private SessionService sessionService;

    private static final String TYPE_DANMU_VISITOR = "danmu_visitor";


    public static void putDanmuQueue(DanmuModel danmuModel){
        try {
            sendDanmuQueue.put(danmuModel);
        } catch (InterruptedException e) {
            log.error("弹幕添加到队列出错，{}", e + "");
        }
    }

    public static void putExpressionQueue(String expression){
        log.debug("putExpressionQueue:{}",expression);
        try{
            sendExpressionQueue.put(expression);
        } catch (InterruptedException e) {
            log.error("表情添加到队列出错，{}", e + "");
        }
    }

    public static void queueClear(){
        sendDanmuQueue.clear();
        sendExpressionQueue.clear();
    }

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
                    log.info("expression,create:{}", expression);
                    log.debug("sendExpressionQueue:{}",sendExpressionQueue.size());
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
}
