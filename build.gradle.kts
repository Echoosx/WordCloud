plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.0"
}

group = "org.echoosx"
version = "1.1.1"
val hibernateVer = "2.1.1"

repositories {
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}

dependencies {
    implementation ("com.huaban:jieba-analysis:1.0.2")
    implementation("com.kennycason:kumo-core:1.28")
    compileOnly("xyz.cssxsh.mirai:mirai-hibernate-plugin:${hibernateVer}")
    testImplementation("xyz.cssxsh.mirai:mirai-hibernate-plugin:${hibernateVer}")
    implementation("org.quartz-scheduler:quartz:2.3.2")
}