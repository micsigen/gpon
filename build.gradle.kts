plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "io.comunda.demo"
version = "0.0.1-SNAPSHOT"
val zeebeVersion = "8.3.4"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// https://mvnrepository.com/artifact/io.camunda.spring/spring-boot-starter-camunda
	implementation("io.camunda.spring:spring-boot-starter-camunda:8.5.16")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.camunda:spring-zeebe-test:8.4.0")

	testImplementation("org.testcontainers:testcontainers:1.19.7")
	testImplementation("org.testcontainers:junit-jupiter:1.19.7")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("org.assertj:assertj-core:3.25.3")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jib {
	from {
		image = "eclipse-temurin:21-jre-jammy"  // Modern Java 21 base image
	}
	to {
		image = "gpon-worker"
		tags = setOf("latest", version.toString())
	}
	container {
		ports = listOf("8080", "9000")
		jvmFlags = listOf(
			"-Xms512m",
			"-Xmx512m",
			"-XX:+UseContainerSupport",
			"-XX:MaxRAMPercentage=75"  // Container-aware memory settings
		)
		mainClass = "io.comunda.demo.gpon.worker.GponApplicationKt"
		environment = mapOf(
			"SPRING_PROFILES_ACTIVE" to "prod",
			"JAVA_TOOL_OPTIONS" to "-XX:+UseContainerSupport"
		)
		creationTime = "USE_CURRENT_TIMESTAMP"

		// Health check configuration
		labels.set(mapOf(
			"org.opencontainers.image.title" to "gpon-worker",
			"org.opencontainers.image.version" to version.toString(),
			"org.opencontainers.image.description" to "GPON Worker Service"
		))
	}
}