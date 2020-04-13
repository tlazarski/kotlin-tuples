import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec

buildscript {
    dependencies {
        classpath("com.squareup:kotlinpoet:1.5.0")
    }
}

plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.70"
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["kotlin"])
            artifact(sourcesJar.get())
        }
    }

    repositories {
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/tlazarski/kotlin-tuples")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            name = "Bintray"
            url = uri("https://api.bintray.com/maven/tlazarski/maven/kotlin-tuples/;publish=1")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("BINTRAY_TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation("org.assertj", "assertj-core", "3.15.0")
    testImplementation("junit", "junit", "4.12")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("generateTuples")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val tuplesDir = buildDir.resolve("generatedTuples")

kotlin.sourceSets {
    getByName("main").kotlin.srcDirs(tuplesDir.path)
}

tasks.register("generateTuples") {
    doLast {
        (2..20).forEach {
            createFileSpecForTuple(it).writeTo(tuplesDir)
        }
    }
}

fun createFileSpecForTuple(size: Int): FileSpec {
    val className = "Tuple$size"

    val constructorBuilder = FunSpec.constructorBuilder()
        .addParameters((1..size).map { ParameterSpec(createPropertyName(it), TypeVariableName(createTypeName(it))) })

    val classBuilder = TypeSpec.classBuilder(className)
        .addModifiers(KModifier.DATA)
        .addTypeVariables((1..size).map { TypeVariableName(createTypeName(it), KModifier.OUT) })
        .primaryConstructor(constructorBuilder.build())

    (1..size).forEach {
        classBuilder.addProperty(
            PropertySpec.builder(createPropertyName(it), TypeVariableName(createTypeName(it)))
                .initializer(createPropertyName(it))
                .build()
        )
    }

    return FileSpec.builder("io.tlazarski.kotlin.tuple", className)
        .addType(classBuilder.build())
        .build()
}

fun createPropertyName(n: Int) = "_$n"
fun createTypeName(n: Int) = "T$n"
