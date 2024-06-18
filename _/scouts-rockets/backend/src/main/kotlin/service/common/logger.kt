package service.common

import java.io.IOException
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object LoggerSetup {
    val logger: Logger = Logger.getLogger(LoggerSetup::class.java.name)

    init {
        try {
            val fileHandler = FileHandler("app.log", true) // Append to the log file
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
            logger.level = Level.ALL
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
