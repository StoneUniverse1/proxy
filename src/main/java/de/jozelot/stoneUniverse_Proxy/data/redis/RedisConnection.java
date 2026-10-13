package de.jozelot.stoneUniverse_Proxy.data.redis;

import de.jozelot.stoneUniverse_Proxy.StoneUniverse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.CompletableFuture;

public class RedisConnection {

    private final StoneUniverse plugin;
    private JedisPool jedisPool;
    private String redisPrefix;

    public RedisConnection(StoneUniverse bot) {
        this.plugin = plugin;
    }

    public boolean init() {
        var logger = plugin.getLogger();
        logger.info("Starting connection to redis host...");
        var cm = plugin.getBootstrap().getConfig();
        var redisConf = cm.getRedis();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(16);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);

        try {
            String password = redisConf.getPassword();
            if (password != null && !password.isEmpty()) {
                this.jedisPool = new JedisPool(
                        poolConfig,
                        redisConf.getHost(),
                        redisConf.getPort(),
                        5000,
                        password
                );
            } else {
                this.jedisPool = new JedisPool(
                        poolConfig,
                        redisConf.getHost(),
                        redisConf.getPort(),
                        5000
                );
            }

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.ping();
            }

            logger.info("Connection to redis host was successful!");

            return true;
        } catch (Exception e) {
            logger.error("");
            logger.error("Failed to connect to your Redis server!");
            logger.error("Make sure you have the right host, port and password configured.");
            logger.error("Error details: " + e.getMessage());
            logger.error("");

            if (jedisPool != null && !jedisPool.isClosed()) {
                jedisPool.close();
            }
            return false;
        }
    }

    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    public Jedis getResource() {
        if (jedisPool == null || jedisPool.isClosed()) {
            throw new IllegalStateException("JedisPool is not initialized or closed.");
        }
        return jedisPool.getResource();
    }

    public void startListeners() {
        var redisConnection = bot.getBootstrap().getRedisConnection();
        CompletableFuture.runAsync(() -> {
            try (var jedis = redisConnection.getResource()) {
                jedis.psubscribe(new RedisManager(bot), "*");
            } catch (Exception e) {
                logger.error("Fehler im Redis-Subscription-Loop: " + e.getMessage());
            }
        });
    }
}
