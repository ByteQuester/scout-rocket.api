package service.dataAccess

import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import java.time.ZoneOffset
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZoneId

class TickerPrice(private val redisClient: Jedis) {
    private val logger = LoggerFactory.getLogger(TickerPrice::class.java)

    fun storeTickerPrice(ticker: String, timestamp: String, value: Double) {
        try {
            redisClient.set("$ticker:price:$timestamp", value.toString())
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
        }
    }

    fun storeTickerVolume(ticker: String, timestamp: String, value: Long) {
        try {
            redisClient.set("$ticker:volume:$timestamp", value.toString())
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
        }
    }

    fun isTickerVolumeExists(ticker: String): Boolean {
        return try {
            redisClient.keys("$ticker:volume:*").isNotEmpty()
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
            false
        }
    }

    fun isTickerPriceExists(ticker: String): Boolean {
        return try {
            redisClient.keys("$ticker:price:*").isNotEmpty()
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
            false
        }
    }


    fun getVolumes(ticker: String, start: Long, end: Long): List<Pair<Long, Double>>? {
        return try {
            val keys = redisClient.keys("$ticker:volume:*")
            keys.mapNotNull { key ->
                val timestamp = key.split(":").last().toLong()
                if (timestamp in start..end) {
                    redisClient.get(key)?.toDouble()?.let { Pair(timestamp, it) }
                } else {
                    null
                }
            }.sortedBy { it.first }
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
            null
        }
    }

    fun getPrices(ticker: String, start: LocalDate, end: LocalDate): List<Pair<LocalDate, Double>>? {
        return try {
            val startEpoch = start.atStartOfDay(ZoneOffset.UTC).toEpochSecond()
            val endEpoch = end.atStartOfDay(ZoneOffset.UTC).toEpochSecond()
            val redisResponse = redisClient.zrangeByScoreWithScores("$ticker:price", startEpoch.toDouble(), endEpoch.toDouble())

            redisResponse.map { entry ->
                val date = LocalDate.ofEpochDay(entry.score.toLong() / (24 * 3600)) // Convert seconds to LocalDate
                Pair(date, entry.score)
            }.sortedBy { it.first }
        } catch (e: JedisException) {
            logger.error("${e.message} ticker: $ticker")
            null
        }
    }


    fun getPrice(ticker: String, date: LocalDate): Double {
        val startDate = date.minusWeeks(1)
        val endDate = date
        val priceRes = getPrices(ticker, startDate, endDate)
        return priceRes?.lastOrNull()?.second ?: 0.0
    }

    fun getVolume(ticker: String, date: LocalDate): Double {
        val startTime = date.minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        val endTime = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        val volumeRes = getVolumes(ticker, startTime, endTime)
        return volumeRes?.lastOrNull()?.second ?: 0.0
    }

    fun getTickerByCik(ticker: String): String? {
        return try {
            redisClient.hget("cik2ticker", ticker)
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
            null
        }
    }

    fun storeTickerCikMapping(ticker: String, cik: String) {
        try {
            redisClient.hset("$ticker:info", "cik", cik)
            redisClient.hset("cik2ticker", cik, ticker)
            redisClient.sadd("ticker_set", ticker)
            logger.info("Stored CIK mapping for ticker: $ticker, CIK: $cik")
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
        }
    }

    fun isTickerMapped(ticker: String): Boolean {
        return try {
            redisClient.sismember("ticker_set", ticker)
        } catch (e: JedisException) {
            logger.warn("${e.message} ticker: $ticker")
            false
        }
    }

    fun isTickerListExist(): Boolean {
        return try {
            redisClient.exists("ticker_set")
        } catch (e: JedisException) {
            logger.warn("${e.message}")
            false
        }
    }

    fun getTickerList(): List<String> {
        return try {
            val tickerListResp = redisClient.sscan("ticker_set", "0").result
            tickerListResp
        } catch (e: JedisException) {
            logger.warn("${e.message}")
            emptyList()
        }
    }
}
