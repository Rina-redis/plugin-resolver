package com.test.JetBrainsOS.api.web

import com.test.JetBrainsOS.api.dto.*
import com.test.JetBrainsOS.domain.PluginId
import com.test.JetBrainsOS.repo.PluginRepository
import com.test.JetBrainsOS.services.*
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")

class Controller(private val repo: PluginRepository, private val resolver: ResolverService) {
    @PostMapping("/save")
    fun save(@Valid @RequestBody body: SavePluginVersionRequest): PluginVersionResponse? {
        return body.toDomain().also(repo::addVersion).toResponse()
    }

    @GetMapping("/resolve")
    fun resolve(q: ResolveQuery): PluginVersionResponse? {
        return resolver.resolveBest(q.toServiceRequest())?.toResponse()
    }

    @GetMapping("/plugins/{pluginId}/versions")
    fun list(@PathVariable pluginId: String): List<PluginVersionResponse> {
        return repo.getAllVersionsById(PluginId(pluginId)).sortedBy { it.version }.map { it.toResponse() }
    }
}