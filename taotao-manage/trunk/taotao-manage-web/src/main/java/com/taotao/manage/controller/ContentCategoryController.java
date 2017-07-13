package com.taotao.manage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.manage.pojo.ContentCategory;
import com.taotao.manage.service.ContentCategoryService;

@RequestMapping("content/category")
@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 根据parentId查询分类
     * 
     * @param parentId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ContentCategory>> queryAllContentCategory(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            ContentCategory contentCategory = new ContentCategory();
            contentCategory.setParentId(parentId);
            List<ContentCategory> list = this.contentCategoryService.queryListByWhere(contentCategory);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 新添分类
     */
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ContentCategory> saveContentCategory(@RequestParam("parentId") Long parentId,
            @RequestParam("name") String name) {
        try {
            ContentCategory contentCategory = this.contentCategoryService.saveContentCategory(parentId,name);
            return ResponseEntity.status(HttpStatus.CREATED).body(contentCategory);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
    }

    /**
     * 更新分类
     * @return
     */
    @RequestMapping(method=RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Void> updateContentCategory(@RequestParam("id") Long id,
            @RequestParam("name") String name){
        try {
            this.contentCategoryService.updateContentCategory(id,name);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * 批量删除
     */
    @RequestMapping(method=RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteContentCategory(ContentCategory contentCategory){
        List<Object> deleteids= new ArrayList<Object>();
        deleteids.add(contentCategory.getId());
        //递归所有的子节点
        findAllSubNode(deleteids,contentCategory.getId());
        
        //批量删除
        this.contentCategoryService.deleteByIds(ContentCategory.class, deleteids);
        
        //需要判断该节点的父节点是否有子节点，没有isParent是否为false
        ContentCategory param = new ContentCategory();
        param.setParentId(contentCategory.getParentId());
        List<ContentCategory> queryListByWhere = this.contentCategoryService.queryListByWhere(param);
        
        if (queryListByWhere.isEmpty()) {
            ContentCategory contentCategory2 = new ContentCategory();
            contentCategory2.setId(contentCategory.getParentId());
            contentCategory2.setIsParent(false);
            contentCategory2.setUpdated(new Date());
            this.contentCategoryService.updateSelective(contentCategory2);
        }
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //递归所有的子节点
    private void findAllSubNode(List<Object> deleteids, Long parentId) {
        ContentCategory param = new ContentCategory();
        param.setParentId(parentId);
        List<ContentCategory> contentCategorys = this.contentCategoryService.queryListByWhere(param);
        for (ContentCategory contentCategory : contentCategorys) {
            deleteids.add(contentCategory.getId());
            //递归子节点
            if (contentCategory.getIsParent()) {
                findAllSubNode(deleteids, contentCategory.getId());
            }
        }
        
    }
}
