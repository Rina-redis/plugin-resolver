package com.test.JetBrainsOS

import com.test.JetBrainsOS.api.dto.*
import com.test.JetBrainsOS.domain.*
import com.test.JetBrainsOS.repo.PluginRepository
import com.test.JetBrainsOS.services.ResolverService
import com.test.JetBrainsOS.services.toDomain
import com.test.JetBrainsOS.services.toServiceRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ResolverServiceTest {

    @Autowired
    lateinit var repo: PluginRepository
    @Autowired
    lateinit var resolver: ResolverService

    private fun newPid(): String = "fmt-" + UUID.randomUUID().toString().substring(0, 8)

    private fun save(req: SavePluginVersionRequest) {
        repo.addVersion(req.toDomain())
    }

    @Test
    fun `prefers exact-arch over newer universal`() {
        val pid = newPid()
        save(SavePluginVersionRequest(pid, "1.0.0", OS.WINDOWS, Arch.X64, "u1", yanked = false))
        save(SavePluginVersionRequest(pid, "2.0.0", OS.WINDOWS, null, "u2", yanked = false))

        val actual =
            resolver.resolveBest(ResolveQuery(pid, OS.WINDOWS, Arch.X64, allowPrerelease = false).toServiceRequest())

        assertNotNull(actual)
        assertEquals("1.0.0", actual!!.version.formatSemVer())
        assertEquals(Arch.X64, actual.arch)
    }

    @Test
    fun `filters out prerelease when not allowed`() {
        val pid = newPid()
        save(SavePluginVersionRequest(pid, "1.0.0-rc1", OS.WINDOWS, Arch.X64, "pre", yanked = false))
        save(SavePluginVersionRequest(pid, "1.0.0", OS.WINDOWS, Arch.X64, "stable", yanked = false))

        val actual =
            resolver.resolveBest(ResolveQuery(pid, OS.WINDOWS, Arch.X64, allowPrerelease = false).toServiceRequest())
        assertNotNull(actual)
        assertEquals("1.0.0", actual!!.version.formatSemVer())
    }

    @Test
    fun `uses prerelease when allowed and it's the highest among exact-arch`() {
        val pid = newPid()
        save(SavePluginVersionRequest(pid, "3.0.0-rc1", OS.WINDOWS, Arch.X64, "pre", yanked = false))
        save(SavePluginVersionRequest(pid, "2.0.0", OS.WINDOWS, null, "u", yanked = false))

        val actual =
            resolver.resolveBest(ResolveQuery(pid, OS.WINDOWS, Arch.X64, allowPrerelease = true).toServiceRequest())
        assertNotNull(actual)
        assertEquals("3.0.0-rc1", actual!!.version.formatSemVer())
        assertEquals(Arch.X64, actual.arch)
    }

    @Test
    fun `when request has no arch, picks highest overall`() {
        val pid = newPid()
        save(SavePluginVersionRequest(pid, "1.0.0", OS.WINDOWS, Arch.X64, "x", yanked = false))
        save(SavePluginVersionRequest(pid, "2.0.0", OS.WINDOWS, null, "u", yanked = false))

        val actual =
            resolver.resolveBest(ResolveQuery(pid, OS.WINDOWS, arch = null, allowPrerelease = false).toServiceRequest())
        assertNotNull(actual)
        assertEquals("2.0.0", actual!!.version.formatSemVer())
    }

    @Test
    fun `returns null when no candidates`() {
        val got = resolver.resolveBest(
            ResolveQuery(
                "unknown-" + newPid(),
                OS.LINUX,
                arch = null,
                allowPrerelease = false
            ).toServiceRequest()
        )
        assertNull(got)
    }
}
