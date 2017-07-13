package com.taotao.manage.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.manage.mapper.ContentCategoryMapper;
import com.taotao.manage.pojo.ContentCategory;

@Service
public class ContentCategoryService extends BaseService<ContentCategory>{
    @Autowired
    private ContentCategoryMapper contentCategoryMapper;

    /**
     * 新添分类
     */
    public ContentCategory saveContentCategory(Long parentId, String name) {
        ContentCategory contentCategory = new ContentCategory();
        contentCategory.setId(null);
        contentCategory.setParentId(parentId);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(contentCategory.getCreated());
        contentCategory.setIsParent(false);
        contentCategory.setStatus(1);
        contentCategory.setSortOrder(1);
        contentCategory.setName(name);
        
        this.contentCategoryMapper.insertSelective(contentCategory);
        
        //判断父节点是否为true,如果不是，需要修改为true
        ContentCategory parent = this.contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()) {
            parent.setIsParent(true);
            parent.setUpdated(new Date());
            this.contentCategoryMapper.updateByPrimaryKeySelective(parent);
        }
        
        return contentCategory;
    }

    /**
     * 更新分类
     * @return
     */
    public void updateContentCategory(Long id, String name) {
        ContentCategory contentCategory = new ContentCategory();
        contentCategory.setId(id);
        contentCategory.setUpdated(new Date());
        contentCategory.setName(name);
        this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        
    }

    
    
}
