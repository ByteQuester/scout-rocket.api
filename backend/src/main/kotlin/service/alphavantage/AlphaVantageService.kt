package service.alphavantage

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URI
import org.slf4j.LoggerFactory


class AlphaVantageService {

    val logger = LoggerFactory.getLogger(AlphaVantageService::class.java)
    private val gson = Gson()
    private val baseUrl = "https://www.alphavantage.co/query"
    private val apiKey = "R8PK4ZKB6HOZDCZ9" // TODO: System.getenv("API_KEY")

    private fun fetchJsonFromApi(function: String, symbol: String): JsonObject? {
        val url = "$baseUrl?function=$function&symbol=$symbol&apikey=$apiKey"
        return try {
            val connection = URI.create(url).toURL().openConnection()
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

    fun safeGetAsLong(jsonElement: JsonElement?): Long? {
        return try {
            jsonElement?.asString?.toLongOrNull()
        } catch (e: Exception) {
            logger.warn("Failed to parse value as long: ${jsonElement?.asString}")
            null
        }
    }

    fun safeGetAsDouble(jsonElement: JsonElement?): Double? {
        return try {
            jsonElement?.asString?.toDoubleOrNull()
        } catch (e: Exception) {
            logger.warn("Failed to parse value as double: ${jsonElement?.asString}")
            null
        }
    }

    // Fetch company overview data
    fun fetchCompanyOverview(ticker: String): CompanyOverview? {
        val json = fetchJsonFromApi("OVERVIEW", ticker) ?: return null
        return CompanyOverview.fromJsonObject(json, this)
    }

    // Fetch income statement data
    fun fetchIncomeStatement(ticker: String): List<IncomeStatement> {
        val json = fetchJsonFromApi("INCOME_STATEMENT", ticker) ?: return emptyList()
        val annualReportsJson = json.getAsJsonArray("annualReports") ?: return emptyList()
        return annualReportsJson.map { IncomeStatement.fromJsonObject(it.asJsonObject, this) }
    }

    // Fetch balance sheet data
    fun fetchBalanceSheet(ticker: String): List<BalanceSheet> {
        val json = fetchJsonFromApi("BALANCE_SHEET", ticker) ?: return emptyList()
        val annualReportsJson = json.getAsJsonArray("annualReports") ?: return emptyList()
        return annualReportsJson.map { BalanceSheet.fromJsonObject(it.asJsonObject, this) }
    }

    // Fetch dividends data
    fun fetchDividends(ticker: String): List<Dividend> {
        val json = fetchJsonFromApi("DIVIDENDS", ticker) ?: return emptyList()
        val annualReportsJson = json.getAsJsonArray("data") ?: return emptyList()
        return annualReportsJson.mapNotNull { Dividend.fromJsonObject(it.asJsonObject, this) }
    }
}