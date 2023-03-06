plugins {
    java
    id("org.springframework.boot") version "2.7.9"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.diffplug.spotless") version "6.15.0"
    id("io.freefair.lombok") version "6.6.2"
    id("jacoco")
}

group = "com.wesang"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

springBoot {
    buildInfo()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

sourceSets {
    main {
        java {
            srcDirs(arrayOf("${projectDir}/src/main/java", "${projectDir}/build/generated"))
        }
    }
}

spotless {
    val excludeFiles = arrayOf(".idea/**/*.*", ".vscode/**/*.*")

    java {
        targetExclude("build/**/*.java")
        removeUnusedImports()
        replaceRegex("Remove wildcard imports", "import\\s+[^\\*\\s]+\\*;(\\r\\n|\\r|\\n)", "$1")
        googleJavaFormat()
        importOrder(
            "java",
            "jakarta",
            "javax",
            "lombok",
            "org.springframework",
            "",
            "\\#",
            "org.junit",
            "\\#org.junit",
            "com.wesang",
            "\\#com.wesang"
        )
        indentWithTabs(2)
        indentWithSpaces(2)
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("yaml") {
        target("**/*.yaml", "**/*.yml")
        targetExclude(*excludeFiles)
        prettier().configFile(".prettierrc")
    }
    format("xml") {
        target("**/*.xml")
        targetExclude(*excludeFiles)
        prettier().config(mapOf("parser" to "html", "printWidth" to 160)).configFile(".prettierrc")
    }
    format("md") {
        target("**/*.md")
        targetExclude(*excludeFiles)
        prettier().config(mapOf("printWidth" to 160)).configFile(".prettierrc")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.projectreactor.tools:blockhound:1.0.6.RELEASE")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.14")
    implementation("com.datadoghq:dd-java-agent:1.8.3")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    // For M1 Mac
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.76.Final:osx-aarch_64")
}


tasks.bootJar {
    dependsOn(copyDependency)
}

tasks.test {
    useJUnitPlatform()
    systemProperties["spring.profiles.active"] = "test"
}

tasks.jacocoTestReport {
    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.00".toBigDecimal()
            }
            excludes = listOf(
                "com.wesang.Application",
                "com.wesang.common.error.CommonErrorHandler"
            )
        }
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"
    dependsOn(":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}

val testAll by tasks.registering {
    group = "verification"
    description = "Runs all tests"
    dependsOn(":spotlessCheck", ":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    tasks["test"].mustRunAfter(tasks["spotlessCheck"])
    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}

val copyDependency by tasks.registering(Copy::class) {
    from(configurations.compileClasspath.get().filter {
        it.name.contains("dd-java-agent")
    })
    into("$buildDir/libs")
}