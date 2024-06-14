package service.dataAccess

import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException // Or use redis.clients.jedis.exceptions.JedisDataException for more specific handling
import java.util.logging.Logger

class TickerInfo(private val redisClient: Jedis) {

    private fun infoKey(ticker: String): String {
        return "$ticker:info"
    }

    fun storeTickerInfo(ticker: String, data: Map<String, String>) {
        try {
            redisClient.hset(infoKey(ticker), data)
        } catch (e: JedisException) {
            Logger.getLogger(TickerInfo::class.java.name).warning("${e.message} ticker: $ticker")
        }
    }

    fun getTickerUrl(ticker: String, year: Int): String? {
        return try {
            redisClient.hget(infoKey(ticker), "txt_url:$year")
        } catch (e: JedisException) {
            Logger.getLogger(TickerInfo::class.java.name).warning("${e.message} ticker: $ticker")
            null
        }
    }

    //SecGov in fetchTickerFinancialsByYear
    fun getTickerCik(ticker: String): String? {
        return try {
            val cik = redisClient.hget(infoKey(ticker), "cik")
            Logger.getLogger(TickerInfo::class.java.name).info("Retrieved CIK for ticker $ticker: $cik")
            cik
        } catch (e: JedisException) {
            Logger.getLogger(TickerInfo::class.java.name).warning("${e.message} ticker: $ticker")
            null
        }
    }
}

