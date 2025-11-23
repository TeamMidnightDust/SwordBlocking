pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net")
		maven("https://maven.architectury.dev")
		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
		maven("https://repo.polyfrost.cc/releases")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"
	create(rootProject) {
		fun mc(mcVersion: String, loaders: Iterable<String>) {
			for (loader in loaders) {
                version("$mcVersion-$loader", mcVersion)
			}
		}

		mc("1.21.8", listOf("fabric", "neoforge"))
		mc("1.21.10", listOf("fabric", "neoforge"))

		vcsVersion = "1.21.8-fabric"
	}
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "SwordBlocking"
