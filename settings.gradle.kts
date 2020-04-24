rootProject.name = "root"

pluginManagement {
  val quarkusPluginVersion: String by settings

	plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.3.72")
    id("com.github.ben-manes.versions") version ("0.28.0")
    id("io.quarkus") version (quarkusPluginVersion)
  }
}

include("platform")
include("model")
include("service")
