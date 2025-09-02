import java.time.LocalDateTime
import java.time.ZoneId

val logbackVersion: String by project
val kotlinLoggingVersion: String by project

val lettuceVersion: String by project
val caffeineVersion: String by project
val kotlinCoroutineVersion: String by project

val exposedVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project
val mySQLVersion: String by project
val sqliteVersion: String by project

val swaggerParserVersion: String by project
val schemaKeneratorVersion: String by project
val swaggerUIVersion: String by project

val awsS3Version: String by project

val gsonVersion: String by project

val semver4jVersion: String by project

val commonsIOVersion: String by project
val commonsCodecVersion: String by project

val mockkVersion: String by project

plugins {
    kotlin("jvm") version "2.2.10"
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ktor)
}

application {
    mainClass = "io.sakurasou.hoshizora.ApplicationKt"
}

ktor {
    fatJar {
        archiveFileName = "hoshizora-pics-$version.jar"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.conditional.headers)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)

    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)

    implementation("io.swagger.parser.v3:swagger-parser:$swaggerParserVersion")
    implementation("io.github.smiley4:schema-kenerator-core:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-reflection:$schemaKeneratorVersion")
    implementation("io.github.smiley4:schema-kenerator-swagger:$schemaKeneratorVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:$swaggerUIVersion")

    implementation(libs.lettuce)
    implementation(libs.caffeine)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.json)
    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.postgresql)
    implementation(libs.mysql)
    implementation(libs.sqlite)
    implementation(libs.hikari)

    implementation(libs.common.io)
    implementation(libs.common.codec)
    implementation(libs.bcrypt)

    implementation(libs.img4j)

    implementation(libs.amazon.s3)

    implementation(libs.semver4j)

    implementation(libs.kotlin.logging)
    implementation(libs.logback.classic)

    testImplementation(libs.mockk)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.kotlin.test)
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    mergeServiceFiles()

    exclude(
        "ASL-2.0.txt",
        "custom.config.conf",
        "custom.config.yaml",
        "LICENSE",
        "LICENSE.txt",
        "LGPL-3.0.txt",
        "README",
        "INFO_BIN",
        "INFO_SRC",
        "sqlite-jdbc.properties",
        "VersionInfo.java",
        "draftv3",
        "draftv4",
        "google",
        "mozilla",
        "patches",
        "samples",
        "schemas",
    )

    from(rootProject.projectDir) {
        include("LICENSE")
    }
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
        val buildRecord =
            """
            buildTime=$buildTime
            commitId=$commitId
            version=$version
            """.trimIndent()
        val file = file("src/main/resources/buildRecord.properties")
        file.writeText(buildRecord)
    }
}

tasks.named("processResources") {
    dependsOn("copyFrontendBuildResults", "generateBuildRecord")
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
