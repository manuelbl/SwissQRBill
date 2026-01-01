plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.codecrete.qrbill:qrbill-generator:3.+")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "net.codecrete.qrbill.examples.kotlin.Example"
}


tasks.register<JavaExec>("execute") {
    group = "net.codecrete.qrbill.examples"
    description = "Runs the QR Bill example application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("net.codecrete.qrbill.examples.kotlin.Example")
}