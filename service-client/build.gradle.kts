plugins {
  `java-library`
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(project(":model"))

  compileOnly("org.apiguardian:apiguardian-api")

  implementation("jakarta.enterprise:jakarta.enterprise.cdi-api")
  implementation("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.1_spec")
  implementation("org.eclipse.microprofile.rest.client:microprofile-rest-client-api")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation("org.zalando:problem")
  implementation("org.zalando:jackson-datatype-problem")
}
