package com.test.JetBrainsOS.services

import com.test.JetBrainsOS.api.dto.*
import com.test.JetBrainsOS.domain.*

fun SavePluginVersionRequest.toDomain(): Plugin = Plugin(
    id = PluginVersionId(buildStableId(pluginId, version, os, arch)),
    pluginId = PluginId(pluginId),
    version = SemVer.parse(version),
    os = os,
    arch = arch,
    url = url,
    yanked = yanked ?: false
)

fun Plugin.toResponse(): PluginVersionResponse = PluginVersionResponse(
    id = id.value,
    pluginId = pluginId.value,
    version = version.format(),
    os = os,
    arch = arch,
    url = url
)

fun ResolveQuery.toServiceRequest(): ResolveRequest =
    ResolveRequest(
        pluginId = pluginId,
        os = os,
        arch = arch,
        allowPrerelease = allowPrerelease ?: false
    )

private fun buildStableId(
    pluginId: String,
    version: String,
    os: OS,
    arch: Arch?
): String = "$pluginId:$version:$os:${arch ?: "any"}"

private fun SemVer.format(): String =
    listOf(major, minor, patch).joinToString(".")+
            (preRelease?.let { "-$it" } ?: "")