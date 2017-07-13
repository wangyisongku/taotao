package com.taotao.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.Cart;
import com.taotao.web.pojo.Item;
import com.taotao.web.pojo.User;

@Service
public class CartService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_CART_URL}")
    private String TAOTAO_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemService itemService;

    public Boolean addItemToCart(Long itemId, User user) {
        try {
            String url = TAOTAO_CART_URL + "/rest/cart";

            Item item = this.itemService.getItemById(itemId);

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userId", user.getId());
            param.put("itemId", itemId);
            param.put("itemTitle", item.getTitle());

            String[] images = item.getImages();

            if (images.length<=0) {
                // 没有图片
                param.put("itemImage", "");
            } else {
                param.put("itemImage", images[0]);
            }
            param.put("itemPrice", item.getPrice());
            param.put("num", 1);// 默认购买数量为1

            HttpResult httpResult = this.apiService.doPost(url, param);
            if (httpResult.getCode() == 201 || httpResult.getCode() == 204) {
                return true;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示购物车商品数据
     * 
     * @param user
     * @return
     */
    public List<Cart> queryCartList(User user) {
        try {
            String url = TAOTAO_CART_URL + "/rest/cart/" + user.getId();

            String jsondata = this.apiService.doGet(url);

            if (jsondata == null) {
                return null;
            }
            return MAPPER.readValue(jsondata,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新商品数量
     * @param user
     * @param itemId
     * @param num
     */
    public Boolean updateCart(User user, Long itemId, Integer num) {
        try {
        String url = TAOTAO_CART_URL +"/rest/cart/"+user.getId()+"/"+itemId+"/"+num;
        
            HttpResult httpResult = this.apiService.doPut(url);
            if (httpResult.getCode() == 204) {
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从购物车中移除商品
     * @param itemId
     * @param user
     */
    public Boolean deleteItemFromCart(Long itemId, User user) {
        String url = TAOTAO_CART_URL +"/rest/cart/"+user.getId()+"/"+itemId;
        try {
            HttpResult httpResult = this.apiService.doDelete(url);
            if (httpResult.getCode() ==  204) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
