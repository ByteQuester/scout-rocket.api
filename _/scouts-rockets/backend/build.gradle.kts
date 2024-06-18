plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.21"

}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation("redis.clients:jedis:5.1.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.1.2")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("org.json:json:20210307")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("io.github.mainstringargs.alphavantagescraper:alpha-vantage-scraper:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("com.google.code.gson:gson:2.11.0")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
    testImplementation("io.mockk:mockk:1.13.11")
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}