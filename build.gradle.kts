plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"

    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "de.tuchemnitz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Version {
    const val ASSERTK = "0.25"
    const val JACKSON = "2.14.1"
    const val JUNIT = "5.9.1"
    const val KMONGO = "4.8.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test-junit5"))

    implementation("com.fasterxml.jackson.core:jackson-databind:${Version.JACKSON}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Version.JACKSON}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Version.JACKSON}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Version.JUNIT}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Version.JUNIT}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Version.JUNIT}")

    testImplementation("com.willowtreeapps.assertk:assertk:${Version.ASSERTK}")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
tasks {
    "wrapper"(Wrapper::class) {
        gradleVersion = "7.6"
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xjsr305=strict"
            )
            jvmTarget = "17"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}