plugins {
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "io.sakurasou"

tasks.register("updateVersion") {
    dependsOn(":app:updateVersion", "updateComposeImageTag")
}

tasks.register("updateComposeImageTag") {
    val composeFile = file("compose.yml")
    val contents = composeFile.readText()
    val regex = Regex("(shiinakin/hoshizora-pics:)(.*)")
    val newContents = contents.replace(regex) { matchResult ->
        "${matchResult.groupValues[1]}$version"
    }
    composeFile.writeText(newContents)
}

tasks.register("build") {
    group = "build"
    dependsOn(":app:build")
    finalizedBy("copyBuildResultToRoot")
}

tasks.register<Copy>("copyBuildResultToRoot") {
    from("${project(":app").projectDir}\\build\\libs")
    include("hoshizora-pics-*.jar")
    into("$projectDir/build")
}

tasks.register("clean") {
    group = "build"
    delete("$projectDir/build")
    dependsOn(":app:clean", ":ui:clean")
}