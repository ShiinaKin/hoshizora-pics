val logbackVersion: String by project
val exposedVersion: String by project
val ktorSimpleCacheVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project
val mySQLVersion: String by project
val sqliteVersion: String by project
val mockkVersion: String by project
val version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "2.3.12"
}

group = "io.sakurasou"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName = "hoshizora-pics-$version.jar"
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/ShiinaKin/ktor-simple-cache")
        credentials {
            username = "ShiinaKin"
            password = env.fetchOrNull("GITHUB_PAT") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-resources-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-auto-head-response-jvm")
    implementation("io.ktor:ktor-server-caching-headers-jvm")
    implementation("io.ktor:ktor-server-compression-jvm")
    implementation("io.ktor:ktor-server-conditional-headers-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-forwarded-header-jvm")

    implementation("io.swagger.parser.v3:swagger-parser:2.1.21")
    implementation("io.github.smiley4:ktor-swagger-ui:3.5.0")

    // implementation("com.ucasoft.ktor:ktor-simple-cache-jvm:$ktorSimpleCacheVersion")
    implementation("io.sakurasou.simple-cache:ktor-simple-cache-jvm:0.4.5")
    implementation("com.ucasoft.ktor:ktor-simple-memory-cache-jvm:$ktorSimpleCacheVersion")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache-jvm:$ktorSimpleCacheVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.mysql:mysql-connector-j:$mySQLVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")

    implementation("commons-codec:commons-codec:1.17.1")
    implementation("at.favre.lib:bcrypt:0.10.2")

    implementation("net.coobird:thumbnailator:0.4.20")
    implementation("org.sejda.imageio:webp-imageio:0.1.6")

    implementation("software.amazon.awssdk:s3:2.28.21")

    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.mockk:mockk:${mockkVersion}")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("io.ktor:ktor-client-content-negotiation")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.register<Copy>("copyFrontendBuildResults") {
    dependsOn(project(":ui").tasks.named("build"))
    val frontendBuildDir = "${project(":ui").projectDir}\\dist"
    val backendStaticResourceDir = "$projectDir\\src\\main\\resources\\static"
    from(frontendBuildDir)
    include("**/*")
    into(backendStaticResourceDir)
}

tasks.named("processResources") {
    dependsOn("copyFrontendBuildResults")
}

tasks.register<Delete>("cleanStaticResources") {
    val backendStaticResourceDir = "$projectDir\\src\\main\\resources\\static"
    delete(backendStaticResourceDir)
}

tasks.named("clean") {
    dependsOn("cleanStaticResources")
}