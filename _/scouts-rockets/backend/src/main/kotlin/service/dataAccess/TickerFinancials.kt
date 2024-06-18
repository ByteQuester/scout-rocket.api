package service.dataAccess

import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import org.slf4j.LoggerFactory

class TickerFinancials(private val redisClient: Jedis) {
    private val logger = LoggerFactory.getLogger(TickerFinancials::class.java)

    private fun financialsKey(ticker: String, year: Int): String {
        return "$ticker:$year"
    }

    fun storeTickerFinancials(ticker: String, year: Int, data: Map<String, String>) {
        try {
            redisClient.hset(financialsKey(ticker, year), data)
        } catch (e: JedisException) {
            logger.error("Unexpected error while storing financial data for $ticker:$year: ${e.message}", e)
        }
    }

    fun getTickerFinancials(ticker: String, year: Int): Map<String, String>? {
        return try {
            redisClient.hgetAll(financialsKey(ticker, year))
        } catch (e: JedisException) {
            logger.error("Reids data error while fetching financial data for '$ticker:$year': ${e.message}")
            null
        }
    }

    fun isTickerStored(ticker: String, year: Int): Boolean {
        return try {
            redisClient.exists(financialsKey(ticker, year))
        } catch (e: JedisException) {
            logger.error("Redis connection error while checking if ticker data exists for $ticker:$year: ${e.message}", e)
            false
        }
    }

    fun commitTickerData(): String? {
        return try {
            redisClient.bgsave()
        } catch (e: JedisException) {
            logger.error("Unexpected error while committing ticker data: ${e.message}", e)
            null
        }
    }
}
