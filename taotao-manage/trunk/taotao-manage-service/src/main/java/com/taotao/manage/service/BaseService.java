package com.taotao.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public abstract class BaseService<T extends BasePojo> {
    
    @Autowired
    private Mapper<T> mapper;
    
    //根据id查询实体
    T queryById(Long id){
        return this.mapper.selectByPrimaryKey(id);
    }
    
    //查询所有
    public List<T> queryAll(){
        return this.mapper.select(null);
    }
    
    //条件查询
   public List<T> queryListByWhere(T param){
        return this.mapper.select(param);
   }
   
   //查询记录数
   public Integer queryCount(T param){
       return this.mapper.selectCount(param);
   }
   
   //分页
   public PageInfo<T> queryPageListByWhere(T param ,Integer page,Integer rows){
       PageHelper.startPage(page, rows);
       List<T> list = this.queryListByWhere(param);
       return  new PageInfo<T>(list);
   }
   
   //查询一条记录
   public T queryOne(T param){
       return this.mapper.selectOne(param);
   }
   
   //插入
   public Integer save(T param){
       if (param.getCreated() == null) {
           param.setCreated(new Date());
           param.setUpdated(param.getCreated());
       } else if (param.getUpdated() == null) {
           param.setUpdated(param.getCreated());
       }
       return this.mapper.insert(param);
   }
   //新增非空字段
   public Integer saveSelective(T t){
       if (t.getCreated() == null) {
           t.setCreated(new Date());
           t.setUpdated(t.getCreated());
       } else if (t.getUpdated() == null) {
           t.setUpdated(t.getCreated());
       }
       return this.mapper.insertSelective(t);
   }
   
   //根据主键更新
   public Integer update(T param){
       param.setUpdated(new Date());
       return this.mapper.updateByPrimaryKey(param);
   }
   //根据主键更新非空内容
   public Integer updateSelective(T param){
       param.setUpdated(new Date());
       return this.mapper.updateByPrimaryKeySelective(param);
   }
   

   //根据主键删除
   public Integer deleteById(Long id){
       return this.mapper.deleteByPrimaryKey(id);
   }
   
   //批量删除
   public Integer deleteByIds(Class<T> clazz,List<Object> values){
       Example example = new Example(clazz);
       example.createCriteria().andIn("id", values);
       return this.mapper.deleteByExample(example);
   }
}
