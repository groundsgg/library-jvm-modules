plugins { id("gg.grounds.kotlin-conventions") }

dependencies {
    api(project(":module-api"))

    testImplementation("org.junit.jupiter:junit-jupiter:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
