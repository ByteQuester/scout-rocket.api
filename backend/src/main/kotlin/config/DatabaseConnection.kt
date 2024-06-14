package config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object DatabaseConnection {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://${Config.DB_HOST_NAME}/${Config.DB_NAME}"
        username = Config.DB_USER
        password = Config.DB_PASSWORD
        driverClassName = "com.mysql.cj.jdbc.Driver"
        isAutoCommit = false
    }

    private val dataSource = HikariDataSource(hikariConfig)

    fun getConnection(): Connection {
        return dataSource.connection
    }
}
