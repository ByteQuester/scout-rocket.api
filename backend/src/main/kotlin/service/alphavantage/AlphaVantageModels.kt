package service.alphavantage


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
    val EBITDA: Double?, //nullable to be able to make use of convertStringToDouble
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
    val _52WeekHigh: Double?, // Underscore prefix for 52WeekHigh
    val _52WeekLow: Double?,  // Underscore prefix for 52WeekLow
    val _50DayMovingAverage: Double?, // Underscore prefix for 50DayMovingAverage
    val _200DayMovingAverage: Double?, // Underscore prefix for 200DayMovingAverage
    val SharesOutstanding: Long?,
    val DividendDate: String,
    val ExDividendDate: String
)


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
    val netIncome: Long?,
)


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
    val accumulatedDepreciationAmortizationPPE: String,
    val intangibleAssets: Long?,
    val intangibleAssetsExcludingGoodwill: Long?,
    val goodwill: Long?,
    val investments: Long?,
    val longTermInvestments: Long?,
    val shortTermInvestments: Long?,
    val otherCurrentAssets: Long?,
    val otherNonCurrentAssets: String,
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
)


data class Dividend(
    val symbol: String,
    val amount: Double,
    val paymentDate: String,
    val declarationDate: String,
    val recordDate: String,
    val exDividendDate: String
)


