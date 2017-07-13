package com.taotao.web.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.Order;

@Service
public class OrderService {

    @Autowired
    private ApiService apiService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${TAOTAO_ORDER_URL}")
    private String TAOTAO_ORDER_URL;

    /**
     * 提交订单
     * 
     * @param order
     * @return
     */
    public String submitOrder(Order order) {

        String url = TAOTAO_ORDER_URL + "/order/create";

        try {
            HttpResult httpResult = this.apiService.doPostJson(url, MAPPER.writeValueAsString(order));
            if (httpResult.getCode() == 200) {
                // 响应成功
                String body = httpResult.getBody();
                JsonNode jsonNode = MAPPER.readTree(body);
                if (jsonNode.get("status").intValue() == 200) {
                    // 下订单成功，返回订单号
                    return jsonNode.get("data").asText();
                }
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据id查询Order
     * @param orderId
     * @return
     */
    public Order queryOrderByid(Long orderId) {
        String url =TAOTAO_ORDER_URL+ "/order/query/"+orderId;
        try {
            String jsonData = this.apiService.doGet(url);
            if (jsonData != null) {
                return MAPPER.readValue(jsonData, Order.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
