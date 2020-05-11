rootProject.name = "root"

pluginManagement {
  val quarkusPluginVersion: String by settings
  val kotlinVersion: String by settings

  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }

	plugins {
    id("org.jetbrains.kotlin.jvm") version (kotlinVersion)
    id("org.jetbrains.kotlin.plugin.allopen") version (kotlinVersion)
    id("com.github.ben-manes.versions") version ("0.28.0")
    id("io.quarkus") version (quarkusPluginVersion)
  }
}

include("platform")
include("model")
include("service")
include("cli")
include("service-client")
