package org.example.service;

import org.example.config.RedisConnection;
import org.example.model.Customer;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author isil
 */

public class CustomerService {

    static final String CUSTOMER_KEY = "customer"; // Müşteri keyinin sabit kısmı
    static final String CUSTOMER_ID_COUNTER_KEY = "customer:id:counter"; // ID sayacı için anahtar
    private final Jedis jedis;

    public CustomerService() {
        this.jedis = RedisConnection.getConnection();
    }

    private String generateCustomerId() { //ID oluşturuyor
        // Sayaç kullanarak yeni bir ID oluşturur
        return String.valueOf(jedis.incr(CUSTOMER_ID_COUNTER_KEY)); // jedis.incr CUSTOMER_ID_COUNTER_KEY anahtarının değerini atomik olarak bir artırır ve bu değeri yeni müşteri ID'si olarak döndürür

    }

    public void createCustomer(Customer customer) {
        // Yeni bir müşteri ID'si oluştur
        if (customer.getId() == null || customer.getId().isEmpty()) {
            customer.setId(generateCustomerId());
        }

        String customerKey = CUSTOMER_KEY + ":" + customer.getId();
        jedis.hset(customerKey, "name", customer.getName());
        jedis.hset(customerKey, "email", customer.getEmail());
        jedis.hset(customerKey, "phoneNumber", customer.getPhoneNumber());
        jedis.hset(customerKey, "address", customer.getAddress());

    }

    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();

        Set<String> customerKeys = jedis.keys(CUSTOMER_KEY + ":*");

        for (String customerKey : customerKeys) {
            String type = jedis.type(customerKey);
            if ("hash".equals(type)) {
                Map<String, String> customerData = jedis.hgetAll(customerKey);
                List<String> purchases = jedis.lrange(customerKey + ":purchases", 0, -1);

                String id = customerKey.split(":")[1];

                Customer customer = new Customer(
                        id,
                        customerData.get("name"),
                        customerData.get("email"),
                        customerData.get("phoneNumber"),
                        customerData.get("address"),
                        purchases
                );

                customerList.add(customer);
            }
        }
        return customerList;
    }

    public Customer getCustomer(String id) {
        String customerKey = CUSTOMER_KEY + ":" + id;

        if (jedis.exists(customerKey)) {
            List<String> purchases = jedis.lrange(customerKey + ":purchases", 0, -1); //Hepsini alır

            return new Customer(
                    id,
                    jedis.hget(customerKey, "name"),
                    jedis.hget(customerKey, "email"),
                    jedis.hget(customerKey, "phoneNumber"),
                    jedis.hget(customerKey, "address"),
                    purchases
            );
        }
        return null;
    }

    public void updateCustomer(Customer customer) {
        String customerKey = CUSTOMER_KEY + ":" + customer.getId();
        if (jedis.exists(customerKey)) {
            jedis.hset(customerKey, "name", customer.getName());
            jedis.hset(customerKey, "email", customer.getEmail());
            jedis.hset(customerKey, "phoneNumber", customer.getPhoneNumber());
            jedis.hset(customerKey, "address", customer.getAddress());

        } else {
            throw new IllegalArgumentException("Customer with ID " + customer.getId() + " does not exist.");
        }
    }

    public void deleteCustomer(String id) {
        String customerKey = CUSTOMER_KEY + ":" + id;
        if (jedis.exists(customerKey)) {
            System.out.println("Deleting customer with ID: " + id);
            jedis.del(customerKey); // Müşteri bilgilerini sil
            String purchasesKey = customerKey + ":purchases";
            jedis.del(purchasesKey); // Alımları sil
            System.out.println("Customer deleted successfully.");
        } else {
            throw new IllegalArgumentException("Customer with ID " + id + " does not exist.");
        }
    }


}
