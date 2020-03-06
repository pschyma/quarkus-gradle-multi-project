rootProject.name = "root"

pluginManagement {
	plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.3.70")
    id("com.github.ben-manes.versions") version ("0.28.0")
  }
}

include("platform")
include("model")
include("service")
