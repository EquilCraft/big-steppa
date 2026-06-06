plugins {
	id("com.gtnewhorizons.gtnhconvention")
}

version = "1.0.0"

minecraft {
	skipSlowTasks.set(true)
	injectedTags.put("VERSION", project.version)
	injectedTags.put("MOD_ID", providers.gradleProperty("modId").get())
	injectedTags.put("MOD_NAME", providers.gradleProperty("modName").get())
}

tasks.injectTags.configure {
	outputClassName.set("${providers.gradleProperty("modGroup").get()}.Tags")
}
