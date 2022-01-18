plugins {
    id("com.google.protobuf") version "0.8.17" apply false
    kotlin("jvm") version "1.5.31" apply false
    idea
}

ext["grpcVersion"] = "1.39.0"
ext["grpcKotlinVersion"] = "1.2.0"
ext["protobufVersion"] = "3.18.1"
ext["coroutinesVersion"] = "1.5.2"
ext["zookeeperVersion"]  = "3.7.0"
ext["log4jVersion"] = "1.7.25"

allprojects {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        google()
    }

    apply(plugin = "idea")
}

tasks.create("assemble").dependsOn(":server:installDist")
