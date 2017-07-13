package com.taotao.manage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.mapper.ItemDescMapper;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.mapper.ItemParamItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemService extends BaseService<Item>{
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Autowired
    private ItemDescMapper itemDescMapper;
    
    @Autowired
    private ItemParamItemMapper itemParamItemMapper;

    @Autowired
    private RabbitTemplate template;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    

    /**
     * 新增商品
     * @param item
     * @param desc
     */
    public void saveItem(Item item, String desc,String itemParams) {
        //事务commit rollback
        item.setId(null);
        item.setStatus(1);
        item.setCreated(new Date());
        item.setUpdated(item.getCreated());
        this.itemMapper.insertSelective(item);
        
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(itemDesc.getCreated());
        itemDesc.setItemId(item.getId());
        this.itemDescMapper.insertSelective(itemDesc);
        
        //保存商品规格参数
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setId(null);
        itemParamItem.setParamData(itemParams);
        itemParamItem.setCreated(new Date());
        itemParamItem.setUpdated(itemParamItem.getCreated());
        itemParamItem.setItemId(item.getId());
        
        this.itemParamItemMapper.insertSelective(itemParamItem);
        //发送新添商品消息
        sendMQ("insert", item.getId());
        
    }


    /**
     * 显示商品列表功能
     */
    public  PageInfo<Item> queryItemList(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Item.class);
        example.setOrderByClause("updated DESC");
        List<Item> list = this.itemMapper.selectByExample(example);
        
        PageInfo<Item> pageInfo = new PageInfo<Item>(list);
        return pageInfo;
    }

    /**
     * 更新商品
     * @param item
     * @param desc
     */
    public void updateItem(Item item, String desc) {
        item.setUpdated(new Date());
        item.setCreated(null);
        this.itemMapper.updateByPrimaryKeySelective(item);
        
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setUpdated(new Date());
        itemDesc.setCreated(null);
        itemDesc.setItemDesc(desc);
        this.itemDescMapper.updateByPrimaryKeySelective(itemDesc);
        //更新商品发送消息
        sendMQ("update", item.getId());
    }


    /**
     * 根据itemId商品详情
     * @param itemId
     * @return
     */
    public Item queryItemByItemId(Long itemId) {
        return this.itemMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 根据类型发送相应的消息
     * @param type
     * @param itemId
     */
    private void sendMQ(String type,Long itemId){
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", type);
            map.put("itemId", itemId);
            this.template.convertAndSend("item."+type,MAPPER.writeValueAsString(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
