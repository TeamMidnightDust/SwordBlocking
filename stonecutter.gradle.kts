import dev.kikugie.stonecutter.data.tree.struct.ProjectNode

plugins {
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.publishing)
}

stonecutter active "1.21.10-fabric" /* [SC] DO NOT EDIT */

stonecutter tasks {
    val ordering = Comparator
        .comparing<ProjectNode, _> { stonecutter.parse(it.metadata.version) }
        .thenComparingInt { if (it.metadata.project.endsWith("fabric")) 1 else 0 }
    order("publishMods", ordering)
}

stonecutter parameters {
    val loader = node.project.property("loom.platform")
    constants["fabric"] = loader == "fabric"
    constants["neoforge"] = loader == "neoforge"
}

tasks.named("publishMods") {
    group = "build"
}