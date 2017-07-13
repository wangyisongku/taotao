package com.taotao.web.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.Item;

@Service
public class SearchService {

    @Value("${TAOTAO_SEARCH_URL}")
    private String TAOTAO_SEARCH_URL;
    
    public static final Integer ROWS=36;
    
    @Autowired
    private ApiService apiService;
    
    public EasyUIResult search(String query, Integer page) {
        String url = TAOTAO_SEARCH_URL+"/item/search/";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("keyWords", query);
        param.put("page", page);
        param.put("rows", ROWS);
        try {
            String jsondata = this.apiService.doGet(url, param);
            return EasyUIResult.formatToList(jsondata, Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
