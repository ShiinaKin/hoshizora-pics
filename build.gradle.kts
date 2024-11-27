plugins {
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "io.sakurasou"

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