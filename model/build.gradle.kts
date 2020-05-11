plugins {
  kotlin("jvm")
}

dependencies {
  implementation(platform(project(":platform")))

  implementation(kotlin("stdlib-jdk8"))

  compileOnly("com.google.code.findbugs:jsr305")
  compileOnly("jakarta.validation:jakarta.validation-api")
  compileOnly("org.jboss.spec.javax.xml.bind:jboss-jaxb-api_2.3_spec")
  compileOnly("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.1_spec")
  compileOnly("org.eclipse.microprofile.rest.client:microprofile-rest-client-api")
}
