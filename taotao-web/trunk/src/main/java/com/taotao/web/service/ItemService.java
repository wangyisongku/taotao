package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisServer;
import com.taotao.web.pojo.Item;
import com.taotao.web.pojo.ItemDesc;
import com.taotao.web.pojo.ItemParamItem;

@Service
public class ItemService {
    @Autowired
    private ApiService apiService;
    @Autowired
    private RedisServer redisServer;
    
    public static final String REDIS_ITEM="TAOTAO_WEB_ITEM_";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;

    public Item getItemById(Long itemId) {
        String key = REDIS_ITEM+itemId;
        //命中
        try {
            String jsonstr = this.redisServer.get(key);
            if (StringUtils.equals("404", jsonstr)) {
                //非法请求 
                return null;
            }
            if (StringUtils.isNoneBlank(jsonstr)) {
                return MAPPER.readValue(jsonstr, Item.class);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String url = TAOTAO_MANAGE_URL + "/rest/item/" + itemId;
        
        try {
            String jsonData = apiService.doGet(url);
            if (null == jsonData) {
                
                this.redisServer.set(key, "404",3600);
                
                // 响应404 500
                return null;
            }
            
            this.redisServer.set(key, jsonData, 3600);
            
            return MAPPER.readValue(jsonData, Item.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 商品介绍
     * 
     * @param itemId
     * @return
     */
    public ItemDesc getItemDescByItemId(Long itemId) {
        String url = TAOTAO_MANAGE_URL + "/rest/item/desc/" + itemId;

        try {
            String jsondata = this.apiService.doGet(url);
            if (jsondata == null) {
                return null;
            }
            return MAPPER.readValue(jsondata, ItemDesc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读出规格参数并转为html代码
     * 
     * @param itemId
     * @return
     */
    public String getItemParamHtml(Long itemId) {
        
        String url = TAOTAO_MANAGE_URL + "/rest/item/param/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (null == jsonData) {
                return null;
            }
            
            ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
            String stringData = itemParamItem.getParamData();
            // 解释json
            JsonNode jsonNode = MAPPER.readTree(stringData);
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            StringBuilder sb = new StringBuilder();
            sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\"> <tbody>");
            for (JsonNode node : arrayNode) {
                String group = node.get("group").asText();
                sb.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + group + "</th></tr>");
                ArrayNode jsonNode2 = (ArrayNode) node.get("params");
                for (JsonNode node2 : jsonNode2) {
                    String k = node2.get("k").asText();
                    String v = node2.get("v").asText();
                    sb.append("<tr><td class=\"tdTitle\">"+k+"</td><td>"+v+"</td></tr>");
                }
            }
            sb.append("</tbody></table>");
            return sb.toString();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
