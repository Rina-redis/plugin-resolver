package com.test.JetBrainsOS.repo

import com.test.JetBrainsOS.domain.*
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface PluginRepository {
    fun addVersion(plugin: Plugin)
    fun getAllVersionsById(pluginId: PluginId): List<Plugin>
}

@Repository
class LocalPluginRepository : PluginRepository {
    //ConcurrentHashMap allows us safe multithreading
    private val pluginRepository = ConcurrentHashMap<PluginId, MutableList<Plugin>>()

    override fun addVersion(plugin: Plugin) {
        pluginRepository.computeIfAbsent(plugin.pluginId) { mutableListOf() }.apply {
            removeIf { it.id == plugin.id }
            add(plugin)
        }
    }

    override fun getAllVersionsById(pluginId: PluginId): List<Plugin> {
        return pluginRepository[pluginId]?.toList().orEmpty()
    }

}