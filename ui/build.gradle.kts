tasks.register<Delete>("cleanDist") {
    delete("${projectDir}\\dist")
}

tasks.register<Delete>("cleanNodeModules") {
    delete("${projectDir}\\node_modules")
}

tasks.register("clean") {
    dependsOn("cleanDist")
}