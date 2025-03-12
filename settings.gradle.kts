plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "crud-javalin"
include("src:main:jar")
findProject(":src:main:jar")?.name = "jar"
