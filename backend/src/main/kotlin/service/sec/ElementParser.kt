//TODO: Make changes to all the tags with : and replace with |

package service.sec

import service.common.LoggerSetup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ElementParser {
    //private val logger = LoggerFactory.getLogger(ElementParser::class.java)
    val logger = LoggerSetup.logger
    fun parseElement(soup: Element, element: Element, checkIsSubEntity: Boolean = true): Map<String, Any?> {
        val logger = LoggerSetup.logger
        val elementName = element.tagName().split("|").last().lowercase()
        try {
            if (!element.hasAttr("contextref")) {
                //logger.warn("Element $elementName has no contextref")
                logger.warning("Element $elementName has no contextref")
                return emptyMap()
            }

            val contextElement = soup.getElementById(element.attr("contextref"))
            if (checkIsSubEntity && contextElement?.getElementsByTag("xbrli|segment")?.isNotEmpty() == true) {
                logger.info("Element $elementName has a sub entity")
                return emptyMap()
            }

            val elementDict = mutableMapOf<String, Any?>()
            //elementDict["name"] = element.tagName().split(":").last().lowercase()
            elementDict["name"] = element.tagName().split("|").last().lowercase()
            logger.info("Checking for $element in element $elementName")
            elementDict["value"] = cleanValue(element.text())
            elementDict["unit"] = retrieveUnit(soup, element)
            elementDict["date"] = retrieveDate(soup, element)

            if (elementDict["value"] == "") {
                elementDict["value"] = retrieveFromContext(soup, element.attr("contextref"))
            }

            if (elementDict["unit"] != "NA" && elementDict["value"] is String ) {
                elementDict["value"] = cleanValue(elementDict["value"] as String)
            }

            if (element.hasAttr("sign") && element.attr("sign") == "-") {
                elementDict["value"] = -(elementDict["value"] as Double)
            }

            return elementDict
        } catch (e: Exception) {
            //logger.error("Error parsing element '$elementName': ${e.message}")
            logger.severe("Exception parsing $elementName")
            return emptyMap()

        }
    }

    fun cleanValue(string: String): Any {
        if (string.trim() == "-") {
            return 0.0
        }

        return try {
            string.trim().replace(",", "").replace(" ", "").toDouble()
        } catch (e: Exception) {
            string
        }
    }

    fun retrieveUnit(soup: Element, element: Element): String {
        return try {
            soup.getElementById(element.attr("unitref"))?.text()?.trim() ?: element.attr("unitref")
        } catch (e: Exception) {
            "NA"
        }
    }

    fun retrieveDate(soup: Element, element: Element): String {
        val dateTagList = listOf("xbrli|enddate", "xbrli|instant", "xbrli|period", "enddate", "instant", "period")

        val contextElement = soup.getElementById(element.attr("contextref"))
        logger.info("Context element: $contextElement")

        // Find the period element within the context element
        val periodElement = contextElement?.selectFirst("xbrli|period")
        logger.info("Period element: $periodElement")

        if (periodElement != null) {
            dateTagList.forEach { tag ->
                val dateElement = periodElement.selectFirst(tag) // Select the first matching date tag under the period element
                logger.info("Date element: $dateElement")

                if (dateElement != null) {
                    val dateVal = dateElement.text().trim()
                    logger.info("Found date value '$dateVal' for tag '$tag'")

                    if (dateVal.isNotEmpty()) {
                        return try {
                            LocalDate.parse(dateVal, DateTimeFormatter.ISO_DATE).toString()
                        } catch (e: Exception) {
                            logger.severe("Error parsing date value '$dateVal' for tag '$tag': ${e.message}")
                            "NA"
                        }
                    }
                }
            }
        }

        // If no date found under the period element, try parsing the contextref as a date
        return try {
            LocalDate.parse(element.attr("contextref"), DateTimeFormatter.ISO_DATE).toString()
        } catch (e: Exception) {
            logger.severe("Error parsing contextref as date: ${e.message}")
            "NA"
        }
    }



    fun retrieveFromContext(soup: Element, contextRef: String): String {
        return try {
            val context = soup.getElementsByTag("xbrli|context").first { it.id() == contextRef }
            logger.info("context being retrieved: $context")
            context.getElementsByTag("xbrldi|explicitmember").text().split("|").last().trim()
        } catch (e: Exception) {
            ""
        }
    }
}
