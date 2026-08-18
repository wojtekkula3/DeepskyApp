// detekt runs as a CLI through JavaExec, matching how ktlint is invoked next door. The pre-migration
// script pointed `--input` at the single `app/src/main/java` module; the input is now every module's
// production source set.
val detekt: Configuration by configurations.creating

val detektCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    detekt(detektCatalog.findLibrary("detekt-cli").get())
}

// detekt takes a comma-separated list of paths rather than glob patterns, so the modules are listed
// explicitly. Test sources are excluded, as they were before the migration.
val detektInputDirs = listOf(
    "androidApp/src/main",
    "core/common/src",
    "core/designsystem/src",
    "core/mvvm/src",
    "core/navigation/src",
    "data/src",
    "domain/src",
    "feature/about/src",
    "feature/favourites/src",
    "feature/picture/src",
    "shared/src"
)

val detektReport = "$rootDir/build/reports/detekt/detekt-report.html"

tasks.register<JavaExec>("detekt") {
    group = "verification"
    description = "Code smell analysis for Kotlin."
    classpath = detekt
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    inputs.files(
        fileTree(rootDir) {
            include("**/src/**/*.kt")
            exclude("**/src/*Test/**", "**/build/**")
        }
    )
    outputs.file(detektReport)
    outputs.cacheIf { true }

    args(
        "--input", detektInputDirs.joinToString(",") { "$rootDir/$it" },
        "--excludes", "**/src/*Test/**,**/build/**",
        "--config", "$rootDir/gradle/detekt-config.yml",
        "--report", "html:$detektReport"
    )

    maxHeapSize = "512m"
}
