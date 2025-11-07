package com.test.JetBrainsOS.api.dto

import com.test.JetBrainsOS.domain.*

data class PluginVersionResponse (
    val id: String,
    val pluginId: String,
    val version: String,
    val os: OS,
    val arch: Arch?,
    val url: String
)