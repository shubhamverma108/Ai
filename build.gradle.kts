plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/release") }
}

extra["springAiVersion"] = "1.0.0"

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")

	// Ollama model
	implementation("org.springframework.ai:spring-ai-starter-model-ollama")

	// PDF reader
	implementation("org.springframework.ai:spring-ai-pdf-document-reader")

	// Vector database
	implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

// JPA
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}