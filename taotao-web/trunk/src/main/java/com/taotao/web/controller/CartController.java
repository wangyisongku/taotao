package com.taotao.web.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.taotao.web.pojo.Cart;
import com.taotao.web.pojo.User;
import com.taotao.web.service.CartCookieService;
import com.taotao.web.service.CartService;
import com.taotao.web.service.UserService;
import com.taotao.web.threadlocal.UserThreadLocal;

@RequestMapping("cart")
@Controller
public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartCookieService cartCookieService;

    /**
     * 添加商品到购物车
     * 
     * @param itemId
     * @param ticket
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId,
            @CookieValue(value = UserController.TAOTAO_COOKIE, required = false) String ticket,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录
            try {
                this.cartCookieService.addItemToCart(itemId, request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // 登录
            this.cartService.addItemToCart(itemId, user);
        }
        // 跳转显示页面
        return "redirect:/cart/show.html";
    }

    /**
     * 显示购物车商品数据
     */
    @RequestMapping(value = "show", method = RequestMethod.GET)
    public ModelAndView showCart(
            @CookieValue(value = UserController.TAOTAO_COOKIE, required = false) String ticket,
            HttpServletRequest request, HttpServletResponse response) {
        User user = UserThreadLocal.get();
        List<Cart> carts = null;
        if (user == null) {
            // 没有登录
            try {
                carts = this.cartCookieService.queryCartList(request, response);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            // 已登录
            carts = this.cartService.queryCartList(user);
        }

        ModelAndView mv = new ModelAndView("cart");
        mv.addObject("cartList", carts);
        return mv;
    }

    /**
     * 更新商品数量
     */
    // http://www.taotao.com/service/cart/update/num/
    @RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateCart(@PathVariable("itemId") Long itemId,
            @PathVariable("num") Integer num, HttpServletRequest request, HttpServletResponse response) {

        User user = UserThreadLocal.get();
        if (null == user) {
            // 没登录
            try {
                this.cartCookieService.updateCart(itemId, num, request, response);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Boolean bool = this.cartService.updateCart(user, itemId, num);
            if (bool) {
                return ResponseEntity.ok(null);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    /**
     * 从购物车移除商品
     */
    @RequestMapping(value = "delete/{itemId}", method = RequestMethod.GET)
    public String deleteItemFromCart(@PathVariable("itemId") Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (user == null) {
            // 没有登录
            try {
                this.cartCookieService.deleteItemFromCart(itemId,request,response);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            this.cartService.deleteItemFromCart(itemId, user);
        }

        // 跳转显示页面
        return "redirect:/cart/show.html";
    }
}
