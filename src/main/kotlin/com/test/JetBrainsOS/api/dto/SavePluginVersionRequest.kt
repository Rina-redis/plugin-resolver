package com.test.JetBrainsOS.api.dto

import com.test.JetBrainsOS.domain.*
import jakarta.validation.constraints.NotBlank

data class SavePluginVersionRequest(
    @field:NotBlank
    val pluginId: String,

    @field:NotBlank
    val version: String,
    val os: OS,
    val arch: Arch? = null,

    @field:NotBlank
    val url: String,
    val yanked: Boolean? = null
)