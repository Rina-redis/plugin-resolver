package com.test.JetBrainsOS.domain

enum class OS { WINDOWS, LINUX, MACOS }
enum class Arch { X64, ARM64 }

data class SemVer(val major: Int, val minor: Int, val patch: Int, val preRelease: String? = null) : Comparable<SemVer> {

    override fun compareTo(other: SemVer): Int {
        val core = compareValuesBy(this, other, SemVer::major, SemVer::minor, SemVer::patch)

        //definitely not preRelease then
        if (core != 0) return core

        //-1 → this < other
        //0 → same major/minor/patch
        //1 → this > other
        return when {
            this.preRelease == null && other.preRelease == null -> 0
            this.preRelease == null -> 1     // stable > prerelease
            other.preRelease == null -> -1
            else -> this.preRelease.compareTo(other.preRelease) //compare lexicographically
        }
    }

    companion object {
        //fix the Regex (more easy version of suggest RegEx by https://semver.org/)
        private val re = Regex("""^(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z.-]+))?$""")

        fun parse(s: String): SemVer {
            val m = re.matchEntire(s) ?: error("Invalid semver: $s")
            return SemVer(
                m.groupValues[1].toInt(),
                m.groupValues[2].toInt(),
                m.groupValues[3].toInt(),
                m.groupValues.getOrNull(4)?.ifBlank { null }
            )
        }
    }

    //for not accidentally mixing the values and for definity
    @JvmInline
    value class PluginId(val value: String)

    @JvmInline
    value class PluginVersionId(val value: String)

    data class Plugin(
        val id: PluginVersionId, //id of a current PluginVersion aka "formatter:1.2.0"
        val pluginId: PluginId, //id of a plugin as a whole
        val version: SemVer,
        val os: OS,
        val arch: Arch? = null,
        val url: String,
        val yanked: Boolean = false
    )
}