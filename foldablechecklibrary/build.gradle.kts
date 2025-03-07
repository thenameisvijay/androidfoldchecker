import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.vjcodes.foldablechecklibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // Enable AAR output
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}


val gitHubProperties = Properties().apply {
    load(FileInputStream(rootProject.file("github.properties")))
}

fun getLibVersionName(): String {
    return gitHubProperties.getProperty("version.name")
}

fun getLibVersionCode(): String {
    return gitHubProperties.getProperty("version.code")
}

fun getLibArtifactId(): String {
    return gitHubProperties.getProperty("artifact.id")
}

fun getLibGroupId(): String {
    return gitHubProperties.getProperty("group.id")
}

fun getLibType(): String {
    return gitHubProperties.getProperty("lib.type")
}

fun getRepoURL(): String {
    return gitHubProperties.getProperty("repo.url")
}

fun getMavenURL(): String {
    return gitHubProperties.getProperty("maven.url")
}

fun getUserName(): String {
    return gitHubProperties.getProperty("gpr.usr")
}

fun getPAT(): String {
    return gitHubProperties.getProperty("gpr.key")
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            groupId = getLibGroupId()
            artifactId = getLibArtifactId()
            version = getLibVersionCode()

            //from(components["release"])

            afterEvaluate {
                artifact(tasks.named("bundleReleaseAar").get()) // Explicit dependency
            }

            pom {
                name.set("Foldable Check Library")
                description.set("A library to check foldable device states")
                url.set(getRepoURL())

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set(getUserName())
                        name.set("Vijay")
                        email.set("vg4029@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/thenameisvijay/androidfoldchecker.git")
                    developerConnection.set("scm:git:ssh://github.com/thenameisvijay/androidfoldchecker.git")
                    url.set(getRepoURL())
                }
            }
        }

    }

    repositories {
        maven {
            name = getLibType()
            url = uri(getMavenURL())
            credentials {
                username = getUserName()
                password = getPAT()
            }
        }
    }
}

// Ensure publishing depends on AAR bundling
tasks.named("publishGprPublicationToGitHubPackagesRepository") {
    dependsOn(tasks.named("bundleReleaseAar"))
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.window) //Jetpack WindowManager library
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}