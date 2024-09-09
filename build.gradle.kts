tasks.register("build") {
    group = "build"
    dependsOn(":app:build")
}

tasks.register("clean") {
    group = "build"
    dependsOn(":app:clean", ":ui:clean")
}