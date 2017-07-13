package com.taotao.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.mapper.ContentMapper;
import com.taotao.manage.pojo.Content;

@Service
public class ContentService extends BaseService<Content>{

    @Autowired
    private ContentMapper contentMapper;
    
    /**
     * 列表
     * @param categoryId
     * @param page
     * @param rows
     * @return
     */
    public PageInfo<Content> queryContentList(Long categoryId, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Content.class);
        List<Object> list = new ArrayList<Object>();
        list.add(categoryId);
        example.createCriteria().andIn("categoryId", list);
        example.setOrderByClause("updated DESC");
        List<Content> selectByExample = this.contentMapper.selectByExample(example);
        PageInfo<Content> pageInfo = new PageInfo<Content>(selectByExample);
        return pageInfo;
    }

}
