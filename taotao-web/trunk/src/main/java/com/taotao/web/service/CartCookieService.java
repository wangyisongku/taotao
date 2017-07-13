package com.taotao.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.utils.CookieUtils;
import com.taotao.web.pojo.Cart;
import com.taotao.web.pojo.Item;

@Service
public class CartCookieService {

    public static final String CART_COOKIE_NAME = "TT_COOKIE";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemService itemService;

    /**
     * 添加商品到购物车
     * 
     * @param itemId
     * @param request
     * @param response
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response)
            throws JsonParseException, JsonMappingException, IOException {
        List<Cart> carts = queryCartList(request, response);
       
        Cart cart = null;
        for (Cart c : carts) {// 判断是否存在当前选中的商品？
            if (c.getItemId().intValue() == itemId.intValue()) {// 有相同的商品
                cart = c;
                break;
            }
        }
        if (null == cart) {// 在cookie购物车中，没有现在添加的商品
            Item item = this.itemService.getItemById(itemId);
            cart = new Cart();
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            cart.setItemId(itemId);
            String[] images = item.getImages();
            if (images.length <= 0) {
                cart.setItemImage("");
            } else {
                cart.setItemImage(images[0]);
            }
            cart.setItemPrice(item.getPrice());
            cart.setItemTitle(item.getTitle());
            cart.setNum(1);
            carts.add(cart);
        } else {
            // 购物车存在该商品，数量加1
            cart.setNum(cart.getNum() + 1);
            cart.setUpdated(new Date());
        }

        // 写入cookie中，并且设置一个月有效
        CookieUtils.setCookie(request, response, CART_COOKIE_NAME, MAPPER.writeValueAsString(carts),
                60 * 60 * 24 * 30,true);

    }

    // 定义一个方法查询cookie中数据
    public List<Cart> queryCartList(HttpServletRequest request, HttpServletResponse response)
            throws JsonParseException, JsonMappingException, IOException {
        String cookieValue = CookieUtils.getCookieValue(request, CART_COOKIE_NAME, true);
        if (StringUtils.isEmpty(cookieValue)) {
            return new ArrayList<Cart>();
        }
        return MAPPER.readValue(cookieValue,
                MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
    }

    /**
     * 修改购物车的数量
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public void updateCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
        List<Cart> carts = queryCartList(request, response);
        for (Cart c : carts) {// 判断是否存在当前选中的商品？
            if (c.getItemId().intValue() == itemId.intValue()) {// 有相同的商品
                c.setNum(num);
                c.setUpdated(new Date());
                break;
            }
        }
        CookieUtils.setCookie(request, response, CART_COOKIE_NAME, MAPPER.writeValueAsString(carts), 60*60*24*30, true);
    }

    /**
     * 从cookie中移除商品
     * @param itemId
     * @param request
     * @param response
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public void deleteItemFromCart(Long itemId, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
        List<Cart> carts = queryCartList(request, response);
        
        for (Cart c : carts) {// 判断是否存在当前选中的商品？
            if (c.getItemId().intValue() == itemId.intValue()) {// 有相同的商品
               carts.remove(c);
                break;
            }
        }
        String itemstr =null;
        if (carts.size()<=0) {
            itemstr = "";
        }else{
            itemstr = MAPPER.writeValueAsString(carts);
        }
        
        CookieUtils.setCookie(request, response, CART_COOKIE_NAME, itemstr, 60*60*24*30, true);
    }

}
