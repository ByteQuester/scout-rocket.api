package service.dataAccess

import config.RedisConnection
import config.DatabaseConnection
import java.sql.Connection
import redis.clients.jedis.Jedis

class DataAccess(
    dbConnection: Connection = DatabaseConnection.getConnection(),
    redisClient: Jedis = RedisConnection.redisClient
) {
    val tickerFinancials = TickerFinancials(redisClient)
    val tickerInfo = TickerInfo(redisClient)
    val tickerPrice = TickerPrice(redisClient)
    val indexDataAccess = IndexDataAccess(dbConnection)
}

