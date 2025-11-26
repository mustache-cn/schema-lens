plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "cn.com.mustache.plugin"
version = "1.0.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("DB", "2024.3")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("com.intellij.database")
        implementation(kotlin("stdlib"))
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
    
    // 发布配置（可选，如果需要发布到 JetBrains Marketplace）
    // 需要在 gradle.properties 中配置 PUBLISH_TOKEN 或通过环境变量设置
    publishing {
        token.set(providers.gradleProperty("PUBLISH_TOKEN").orElse(providers.environmentVariable("PUBLISH_TOKEN")))
        channels.set(listOf("stable"))
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
