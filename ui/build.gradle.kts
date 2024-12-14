import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version = "20.18.1"
    workDir = file("${projectDir}/.cache/node")
    download = true
    pnpmVersion = "9.15.0"
    pnpmWorkDir = file("${projectDir}/.cache/pnpm")
}

tasks.register<PnpmTask>("build-packages") {
    dependsOn("pnpmInstall")
    args = listOf("build-packages")
}

tasks.register<PnpmTask>("build") {
    dependsOn("build-packages")
    args = listOf("build")
}

tasks.register<Delete>("cleanDist") {
    delete("${projectDir}\\dist")
}

tasks.register<Delete>("cleanNodeModules") {
    delete("${projectDir}\\node_modules")
}

tasks.register("clean") {
    dependsOn("cleanDist")
}