// ktlint runs as a CLI through JavaExec rather than through a Gradle plugin, which is how this project
// has always invoked it. The alternative — a plugin applied in each of the eleven modules — buys source
// set awareness that a whole-tree glob does not need.
// The SHADOWED variant is the self-contained CLI jar; the attribute belongs on the configuration so the
// dependency itself can come straight from the version catalog.
val ktlint: Configuration by configurations.creating {
    attributes {
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.SHADOWED))
    }
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    ktlint(versionCatalog.findLibrary("ktlint-cli").get())
}

// Every module keeps its Kotlin under `src/<sourceSet>/kotlin`, so one pair of patterns covers the whole
// tree and picks up any platform source set added later. Test sources stay excluded, as they were before
// the migration.
val ktlintInclude = "**/src/**/*.kt"
val ktlintExcludeTests = "!**/src/*Test/**"
val ktlintReport = "$rootDir/build/reports/ktlint/ktlint-report.html"

tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    inputs.files(
        fileTree(rootDir) {
            include(ktlintInclude)
            exclude("**/src/*Test/**", "**/build/**")
        }
    )
    outputs.file(ktlintReport)
    outputs.cacheIf { true }

    args(
        ktlintInclude,
        ktlintExcludeTests,
        "--editorconfig=$rootDir/gradle/.editorconfig",
        "--reporter=html,output=$ktlintReport",
        "--reporter=plain"
    )

    maxHeapSize = "512m"
}

// Applies every deviation ktlint can fix on its own. Deliberately not wired into `check`.
tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations that ktlint can correct automatically."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")

    args(
        "--format",
        ktlintInclude,
        ktlintExcludeTests,
        "--editorconfig=$rootDir/gradle/.editorconfig"
    )

    maxHeapSize = "512m"
}
