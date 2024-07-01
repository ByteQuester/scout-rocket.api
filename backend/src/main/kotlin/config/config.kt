package config

object Config {
    val REDIS_HOST_NAME: String = System.getenv("my-redis") ?: "localhost"
    const val REDIS_PORT: Int = 6379

    val DB_HOST_NAME: String = System.getenv("my-mysql") ?: "localhost"
    const val DB_USER: String = "root"
    const val DB_PASSWORD: String = "mysqlR0cks!"
    const val DB_NAME: String = "findb"
}
