```kotlin
import config.Config
import config.DatabaseConnection
import config.RedisConnection
import service.dataAccess.TickerInfo
import service.dataAccess.IndexDataAccess
import service.sec.SecGov
import service.sec.FinancialDataRetriever
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException
import org.slf4j.LoggerFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import service.services.DataServices


`val df = DataServices().getTickerData(c, y, y)`

val redisClient = RedisConnection.redisClient
val dbConnection = DatabaseConnection.getConnection()

val tickerInfo = TickerInfo(redisClient)
val indexDataAccess = IndexDataAccess(dbConnection)
val secGov = SecGov()

SecGov().fetchTickerFinancialsByYear(2023, "ba")

```