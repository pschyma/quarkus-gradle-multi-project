plugins {
  java

  id("io.quarkus")
}

val quarkusPlatformGroupId: String by ext
val quarkusPlatformArtifactId: String by ext
val quarkusPlatformVersion: String by ext

dependencies {
  implementation(platform(project(":platform")))
  implementation(project(":model"))
  implementation(project(":service-client"))

  implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
  implementation("io.quarkus:quarkus-config-yaml")
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-core")
  implementation("io.quarkus:quarkus-rest-client")
  implementation("io.quarkus:quarkus-rest-client-jackson")
  implementation("io.quarkus:quarkus-jackson")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  compileOnly("org.apiguardian:apiguardian-api")
  implementation("com.google.code.findbugs:jsr305")

  implementation("org.apache.poi:poi-ooxml:4.1.2")

  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.mockito:mockito-junit-jupiter")
  testImplementation("org.assertj:assertj-core")

  implementation("org.zalando:problem")
  implementation("org.zalando:jackson-datatype-problem")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}
