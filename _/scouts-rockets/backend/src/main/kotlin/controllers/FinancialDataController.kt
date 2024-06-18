package controllers

import service.services.DataServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.financialDataController(dataServices: DataServices) {
    route("/api") {
        get("/ticker-data/{ticker}/{startYear}/{endYear}") {
            val ticker = call.parameters["ticker"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ticker")
            val startYear = call.parameters["startYear"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid start year")
            val endYear = call.parameters["endYear"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid end year")

            val tickerData = dataServices.getTickerData(ticker, startYear, endYear)
            call.respond(tickerData)
        }
    }
}

