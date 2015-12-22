package com.acfun.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jack on 15/7/1.
 */
@Service
@Slf4j
public class VideoService {

  @Value("${video.0}")
  @NonNull
  private String videoName;
  @Value("${colors}")
  @NonNull
  private String[] colors;
  @Value("${expressions}")
  @NonNull
  private String[] expressions;

  private boolean isBlockEnable = true;//屏蔽词功能是否开启
  private boolean isDelayEnable = true;//延时功能是否开启
  private int delaySecond = 5;
  private int playerStatus = 2;//0 关闭 1 显示视频 2 显示弹幕

  public String getVideoName() {
    return videoName;
  }

  public boolean isBlockEnable() {
    return isBlockEnable;
  }

  public void setIsBlockEnable(boolean isBlockEnable) {
    this.isBlockEnable = isBlockEnable;
  }

  public boolean isDelayEnable() {
    return isDelayEnable;
  }

  public void setIsDelayEnable(boolean isDelayEnable) {
    this.isDelayEnable = isDelayEnable;
  }

  public int getDelaySecond() {
    return delaySecond;
  }

  public void setDelaySecond(int delaySecond) {
    this.delaySecond = delaySecond;
  }

  public int getPlayerStatus() {
    return playerStatus;
  }

  public void setPlayerStatus(int playerStatus) {
    this.playerStatus = playerStatus;
  }

  public String[] getColors() {
    return colors;
  }

  public String[] getExpressions() {
    return expressions;
  }
}
