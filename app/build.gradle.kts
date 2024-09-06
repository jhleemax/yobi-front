plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.yobi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yobi"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true;
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("de.hdodenhof:circleimageview:3.1.0") //Circle ImageView
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.lombok) // 롬복
    implementation("com.google.android.gms:play-services-auth:20.7.0") // 구글 로그인
    implementation ("com.navercorp.nid:oauth:5.9.1") // 네이버 로그인
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}