import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("org.jetbrains.kotlin.jvm") apply (false)
  id("com.github.ben-manes.versions")

  base
}

allprojects {
  group = "com.example"

  repositories {
    mavenCentral()
    jcenter()
  }

  tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean {
      val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
      val regex = "^[0-9,.v-]+(-r)?$".toRegex()
      val isStable = stableKeyword || regex.matches(version)
      return isStable.not()
    }

    resolutionStrategy {
      componentSelection {
        all {
          if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
            reject("Release candidate")
          }
        }
      }
    }
  }
}

configure(subprojects.filter { it.name != "platform"}) {
  apply(plugin = "java")

  tasks {
    named<Javadoc>("javadoc") {
      if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
      }
    }
  }

  configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
  }
}
