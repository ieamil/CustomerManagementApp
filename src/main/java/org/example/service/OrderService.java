package org.example.service;

import org.example.config.RedisConnection;
import org.example.model.Order;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author isil
 */

public class OrderService {
    private static final String ORDER_KEY_PREFIX = "order:";
    private static final String ORDER_ID_COUNTER_KEY = "order:id:counter"; // Sipariş ID'si için sayaç anahtarı
    private Jedis jedis;

    public OrderService() {
        this.jedis = RedisConnection.getConnection();
    }

    public String generateOrderNumber() {
        return String.valueOf(jedis.incr(ORDER_ID_COUNTER_KEY));
    }

    public void deleteOrder(String orderNumber) {
        String orderKey = ORDER_KEY_PREFIX + orderNumber;
        if (jedis.exists(orderKey)) {
            jedis.del(orderKey);
            jedis.del(orderKey + ":items");
        } else {
            throw new IllegalArgumentException("Order with number " + orderNumber + " does not exist.");
        }
    }

    public void saveOrder(Order order) { // add Order
        // Item listesini ve itemCount'u güncelleyin
        order.setItems(order.getItems());

        String orderKey = ORDER_KEY_PREFIX + order.getOrderNumber();
        jedis.hset(orderKey, "customerId", order.getCustomerId());
        jedis.hset(orderKey, "address", order.getAddress());
        jedis.hset(orderKey, "orderDate", order.getOrderDate().toString());
        jedis.hset(orderKey, "orderStatus", order.getOrderStatus());

        // Ürünleri virgülle ayırarak string olarak kaydediyoruz
        String itemsString = String.join(", ", order.getItems());
        jedis.hset(orderKey, "items", itemsString);

        // itemCount değerini kaydediyoruz
        jedis.hset(orderKey, "itemCount", String.valueOf(order.getItemCount()));
    }

    public void updateOrder(Order order) {
        String orderKey = ORDER_KEY_PREFIX + order.getOrderNumber();

        // Siparişin mevcut olup olmadığını kontrol et
        if (jedis.exists(orderKey)) {
            jedis.hset(orderKey, "customerId", order.getCustomerId());
            jedis.hset(orderKey, "address", order.getAddress());
            jedis.hset(orderKey, "orderDate", order.getOrderDate().toString());
            jedis.hset(orderKey, "orderStatus", order.getOrderStatus());

            // Ürünleri virgülle ayrılmış bir string olarak kaydet
            String itemsString = String.join(", ", order.getItems());
            jedis.hset(orderKey, "items", itemsString);

            // itemCount değerini güncelle
            jedis.hset(orderKey, "itemCount", String.valueOf(order.getItemCount()));
        } else {
            throw new IllegalArgumentException("Order with number " + order.getOrderNumber() + " does not exist.");
        }
    }


    public List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();

        Set<String> orderKeys = jedis.keys(ORDER_KEY_PREFIX + "*");

        for (String orderKey : orderKeys) {
            if (jedis.type(orderKey).equals("hash")) {
                String storedCustomerId = jedis.hget(orderKey, "customerId");
                if (storedCustomerId != null && storedCustomerId.equals(customerId)) {
                    String orderNumber = orderKey.split(":")[1];
                    String address = jedis.hget(orderKey, "address");
                    LocalDate orderDate = LocalDate.parse(jedis.hget(orderKey, "orderDate"));
                    String itemsString = jedis.hget(orderKey, "items");
                    List<String> items = Arrays.asList(itemsString.split(", "));
                    String orderStatus = jedis.hget(orderKey, "orderStatus");

                    Order order = new Order(orderNumber, storedCustomerId, address, items, orderDate, orderStatus);
                    orders.add(order);
                }
            }
        }
        return orders;
    }
}
