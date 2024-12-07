import java.time.LocalDateTime
import java.time.ZoneId

val logbackVersion: String by project
val kotlinLoggingVersion: String by project

val ktorSimpleCacheVersion: String by project

val exposedVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project
val mySQLVersion: String by project
val sqliteVersion: String by project

val swaggerParserVersion: String by project
val schemaKeneratorVersion: String by project
val swaggerUIVersion: String by project

val awsS3Version: String by project

val commonsIOVersion: String by project
val commonsCodecVersion: String by project

val mockkVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
}

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
            password = env.fetchOrNull("GITHUB_PAT") ?: System.getenv("PAT")
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
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-rate-limit")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")

    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")
    implementation("io.github.smiley4:schema-kenerator-core:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-reflection:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-swagger:$schemaKeneratorVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:$swaggerUIVersion")

    implementation("io.sakurasou.ktor:ktor-simple-cache-jvm:0.4.7")
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

    implementation("commons-io:commons-io:$commonsIOVersion")
    implementation("commons-codec:commons-codec:$commonsCodecVersion")
    implementation("at.favre.lib:bcrypt:0.10.2")

    implementation("net.coobird:thumbnailator:0.4.20")
    implementation("org.sejda.imageio:webp-imageio:0.1.6")

    implementation("software.amazon.awssdk:s3:$awsS3Version")

    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
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

tasks.register("updateVersion") {
    doLast {
        val yamlFile = file("src/main/resources/application.yaml")
        val contents = yamlFile.readText()
        val regex = Regex("^version:\\s*.+$", RegexOption.MULTILINE)
        val newContents = contents.replace(regex, "version: $version")
        yamlFile.writeText(newContents)
    }
}

tasks.register("generateBuildRecord") {
    group = "build"
    doLast {
        val buildTime = LocalDateTime.now(ZoneId.of("UTC")).toString()
        val commitId = getCheckedOutGitCommitHash()
        val buildRecord = """
            buildTime=$buildTime
            commitId=$commitId
            version=$version
        """.trimIndent()
        val file = file("src/main/resources/buildRecord.properties")
        file.writeText(buildRecord)
    }
}

tasks.named("processResources") {
    dependsOn("copyFrontendBuildResults", "updateVersion", "generateBuildRecord")
}

tasks.register<Delete>("cleanStaticResources") {
    val backendStaticResourceDir = "$projectDir\\src\\main\\resources\\static"
    delete(backendStaticResourceDir)
}

tasks.named("clean") {
    dependsOn("cleanStaticResources")
}

// https://gist.github.com/JonasGroeger/7620911
private fun getCheckedOutGitCommitHash(): String {
    val gitFolder = "${rootProject.projectDir}/.git"
    val takeFromHash = 12

    // '.git/HEAD' contains either
    //      in case of detached head: the currently checked out commit hash
    //      otherwise: a reference to a file containing the current commit hash
    val head = File(gitFolder, "HEAD").readText().split(":") // .git/HEAD
    val isCommit = head.size == 1 // e5a7c79edabbf7dd39888442df081b1c9d8e88fd
    // val isRef = head.size > 1     // ref: refs/heads/master

    return if (isCommit) {
        head[0].trim().take(takeFromHash) // e5a7c79edabb
    } else {
        val refHead = File(gitFolder, head[1].trim()) // .git/refs/heads/master
        refHead.readText().trim().take(takeFromHash)
    }
}
