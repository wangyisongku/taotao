package com.taotao.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.User;

@Service
public class UserService {
    
    @Autowired
    private ApiService apiService;
    
    @Value("${TAOTAO_SSO_URL}")
    private String TAOTAO_SSO_URL;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 用户注册
     * @param username
     * @param password
     * @param phone
     * @return
     */
    public Boolean doRegister(String username, String password, String phone) {
        String url = TAOTAO_SSO_URL +"/user/register";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("username", username);
        param.put("password", password);
        param.put("phone", phone);
        
        try {
            HttpResult httpResult = this.apiService.doPost(url, param);
            if (httpResult.getCode() == 201) {
                //注册成功
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //其它为注册失败
        return false;
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public String doLogin(String username, String password) {
        String url =TAOTAO_SSO_URL + "/user/login";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("u", username);
        param.put("p", password);
        try {
            HttpResult httpResult = this.apiService.doPost(url, param);
            if (httpResult.getCode() == 200) {
                //成功条件之一
                String body = httpResult.getBody();
                if (body == null) {
                    //登录失败
                    return null;
                }
                //登录成功
                return body;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 根据ticket查询用户
     * @param ticket
     * @return
     */
    public User queryUserById(String ticket) {
        String url = TAOTAO_SSO_URL+"/user/"+ticket;
        try {
            String jsondata = this.apiService.doGet(url);
            if (jsondata == null) {
                //没有登录
                return null;
            }
            return MAPPER.readValue(jsondata, User.class);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    
    
}
