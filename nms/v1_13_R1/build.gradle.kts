dependencies {
    compileOnly(project(":nms"))
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT") {
        // Fix issues related to these being compiled to java 17 and the project
        // is still on java 8
        exclude(group = "com.mojang", module = "authlib")
        exclude(group = "com.mojang", module = "logging")
    }
}
