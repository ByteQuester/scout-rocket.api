package service.services

import service.dataAccess.DataAccess
import service.sec.SecGov
//import kotlinx.coroutines.*
import java.time.LocalDate
import java.util.logging.Logger

class DataServices {

    private val secGov = SecGov()
    private val dataAccess = DataAccess()

    init {
        if (!dataAccess.tickerPrice.isTickerListExist()) {
            secGov.fetchTickersList()
        }
    }

    suspend fun getTickerPrice(ticker: String, date: LocalDate): Double {
        if (!dataAccess.tickerPrice.isTickerPriceExists(ticker)) {
            fetchTickerPriceVolume(ticker)
        }
        return dataAccess.tickerPrice.getPrice(ticker, date)
    }

    suspend fun getTickerVolume(ticker: String, date: LocalDate): Double {
        if (!dataAccess.tickerPrice.isTickerVolumeExists(ticker)) {
            fetchTickerPriceVolume(ticker)
        }
        return dataAccess.tickerPrice.getVolume(ticker, date)
    }

    companion object {
        @JvmStatic
        suspend fun fetchTickerPrices(ticker: String) {
            fetchTickerPriceVolume(ticker)
        }
    }

    fun fetchTickerList(): List<String> {
        return dataAccess.tickerPrice.getTickerList()
    }

    fun getTickerData(ticker: String, startYear: Int, endYear: Int): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        if (!dataAccess.tickerPrice.isTickerPriceExists(ticker)) {
            fetchTickerPriceVolume(ticker)
        }

        data["volume"] = mapOf("volume" to "NA")
        val startYearDateTime = LocalDate.of(startYear, 1, 1)
        val endYearDateTime = LocalDate.of(endYear, 12, 30)
        data["price"] = dataAccess.tickerPrice.getPrices(ticker, startYearDateTime, endYearDateTime) ?: emptyList<Pair<LocalDate, Double>>()

        for (year in startYear..endYear) {
            fetchTickerFinancialsByYear(year, ticker)
            val entry = dataAccess.tickerFinancials.getTickerFinancials(ticker, year)
            if (entry == null) {
                Logger.getGlobal().severe("Could not retrieve data for '$ticker $year'")
            }
            data[year.toString()] = entry ?: "No data"
        }

        return data
    }

    fun getTickerFinancials(ticker: String, startYear: Int, endYear: Int): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        for (year in startYear..endYear) {
            fetchTickerFinancialsByYear(year, ticker)
            val entry = dataAccess.tickerFinancials.getTickerFinancials(ticker, year)
            if (entry == null) {
                Logger.getGlobal().severe("Could not retrieve data for '$ticker $year'")
            }
            data[year.toString()] = entry ?: "No data"
        }

        return data
    }

    fun getTickerVolumes(ticker: String, start: LocalDate, end: LocalDate? = null): List<Pair<LocalDate, Double>> {
        // Implementation needed
        return emptyList()
    }

    fun fetchTickerFinancialsByYear(year: Int, ticker: String? = null) {
        if (ticker in listOf("spy", "qqq")) {
            return
        }

        if (ticker != null && dataAccess.tickerFinancials.isTickerStored(ticker, year)) {
            Logger.getGlobal().info("Data is already cached for $ticker $year")
            return
        }
        secGov.fetchTickerFinancialsByYear(year, ticker)
    }
}
