plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.dogfinder"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.dogfinder"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    /*dependecias agregadas por mi*/
    implementation("androidx.navigation:navigation-compose:2.7.7")// se usa para mover entre pantallas
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // hacer solicitudes HTTP
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") //convertir automáticamente JSON en objetos Kotlin
    implementation("io.coil-kt:coil-compose:2.6.0") // cargar imágenes desde internet en aplicaciones Android ya que compose no puede mostrar imagenes direcamente
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")//guardar y manejar los datos de la UI
    implementation("androidx.room:room-runtime:2.6.1")// guardar datos localmente en el celular.
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.compose.material:material-icons-extended")//iconos de material
    implementation("androidx.datastore:datastore-preferences:1.1.1") // Preferencias persistentes

    /*dependencia para los test*/
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") //Permite probar código que usa viewModelScope.
    testImplementation("io.mockk:mockk:1.13.9") // Sirve para crear "fakes" de tu API (simular respuestas sin ir a internet).
    testImplementation("androidx.arch.core:core-testing:2.2.0") //  Ayuda a que las tareas instantáneas se ejecuten en orden


}