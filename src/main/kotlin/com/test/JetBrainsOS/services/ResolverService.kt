package com.test.JetBrainsOS.services

import com.test.JetBrainsOS.domain.*
import com.test.JetBrainsOS.repo.PluginRepository
import org.springframework.stereotype.Service

data class ResolveRequest(
    val pluginId: String,
    val os: OS,
    val arch: Arch? = null,
    val allowPrerelease: Boolean = false
)

@Service
class ResolverService(private val repo: PluginRepository) {

    fun resolveBet(request: ResolveRequest): SemVer.Plugin? {
        val bestPlugin =
            repo.getAllVersionsById(SemVer.PluginId(request.pluginId)).filter { request.os == it.os && !it.yanked }
                .filter { request.allowPrerelease || it.version.preRelease.isNullOrEmpty() }
                .filter { request.arch == null || it.arch == null || request.arch == it.arch }

        if (bestPlugin.isEmpty())
            return null

        // Prefer exact arch match first, then by highest version
        val (exact, others) = bestPlugin.partition { it.arch == request.arch && it.arch != null }
        return (exact + others).maxByOrNull { it.version }
    }
}