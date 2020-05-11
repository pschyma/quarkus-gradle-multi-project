plugins {
  kotlin("jvm")
  kotlin("plugin.allopen")

  id("io.quarkus")

  jacoco
}

val quarkusPlatformGroupId: String by ext
val quarkusPlatformArtifactId: String by ext
val quarkusPlatformVersion: String by ext

dependencies {
  implementation(platform(project(":platform")))
  implementation(project(":model"))

  implementation(kotlin("stdlib-jdk8"))

  implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-resteasy")
  implementation("io.quarkus:quarkus-resteasy-jackson")
  implementation("io.quarkus:quarkus-smallrye-openapi")
  implementation("io.quarkus:quarkus-smallrye-fault-tolerance")
  implementation("io.quarkus:quarkus-smallrye-health")
  implementation("io.quarkus:quarkus-smallrye-metrics")
  implementation("io.quarkus:quarkus-smallrye-context-propagation")
  implementation("io.quarkus:quarkus-jdbc-postgresql")
  implementation("io.quarkus:quarkus-flyway")
  implementation("io.quarkus:quarkus-hibernate-validator")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.zalando:problem")
  implementation("org.zalando:jackson-datatype-problem")

  implementation(platform("org.jdbi:jdbi3-bom:3.13.0"))
  implementation("org.jdbi:jdbi3-core")
  implementation("org.jdbi:jdbi3-kotlin")
  implementation("org.jdbi:jdbi3-kotlin-sqlobject")
  implementation("org.jdbi:jdbi3-postgres")

  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("org.assertj:assertj-core")
  testImplementation("io.rest-assured:rest-assured:4.3.0")
  testImplementation("io.rest-assured:kotlin-extensions:4.3.0")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

  testImplementation("org.testcontainers:testcontainers:1.14.1")
  testImplementation("org.testcontainers:postgresql:1.14.1")
  testImplementation("org.testcontainers:junit-jupiter:1.14.1")
}

allOpen {
  annotation("javax.enterprise.context.ApplicationScoped")
  annotation("javax.ws.rs.Path")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

val outputDir = file("$projectDir/build/classes/kotlin/main")

quarkus {
  setOutputDirectory(outputDir.absolutePath)
}

tasks {
  test {
    useJUnitPlatform()

    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED")

    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")

    reports {
      junitXml.isEnabled = true
      html.isEnabled = true
    }
  }

  quarkusDev {
    setSourceDir("$projectDir/src/main/kotlin")
  }

  create("instrument") {
    dependsOn(classes)
    finalizedBy(test)

    val jacocoAnt by configurations
    val originalClasses = file("$outputDir-orig")

    doLast {
      originalClasses.deleteRecursively()
      outputDir.renameTo(originalClasses)

      ant.withGroovyBuilder {
        "taskdef"("name" to "instrument", "classname" to "org.jacoco.ant.InstrumentTask", "classpath" to jacocoAnt.asPath)

        "instrument"("destDir" to outputDir) {
          "fileset"("dir" to originalClasses)
        }
      }
    }
  }

  create("restore") {
    dependsOn("instrument")

    val originalClasses = file("$outputDir-orig")

    doLast {
      outputDir.deleteRecursively()
      originalClasses.renameTo(outputDir)
    }
  }

  jacocoTestReport {
    dependsOn("instrument", test, "restore")
  }

  jacocoTestCoverageVerification {
    dependsOn("instrument", test, "restore")

    violationRules {
      rule {
        element = "CLASS"
        limit {
          counter = "METHOD"
          value = "MISSEDCOUNT"
          maximum = "0".toBigDecimal()
        }
      }
      rule {
        element = "METHOD"
        limit {
          counter = "LINE"
          value = "COVEREDRATIO"
          minimum = "1".toBigDecimal()
        }
        limit {
          counter = "BRANCH"
          value = "COVEREDRATIO"
          minimum = "1".toBigDecimal()
        }
        limit {
          counter = "INSTRUCTION"
          value = "COVEREDRATIO"
          minimum = "1".toBigDecimal()
        }
        limit {
          counter = "COMPLEXITY"
          value = "TOTALCOUNT"
          maximum = "12".toBigDecimal()
        }
      }
    }
  }
}

gradle.taskGraph.whenReady {
  val instrument = tasks.named("instrument").get()

  if (hasTask(instrument)) {
    val originalClasses = file("$outputDir-orig")

    tasks.test.configure {
      classpath = layout.files(originalClasses, classpath.minus(outputDir))
    }
  }
}
