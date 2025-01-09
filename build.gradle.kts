import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object VersionInfo {
    const val GROUP_NAME = "2match"
    const val BASE_NAME = "2match"
    const val VERSION = "1.0.1"
}

plugins {
    application
    kotlin("jvm") version "2.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    ImplementationDependencies().forEach {
        implementation("${it.key}:${it.value}")
    }

    TestImplementationDependencies().forEach {
        testImplementation("${it.key}:${it.value}")
    }
}

application {
    mainClass = "stringstranslator.MainKt"
}

tasks.withType(KotlinCompile::class.java).all {
    compilerOptions {
        jvmTarget = JVM_1_8
    }
}

tasks.register("fatJar", type = Jar::class) {
    group = VersionInfo.GROUP_NAME
    description = "Build a fat jar containing all runtime dependencies"
    archiveBaseName = VersionInfo.BASE_NAME
    version = VersionInfo.VERSION
    manifest {
        attributes["Main-Class"] = "com.miquido.stringstranslator.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    with(tasks["jar"] as CopySpec)
}

tasks.register("buildFatJarWithSampleConfig") {
    group = "2match"
    description = "Builds fat jar and copies sample properties file into ./2match-release directory"
    dependsOn("fatJar")
    doLast {
        copy {
            from("build/libs/${VersionInfo.BASE_NAME}-${VersionInfo.VERSION}.jar")
            into("2match-release/")
        }
        copy {
            from("strings_spreadsheet_SAMPLE.ods")
            into("2match-release/")
        }
        copy {
            from("config.properties_SAMPLE")
            into("2match-release/")
        }
    }
}

tasks.register("cleanReleaseDirectory", type = Delete::class) {
    group = "2match"
    description = "Cleans ./2match-release directory"
    delete("2match-release")
}

project.tasks.clean {
    dependsOn("cleanReleaseDirectory")
}

class ImplementationDependencies : HashMap<String, String>() {

    init {
        put("org.jetbrains.kotlin:kotlin-stdlib-jdk8", KOTLIN_STDLIB_VERSION)
        put("org.simpleframework:simple-xml", XML_PARSER_VERSION)
        put("org.apache.poi:poi-ooxml", XLS_PARSER_VERSION)
        put("com.xenomachina:kotlin-argparser", CLI_PARSER_VERSION)
        put("com.squareup.retrofit2:retrofit", RETROFIT_VERSION)
        put("io.insert-koin:koin-core", KOIN_VERSION)
        put("com.googlecode.plist:dd-plist", PLIST_VERSION)
        put("com.uchuhimo:kotlinx-bimap", BIMAP_VERSION)
        put("org.slf4j:slf4j-api", LOGGER_VERSION)
        put("org.slf4j:slf4j-simple", LOGGER_VERSION)
        put("org.slf4j:slf4j-nop", LOGGER_VERSION)
        put("com.google.code.gson:gson", GSON_VERSION)
    }

    companion object {
        const val RETROFIT_VERSION = "2.11.0"
        const val KOTLIN_STDLIB_VERSION = ""
        const val XML_PARSER_VERSION = "2.7.1"
        const val XLS_PARSER_VERSION = "5.4.0"
        const val CLI_PARSER_VERSION = "2.0.7"
        const val KOIN_VERSION = "4.0.1"
        const val PLIST_VERSION = "1.28"
        const val BIMAP_VERSION = "1.2"
        const val LOGGER_VERSION = "2.0.16"
        const val GSON_VERSION = "2.11.0"
    }
}

class TestImplementationDependencies : HashMap<String, String>() {
    init {
        put("org.jetbrains.kotlin:kotlin-test", KOTLIN_TEST_VERSION)
        put("org.jetbrains.kotlin:kotlin-test-junit", KOTLIN_TEST_JUNIT_VERSION)
        put("io.insert-koin:koin-test", KOIN_VERSION)
    }

    companion object {
        const val KOTLIN_TEST_VERSION = ""
        const val KOTLIN_TEST_JUNIT_VERSION = ""
        const val KOIN_VERSION = "4.0.1"
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    jvmTarget = JVM_1_8
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.compilerOptions {
    jvmTarget = JVM_1_8
}
