plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.najaf.bubblevpn"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.najaf.bubblevpn"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES", "META-INF/LICENSE", "META-INF/LICENSE.txt",
            "META-INF/NOTICE", "META-INF/NOTICE.txt", "META-INF/INDEX.LIST",
            "META-INF/*.kotlin_module"
        )
    }
}

dependencies {
    implementation("org.apache.ftpserver:ftpserver-core:1.1.1")
    implementation("org.slf4j:slf4j-android:1.7.21")
}
