package com.taotao.manage.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Content;
import com.taotao.manage.service.ContentService;

@RequestMapping("content")
@Controller
public class ContentController {
    
    @Autowired
    private ContentService contentService;

    /**
     * 列表
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    // http://manage.taotao.com/rest/content?categoryId=65&page=1&rows=20
    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<EasyUIResult> queryContentList(@RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "20") Integer rows) {
        try {
            PageInfo<Content> pageInfo =   this.contentService.queryContentList(categoryId,page,rows);
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 新添内容
     */
    //http://manage.taotao.com/rest/content
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> saveContent(Content content){
        try {
            content.setId(null);
            content.setCreated(new Date());
            content.setUpdated(content.getCreated());
            this.contentService.saveSelective(content);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
}
