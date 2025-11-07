package com.test.JetBrainsOS.api.dto

import com.test.JetBrainsOS.domain.*

data class ResolveQuery (
    val pluginId: String,
    val os: OS,
    val arch: Arch? = null,
    val allowPrerelease: Boolean? = null
)