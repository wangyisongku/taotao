package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.service.RedisServer;
import com.taotao.web.service.ItemService;

@RequestMapping("item/cache")
@Controller
public class ItemCacheController {
    
    @Autowired
    private RedisServer redisServer;
    /**
     * 删除商品基本信息缓存
     * @return
     */
    @RequestMapping(method=RequestMethod.POST,params="itemId")
    @ResponseBody
    public ResponseEntity<Void> deleteCacheByItemId(@RequestParam("itemId") Long itemId){
        try {
            this.redisServer.del(ItemService.REDIS_ITEM+itemId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
