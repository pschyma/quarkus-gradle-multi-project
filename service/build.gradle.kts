import io.quarkus.gradle.tasks.QuarkusTestConfig

plugins {
  id("io.quarkus") version ("1.3.0.CR1")
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val jacoco by configurations.registering
val jacocoRuntime by configurations.registering

dependencies {
  implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
  implementation(platform(project(":platform")))

  compileOnly("com.google.code.findbugs:jsr305:3.0.2")

  implementation(project(":model"))

  implementation("io.quarkus:quarkus-resteasy")
  implementation("io.quarkus:quarkus-resteasy-jsonb")
  implementation("io.quarkus:quarkus-smallrye-openapi")
  implementation("io.quarkus:quarkus-smallrye-fault-tolerance")
  implementation("io.quarkus:quarkus-smallrye-health")
  implementation("io.quarkus:quarkus-smallrye-metrics")
  implementation("io.quarkus:quarkus-smallrye-context-propagation")
  implementation("io.quarkus:quarkus-jdbc-postgresql")
  implementation("io.quarkus:quarkus-hibernate-orm")
  implementation("io.quarkus:quarkus-hibernate-validator")

  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("io.quarkus:quarkus-test-h2")
  testImplementation("org.assertj:assertj-core")
  testImplementation("io.rest-assured:rest-assured:4.2.0")

  "jacoco"("org.jacoco:org.jacoco.ant:0.8.5:nodeps")
  "jacocoRuntime"("org.jacoco:org.jacoco.agent:0.8.5:runtime")
}

version = "1.0.0-SNAPSHOT"

tasks {
  withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
  }

  create("instrument") {
    dependsOn(classes)

    val main by sourceSets
    val mainJavaOutputDir = main.java.outputDir
    val instrumentedDirectory = layout.buildDirectory.dir("$mainJavaOutputDir-instrumented").get()

    doLast {
      ant.withGroovyBuilder {
        instrumentedDirectory.asFile.deleteRecursively()

        "taskdef"("name" to "instrument", "classname" to "org.jacoco.ant.InstrumentTask", "classpath" to jacoco.get().asPath)

        "instrument"("destDir" to instrumentedDirectory.asFile) {
          "fileset"("dir" to mainJavaOutputDir)
        }
      }
    }

    outputs.dirs(instrumentedDirectory)
  }

  create("report") {
    dependsOn("instrument", test)

    val main by sourceSets

    doLast {
      ant.withGroovyBuilder {
        "taskdef"("name" to "report", "classname" to "org.jacoco.ant.ReportTask", "classpath" to jacoco.get().asPath)

        "report"() {
          "executiondata" {
            "file"("file" to "$buildDir/jacoco/tests.exec")
          }
          "structure"("name" to "Example") {
            "classfiles" {
              main.output.classesDirs.onEach {
                "fileset"("dir" to it)
              }
            }
            "sourcefiles" {
              "fileset"("dir" to "src/main/java")
            }
          }
          "html"("destdir" to "$buildDir/reports/jacoco")
        }
      }
    }
  }

  test {
    useJUnitPlatform()

    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    jvmArgs("--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED")
  }

  create<JacocoCoverageVerification>("verify") {
    dependsOn("instrument", test)

    val main by sourceSets

    jacocoClasspath = layout.files(jacoco)

    executionData.from("$buildDir/jacoco/tests.exec")
    allClassDirs.minus(main.java.outputDir)
    allClassDirs.plus("${main.java.outputDir}-instrumented")

    violationRules {
      rule {
        limit {
          counter = "LINE"
          value = "COVEREDRATIO"
          minimum = "1.0".toBigDecimal()
        }
      }
      rule {
        element = "BUNDLE"
        excludes = listOf("*Test")
        limit {
          counter = "CLASS"
          value = "MISSEDCOUNT"
          maximum = "0".toBigDecimal()
        }
      }
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
          maximum = "5".toBigDecimal()
        }
      }
    }
  }
}

gradle.taskGraph.whenReady {
  val instrument = tasks.named("instrument").get()
  if (hasTask(instrument)) {
    val main by sourceSets

    tasks {
      withType<QuarkusTestConfig>() {
        finalizedBy(instrument)
      }

      withType<Test>() {
        doFirst {
          systemProperty("jacoco-agent.destfile", buildDir.path + "/jacoco/tests.exec")
          classpath = layout.files(instrument.outputs.files, classpath, jacocoRuntime)
          classpath.minus(main.java.outputDir)
        }
      }
    }
  }
}
