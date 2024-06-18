package config

object Config {
    val REDIS_HOST_NAME: String = System.getenv("REDIS_URL") ?: "localhost"
    const val REDIS_PORT: Int = 6379
    const val ASSETS_DIR: String = "./assets"

    val DB_HOST_NAME: String = System.getenv("MYSQL_URL") ?: "localhost"
    const val DB_USER: String = "root"
    const val DB_PASSWORD: String = "mysqlR0cks!"
    const val DB_NAME: String = "findb"
}
