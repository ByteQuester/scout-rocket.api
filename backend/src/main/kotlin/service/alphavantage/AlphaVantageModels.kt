package service.alphavantage

import com.google.gson.JsonObject


data class CompanyOverview(
    val Symbol: String,
    val AssetType: String,
    val Name: String,
    val Description: String,
    val CIK: String,
    val Exchange: String,
    val Currency: String,
    val Country: String,
    val Sector: String,
    val Industry: String,
    val Address: String,
    val FiscalYearEnd: String,
    val LatestQuarter: String,
    val MarketCapitalization: Long?,
    val EBITDA: Double?,
    val PERatio: Double?,
    val PEGRatio: Double?,
    val BookValue: Double?,
    val DividendPerShare: Double?,
    val DividendYield: Double?,
    val EPS: Double?,
    val RevenuePerShareTTM: Double?,
    val ProfitMargin: Double?,
    val OperatingMarginTTM: Double?,
    val ReturnOnAssetsTTM: Double?,
    val ReturnOnEquityTTM: Double?,
    val RevenueTTM: Long?,
    val GrossProfitTTM: Long?,
    val DilutedEPSTTM: Double?,
    val QuarterlyEarningsGrowthYOY: Double?,
    val QuarterlyRevenueGrowthYOY: Double?,
    val AnalystTargetPrice: Double?,
    val TrailingPE: Double?,
    val ForwardPE: Double?,
    val PriceToSalesRatioTTM: Double?,
    val PriceToBookRatio: Double?,
    val EVToRevenue: Double?,
    val EVToEBITDA: Double?,
    val Beta: Double?,
    val _52WeekHigh: Double?,
    val _52WeekLow: Double?,
    val _50DayMovingAverage: Double?,
    val _200DayMovingAverage: Double?,
    val SharesOutstanding: Long?,
    val DividendDate: String,
    val ExDividendDate: String
) {
    companion object {
        fun fromJsonObject(json: JsonObject, service: AlphaVantageService): CompanyOverview? {
            return try {
                CompanyOverview(
                    Symbol = json["Symbol"].asString,
                    AssetType = json["AssetType"].asString,
                    Name = json["Name"].asString,
                    Description = json["Description"].asString,
                    CIK = json["CIK"].asString,
                    Exchange = json["Exchange"].asString,
                    Currency = json["Currency"].asString,
                    Country = json["Country"].asString,
                    Sector = json["Sector"].asString,
                    Industry = json["Industry"].asString,
                    Address = json["Address"].asString,
                    FiscalYearEnd = json["FiscalYearEnd"].asString,
                    LatestQuarter = json["LatestQuarter"].asString,
                    MarketCapitalization = service.safeGetAsLong(json["MarketCapitalization"]),
                    EBITDA = service.safeGetAsDouble(json["EBITDA"]),
                    PERatio = service.safeGetAsDouble(json["PERatio"]),
                    PEGRatio = service.safeGetAsDouble(json["PEGRatio"]),
                    BookValue = service.safeGetAsDouble(json["BookValue"]),
                    DividendPerShare = service.safeGetAsDouble(json["DividendPerShare"]),
                    DividendYield = service.safeGetAsDouble(json["DividendYield"]),
                    EPS = service.safeGetAsDouble(json["EPS"]),
                    RevenuePerShareTTM = service.safeGetAsDouble(json["RevenuePerShareTTM"]),
                    ProfitMargin = service.safeGetAsDouble(json["ProfitMargin"]),
                    OperatingMarginTTM = service.safeGetAsDouble(json["OperatingMarginTTM"]),
                    ReturnOnAssetsTTM = service.safeGetAsDouble(json["ReturnOnAssetsTTM"]),
                    ReturnOnEquityTTM = service.safeGetAsDouble(json["ReturnOnEquityTTM"]),
                    RevenueTTM = service.safeGetAsLong(json["RevenueTTM"]),
                    GrossProfitTTM = service.safeGetAsLong(json["GrossProfitTTM"]),
                    DilutedEPSTTM = service.safeGetAsDouble(json["DilutedEPSTTM"]),
                    QuarterlyEarningsGrowthYOY = service.safeGetAsDouble(json["QuarterlyEarningsGrowthYOY"]),
                    QuarterlyRevenueGrowthYOY = service.safeGetAsDouble(json["QuarterlyRevenueGrowthYOY"]),
                    AnalystTargetPrice = service.safeGetAsDouble(json["AnalystTargetPrice"]),
                    TrailingPE = service.safeGetAsDouble(json["TrailingPE"]),
                    ForwardPE = service.safeGetAsDouble(json["ForwardPE"]),
                    PriceToSalesRatioTTM = service.safeGetAsDouble(json["PriceToSalesRatioTTM"]),
                    PriceToBookRatio = service.safeGetAsDouble(json["PriceToBookRatio"]),
                    EVToRevenue = service.safeGetAsDouble(json["EVToRevenue"]),
                    EVToEBITDA = service.safeGetAsDouble(json["EVToEBITDA"]),
                    Beta = service.safeGetAsDouble(json["Beta"]),
                    _52WeekHigh = service.safeGetAsDouble(json["_52WeekHigh"]),
                    _52WeekLow = service.safeGetAsDouble(json["_52WeekLow"]),
                    _50DayMovingAverage = service.safeGetAsDouble(json["_50DayMovingAverage"]),
                    _200DayMovingAverage = service.safeGetAsDouble(json["_200DayMovingAverage"]),
                    SharesOutstanding = service.safeGetAsLong(json["SharesOutstanding"]),
                    DividendDate = json["DividendDate"].asString,
                    ExDividendDate = json["ExDividendDate"].asString
                )
            } catch (e: Exception) {
                service.logger.error("Error parsing company overview data: ${e.message}")
                null
            }
        }
    }
}


data class IncomeStatement(
    val fiscalDateEnding: String,
    val reportedCurrency: String,
    val grossProfit: Long?,
    val totalRevenue: Long?,
    val costOfRevenue: Long?,
    val costofGoodsAndServicesSold: Long?,
    val operatingIncome: Long?,
    val sellingGeneralAndAdministrative: Long?,
    val researchAndDevelopment: Long?,
    val operatingExpenses: Long?,
    val investmentIncomeNet: String?,
    val netInterestIncome: Long?,
    val interestIncome: Long?,
    val interestExpense: Long?,
    val nonInterestIncome: Long?,
    val otherNonOperatingIncome: Long?,
    val depreciation: Long?,
    val depreciationAndAmortization: Long?,
    val incomeBeforeTax: Long?,
    val incomeTaxExpense: Long?,
    val interestAndDebtExpense: Long?,
    val netIncomeFromContinuingOperations: Long?,
    val comprehensiveIncomeNetOfTax: Long?,
    val ebit: Long?,
    val ebitda: Long?,
    val netIncome: Long?
) {
    companion object {
        fun fromJsonObject(json: JsonObject, service: AlphaVantageService): IncomeStatement {
            return IncomeStatement(
                fiscalDateEnding = json["fiscalDateEnding"].asString,
                reportedCurrency = json["reportedCurrency"].asString,
                grossProfit = service.safeGetAsLong(json["grossProfit"]),
                totalRevenue = service.safeGetAsLong(json["totalRevenue"]),
                costOfRevenue = service.safeGetAsLong(json["costOfRevenue"]),
                costofGoodsAndServicesSold = service.safeGetAsLong(json["costofGoodsAndServicesSold"]),
                operatingIncome = service.safeGetAsLong(json["operatingIncome"]),
                sellingGeneralAndAdministrative = service.safeGetAsLong(json["sellingGeneralAndAdministrative"]),
                researchAndDevelopment = service.safeGetAsLong(json["researchAndDevelopment"]),
                operatingExpenses = service.safeGetAsLong(json["operatingExpenses"]),
                investmentIncomeNet = json["investmentIncomeNet"].asString,
                netInterestIncome = service.safeGetAsLong(json["netInterestIncome"]),
                interestIncome = service.safeGetAsLong(json["interestIncome"]),
                interestExpense = service.safeGetAsLong(json["interestExpense"]),
                nonInterestIncome = service.safeGetAsLong(json["nonInterestIncome"]),
                otherNonOperatingIncome = service.safeGetAsLong(json["otherNonOperatingIncome"]),
                depreciation = service.safeGetAsLong(json["depreciation"]),
                depreciationAndAmortization = service.safeGetAsLong(json["depreciationAndAmortization"]),
                incomeBeforeTax = service.safeGetAsLong(json["incomeBeforeTax"]),
                incomeTaxExpense = service.safeGetAsLong(json["incomeTaxExpense"]),
                interestAndDebtExpense = service.safeGetAsLong(json["interestAndDebtExpense"]),
                netIncomeFromContinuingOperations = service.safeGetAsLong(json["netIncomeFromContinuingOperations"]),
                comprehensiveIncomeNetOfTax = service.safeGetAsLong(json["comprehensiveIncomeNetOfTax"]),
                ebit = service.safeGetAsLong(json["ebit"]),
                ebitda = service.safeGetAsLong(json["ebitda"]),
                netIncome = service.safeGetAsLong(json["netIncome"])
            )
        }
    }
}


data class BalanceSheet(
    val fiscalDateEnding: String,
    val reportedCurrency: String,
    val totalAssets: Long?,
    val totalCurrentAssets: Long?,
    val cashAndCashEquivalentsAtCarryingValue: Long?,
    val cashAndShortTermInvestments: Long?,
    val inventory: Long?,
    val currentNetReceivables: Long?,
    val totalNonCurrentAssets: Long?,
    val propertyPlantEquipment: Long?,
    val intangibleAssets: Long?,
    val intangibleAssetsExcludingGoodwill: Long?,
    val goodwill: Long?,
    val investments: Long?,
    val longTermInvestments: Long?,
    val shortTermInvestments: Long?,
    val otherCurrentAssets: Long?,
    val totalLiabilities: Long?,
    val totalCurrentLiabilities: Long?,
    val currentAccountsPayable: Long?,
    val deferredRevenue: Long?,
    val currentDebt: Long?,
    val shortTermDebt: Long?,
    val totalNonCurrentLiabilities: Long?,
    val capitalLeaseObligations: Long?,
    val longTermDebt: Long?,
    val currentLongTermDebt: Long?,
    val longTermDebtNoncurrent: Long?,
    val shortLongTermDebtTotal: Long?,
    val otherCurrentLiabilities: Long?,
    val otherNonCurrentLiabilities: Long?,
    val totalShareholderEquity: Long?,
    val treasuryStock: Long?,
    val retainedEarnings: Long?,
    val commonStock: Long?,
    val commonStockSharesOutstanding: Long?
) {
    companion object {
        fun fromJsonObject(json: JsonObject, service: AlphaVantageService): BalanceSheet {
            return BalanceSheet(
                fiscalDateEnding = json["fiscalDateEnding"].asString,
                reportedCurrency = json["reportedCurrency"].asString,
                totalAssets = service.safeGetAsLong(json["totalAssets"]),
                totalCurrentAssets = service.safeGetAsLong(json["totalCurrentAssets"]),
                cashAndCashEquivalentsAtCarryingValue = service.safeGetAsLong(json["cashAndCashEquivalentsAtCarryingValue"]),
                cashAndShortTermInvestments = service.safeGetAsLong(json["cashAndShortTermInvestments"]),
                inventory = service.safeGetAsLong(json["inventory"]),
                currentNetReceivables = service.safeGetAsLong(json["currentNetReceivables"]),
                totalNonCurrentAssets = service.safeGetAsLong(json["totalNonCurrentAssets"]),
                propertyPlantEquipment = service.safeGetAsLong(json["propertyPlantEquipment"]),
                intangibleAssets = service.safeGetAsLong(json["intangibleAssets"]),
                intangibleAssetsExcludingGoodwill = service.safeGetAsLong(json["intangibleAssetsExcludingGoodwill"]),
                goodwill = service.safeGetAsLong(json["goodwill"]),
                investments = service.safeGetAsLong(json["investments"]),
                longTermInvestments = service.safeGetAsLong(json["longTermInvestments"]),
                shortTermInvestments = service.safeGetAsLong(json["shortTermInvestments"]),
                otherCurrentAssets = service.safeGetAsLong(json["otherCurrentAssets"]),
                totalLiabilities = service.safeGetAsLong(json["totalLiabilities"]),
                totalCurrentLiabilities = service.safeGetAsLong(json["totalCurrentLiabilities"]),
                currentAccountsPayable = service.safeGetAsLong(json["currentAccountsPayable"]),
                deferredRevenue = service.safeGetAsLong(json["deferredRevenue"]),
                currentDebt = service.safeGetAsLong(json["currentDebt"]),
                shortTermDebt = service.safeGetAsLong(json["shortTermDebt"]),
                totalNonCurrentLiabilities = service.safeGetAsLong(json["totalNonCurrentLiabilities"]),
                capitalLeaseObligations = service.safeGetAsLong(json["capitalLeaseObligations"]),
                longTermDebt = service.safeGetAsLong(json["longTermDebt"]),
                currentLongTermDebt = service.safeGetAsLong(json["currentLongTermDebt"]),
                longTermDebtNoncurrent = service.safeGetAsLong(json["longTermDebtNoncurrent"]),
                shortLongTermDebtTotal = service.safeGetAsLong(json["shortLongTermDebtTotal"]),
                otherCurrentLiabilities = service.safeGetAsLong(json["otherCurrentLiabilities"]),
                otherNonCurrentLiabilities = service.safeGetAsLong(json["otherNonCurrentLiabilities"]),
                totalShareholderEquity = service.safeGetAsLong(json["totalShareholderEquity"]),
                treasuryStock = service.safeGetAsLong(json["treasuryStock"]),
                retainedEarnings = service.safeGetAsLong(json["retainedEarnings"]),
                commonStock = service.safeGetAsLong(json["commonStock"]),
                commonStockSharesOutstanding = service.safeGetAsLong(json["commonStockSharesOutstanding"])
            )
        }
    }
}


data class Dividend(
    val amount: Double,
    val payment_date: String,
    val declaration_date: String,
    val record_date: String,
    val ex_dividend_date: String
) {
    companion object {
        fun fromJsonObject(json: JsonObject, service: AlphaVantageService): Dividend? {
            return try {
                Dividend(
                    amount = service.safeGetAsDouble(json["amount"]) ?: 0.0, // Default to 0.0 if amount is null
                    payment_date = json["payment_date"].asString,
                    declaration_date = json["declaration_date"].asString,
                    record_date = json["record_date"].asString,
                    ex_dividend_date = json["ex_dividend_date"].asString
                )
            } catch (e: Exception) {
                service.logger.error("Error parsing dividend data: ${e.message}")
                null
            }
        }
    }
}
