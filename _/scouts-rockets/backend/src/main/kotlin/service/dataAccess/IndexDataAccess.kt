package service.dataAccess

import config.DatabaseConnection
import java.sql.Connection
//import org.slf4j.LoggerFactory
import service.common.LoggerSetup

class IndexDataAccess(private var dbConnection: Connection) {
    private val logger = LoggerSetup.logger

    fun storeIndex(data: List<String>, year: Int, filling: String) {
        val sql = "INSERT INTO `sec_idx` (`cik`, `year`, `company`, `report_type`, `url`) VALUES (?, ?, ?, ?, ?)"
        dbConnection.autoCommit = false
        try {
            dbConnection.prepareStatement(sql).use { stmt ->
                data.forEach { item ->
                    if (filling in item) {
                        val values = item.split('|')
                        logger.info("values are $values")
                        logger.fine("data is $data")
                        try {
                            stmt.setString(1, values[0])
                            stmt.setInt(2, year)
                            stmt.setString(3, values[1])
                            stmt.setString(4, values[2])
                            stmt.setString(5, values[4])
                            stmt.addBatch()
                        } catch (e: Exception) {
                            logger.severe("Failed to store index for item: $item {$e}")
                        }
                    }
                }
                stmt.executeBatch()
            }
            dbConnection.commit()
        } catch (e: Exception) {
            dbConnection.rollback()
            logger.severe("Transaction failed and rolled back {$e}")
        } finally {
            dbConnection.autoCommit = true
        }
    }

    fun isIndexStored(year: Int): Boolean {
        val sql = "SELECT COUNT(*) FROM sec_idx WHERE year = ?"
        return getValidConnection(dbConnection).use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, year)
                stmt.executeQuery().use { rs ->
                    rs.next() && rs.getInt(1) > 0
                }
            }
        }
    }

    fun getIndexRowByCik(cik: Int, year: Int): List<String>? {
        val sql = "SELECT company, url FROM sec_idx WHERE cik = ? AND year = ?"
        logger.info("Executing SQL: $sql in $year and Cik: $cik")

        return getValidConnection(dbConnection).use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, cik)
                stmt.setInt(2, year)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) {
                        logger.info("first column:${rs.getString(1)}, Second column: ${rs.getString(2)}")
                        listOf(rs.getString(1), rs.getString(2))  // Directly getting the results without named columns
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun getIndexByYear(year: Int): List<List<String>> {
        val sql = "SELECT company, url, cik FROM sec_idx WHERE year = ?"
        logger.info("Executing SQL: $sql")

         validateConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, year)
                stmt.executeQuery().use { rs ->
                    val result = mutableListOf<List<String>>()
                    while (rs.next()) {
                        result.add(
                            listOf(
                                rs.getString(1),
                                rs.getString(2),
                                rs.getString(3)
                            )
                        )  // Directly getting the results without named columns
                    }
                    return result
                }
            }
        }
    }

    private fun validateConnection(): Connection {
        return if (dbConnection.isClosed) {
            logger.info("Reopening closed connection")
            getValidConnection(dbConnection)
        } else {
            dbConnection
        }
    }
    private fun getValidConnection(currentConnection: Connection?): Connection {
        return if (currentConnection == null || currentConnection.isClosed) {
            DatabaseConnection.getConnection()
        } else {
            currentConnection
        }
    }
}
