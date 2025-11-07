package com.test.JetBrainsOS.repo

import com.test.JetBrainsOS.domain.*
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface PluginRepository{
    fun addVersion(plugin: SemVer.Plugin)
    fun getAllVersionsById(pluginId:SemVer.PluginId): List<SemVer.Plugin>
}

@Repository
class LocalPluginRepository : PluginRepository {
    //ConcurrentHashMap allows us safe multithreading
    private val pluginRepository = ConcurrentHashMap<SemVer.PluginId,MutableList<SemVer.Plugin>>()

    override fun addVersion(plugin: SemVer.Plugin) {
        pluginRepository.computeIfAbsent(plugin.pluginId) { mutableListOf() }.apply {
            add(plugin)
        }
    }

    override fun getAllVersionsById(pluginId: SemVer.PluginId): List<SemVer.Plugin> {
        return pluginRepository[pluginId]?.toList().orEmpty()
    }

}