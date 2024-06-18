Certainly! Here's a summary of the `sec` directory in a Markdown table format:

### Summary of the `sec` Directory

| Class | Description | Methods |
| --- | --- | --- |
| `ElementParser` | Parses elements from HTML documents | - `parseElement(soup: Element, element: Element, checkIsSubEntity: Boolean = true): Map<String, Any?>` <br> - `cleanValue(string: String): Any` <br> - `retrieveUnit(soup: Element, element: Element): String` <br> - `retrieveDate(soup: Element, element: Element): String` <br> - `retrieveFromContext(soup: Element, contextRef: String): String` |
| `FinancialDataRetriever` | Retrieves financial data from HTML documents | - `getFinancialData(soup: Document, ticker: String, year: Int)` <br> - `fetchCompanyData(ticker: String, year: Int)` <br> - `prepareIndex(year: Int, quarter: Int)` <br> - `getDataByKey(soup: Document, keywords: List<String>, docFilter: String): MutableMap<String, Any?>` <br> - `fetchCompanyDataInternal(ticker: String, year: Int, txtUrl: String)` <br> - `fetchTickerFinancialsByYear(year: Int, ticker: String?)` <br> - `fetchTickersList(): List<String>` |
| `SecGov` | Interacts with the SEC website to fetch financial data | - `fetchTickerFinancialsByYear(year: Int, ticker: String?)` <br> - `fetchTickersList(): List<String>` |

### Interrelation with Other Components

- **Integration with `DataAccess`:**
    - `FinancialDataRetriever` interacts with components from the `dataAccess` directory, such as `TickerInfo`, `TickerFinancials`, and `TickerPrice`, to store and retrieve financial data and ticker-related information.
- **Utilization of External Libraries:**
    - `Jsoup` library is used for parsing HTML documents to extract financial data.
- **Dependency on `SecGov`:**
    - The `SecGov` class interacts with the `FinancialDataRetriever` to retrieve financial data from the SEC website based on specified tickers and years.e

## Summary of subclasses

### ElementParser Detailed Table

| Method | Description | Parameters | Return Value | Error Handling | Notes |
|---|---|---|---|---|---|
| `parseElement(soup: Element, element: Element, checkIsSubEntity: Boolean = true)` | Extracts key financial information (name, value, unit, date) from an XBRL element. | `soup`: The JSoup document containing the XBRL data. <br> `element`: The specific XBRL element to parse. <br> `checkIsSubEntity`: Flag to indicate whether to check if the element is a sub-entity. (Default: true) | `Map<String, Any?>`: A map containing the parsed information: <br> - `name`: Element name (e.g., "revenue"). <br> - `value`: Element value (number or string). <br> - `unit`: Unit of the value (e.g., "USD"). <br> - `date`: Date associated with the value. | Returns an empty map if the element does not have a "contextref" attribute or if it's a sub-entity (when `checkIsSubEntity` is true). |  |
| `cleanValue(string: String)` | Cleans a string value by removing commas, spaces, and converting to a number if possible. | `string`: The string to clean. | `Any`: The cleaned value (either a number or the original string). | If the string cannot be converted to a number, it returns the original string. |  |
| `retrieveUnit(soup: Element, element: Element)` | Retrieves the unit of measurement for an XBRL element. | `soup`: The JSoup document. <br> `element`: The XBRL element. | `String`: The unit of measurement (e.g., "USD") or "NA" if not found. | Returns "NA" if the unit cannot be retrieved. |  |
| `retrieveDate(soup: Element, element: Element)` | Retrieves the date associated with an XBRL element. | `soup`: The JSoup document. <br> `element`: The XBRL element. | `String`: The date in ISO format (e.g., "2023-12-31") or "NA" if not found. | Returns "NA" if the date cannot be retrieved. |  |
| `retrieveFromContext(soup: Element, contextRef: String)` | Retrieves a value from the context of an XBRL element. | `soup`: The JSoup document. <br> `contextRef`: The ID of the context element. | `String`: The value found in the context or an empty string if not found. | Returns an empty string if the value cannot be retrieved. |  |

### FinancialDataRetriever Detailed Table

| Method | Description | Parameters | Return Value | Error Handling | Notes |
|---|---|---|---|---|---|
| `getFinancialData(soup: Document, ticker: String, year: Int)` | Extracts and stores financial data from an XBRL document for a specific ticker and year. | `soup`: The JSoup document representing the XBRL filing. <br> `ticker`: The company's ticker symbol. <br> `year`: The financial year. | None (void) | Prints error messages if required data is not found or if there are issues storing the data. |  |
| `getDataByKey(soup: Document, keywords: List<String>, docFilter: String)` | Finds an XBRL element in a document based on a list of keywords and a context filter. | `soup`: The JSoup document. <br> `keywords`: A list of keywords to search for in element names. <br> `docFilter`: A context filter to match against element attributes. | `MutableMap<String, Any?>`: A map containing the parsed data from the found element, or an empty map if not found. |  |  |
| `fetchCompanyData(ticker: String, year: Int)` | Fetches and processes company financial data for a specific ticker and year. | `ticker`: The company's ticker symbol. <br> `year`: The financial year. | None (void) | Prints error messages if the ticker URL is not found or if there are errors fetching or parsing the data. | Calls `fetchCompanyDataInternal` to do the actual fetching and parsing. |
| `fetchCompanyDataInternal(ticker: String, year: Int, txtUrl: String)` | (Private) Fetches and parses company financial data from a given URL. | `ticker`: The company's ticker symbol. <br> `year`: The financial year. <br> `txtUrl`: The URL of the XBRL filing. | None (void) | Prints error messages if there are errors fetching or parsing the data. | This is a private helper method called by `fetchCompanyData`. |
| `prepareIndex(year: Int, quarter: Int)` | Fetches and stores the SEC index data for a given year and quarter. | `year`: The year of the index. <br> `quarter`: The quarter of the index (1-4). | None (void) | Prints error messages if there are errors fetching or storing the index data. |  |


### SecGov Detailed Table

| Method | Description | Parameters | Return Value | Error Handling | Notes |
|---|---|---|---|---|---|
| `fetchTickerFinancialsByYear(year: Int, ticker: String?)` | Fetches and stores financial data for either a single ticker or all tickers in a given year. | `year`: The financial year. <br> `ticker`: The ticker symbol (optional). If not provided, data for all tickers is fetched. | None (void) | Prints error messages if the index file is not found or if there are errors fetching or storing data. |  |
| `fetchTickersList()` | Fetches the list of all tickers and their corresponding CIKs from the SEC website and stores them. | None | `List<String>`: A list of ticker symbols. | Retries up to 5 times if there are errors fetching the data. Prints error messages if fetching fails. | Uses a user agent header to identify the request. |
