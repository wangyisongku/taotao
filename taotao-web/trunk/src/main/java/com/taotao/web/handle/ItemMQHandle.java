package com.taotao.web.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.service.RedisServer;
import com.taotao.web.service.ItemService;

/**
 * 处理消息，删除redis缓存数据
 * 
 * @author yangbingwen
 *
 */
@Component
public class ItemMQHandle {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RedisServer redisServer;

    /**
     * 接收消息，删除redis
     */
    public void execute(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            this.redisServer.del(ItemService.REDIS_ITEM + jsonNode.get("itemId").asLong());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
