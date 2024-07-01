package service.alphavantage

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import model.Price
import model.Volume
import model.BalanceSheet
import model.CompanyOverview
import model.Dividend
import model.IncomeStatement
import java.net.URI
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AlphaVantageService {

    val logger = LoggerFactory.getLogger(AlphaVantageService::class.java)
    private val baseUrl = "https://www.alphavantage.co/query"
    private val apiKey = "Q2OFL8K8ZYLHZ0L4" // TODO: System.getenv("API_KEY")

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

//------------GENERAL OVERVIEW--------------
    // Fetch company overview data
    fun fetchCompanyOverview(ticker: String): CompanyOverview? {
        val json = fetchJsonFromApi("OVERVIEW", ticker) ?: return null
        return CompanyOverview.fromJsonObject(json, this)
    }
//------------Real-Time-----------------
// Existing method to fetch price by date
    fun fetchPriceByDate(ticker: String, date: String): Price? {
        val json = fetchJsonFromApi("TIME_SERIES_DAILY", ticker) ?: return null
        val timeSeries = json.getAsJsonObject("Time Series (Daily)") ?: return null
        return timeSeries.entrySet().firstOrNull { it.key == date }?.value?.let {
            Price.fromJsonObject(it.asJsonObject, this)
        }
    }

    // Existing method to fetch volume by date
    fun fetchVolumeByDate(ticker: String, date: String): Volume? {
        val json = fetchJsonFromApi("TIME_SERIES_DAILY", ticker) ?: return null
        val timeSeries = json.getAsJsonObject("Time Series (Daily)") ?: return null
        return timeSeries.entrySet().firstOrNull { it.key == date }?.value?.let {
            Volume.fromJsonObject(it.asJsonObject, this)
        }
    }

    // Fetch volume data for a range of dates
    
//------------Fiscal Period--------------
    // Fetch income statement data for a single year
    fun fetchIncomeStatementByYear(ticker: String, year: Int): IncomeStatement? {
        val json = fetchJsonFromApi("INCOME_STATEMENT", ticker) ?: return null
        val annualReportsJson = json.getAsJsonArray("annualReports") ?: return null
        return annualReportsJson.mapNotNull {
            val reportYear = it.asJsonObject.get("fiscalDateEnding").asString.substring(0, 4).toInt()
            if (reportYear == year) {
                IncomeStatement.fromJsonObject(it.asJsonObject, this)
            } else null
        }.firstOrNull()
    }

    // Fetch balance sheet data for a single year
    fun fetchBalanceSheetByYear(ticker: String, year: Int): BalanceSheet? {
        val json = fetchJsonFromApi("BALANCE_SHEET", ticker) ?: return null
        val annualReportsJson = json.getAsJsonArray("annualReports") ?: return null
        return annualReportsJson.mapNotNull {
            val reportYear = it.asJsonObject.get("fiscalDateEnding").asString.substring(0, 4).toInt()
            if (reportYear == year) {
                BalanceSheet.fromJsonObject(it.asJsonObject, this)
            } else null
        }.firstOrNull()
    }

    // Fetch dividends data for a single year
    fun fetchDividendByYear(ticker: String, year: Int): List<Dividend> {
        val json = fetchJsonFromApi("DIVIDENDS", ticker) ?: return emptyList()
        val annualReportsJson = json.getAsJsonArray("data") ?: return emptyList()
        return annualReportsJson.mapNotNull {
            val reportYear = it.asJsonObject.get("ex_dividend_date").asString.substring(0, 4).toInt()
            if (reportYear == year) {
                Dividend.fromJsonObject(it.asJsonObject, this)
            } else null
        }
    }

    // Fetch income statement data for a range of years
    fun getIncomeStatements(ticker: String, startYear: Int, endYear: Int): List<IncomeStatement> {
        val incomeStatements = mutableListOf<IncomeStatement>()
        for (year in startYear..endYear) {
            fetchIncomeStatementByYear(ticker, year)?.let { incomeStatements.add(it) }
        }
        return incomeStatements
    }

    // Fetch balance sheet data for a range of years
    fun getBalanceSheets(ticker: String, startYear: Int, endYear: Int): List<BalanceSheet> {
        val balanceSheets = mutableListOf<BalanceSheet>()
        for (year in startYear..endYear) {
            fetchBalanceSheetByYear(ticker, year)?.let { balanceSheets.add(it) }
        }
        return balanceSheets
    }

    // Fetch dividends data for a range of years
    fun getDividends(ticker: String, startYear: Int, endYear: Int): List<Dividend> {
        val dividends = mutableListOf<Dividend>()
        for (year in startYear..endYear) {
            dividends.addAll(fetchDividendByYear(ticker, year))
        }
        return dividends
    }
}
