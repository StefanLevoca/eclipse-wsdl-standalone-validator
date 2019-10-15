plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(fileTree("lib"))

    implementation("xerces:xercesImpl:2.12.0")
    implementation("wsdl4j:wsdl4j:1.6.3")
    implementation("com.ibm.icu:icu4j:64.2")
    testImplementation("junit:junit:4.12")
}