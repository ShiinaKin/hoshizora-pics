import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

val baseUrl = env.fetchOrNull("BASE_URL") ?: "/"

node {
    version = "20.18.2"
    workDir = file("$projectDir/.cache/node")
    download = true
    pnpmVersion = "10.5.2"
    pnpmWorkDir = file("$projectDir/.cache/pnpm")
}

tasks.register("replaceBaseURL") {
    dependsOn("pnpmInstall")
    doLast {
        val baseTS = file("$projectDir/packages/api-client/src/base.ts")
        val contents = baseTS.readText()
        val regex = Regex("""BASE_PATH = "(.*?)"""")
        val newContents = contents.replace(regex) {
            """BASE_PATH = "$baseUrl""""
        }
        baseTS.writeText(newContents)
    }
}

tasks.register<PnpmTask>("build-packages") {
    dependsOn("replaceBaseURL")
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