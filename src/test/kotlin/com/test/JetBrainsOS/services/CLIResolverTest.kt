package com.test.JetBrainsOS

import com.test.JetBrainsOS.api.dto.SavePluginVersionRequest
import com.test.JetBrainsOS.cli.*
import com.test.JetBrainsOS.domain.OS
import com.test.JetBrainsOS.domain.PluginId
import com.test.JetBrainsOS.repo.PluginRepository
import com.test.JetBrainsOS.services.toDomain
import com.test.JetBrainsOS.services.toResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class CliCommandsIT {

    @Autowired
    lateinit var repo: PluginRepository
    @Autowired
    lateinit var saveCmd: Save
    @Autowired
    lateinit var resolveCmd: Resolve
    @Autowired
    lateinit var listCmd: ListCmd

    private fun newPid(): String = "fmt-" + UUID.randomUUID().toString().substring(0, 8)

    @Test
    fun `save writes a version into repository`() {
        val pid = newPid()


        saveCmd.apply {
            pluginId = pid; version = "1.2.3";os = OS.WINDOWS;arch = null; url =
            "https://example/$pid/1.2.3.zip"; yanked = false
        }.run()

        val stored = repo.getAllVersionsById(PluginId(pid))
        assertTrue(stored.isNotEmpty())

        val resp = stored.first().toResponse()
        assertEquals(pid, resp.pluginId)
        assertEquals("1.2.3", resp.version)
        assertEquals(OS.WINDOWS, resp.os)
        assertNull(resp.arch)
    }

    @Test
    fun `list prints responses`() {
        val pid = newPid()
        repo.addVersion(
            SavePluginVersionRequest(
                pid,
                "0.1.0",
                OS.LINUX,
                null,
                "https://example/$pid/0.1.0",
                yanked = false
            ).toDomain()
        )
        listCmd.apply { pluginId = pid }.run()

    }

    @Test
    fun `resolve executes without error`() {
        val pid = newPid()
        resolveCmd.apply {
            pluginId = pid
            os = OS.MACOS
            arch = null
            allowPrerelease = true
        }.run()
    }
}
