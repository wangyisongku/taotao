package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.service.ItemService;

@RequestMapping("item")
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value="{itemId}",method=RequestMethod.GET)
    public ModelAndView item(@PathVariable("itemId") Long itemId){
        ModelAndView mv = new ModelAndView("item");
        mv.addObject("item", this.itemService.getItemById(itemId));
        mv.addObject("itemDesc", this.itemService.getItemDescByItemId(itemId));
        mv.addObject("itemParam", this.itemService.getItemParamHtml(itemId));
        return mv;
    }
    
    
}
