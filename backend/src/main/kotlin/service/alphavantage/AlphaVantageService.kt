package service.alphavantage

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URL
import org.slf4j.LoggerFactory

class AlphaVantageService() {

    private val logger = LoggerFactory.getLogger(AlphaVantageService::class.java)
    private val gson = Gson()
    private val baseUrl = "https://www.alphavantage.co/query"
    private val apiKey = "R8PK4ZKB6HOZDCZ9"//TODO:System.getenv("API_KEY")

    private fun fetchJsonFromApi(function: String, symbol: String): JsonObject? {
        val url = "$baseUrl?function=$function&symbol=$symbol&apikey=$apiKey"
        return try {
            val connection = URL(url).openConnection()
            val data = connection.getInputStream().bufferedReader().readText()
            val json = JsonParser.parseString(data).asJsonObject
            if (json.has("Error Message")) {
                logger.error(json["Error Message"].asString)
                null
            } else {
                json
            }
        } catch (e: Exception) {
            logger.error("Error fetching data from Alpha Vantage: ${e.message}")
            null
        }
    }

    fun fetchCompanyOverview(ticker: String): CompanyOverview? {
        val json = fetchJsonFromApi("OVERVIEW", ticker) ?: return null
        return gson.fromJson(json, CompanyOverview::class.java)
    }

    fun fetchIncomeStatement(ticker: String): List<IncomeStatement> {
        val json = fetchJsonFromApi("INCOME_STATEMENT", ticker) ?: return emptyList()
        val annualReportsJson = json["annualReports"].asJsonArray
        return annualReportsJson.map { gson.fromJson(it, IncomeStatement::class.java) }
    }

    fun fetchBalanceSheet(ticker: String): List<BalanceSheet> {
        val json = fetchJsonFromApi("BALANCE_SHEET", ticker) ?: return emptyList()
        val annualReportsJson = json["annualReports"].asJsonArray
        return annualReportsJson.map { gson.fromJson(it, BalanceSheet::class.java) }
    }

    fun fetchDividends(ticker: String): List<Dividend> {
        val json = fetchJsonFromApi("DIVIDENDS", ticker) ?: return emptyList()
        val annualReportsJson = json.getAsJsonArray("data")
        return annualReportsJson.map { gson.fromJson(it, Dividend::class.java) }
    }

    private fun convertStringToLong(str: String): Long? {
        return if (str == "None") {
            null
        } else try {
            str.toLong()
        } catch (e: NumberFormatException) {
            logger.warn("Failed to parse value as long: $str")
            null
        }
    }

    private fun convertStringToDouble(str: String): Double? {
        return if (str == "None") {
            null
        } else try {
            str.toDouble()
        } catch (e: NumberFormatException) {
            logger.warn("Failed to parse value as double: $str")
            null
        }
    }

}
