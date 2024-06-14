// File: src/main/kotlin/secgov/FinancialDataRetriever.kt

package service.sec

import service.common.LoggerSetup
import service.dataAccess.DataAccess
import service.sec.SecGov.Companion.MAX_RETRIES
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FinancialDataRetriever {
    private val dataAccess = DataAccess()
    private val elementParser = ElementParser()

    companion object {
        private val ELEMENT_LIST = mapOf(
            "Revenue" to listOf("us-gaap|Revenues", "us-gaap|SalesRevenueNet"),
            "Costs" to listOf("us-gaap|CostOfRevenue", "us-gaap|CostOfGoodsAndServicesSold", "us-gaap|CostOfGoodsSold"),
            "GrossProfit" to listOf("us-gaap|GrossProfit"),
            "AdminExpenses" to listOf("us-gaap|GeneralAndadministrativeExpense", "us-gaap|SellingGeneralAndadministrativeExpense"),
            "RndExpenses" to listOf("us-gaap|ResearchAndDevelopmentExpense"),
            "OperatingExpenses" to listOf("us-gaap|OperatingExpenses", "us-gaap|OperatingIncomeLoss"),
            "NetIncome" to listOf("us-gaap|netincomeloss"),
            "Liabilities" to listOf("us-gaap|liabilities", "us-gaap|liabilitiescurrent"),
            "Assets" to listOf("us-gaap|assets", "us-gaap|assetscurrent")
        )
    }

    fun getFinancialData(soup: Document, ticker: String, year: Int) {
        val logger = LoggerSetup.logger
        val start = System.currentTimeMillis()
        val elementList = ELEMENT_LIST

        //TODO: tags name should be all updated to replace : with |
        //Comment: selectFirst may not be the best option
        val reportDateFocus = soup.selectFirst("dei|documentfiscalperiodfocus")
        logger.info("reportDateFocus = $reportDateFocus")

        if (reportDateFocus == null) {
            logger.warning("No report date focus found.")
            return
        }

        val reportDate = elementParser.parseElement(soup, reportDateFocus)
        logger.info("reportDate = $reportDate")
        val shares = soup.select("dei|entitycommonstocksharesoutstanding")
        logger.info("shares = $shares")
        if (shares.isEmpty()) {
            logger.warning("No shares found.")
            return
        }

        val filteredList = mutableListOf<MutableMap<String, Any?>>()
        for ((keyName, keywords) in elementList) {
            var reportFocus = reportDateFocus.attr("contextref")
            logger.info(" for keyName = $keyName")
            var elementDict = getDataByKey(soup, keywords, reportFocus)
            logger.fine("Element Dict for $keyName with $reportFocus: $elementDict")
            if (elementDict.isNotEmpty()) {
                elementDict["name"] = keyName
                filteredList.add(elementDict.toMutableMap())
            } else {
                reportFocus = "FI${reportDateFocus.attr("contextref").replace(Regex("[FDYT]"), "")}"
                elementDict = getDataByKey(soup, keywords, reportFocus)
                logger.fine("Element Dict for $keyName with fallback $reportFocus: $elementDict")
                if (elementDict.isNotEmpty()) {
                    elementDict["name"] = keyName
                    filteredList.add(elementDict.toMutableMap())
                }
            }
        }

        var totalShares = 0.0
        for (share in shares) {
            val elementDict = elementParser.parseElement(soup, share, false)
            if (elementDict.isNotEmpty()) {
                totalShares += elementDict["value"] as Double
            }
        }
        if (shares.isNotEmpty()) {
            val elementDict = mutableMapOf<String, Any?>()
            elementDict["value"] = totalShares
            elementDict["name"] = "SharesOutstanding"
            filteredList.add(elementDict)
        }

        val data = filteredList.associate { it["name"] as String to it["value"] }

        if (data.isEmpty()) {
            logger.info("Data for $ticker $year is empty")
            return
        }

        val finalData = data.toMutableMap()
        if ("Assets" in data && "Liabilities" in data) {
            finalData["TotalEquityGross"] = (data["Assets"] as Double) - (data["Liabilities"] as Double)
        }

        if ("SharesOutstanding" in data) {
            val dt = LocalDate.parse(reportDate["date"] as String, DateTimeFormatter.ISO_DATE)
            val tickerPrice = dataAccess.tickerPrice.getPrice(ticker, dt)
            finalData["MarketCap"] = (data["SharesOutstanding"] as Double) * tickerPrice
        }

       if ("GrossProfit" !in data) {
           finalData["GrossProfit"] = (data["Revenue"] as Double) - (data["Costs"] as Double)
       }

        finalData["ReportFocus"] = reportDate["date"]

        // Convert finalData to Map<String, String>
        val stringData = finalData.mapValues { it.value.toString() }
        dataAccess.tickerFinancials.storeTickerFinancials(ticker, year, stringData)
        logger.info("Successfully stored $ticker $year from sec")
        val end = System.currentTimeMillis()
        logger.info("Elapsed time to parse: ${end - start}")
    }


    private fun getDataByKey(soup: Document, keywords: List<String>, docFilter: String): MutableMap<String, Any?> {
        val logger = LoggerSetup.logger
        for (key in keywords) {
            val query = "${key.lowercase()}[contextref=$docFilter]"
            logger.fine("Query: $query") // Debugging line to check the query
            try {
                val element = soup.selectFirst(query) ?:continue
                logger.info("Found element: $element for key: $key with contextref: $docFilter")
                val elementDict = elementParser.parseElement(soup, element)
                if (elementDict.isNotEmpty()) {
                    return elementDict.toMutableMap()
                }
            } catch (e: Exception) {
                logger.warning("Error parsing query '$query': ${e.message}")
            }
        }
        return mutableMapOf()
    }

    fun fetchCompanyData(ticker: String, year: Int) {
        val txtUrl = dataAccess.tickerInfo.getTickerUrl(ticker, year)
        if (txtUrl != null) {
            fetchCompanyDataInternal(ticker, year, txtUrl)
        } else {
            println("Couldn't load data for $ticker:$year")
        }
    }

    private fun fetchCompanyDataInternal(ticker: String, year: Int, txtUrl: String) {
        val logger = LoggerSetup.logger
        var retries = 0
        val headers = mapOf("User-Agent" to "SampleCompanyName AdminContact@samplecompany.com")

        //val logFileName = "fetch_data_${ticker}_$year.log"
        //val logFile = File(logFileName)

        if (txtUrl.isEmpty()) {
            logger.warning("No Url was provided for $ticker:$year")
        }

        while (retries < MAX_RETRIES) {
            try {
                val toGetHtmlSite = "https://www.sec.gov/Archives/$txtUrl"
                logger.info("Fetching data from $toGetHtmlSite (attempt ${retries + 1})")
                val url = URL(toGetHtmlSite)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    println("Failed to fetch data from $toGetHtmlSite, status code: $responseCode")
                    return
                }

                val data = connection.inputStream.bufferedReader().readText()
                //println("Fetched data: $data")
                //logger.fine("Fetched data (attempt ${retries + 1}):\n$data\n\n")

                // parse only the <xbrl> doc
                val xbrlDoc = Jsoup.parse(data).select("xbrl")
                //logger.fine("Parsed XBRL data (attempt ${retries + 1}):\n$xbrlDoc\n\n")

                val soup = Jsoup.parse(xbrlDoc.toString())
                //logger.fine("Parsed soup data (attempt ${retries + 1}):\n$soup\n\n")
                logger.info("Parsing financial data for $ticker in $year")
                getFinancialData(soup, ticker, year)

            } catch (e: Exception) {
                logger.severe("Error fetching data for $ticker in $year: $e")
                //logFile.appendText("Error fetching data for $ticker in $year (Attempt ${retries + 1}):\n${e.message}\n\n")
            }
            retries++
            val retryDelay = 1000L * retries * retries
            logger.info("Retrying $retryDelay seconds...")
            Thread.sleep(retryDelay)
        }
        //logFile.appendText("fetch data for $ticker from $year after $MAX_RETRIES retries\n")
        logger.info("fetch data for $ticker from $year after $MAX_RETRIES retries")
    }

    fun prepareIndex(year: Int, quarter: Int) {
        val headers = mapOf("User-Agent" to "SampleCompanyName AdminContact@samplecompany.com")
        val filing = "|10-K|"
        val url = "${SecGov.SEC_ARCHIVE_URL}/edgar/full-index/$year/QTR$quarter/master.idx"
        //e.g, val url = "https://www.sec.gov/Archives/edgar/full-index/2022/QTR2/master.idx"
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        headers.forEach { (key, value) -> urlConnection.setRequestProperty(key, value) }
        urlConnection.connect()

        val responseCode = urlConnection.responseCode
        if (responseCode != 200) {
            println("Failed to fetch index, status code: $responseCode")
            return
        }

        val content = urlConnection.inputStream.bufferedReader().readText()
        val decoded = content.split("\n")

        dataAccess.indexDataAccess.storeIndex(decoded, year, filing)
        //dA.indexDataAccess.storeIndex(decoded, year, filing)
        //println("printing $decoded in $year with $filing")
        println("Inserted year $year qtr $quarter to DB")
    }
}
