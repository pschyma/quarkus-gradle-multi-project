plugins {
  `java-platform`
}

dependencies {
  constraints {
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("org.apiguardian:apiguardian-api:1.1.0")

    api("org.assertj:assertj-core:3.16.1")
    api("jakarta.enterprise:jakarta.enterprise.cdi-api:2.0.2")
    api("jakarta.validation:jakarta.validation-api:2.0.2")
    api("org.jboss.spec.javax.xml.bind:jboss-jaxb-api_2.3_spec:2.0.0.Final")
    api("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.1_spec:2.0.1.Final")
    api("org.eclipse.microprofile.rest.client:microprofile-rest-client-api:1.4.1")

    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.0")
    api("org.zalando:problem:0.24.0")
    api("org.zalando:jackson-datatype-problem:0.24.0")

    api("org.mockito:mockito-core:3.3.1")
    api("org.mockito:mockito-junit-jupiter:3.3.1")
  }
}
