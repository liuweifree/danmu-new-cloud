package com.acfun.controller;

import com.acfun.service.VideoService;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by jack on 15/7/8.
 */
@Controller
@ConfigurationProperties(prefix = "flash")
public class DanmuController {

  private double widthPercent = 1.5;
  private double heightPercent = 1.5;
  private double maxSize = 50;
  private double expressionScale = 1;
  private double speed1 = 0.3;
  private double speed2 = 0.3;
  private double offY = 50;

  @Autowired
  private VideoService videoService;

  @RequestMapping("/admin")
  public String index(Model model) {
    return "admin/index";
  }

  @RequestMapping(value = "/sendDM", method = RequestMethod.GET)
  public String send(Model model) {
    model.addAttribute("expressions", videoService.getExpressions());
    model.addAttribute("colors", videoService.getColors());
    return "sendDM";
  }

  @RequestMapping(value = "/play/{index}", method = RequestMethod.GET)
  public String play(@PathVariable String index, String show, String speed1, String speed2, String offY,
                     String expressionScale, String maxSize, Model model) {
    model.addAttribute("widthPercent", widthPercent);
    model.addAttribute("heightPercent", heightPercent);

    model.addAttribute("expressionScale", StringUtils.isEmpty(expressionScale) ? this.expressionScale : expressionScale);
    model.addAttribute("maxSize", StringUtils.isEmpty(maxSize) ? this.maxSize : maxSize);
    model.addAttribute("index", StringUtils.isEmpty(index) ? 0 : index);
    model.addAttribute("show", StringUtils.isEmpty(show) ? "l" : show);
    model.addAttribute("speed1", StringUtils.isEmpty(speed1) ? this.speed1 : speed1);
    model.addAttribute("speed2", StringUtils.isEmpty(speed2) ? this.speed2 : speed2);
    model.addAttribute("offY", StringUtils.isEmpty(offY) ? this.offY : offY);
    return "play";
  }

  public double getWidthPercent() {
    return widthPercent;
  }

  public void setWidthPercent(double widthPercent) {
    this.widthPercent = widthPercent;
  }

  public double getHeightPercent() {
    return heightPercent;
  }

  public void setHeightPercent(double heightPercent) {
    this.heightPercent = heightPercent;
  }

  public double getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(double maxSize) {
    this.maxSize = maxSize;
  }

  public double getExpressionScale() {
    return expressionScale;
  }

  public void setExpressionScale(double expressionScale) {
    this.expressionScale = expressionScale;
  }

  public double getSpeed1() {
    return speed1;
  }

  public void setSpeed1(double speed1) {
    this.speed1 = speed1;
  }

  public double getSpeed2() {
    return speed2;
  }

  public void setSpeed2(double speed2) {
    this.speed2 = speed2;
  }

  public double getOffY() {
    return offY;
  }

  public void setOffY(double offY) {
    this.offY = offY;
  }

  public VideoService getVideoService() {
    return videoService;
  }

  public void setVideoService(VideoService videoService) {
    this.videoService = videoService;
  }
}
