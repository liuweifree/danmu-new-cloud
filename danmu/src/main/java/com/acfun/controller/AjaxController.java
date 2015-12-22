package com.acfun.controller;

import com.acfun.model.RestResultModel;
import com.acfun.service.BlockKeywordService;
import com.acfun.service.DanmuService;
import com.acfun.service.VideoService;
import com.acfun.utils.MyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jack on 15/6/26.
 */
@RestController
@RequestMapping(value = "/v1/api")
@Slf4j
public class AjaxController {

  @Autowired
  private BlockKeywordService blockKeywordService;
  @Autowired
  private StringRedisTemplate stringRedisTemplate;
  @Autowired
  private DanmuService danmuService;
  @Autowired
  private VideoService videoService;

  private boolean checkFrequency(HttpServletRequest request) {
    String key = "danmuNew-last-send-time-user-" + MyUtils.getIp(request);
    ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    boolean has = !StringUtils.isEmpty(valueOperations.get(key));
    valueOperations.set(key, "1");
    stringRedisTemplate.expire(key, 2, TimeUnit.SECONDS);
    return has;
  }

  @RequestMapping(value = "/danmu", method = RequestMethod.POST)
  public RestResultModel sendDanmu(String msg, String color, HttpServletRequest request) {
    RestResultModel restResultModel = new RestResultModel();
    msg = danmuService.filterEmoji(msg);
    if (StringUtils.isEmpty(msg) || msg.length() > DanmuService.MAX_CHAR_LEN || StringUtils.isEmpty(color)) {
      restResultModel.setResult(402);
      restResultModel.setResult_msg("msg is empty or too long or color is empty");
    } else if (videoService.isBlockEnable() && blockKeywordService.check(msg)) {
      restResultModel.setResult(403);
      restResultModel.setResult_msg("msg is blocked");
    } else if (checkFrequency(request)) {
      restResultModel.setResult(400);
      restResultModel.setResult_msg("Limited Frequency");
    } else if (danmuService.add(msg, color)) {
      restResultModel.setResult(200);
      restResultModel.setResult_msg("ok");
    } else {
      restResultModel.setResult(500);
      restResultModel.setResult_msg("server err");
    }
    return restResultModel;
  }

  @RequestMapping(value = "/expression", method = RequestMethod.POST)
  public RestResultModel sendExpression(String expression, HttpServletRequest request) {
    RestResultModel restResultModel = new RestResultModel();
    if (StringUtils.isEmpty(expression)) {
      restResultModel.setResult(402);
      restResultModel.setResult_msg("expression is empty");
    } else {
      danmuService.sendExpression(expression);
      restResultModel.setResult(200);
      restResultModel.setResult_msg("ok");
    }
    return restResultModel;
  }

  @RequestMapping(value = "/getPrincipal")
  public Map<String, Object> getPrincipal() {
    Map<String, Object> result = new HashMap<>();
    result.put("principal", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    return result;
  }

  @RequestMapping(value = "/admin/blockKeywords", method = RequestMethod.GET)
  public RestResultModel getAdminBlockKeywords() {
    RestResultModel restResultModel = new RestResultModel();
    restResultModel.setResult(200);
    restResultModel.setResult_msg("ok");
    restResultModel.setData(blockKeywordService.findAll());
    return restResultModel;
  }

  @RequestMapping(value = "/admin/blockKeywords/{id}", method = RequestMethod.DELETE)
  public RestResultModel delAdminBlockKeyword(@PathVariable String id) {
    RestResultModel restResultModel = new RestResultModel();
    if (StringUtils.isEmpty(id)) {
      restResultModel.setResult(401);
      restResultModel.setResult_msg("id is empty");
    } else {
      try {
        blockKeywordService.delete(id);
        restResultModel.setResult(200);
        restResultModel.setResult_msg("ok");
      } catch (Exception e) {
        log.error("", e);
        restResultModel.setResult(402);
        restResultModel.setResult_msg("error");
      }
    }
    return restResultModel;
  }

  @RequestMapping(value = "/admin/blockKeywords", method = RequestMethod.POST)
  public RestResultModel addAdminBlockKeyword(@RequestParam String word) {
    RestResultModel restResultModel = new RestResultModel();
    if (StringUtils.isEmpty(word)) {
      restResultModel.setResult(401);
      restResultModel.setResult_msg("id is empty");
    } else {
      try {
        if (blockKeywordService.findByWord(word) == null) {
          blockKeywordService.save(word);
          restResultModel.setResult(200);
          restResultModel.setResult_msg("ok");
        } else {
          restResultModel.setResult(401);
          restResultModel.setResult_msg("fail");
        }
      } catch (Exception e) {
        log.error("", e);
        restResultModel.setResult(402);
        restResultModel.setResult_msg("error");
      }
    }
    return restResultModel;
  }


}
