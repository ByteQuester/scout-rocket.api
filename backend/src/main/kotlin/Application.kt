//import controllers.financialDataController
//import controllers.tickerController
//import service.services.DataServices
//import io.ktor.server.engine.*
//import io.ktor.server.netty.*
//import io.ktor.server.routing.*
//
//fun main() {
//    val dataServices = DataServices() // Initialize your DataServices class
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0") { // Start the server
//        routing {
//            tickerController(dataServices)
//            financialDataController(dataServices)
//        }
//    }.start(wait = true)
//}
