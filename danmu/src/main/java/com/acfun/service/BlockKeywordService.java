package com.acfun.service;

import com.acfun.model.BlockKeywordModel;
import com.acfun.repository.BlockKeywordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by jack on 15/7/8.
 */
@Service
@Slf4j
public class BlockKeywordService {
  @Autowired
  private BlockKeywordRepository blockKeywordRepository;

  private List<BlockKeywordModel> list;

  @PostConstruct
  void refreshCache() {
    list = blockKeywordRepository.findAll();
  }

  public List<BlockKeywordModel> findAll() {
    return list;
  }

  public void delete(String id) {
    blockKeywordRepository.delete(id);
    refreshCache();
  }

  public BlockKeywordModel findByWord(String word) {
    if (StringUtils.isEmpty(word)) {
      return null;
    }
    return blockKeywordRepository.findByWord(word);
  }

  public void save(String word) {
    if (StringUtils.isEmpty(word)) {
      return;
    }
    BlockKeywordModel blockKeywordModel = new BlockKeywordModel();
    blockKeywordModel.setWord(word.trim());
    blockKeywordRepository.save(blockKeywordModel);
    refreshCache();
  }

  public boolean check(String msg) {
    if (StringUtils.isEmpty(msg)) {
      return false;
    }
    for (BlockKeywordModel blockKeywordModel : list) {
      if (msg.toLowerCase().contains(blockKeywordModel.getWord().toLowerCase())) {
        return true;
      }
    }
    return false;
  }

}
