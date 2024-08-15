package org.example.config;

import redis.clients.jedis.Jedis;

/**
 * Manages the Redis database connection for the application.
 * Provides methods to establish and close a connection with Redis.
 * The connection is established using Jedis client with default settings (localhost and port 6379).
 *
 * @author isil
 */
public class RedisConnection {
    private static Jedis jedis = null;

    static {
        try {
            // Initialize and connect to Redis server
            jedis = new Jedis("127.0.0.1", 6379); // localhost and default port 6379
            jedis.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides the Redis connection instance.
     *
     * @return jedis instance if connected, null otherwise
     */
    public static Jedis getConnection() {
        return jedis;
    }

    /**
     * Main method to test the Redis connection.
     * It checks if the connection is successful by sending a PING command.
     */
    public static void main(String[] args) {
        Jedis jedis = getConnection();
        if (jedis != null) {
            System.out.println("Connection successful: " + jedis.ping());
        } else {
            System.out.println("Failed to connect to Redis.");
        }
    }

    /**
     * Closes the Redis connection.
     * It ensures the connection is properly terminated to avoid resource leaks.
     */
    public static void closeConnection() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
