package config

import redis.clients.jedis.Jedis

object RedisConnection {
    val redisClient: Jedis = Jedis(Config.REDIS_HOST_NAME, Config.REDIS_PORT).apply {
        this.use {
            // Optional: Perform any initialization or configuration
        }
    }
}
