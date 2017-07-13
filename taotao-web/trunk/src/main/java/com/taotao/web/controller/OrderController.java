package com.taotao.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.pojo.Item;
import com.taotao.web.pojo.Order;
import com.taotao.web.pojo.User;
import com.taotao.web.service.CartService;
import com.taotao.web.service.ItemService;
import com.taotao.web.service.OrderService;
import com.taotao.web.service.UserService;
import com.taotao.web.threadlocal.UserThreadLocal;

@RequestMapping("order")
@Controller
public class OrderController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CartService cartService;

    /**
     * 去下单页
     * 
     * @return
     */
    // http://www.taotao.com/order/868462.html
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
        ModelAndView mv = new ModelAndView("order");
        Item item = this.itemService.getItemById(itemId);
        mv.addObject("item", item);
        return mv;
    }

    // http://www.taotao.com/service/order/submit
    /**
     * 提交订单
     */
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> submitOrder(Order order,@CookieValue(UserController.TAOTAO_COOKIE) String ticket) {
        //设置买家信息
        User user = UserThreadLocal.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        
        String orderNumber = this.orderService.submitOrder(order);
        Map<String, Object> result = new HashMap<String, Object>();
        if (orderNumber == null) {
            //创建失败
            result.put("status", "500");
        }else{
            result.put("status", "200");
            result.put("data", orderNumber);
        }
        return result;
    }
    
    /**
     * 下单成功页
     */
    //http://www.taotao.com/order/success.html?id=11462347727130
    @RequestMapping(value="success",method=RequestMethod.GET)
    public ModelAndView toSuccess(@RequestParam("id") Long orderId){
        Order order = this.orderService.queryOrderByid(orderId);
        ModelAndView mv = new ModelAndView("success");
        mv.addObject("order", order);
        return mv;
    }
    
    
    /**
     * 去下单确认页
     */
   // http://www.taotao.com/order/create.html
    @RequestMapping(value="create",method=RequestMethod.GET)
    public ModelAndView toOrderCart(){
        ModelAndView mv = new ModelAndView("order-cart");
        mv.addObject("carts", this.cartService.queryCartList(UserThreadLocal.get()));
        return mv;
    }
}
