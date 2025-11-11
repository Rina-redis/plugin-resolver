package com.test.JetBrainsOS.cli

import com.test.JetBrainsOS.api.dto.ResolveQuery
import com.test.JetBrainsOS.api.dto.SavePluginVersionRequest
import com.test.JetBrainsOS.domain.*
import com.test.JetBrainsOS.repo.PluginRepository
import com.test.JetBrainsOS.services.*
import org.springframework.stereotype.Component
import picocli.CommandLine.Command
import picocli.CommandLine.Option


// save - create/update a plugin version
// resolve - pick best version for a given client
// list -list all stored versions for a plugin

@Component
@Command(
    description = ["Plugin resolver CLI"],
    mixinStandardHelpOptions = true,
    subcommands = [Save::class, Resolve::class, ListCmd::class]
)
class Root : Runnable {
    override fun run() = Unit // prints help if no subcommand is  provided
}


@Component
@Command(name = "save", description = ["Save (create/update) a plugin version"])
class Save(private val repo: PluginRepository) : Runnable {
    @Option(names = ["--pluginId"], required = true)
    lateinit var pluginId: String
    @Option(names = ["--version"], required = true)
    lateinit var version: String
    @Option(names = ["--os"], required = true)
    lateinit var os: OS
    @Option(names = ["--arch"])
    var arch: Arch? = null
    @Option(names = ["--url"], required = true)
    lateinit var url: String
    @Option(names = ["--yanked"])
    var yanked: Boolean = false

    override fun run() =
        println(
            SavePluginVersionRequest(pluginId, version, os, arch, url, yanked).toDomain().also(repo::addVersion)
        )
}

@Component
@Command(name = "resolve", description = ["Resolve best version for a client"])
class Resolve(private val resolver: ResolverService) : Runnable {
    @Option(names = ["--pluginId"], required = true)
    lateinit var pluginId: String
    @Option(names = ["--os"], required = true)
    lateinit var os: OS
    @Option(names = ["--arch"])
    var arch: Arch? = null
    @Option(names = ["--allowPrerelease"])
    var allowPrerelease = false
    override fun run() =
        println(
            // Print resolved version or "null" if none
            resolver.resolveBest(ResolveQuery(pluginId, os, arch, allowPrerelease).toServiceRequest())?.toResponse() ?: "null"
        )
}

@Component
@Command(name = "list", description = ["List stored versions of a plugin"])
class ListCmd(private val repo: PluginRepository) : Runnable {
    @Option(names = ["--pluginId"], required = true)
    lateinit var pluginId: String
    override fun run() =
        println(repo.getAllVersionsById(PluginId(pluginId)).map { it.toResponse() })
}
