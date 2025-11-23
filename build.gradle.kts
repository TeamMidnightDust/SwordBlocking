plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom)
    alias(libs.plugins.publishing)
    alias(libs.plugins.blossom)
    alias(libs.plugins.ksp)
    alias(libs.plugins.fletchingtable.fabric)
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
    maven("https://maven.parchmentmc.org") // Parchment
    maven("https://maven.neoforged.net/releases") // NeoForge
    maven("https://maven.bawnorton.com/releases") // MixinSquared
    maven("https://maven.terraformersmc.com/") // Mod Menu
    maven("https://api.modrinth.com/maven") // MidnightLib
}

class ModData {
    val id = property("mod.id") as String
    val name = property("mod.name") as String
    val version = property("mod.version") as String
    val group = property("mod.group") as String
    val description = property("mod.description") as String
    val source = property("mod.source") as String
    val issues = property("mod.issues") as String
    val license = property("mod.license") as String
    val modrinth = property("mod.modrinth") as String
    val curseforge = property("mod.curseforge") as String
    val discord = property("mod.discord") as String

    val minecraftVersion = property("mod.minecraft_version") as String
    val minecraftVersionRange = property("mod.minecraft_version_range") as String
}

class Dependencies {
    val fabricLoaderVersion = property("deps.fabric_loader_version") as String?
    val midnightLibVersion = property("deps.midnightlib_version") as String?

    val devAuthVersion = property("deps.devauth_version") as String?
    val lombokVersion = property("deps.lombok_version") as String?
    val mixinConstraintsVersion = property("deps.mixinconstraints_version") as String?
    val mixinSquaredVersion = property("deps.mixinsquared_version") as String?

    // Versioned
    val neoForgeVersion = property("deps.neoforge_version") as String?
    val fabricApiVersion = property("deps.fabric_api_version") as String?
}

class LoaderData {
    val name = loom.platform.get().name.lowercase()
    val isFabric = name == "fabric"
    val isNeoForge = name == "neoforge"
}

val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

group = mod.group
base {
    archivesName.set("${mod.id}-${mod.version}+${mod.minecraftVersion}-${loader.name}")
}

java {
    val requiredJava = when {
        stonecutter.eval(stonecutter.current.version, ">=1.20.6") -> JavaVersion.VERSION_21
        stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
        stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
        else -> JavaVersion.VERSION_1_8
    }

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
}

stonecutter {
    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }
}

val currentCommitHash: String by lazy {
    Runtime.getRuntime()
        .exec("git rev-parse --verify --short HEAD", null, rootDir)
        .inputStream.bufferedReader().readText().trim()
}

blossom {
    replaceToken("@MODID@", mod.id)
    replaceToken("@VERSION@", mod.version)
    replaceToken("@COMMIT@", currentCommitHash)
}

loom {
    silentMojangMappingsLicense()
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runConfigs.remove(runConfigs["server"]) // Removes server run configs
}

loom.runs {
    afterEvaluate {
        val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
            componentFilter {
                it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
            }
        }.files.first()
        configureEach {
            vmArg("-javaagent:$mixinJarFile")
            property("mixin.hotSwap", "true")
            property("mixin.debug.export", "true") // Puts mixin outputs in /run/.mixin.out
            property("devauth.enabled", "true")
            property("devauth.account", "main")
        }
    }
}

fletchingTable {
    mixins.create("main") {
        mixin("default", "${mod.id}.mixins.json")
    }

    lang.create("main") {
        patterns.add("assets/${mod.id}/lang/**")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.minecraftVersion}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()

        // Parchment mappings (it adds parameter mappings & javadoc)
        optionalProp("deps.parchment_version") {
            parchment("org.parchmentmc.data:parchment-${mod.minecraftVersion}:$it@zip")
        }
    })

    modImplementation("maven.modrinth:midnightlib:${deps.midnightLibVersion}+${mod.minecraftVersion}-${loader.name}")

    compileOnly("org.projectlombok:lombok:${deps.lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${deps.lombokVersion}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-${loader.name}:${deps.devAuthVersion}")

    include(implementation("com.moulberry:mixinconstraints:${deps.mixinConstraintsVersion}")!!)!!
    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-${loader.name}:${deps.mixinSquaredVersion}")!!)!!)
    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoaderVersion}")!!
        modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fabricApiVersion}")
        optionalProp("deps.modmenu_version") { prop ->
            modImplementation("com.terraformersmc:modmenu:$prop") {
                exclude(group, "net.fabricmc.fabric-api")
            }
        }
    } else if (loader.isNeoForge) {
        "neoForge"("net.neoforged:neoforge:${deps.neoForgeVersion}")
    }
}

publishMods {
}

tasks {
    processResources {
        val props = buildMap {
            put("id", mod.id)
            put("name", mod.name)
            put("version", mod.version)
            put("description", mod.description)
            put("source", mod.source)
            put("issues", mod.issues)
            put("license", mod.license)
            put("modrinth", mod.modrinth)
            put("curseforge", mod.curseforge)
            put("discord", mod.discord)
            put("minecraft_version_range", mod.minecraftVersionRange)
            if (loader.isFabric) {
                put("fabric_api_version", deps.fabricApiVersion?.trim())
                put("fabric_loader_version", deps.fabricLoaderVersion?.trim())
            } else if (loader.isNeoForge) {
                put("neoforge_version", deps.neoForgeVersion?.trim())
            }
        }

        props.forEach(inputs::property)
        filesMatching("**/lang/en_us.json") { // Defaults description to English translation
            expand(props)
            filteringCharset = "UTF-8"
        }

        if (loader.isFabric) {
            filesMatching("fabric.mod.json") { expand(props) }
            exclude(listOf("META-INF/neoforge.mods.toml"))
        }

        if (loader.isNeoForge) {
            filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
            exclude(listOf("fabric.mod.json"))
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(tasks.named("build"))
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)