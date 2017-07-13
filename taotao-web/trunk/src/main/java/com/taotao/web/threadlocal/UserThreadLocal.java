package com.taotao.web.threadlocal;

import com.taotao.web.pojo.User;

public class UserThreadLocal {

    private static ThreadLocal<User> USER =new ThreadLocal<User>();
    
    /**
     * 设置用户信息
     * @param user
     */
    public static void set(User user){
        USER.set(user);
    }
    
    /**
     * 获得用户信息
     * @param user
     */
    public static User get(){
        return USER.get();
    }
}
