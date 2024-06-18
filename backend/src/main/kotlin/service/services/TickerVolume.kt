//package service.services
//
//
//import service.dataAccess.DataAccess
//import service.common.LoggerSetup
//import com.google.gson.JsonParser
//import java.net.URL
//import org.slf4j.LoggerFactory
//import kotlin.math.log
//
//fun fetchTickerPriceVolume(ticker: String) {
//    val da = DataAccess()
//    val logger = LoggerSetup.logger
//    //val logger = LoggerFactory.getLogger("TickerPriceVolumeFetcher") (enable: production only)
//    val apiKey = "R8PK4ZKB6HOZDCZ9" // TODO: getenv
//    val url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=$ticker&outputsize=full&apikey=$apiKey"
//
//    val maxRetries = 5
//    var retries = 0
//    val waitTime = 5000L
//
//    while (retries < maxRetries) {
//        try {
//            val connection = URL(url).openConnection()
//            val data = connection.getInputStream().bufferedReader().readText()
//
//            val json = JsonParser.parseString(data).asJsonObject
//            if (json.has("Error Message")) {
//                logger.severe(json["Error Message"].asString)
//                return
//            }
//
//            val timeSeries = json.getAsJsonObject("Time Series (Daily)")
//            for ((dateString, values) in timeSeries.entrySet()) {
//                // Use dateString directly as the timestamp (YYYY-MM-DD)
//                val closePrice = values.asJsonObject.get("4. close").asDouble
//                val volume = values.asJsonObject.get("5. volume").asLong
//
//                logger.info("closePrice: $closePrice and timestamp: $dateString")
//                logger.info("volume: $volume and timestamp: $dateString")
//
//                da.tickerPrice.storeTickerPrice(ticker, dateString, closePrice)
//                da.tickerPrice.storeTickerVolume(ticker, dateString, volume)
//            }
//            da.tickerFinancials.commitTickerData() // Make sure this is correct
//
//            logger.info("Successfully fetched price and volume for $ticker")
//            break // Exit loop if successful
//
//        } catch (e: Exception) {
//            logger.severe("Error fetching price and volume for $ticker (Attempt ${retries + 1}): ${e.message}")
//            retries++
//            logger.info("Retrying in $waitTime milliseconds... ($retries/$maxRetries)")
//            Thread.sleep(waitTime)
//        }
//    }
//
//    if (retries == maxRetries) {
//        logger.severe("Failed to fetch price and volume for $ticker after $maxRetries retries.")
//    }
//}

