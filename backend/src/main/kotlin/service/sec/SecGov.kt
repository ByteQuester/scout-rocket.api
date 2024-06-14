//kotlinc -cp "slf4j-api-2.0.9.jar:ojdbc11.jar:HikariCP-5.0.1.jar:jedis-5.1.3.jar:mysql-connector-j-8.2.0.jar:jsoup-1.17.2.jar" -d out data/src/main/kotlin/data/dataAccess/*.kt data/src/main/kotlin/data/sec/*.kt
//kotlinc -cp "out:slf4j-api-2.0.9.jar:ojdbc11.jar:HikariCP-5.0.1.jar:jedis-5.1.3.jar:mysql-connector-j-8.2.0.jar:jsoup-1.17.2.jar"
package service.sec

import service.dataAccess.DataAccess
import java.net.HttpURLConnection
import java.net.URL
import service.common.LoggerSetup
import kotlin.math.log

class SecGov {
    private val logger = LoggerSetup.logger
    private val dataAccess = DataAccess()
    private val financialDataRetriever = FinancialDataRetriever()

    companion object {
        const val SEC_ARCHIVE_URL = "https://www.sec.gov/Archives/"
        const val TICKER_CIK_LIST_URL = "https://www.sec.gov/include/ticker.txt"
        const val MAX_RETRIES = 2
    }

//    fun fetchTickerFinancialsByYear(year: Int, ticker: String?) {
//        //println("Fetching financial data for $ticker in year $year")
//        logger.info("Fetching financial data for $ticker in year $year")
//
//        val isIdxStored = dataAccess.indexDataAccess.isIndexStored(year)
//        logger.info("Idx is stored in DB: $isIdxStored ")
//        if (!isIdxStored) {
//            //println("Index file for year $year is not accessible, fetching from web")
//            logger.warning("Index file for year $year not found, fetching from web")
//            for (q in 1..4) {
//                financialDataRetriever.prepareIndex(year, q)
//            }
//        }
//
//        if (ticker != null) {
//            val tickerCikStr = dataAccess.tickerInfo.getTickerCik(ticker)
//            val tickerCik = tickerCikStr?.toIntOrNull()
//            logger.info("CIK for $ticker is $tickerCik")
//
//            if (tickerCik != null) {
//                try {
//                    val result = dataAccess.indexDataAccess.getIndexRowByCik(tickerCik, year)
//                    if (result != null) {
//                        val tickerInfoHash = mapOf("company_name" to result[0], "txt_url:$year" to result[1])
//                        dataAccess.tickerInfo.storeTickerInfo(ticker, tickerInfoHash)
//                        financialDataRetriever.fetchCompanyData(ticker, year)
//                        dataAccess.tickerFinancials.commitTickerData()
//                        logger.info("Fetched index row for CIK: $tickerCik, Result: $result")
//                        logger.info("Stored tickerInfo for $year: $tickerInfoHash")
//                        logger.info("Fetched companyData for $year: $tickerInfoHash")
//                        logger.info("Completed fetching financial data for $year: $tickerInfoHash")
//                    } else {
//                        logger.warning("Could not fetch data for $year: $ticker")
//                    }
//                } catch (e: Exception) {
//                    println("Error fetching index row by CIK: ${e.message}")
//                    logger.severe("Error fetching financial data for $year: $ticker ${e.message}")
//                }
//            } else {
//                logger.info("Invalid CIK for $ticker")
//            }
//        } else {
//            try {
//                val idx = dataAccess.indexDataAccess.getIndexByYear(year)
//                logger.info("Index file for $year is $idx")
//                for (result in idx) {
//                    val currentTicker = dataAccess.tickerPrice.getTickerByCik(result[2])
//                    if (currentTicker != null) {
//                        val tickerInfoHash = mapOf("company_name" to result[0], "txt_url:$year" to result[1])
//                        dataAccess.tickerInfo.storeTickerInfo(currentTicker, tickerInfoHash)
//                        financialDataRetriever.fetchCompanyData(currentTicker, year)
//                        logger.info("Ticker InfoHash: $tickerInfoHash")
//                        logger.info("Current ticker: $currentTicker for result: $result")
//
//                    } else {
//                        logger.warning("Could not fetch data for ticker for year $year")
//                    }
//                }
//                dataAccess.tickerFinancials.commitTickerData()
//            } catch (e: Exception) {
//                logger.severe("Error fetching index by year: ${e.message}")
//                return
//            }
//        }
//    }

    fun fetchTickerFinancialsByYear(year: Int, ticker: String? = null) {
        logger.info("Fetching financial data for $ticker in year $year")

        val isIdxStored = dataAccess.indexDataAccess.isIndexStored(year)
        logger.info("Idx is stored in DB: $isIdxStored ")
        if (!isIdxStored) {
            logger.warning("Index file for year $year not found, fetching from web")
            for (q in 1..4) {
                financialDataRetriever.prepareIndex(year, q)
            }
        }

        if (ticker != null) {
            val tickerCikStr = dataAccess.tickerInfo.getTickerCik(ticker)
            val tickerCik = tickerCikStr?.toIntOrNull()
            logger.info("CIK for $ticker is $tickerCik")

            if (tickerCik != null) {
                try {
                    val result = dataAccess.indexDataAccess.getIndexRowByCik(tickerCik, year)
                    if (result != null) {
                        val tickerInfoHash = mapOf("company_name" to result[0], "txt_url:$year" to result[1])
                        dataAccess.tickerInfo.storeTickerInfo(ticker, tickerInfoHash)
                        financialDataRetriever.fetchCompanyData(ticker, year)
                        dataAccess.tickerFinancials.commitTickerData()
                        logger.info("Fetched index row for CIK: $tickerCik, Result: $result")
                        logger.info("Stored tickerInfo for $year: $tickerInfoHash")
                        logger.info("Fetched companyData for $year: $tickerInfoHash")
                        logger.info("Completed fetching financial data for $year: $tickerInfoHash")
                    } else {
                        logger.warning("Could not fetch data for $year: $ticker")
                    }
                } catch (e: Exception) {
                    logger.severe("Error fetching index row by CIK: ${e.message}")

                    // Continue to fetch data from the index even if there was an error with the specific ticker
                    fetchFromIndex(year) // Call the helper function to fetch from index
                }
            } else {
                logger.info("Invalid CIK for $ticker")
                fetchFromIndex(year)
            }
        } else {
            fetchFromIndex(year)
        }
    }

    private fun fetchFromIndex(year: Int) {
        try {
            val idx = dataAccess.indexDataAccess.getIndexByYear(year)
            logger.info("Index file for $year is $idx")
            for (result in idx) {
                val currentTicker = dataAccess.tickerPrice.getTickerByCik(result[2])
                if (currentTicker != null) {
                    val tickerInfoHash = mapOf("company_name" to result[0], "txt_url:$year" to result[1])
                    dataAccess.tickerInfo.storeTickerInfo(currentTicker, tickerInfoHash)
                    financialDataRetriever.fetchCompanyData(currentTicker, year)
                    logger.info("Ticker InfoHash: $tickerInfoHash")
                    logger.info("Current ticker: $currentTicker for result: $result")
                } else {
                    logger.warning("Could not fetch data for ticker for year $year")
                }
            }
            dataAccess.tickerFinancials.commitTickerData()
        } catch (e: Exception) {
            logger.severe("Error fetching index by year: ${e.message}")
            return
        }
    }


    fun fetchTickersList(): List<String> {
        val logger = LoggerSetup.logger
        val tickerList = mutableListOf<String>()
        var retries = 0
        val headers = mapOf("User-Agent" to "SampleCompanyName AdminContact@samplecompany.com")

        while (retries < MAX_RETRIES) {
            try {
                val url = URL(TICKER_CIK_LIST_URL)
                logger.info("URL: $url")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    println("Failed to fetch data, status code: $responseCode")
                    retries++
                    Thread.sleep(1000L * retries)
                    continue
                }

                val content = connection.inputStream.bufferedReader().readText()
                val tickerCikListLines = content.split("\n")
                logger.info("TickerCikListLines: $tickerCikListLines")

                for (entry in tickerCikListLines) {
                    val parts = entry.trim().split(Regex("\\s+"))
                    if (parts.size == 2) {
                        val (ticker, cik) = parts
                        dataAccess.tickerPrice.storeTickerCikMapping(ticker, cik)
                        tickerList.add(ticker)
                    } else {
                        logger.warning("Unexpected format in entry: '$entry'")
                    }
                }
                logger.info("Successfully mapped ${tickerList.size} tickers to CIK")
                return tickerList

            } catch (e: Exception) {
                logger.severe("Error fetching tickers: ${e.message}")
                retries++
                Thread.sleep(1000L * retries)
            }
        }

        logger.severe("Failed to fetch tickers after maximum retries.")
        return tickerList
    }
}

//for (q in 1..2) {
//    fDR.prepareIndex(year, q)
//}