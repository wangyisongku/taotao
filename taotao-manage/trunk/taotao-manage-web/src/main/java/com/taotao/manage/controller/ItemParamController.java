package com.taotao.manage.controller;

import java.util.Date;

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
import com.taotao.manage.pojo.ItemParam;
import com.taotao.manage.service.ItemParamService;

@RequestMapping("item/param")
@Controller
public class ItemParamController {

    @Autowired
    private ItemParamService itemParamService;

    // http://manage.taotao.com/rest/item/param/497
    /**
     * 根据itemCatId查询模板
     * 
     * @param itemCatId
     * @return
     */
    @RequestMapping(value = "{itemCatId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ItemParam> queryItemParamByItemCatId(@PathVariable("itemCatId") Long itemCatId) {
        try {
            ItemParam itemParam = new ItemParam();
            itemParam.setItemCatId(itemCatId);
            return ResponseEntity.ok(this.itemParamService.queryOne(itemParam));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    // http://manage.taotao.com/rest/item/param/3
    @RequestMapping(value = "{itemCatId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> saveItemParam(ItemParam itemParam, @PathVariable("itemCatId") Long itemCatId) {
        try {
            itemParam.setCreated(new Date());
            itemParam.setUpdated(itemParam.getCreated());
            itemParam.setId(null);
            itemParam.setItemCatId(itemCatId);
            this.itemParamService.saveSelective(itemParam);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 显示规格参数列表
     */
    // http://manage.taotao.com/rest/item/param/?page=1&rows=30
    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EasyUIResult> queryItemParamByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows) {

        try {
            PageInfo<ItemParam> pageInfo =  this.itemParamService.queryItemParamByPage(page,rows);
            return ResponseEntity.status(HttpStatus.OK).body(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
