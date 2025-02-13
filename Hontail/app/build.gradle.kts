plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Kotlin Annotation
//    id ("org.jetbrains.kotlin.kapt")
    id ("com.google.devtools.ksp")
}

android {
    namespace = "com.hontail"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hontail"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //framework ktx dependency 추가
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Material Components
    implementation("com.google.android.material:material:1.9.0")

    // Google Cloud Vision & ML Kit
    implementation("com.google.cloud:google-cloud-vision:3.27.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:16.1.3")

    // gRPC 의존성 추가
    implementation("io.grpc:grpc-okhttp:1.45.0")  // HTTP/2를 통한 gRPC 통신을 위한 OkHttp 의존성
    implementation("io.grpc:grpc-stub:1.45.0")    // gRPC 클라이언트/서버 생성에 필요한 의존성
    implementation("io.grpc:grpc-netty-shaded:1.45.0") // gRPC Netty 의존성

    // CameraX
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // 원형 이미지
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // indicator
    implementation("me.relex:circleindicator:2.1.6")

    // kakao login
    implementation("com.kakao.sdk:v2-user:2.14.0")
    // naver login
    implementation("com.navercorp.nid:oauth:5.3.0")
    // google login
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    // flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Room DB
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler-processing:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Glide 사용
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")

    // lottie
    implementation ("com.airbnb.android:lottie:6.6.2")
}