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

    fun resolveBest(request: ResolveRequest): Plugin? {
        val bestPlugin =
            repo.getAllVersionsById(PluginId(request.pluginId)).filter { request.os == it.os && !it.yanked }
                .filter { request.allowPrerelease || it.version.preRelease.isNullOrEmpty() }
                .filter { request.arch == null || it.arch == null || request.arch == it.arch }

        if (bestPlugin.isEmpty())
            return null

        // Prefer exact arch match first, then by highest version
        val (exact, others) = bestPlugin.partition { request.arch != null && it.arch == request.arch }
        val pool = if (exact.isNotEmpty()) exact else others

        return pool.maxByOrNull { it.version }
    }
}