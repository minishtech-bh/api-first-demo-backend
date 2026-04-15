import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.10.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    // swagger-annotations는 springdoc이 포함 — 별도 선언 제거
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// ──────────────────────────────────────────
// openapi-generator 공통 설정 헬퍼
// ──────────────────────────────────────────
val specDir = "$rootDir/../contract/tsp-output/@typespec/openapi3"
val generatedDir = "${layout.buildDirectory.get()}/generated"
val commonConfigOptions = mapOf(
    "interfaceOnly"        to "true",
    "useSpringBoot3"       to "true",
    "useJakartaEe"         to "true",
    "skipDefaultInterface" to "true",
    "useTags"              to "true",
    "openApiNullable"      to "false",
)

// ──────────────────────────────────────────
// openApiGenerate 태스크 x3
// TypeSpec audience별 yaml → Spring 인터페이스 + 모델 DTO 빌드타임 생성
// ──────────────────────────────────────────

// 기본 태스크 (plugin이 자동 등록) — App B2C
openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$specDir/TodoDemo.PointManagement.App.yaml")
    outputDir.set(generatedDir)
    apiPackage.set("com.example.todo.api")
    modelPackage.set("com.example.todo.model")
    configOptions.set(commonConfigOptions)
}

// Admin — 제휴사 (Partner)
val openApiGenerateAdminPartner = tasks.register<GenerateTask>("openApiGenerateAdminPartner") {
    generatorName.set("spring")
    inputSpec.set("$specDir/TodoDemo.PointManagement.Admin.Partner.yaml")
    outputDir.set(generatedDir)
    apiPackage.set("com.example.todo.api")
    modelPackage.set("com.example.todo.model")
    configOptions.set(commonConfigOptions)
}

// Admin — 플랫폼
val openApiGenerateAdminPlatform = tasks.register<GenerateTask>("openApiGenerateAdminPlatform") {
    generatorName.set("spring")
    inputSpec.set("$specDir/TodoDemo.PointManagement.Admin.Platform.yaml")
    outputDir.set(generatedDir)
    apiPackage.set("com.example.todo.api")
    modelPackage.set("com.example.todo.model")
    configOptions.set(commonConfigOptions)
}

sourceSets {
    main {
        java {
            srcDir("${layout.buildDirectory.get()}/generated/src/main/java")
        }
    }
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate, openApiGenerateAdminPartner, openApiGenerateAdminPlatform)
}

// ──────────────────────────────────────────
// copyOpenApiSpec: 3개 yaml → 정적 리소스로 복사
// Swagger UI 드롭다운에서 각 audience별 스펙 서빙
// ──────────────────────────────────────────
val copyOpenApiSpec = tasks.register<Copy>("copyOpenApiSpec") {
    from(specDir) {
        include(
            "TodoDemo.PointManagement.App.yaml",
            "TodoDemo.PointManagement.Admin.Partner.yaml",
            "TodoDemo.PointManagement.Admin.Platform.yaml",
        )
    }
    into(layout.buildDirectory.dir("resources/main/static"))
}

tasks.processResources {
    dependsOn(copyOpenApiSpec)
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}
