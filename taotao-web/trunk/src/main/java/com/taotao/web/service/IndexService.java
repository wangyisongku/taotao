package com.taotao.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisServer;
import com.taotao.web.pojo.Content;

@Service
public class IndexService {
    
    private static final ObjectMapper MAPPER= new ObjectMapper();
    private static final String REDIS_INDEXAD1 = "TAOTAO_WEB_INDEX_AD1";
    private static final Integer REDIS_TIME = 60*60*24;

    @Autowired
    private ApiService apiService;
    
    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;
    
    @Value("${INDEX_AD1_URL}")
    private String INDEX_AD1_URL;
    
    @Autowired
    private RedisServer redisServer;
    
    /**
     * 获取大广告位数据
     * @return
     */
    public String getIndexAd1() {
        
        try {
            String data = this.redisServer.get(REDIS_INDEXAD1);
            if (StringUtils.isNoneBlank(data)) {
                return data;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        String url=TAOTAO_MANAGE_URL+INDEX_AD1_URL;
        try {
            String jsonData = this.apiService.doGet(url);
            EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData,Content.class);
            if (easyUIResult!=null) {
                List<Content> contents = (List<Content>) easyUIResult.getRows();
                
                List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
                
                for (Content content : contents) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("srcB", content.getPic());
                    map.put("height", 240);
                    map.put("alt", content.getTitle());
                    map.put("width", 670);
                    map.put("src", content.getPic());
                    map.put("widthB", 550);
                    map.put("href", content.getUrl());
                    map.put("heightB", 240);
                    result.add(map);
                }
                String jsonResult = MAPPER.writeValueAsString(result);
                try {
                   
                    this.redisServer.set(REDIS_INDEXAD1, jsonResult, REDIS_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return jsonResult; 
            }
            
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
