tasks.register("build") {
    dependsOn(":app:build")
}

tasks.register("clean") {
    dependsOn(":app:clean", ":ui:clean")
}