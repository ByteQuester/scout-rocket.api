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

    fun getTickerPrice(ticker: String, date: LocalDate): Double {
        if (!dataAccess.tickerPrice.isTickerPriceExists(ticker)) {
            fetchTickerPriceVolume(ticker)
        }
        return dataAccess.tickerPrice.getPrice(ticker, date)
    }

    fun getTickerVolume(ticker: String, date: LocalDate): Double {
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


    fun getTickerFinancials(ticker: String, startYear: Int, endYear: Int): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        // modifications to add information we can get from the both ends with SEC overriding
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
