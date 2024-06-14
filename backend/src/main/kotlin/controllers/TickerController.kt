package controllers

import service.services.DataServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Route.tickerController(dataServices: DataServices) {
    route("/api") {
        get("/ticker-volume/{ticker}/{date}") {
            val ticker = call.parameters["ticker"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ticker")
            val dateStr = call.parameters["date"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing date")

            // Validate date format
            val date = try {
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
            } catch (e: Exception) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid date format. Use YYYY-MM-DD")
            }

            val volume = dataServices.getTickerVolume(ticker, date)
            call.respond(volume.toString())
        }
        get("/ticker-price/{ticker}/{date}") {
            val ticker = call.parameters["ticker"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ticker")
            val dateStr = call.parameters["date"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing date")

            // Validate date format
            val date = try {
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
            } catch (e: Exception) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid date format. Use YYYY-MM-DD")
            }

            val price = dataServices.getTickerPrice(ticker, date)
            call.respond(price.toString())
        }
    }
}
