package com.taotao.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.service.ApiService;
import com.taotao.common.utils.CookieUtils;
import com.taotao.web.service.UserService;

@RequestMapping("user")
@Controller
public class UserController {
    
    public static final String TAOTAO_COOKIE = "TT_TICKET";

    @Autowired
    private UserService userService;

    /**
     * 显示注册页面
     * 
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("register");
        return mv;
    }

    /**
     * 显示登录页面
     * 
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("login");
        return mv;
    }

    /**
     * 实现注册功能
     */
    // http://www.taotao.com/service/user/doRegister
    // url : "http://www.taotao.com/user/doRegister.html",
    @RequestMapping(value = "doRegister", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> doRegister(@RequestParam("username") String username,
            @RequestParam("password") String password, @RequestParam("phone") String phone) {

        Boolean bool = this.userService.doRegister(username, password, phone);

        Map<String, Object> result = new HashMap<String, Object>(1);

        if (bool) {
            // 注册成功
            result.put("status", "200");
        } else {
            // 注册失败
            result.put("status", "500");
        }

        return result;
    }

    /**
     * 用户登录
     */
    // http://www.taotao.com/service/user/doLogin?r=0.3756389315240085
    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> doLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,HttpServletRequest request,HttpServletResponse response) {
        String ticket  = this.userService.doLogin(username,password);
        Map<String, Object> result = new HashMap<String, Object>(1);
        if (ticket == null) {
            //登录失败
            result.put("status", "500");
        }else{
           //成功
           result.put("status", "200");
           
           //会话级的Cookie,关闭浏览器就没有效了
           CookieUtils.setCookie(request, response, TAOTAO_COOKIE, ticket);
        }
        return result;
    }
}
