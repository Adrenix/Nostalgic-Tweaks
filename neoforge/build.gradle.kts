plugins {
    `multiloader-loader`
    id("org.relativitymc.neo-loom")
}

loom {
    accessWidenerPath = project(":").loom.accessWidenerPath

    runConfigs.all {
        generateRunConfig = true
        runDirectory.set(project.file("../run"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.mod.mc}")
    forgeUserdev("net.neoforged:neoforge:${rootProject.mod.prop("neoforge_version")}:userdev")
}

loom.convertAw2At(tasks.jar, listOf("nostalgic_tweaks.classtweaker"))

/*
publishMods {
    file.set(tasks.jar.get().archiveFile)
}*/
