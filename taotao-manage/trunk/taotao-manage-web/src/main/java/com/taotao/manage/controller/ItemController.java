package com.taotao.manage.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemParam;
import com.taotao.manage.service.ItemService;

@RequestMapping("item")
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ApiService apiService;
    
    // 定义日志输出常量
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    /**
     * 新增商品
     * 
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> saveItem(Item item, @RequestParam("desc") String desc, ItemParam itemParam,
            @RequestParam("itemParams") String itemParams) {

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("执行新增商品操作！，item={} , itemDesc={} ", item, desc);
            }
            this.itemService.saveItem(item, desc, itemParams);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("新增商品操作成功！，id = {} ", item.getId());
            }
            // 201
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            LOGGER.error("新增商品失败！ item=" + item, e);
            e.printStackTrace();
        }
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    /**
     * 显示商品列表功能
     */
    // http://manage.taotao.com/rest/item?page=1&rows=30
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EasyUIResult> queryItemList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows) {
        try {
            PageInfo<Item> pageInfo = this.itemService.queryItemList(page, rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 更新商品
     * 
     * @param item
     * @param desc
     */
    // http://manage.taotao.com/rest/item
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Void> updateItem(Item item, @RequestParam("desc") String desc) {
       
        try {
            this.itemService.updateItem(item, desc);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }
    
    @RequestMapping(value="{itemId}",method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Item> queryItemByItemId(@PathVariable("itemId") Long itemId){
        try {
            Item item =  this.itemService.queryItemByItemId(itemId);
            if (item == null) {
                //404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            //200
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
