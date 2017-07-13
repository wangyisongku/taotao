package com.taotao.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.bean.ItemCatResult;
import com.taotao.common.service.RedisServer;
import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;

@RequestMapping("item/cat")
@Controller
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;
    
    @Autowired
    private RedisServer redisServer;

    // http://localhost:8081/rest/item/cat
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ItemCat>> queryItemListAll(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            List<ItemCat> list = this.itemCatService.queryListByWhere(itemCat);
            //200
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 首页左侧菜单
     * @return
     */
    //http://manage.taotao.com/item/cat/web/all
    @RequestMapping(value="web/all",method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ItemCatResult> queryAllToTree(){
       
        
        try {
            ItemCatResult itemCatResult = this.itemCatService.queryAllToTree();
           
            return ResponseEntity.ok(itemCatResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     
    }
}
