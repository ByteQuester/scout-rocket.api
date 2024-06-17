from enum import Enum
from collections import namedtuple
from typing import Dict


class Period(Enum):
    Year = 1
    Quarter = 2


class Statements(Enum):
    Profile = 1
    Income = 2
    BalanceSheet = 3
    # CashFlow = 4
    # KeyMetrics = 5


Profile = namedtuple('Profile', ['mktCap', 'lastDiv', 'country', 'industry', 'currency', 'exchange'])

Income = namedtuple('Income', ['Date', 'Revenue', 'CostOfRevenue', 'GrossProfit', 'RnDExpenses',
                               'GAExpense', 'SaMExpense', 'OperatingExpenses',
                               'OperatingIncome', 'InterestExpense',
                               'NetIncome', 'EBITDA', 'EBITratio'])

BalanceSheet = namedtuple('BalanceSheet',
                          ['Date', 'CashAndCashEquivalents', 'ShortTermInvestments', 'Receivables',
                           'PropertyPlantAndEquipmentNet', 'GoodwillAndIntangibleAssets',
                           'LongTermInvestments', 'TaxAssets', 'TotalNonCurrentAssets', 'TotalAssets', 'Payables',
                           'ShortTermDebt', 'TotalDebt', 'TotalLiabilities',
                           'DeferredRevenue', 'NetDebt'])


# CashFlow = namedtuple('CashFlow', ['Date'])

# KeyMetrics = namedtuple('KeyMetrics', ['Date', 'MarketCap', 'Dividend'])

TickerData = namedtuple('TickerData', ['profile', 'income_list', 'balance_sheet_list'])

SUPPORTED_STOCK_EXCHANGES = ['NASDAQ Capital Market', 'NASDAQ Global Market', 'NYSE', 'NYSE American', 'NYSE Arca',
                             'NYSEArca', 'Nasdaq', 'Nasdaq Global Select', 'NasdaqGM', 'NasdaqGS',
                             'New York Stock Exchange']




def dict2profile(d: Dict) -> Profile:
    return Profile(
        mktCap=d.get('MarketCap'),
        lastDiv=d.get('lastDiv'),
        country=d.get('country'),
        industry=d.get('industry'),
        currency=d.get('currency'),
        exchange=d.get('exchangeShortName')
    )
### we can get all the marked one in here form the following:
#https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo
## with sample of the ifno:
"""	
Symbol	"IBM"
AssetType	"Common Stock"
Name	"International Business Machines"
Description	"International Business Machines Corporation (IBM) is an American multinational technology company headquartered in Armonk, New York, with operations in over 170 countries. The company began in 1911, founded in Endicott, New York, as the Computing-Tabulating-Recording Company (CTR) and was renamed International Business Machines in 1924. IBM is incorporated in New York. IBM produces and sells computer hardware, middleware and software, and provides hosting and consulting services in areas ranging from mainframe computers to nanotechnology. IBM is also a major research organization, holding the record for most annual U.S. patents generated by a business (as of 2020) for 28 consecutive years. Inventions by IBM include the automated teller machine (ATM), the floppy disk, the hard disk drive, the magnetic stripe card, the relational database, the SQL programming language, the UPC barcode, and dynamic random-access memory (DRAM). The IBM mainframe, exemplified by the System/360, was the dominant computing platform during the 1960s and 1970s."
CIK	"51143"
Exchange	"NYSE"
Currency	"USD"
Country	"USA"
Sector	"TECHNOLOGY"
Industry	"COMPUTER & OFFICE EQUIPMENT"
Address	"1 NEW ORCHARD ROAD, ARMONK, NY, US"
FiscalYearEnd	"December"
LatestQuarter	"2024-03-31"
MarketCapitalization	"155354137000"
EBITDA	"14380000000"
PERatio	"19.15"
PEGRatio	"4.2"
BookValue	"25.32"
DividendPerShare	"6.64"
DividendYield	"0.0395"
EPS	"8.83"
RevenuePerShareTTM	"67.94"
ProfitMargin	"0.132"
OperatingMarginTTM	"0.102"
ReturnOnAssetsTTM	"0.0458"
ReturnOnEquityTTM	"0.362"
RevenueTTM	"62068998000"
GrossProfitTTM	"32688000000"
DilutedEPSTTM	"8.83"
QuarterlyEarningsGrowthYOY	"0.701"
QuarterlyRevenueGrowthYOY	"0.015"
AnalystTargetPrice	"167.46"
AnalystRatingStrongBuy	"4"
AnalystRatingBuy	"4"
AnalystRatingHold	"10"
AnalystRatingSell	"2"
AnalystRatingStrongSell	"0"
TrailingPE	"19.15"
ForwardPE	"18.12"
PriceToSalesRatioTTM	"2.701"
PriceToBookRatio	"7.42"
EVToRevenue	"3.453"
EVToEBITDA	"14.54"
Beta	"0.718"
52WeekHigh	"197.22"
52WeekLow	"123.91"
50DayMovingAverage	"173.16"
200DayMovingAverage	"166.2"
SharesOutstanding	"918603000"
DividendDate	"2024-06-10"
ExDividendDate	"2024-05-09""""


def dict2income(d: Dict) -> Income:
    return Income(
        Date=d.get('date'),
        Revenue=d.get('Revenue'),
        CostOfRevenue=None,
        GrossProfit=d.get('GrossProfit'),
        RnDExpenses=d.get('RndExpenses'),
        GAExpense=None,
        SaMExpense=d.get('AdminExpenses'),
        OperatingExpenses=d.get('OperatingExpenses'),
        OperatingIncome=(float(d.get('GrossProfit')) - float(d.get('OperatingExpenses'))
                         if d.get('GrossProfit') and d.get('OperatingExpenses') else 0),
        InterestExpense=None,
        NetIncome=d.get('NetIncome'),
        EBITDA=None,
        EBITratio=None
    )

### expect the EBITratio=None, we already have the SEC i orde r to get these infroamtion



## as for the following:
## when I access the https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo
## there are some remarks that I will put in from of the metrics
## if there's no remarks, it means that the metric is already presen tin the endpint
def dict2balance_sheet(d: Dict) -> BalanceSheet:
    return BalanceSheet(
        Date=d.get('date'),
        CashAndCashEquivalents=d.get('cashAndCashEquivalents'), # I found `cashAndCashEquivalentsAtCarryingValue` in the both sec and the mentioend endpoitn
        ShortTermInvestments=d.get('shortTermInvestments'),
        Receivables=d.get('netReceivables'),
        # Inventories=d.get('Inventories', ''),
        # TotalCurrentAssets=d.get('Total current assets'),
        PropertyPlantAndEquipmentNet=d.get('propertyPlantEquipmentNet'), # i found the metric in the sec `PropertyPlantAndEquipmentNet` and in the endpoint as tyou can see there's the `propertyPlantEquipment` so whicih one to pick?
        GoodwillAndIntangibleAssets=d.get('goodwillAndIntangibleAssets'), # i found intangibleAssetsExcludingGoodwill in the endpint of api, are they the same?
        LongTermInvestments=d.get('longTermInvestments'), #6
        TaxAssets=d.get('taxAssets'),#7(8) # as for this one, I don't really find anything exact from both ends. meang that ther are some that are maybe respresting the similar name in a different string you know , bit can you observeer if this metric is realy includedi n the alpha endpoint? maybe other name?
        TotalNonCurrentAssets=d.get('totalNonCurrentAssets'),#9
        TotalAssets=d.get('Assets'),
        Payables=d.get('accountPayables'), # there's a 'currentAccountsPayable' in the alpha endpint , but there's a `AccountsPayable` in the sec so idk what to pick now
        ShortTermDebt=d.get('shortTermDebt'),
        # LongTermDebt=d.get('Long-term debt'),
        TotalLiabilities=d.get('Liabilities'),
        TotalDebt=d.get('totalDebt'), # as for this one, I can't find a exact match in neither of the endpoints. can you maybe observe the realtive metric inthe aloha endpoint?
        DeferredRevenue=d.get('deferredRevenue'),
        NetDebt=d.get('netDebt'), # same debt mark for this onne as the totaldebt
    )

""" here's a smaple from the endpoint
	
symbol	"IBM"
annualReports	
0	
fiscalDateEnding	"2023-12-31"
reportedCurrency	"USD"
totalAssets	"135241000000"
totalCurrentAssets	"32908000000"
cashAndCashEquivalentsAtCarryingValue	"13068000000"
cashAndShortTermInvestments	"13068000000"
inventory	"1161000000"
currentNetReceivables	"7725000000"
totalNonCurrentAssets	"101302000000"
propertyPlantEquipment	"-472000000"
accumulatedDepreciationAmortizationPPE	"None"
intangibleAssets	"71214000000"
intangibleAssetsExcludingGoodwill	"11036000000"
goodwill	"60178000000"
investments	"125000000"
longTermInvestments	"125000000"
shortTermInvestments	"373000000"
otherCurrentAssets	"2219000000"
otherNonCurrentAssets	"None"
totalLiabilities	"112628000000"
totalCurrentLiabilities	"34122000000"
currentAccountsPayable	"4132000000"
deferredRevenue	"16984000000"
currentDebt	"12851000000"
shortTermDebt	"6426000000"
totalNonCurrentLiabilities	"87072000000"
capitalLeaseObligations	"379000000"
longTermDebt	"54588000000"
currentLongTermDebt	"6304000000"
longTermDebtNoncurrent	"50121000000"
shortLongTermDebtTotal	"120630000000"
otherCurrentLiabilities	"9292000000"
otherNonCurrentLiabilities	"11475000000"
totalShareholderEquity	"22533000000"
treasuryStock	"169624000000"
retainedEarnings	"151276000000"
commonStock	"59643000000"
commonStockSharesOutstanding	"915013646"
1	{…}
2	{…}
"""


