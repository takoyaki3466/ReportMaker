plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "org.takoyaki"
version = "1.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("org.takoyaki.reportmaker")
    mainClass.set("org.takoyaki.reportmaker.ReportMakerApplication")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

var baseName = "レポート！"

jlink {

    options.set(
        listOf(
            "--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"
        )
    )

    launcher {
        name = baseName
    }

    jpackage {

        imageName = baseName

        installerName = baseName

        appVersion = version.toString()

        icon = "src/main/resources/icon.ico"

        installerType = "exe"

        installerOptions = listOf(
            "--win-menu", "--win-shortcut", "--win-dir-chooser"
        )
    }
}

