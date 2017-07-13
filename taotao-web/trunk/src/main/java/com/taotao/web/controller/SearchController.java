package com.taotao.web.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.bean.EasyUIResult;
import com.taotao.web.service.SearchService;

@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @RequestMapping(method = RequestMethod.GET, params = "q")
    public ModelAndView search(@RequestParam("q") String query,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {

        try {
            query =  new String(query.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        ModelAndView mv = new ModelAndView("search");
        
       EasyUIResult easyUIResult = this.searchService.search(query,page);
       mv.addObject("query", query);
       Integer total = easyUIResult.getTotal();
       //(总数+页面大小-1)/页面大小
       mv.addObject("totalPages", (total+SearchService.ROWS-1)/SearchService.ROWS);
       mv.addObject("itemList", easyUIResult.getRows());
       mv.addObject("page", page);
       return mv;
    }
}
