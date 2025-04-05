import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.10"
    application
}

group = "mx.edu.uttt"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io") // Agregar este repositorio
}

dependencies {
    // Webserver, Data Logger
    implementation("io.javalin:javalin:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0-rc1")

    /* Vue 3, Vuetify 3 & Material Design */
    implementation("org.webjars.npm:vue:3.5.4")
    implementation("org.webjars.npm:vuetify:3.7.0")
    implementation("org.webjars.npm:mdi__font:7.4.47")
    implementation("org.webjars:font-awesome:6.5.2")
    implementation("org.webjars.npm:roboto-fontface:0.10.0")

    implementation("org.webjars.npm:sweetalert2:11.14.3")

    /* Connected to database */
    implementation("com.zaxxer:HikariCP:5.1.0") // Pool de conexiones
    implementation("mysql:mysql-connector-java:8.0.33") // Conector de MySQL
    implementation("com.github.seratch:kotliquery:1.9.0") // Kotlin-Query
    implementation("org.mindrot:jbcrypt:0.4")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Jar> {
    manifest {
        attributes("Main-Class" to "mx.edu.uttt.MainKt")
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies: ${it.name}") }
        .filter { it.name.endsWith("jar") }
        .map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/INDEX-LIST", "META-INF/*.SF","META-INF/*.DSA", "META-INF/*.RSA")
    }
    sourceSets.main.get()
        .allSource.forEach { println("add from sources: ${it.canonicalPath}") }
}

application {
    mainClass.set("mx.edu.uttt.MainKt")
}
