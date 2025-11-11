
##  Project Description

A tiny **Kotlin / Spring Boot** service & "CLI" that resolves the **best plugin build** for a given `pluginId`, target `os`, optional `arch`, and whether pre-releases are allowed.  
The core is a `ResolverService` that filters out yanked builds, respects prerelease flags, matches OS/arch, and then picks the highest semantic version.
Created as a test task for JetBrains internship.

As i have not totaly understand the line "Spring Boot (Kotlin) CLI/service", i **builded REST and CLI in one app, that`s why, CLI is not perfect**, and could be done better. But i find this implementation okay for small and rare request. The main part is REST.

**Core logic:**  
- Excludes yanked plugins  
- Honors `allowPrerelease`  
- Prefers exact architecture matches  
- Sorts by semantic version

---

##  Getting Started

### Prerequisites

You’ll need:
- [JDK 17+](https://adoptium.net/)
- [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) (included)

### Installing

Clone and build the project:

```bash
git clone https://github.com/Rina-redis/plugin-resolver.git
cd plugin-resolver
./gradlew clean build
```

---

##  Web vs CLI

Application can run in **two modes**, decided by the command-line arguments:

| Mode | How it starts | What it does | Output |
|---|---|---|---|
| **Web (REST API)** | **No arguments** | Starts Spring Boot server (e.g., `http://localhost:8080`) and exposes endpoints like `/resolve`  | HTTP JSON responses |
| **CLI (picocli)** | First arg is one of `plugins`, `save`, `resolve`, `list`, `-h`, `--help` | Also Starts Spring, executes one command and exits with an exit code | Plain text to stdout/stderr + exit code |
 
`PicoRunner` checks `args`. If empty → continue as Web; if the first arg is a known subcommand → run picocli and `exitProcess(...)`.

---


## Web API Usage

Run the web service:
```bash
./gradlew bootRun
```
Server runs at `http://localhost:8080`
| Parameter | Required | Description |
|------------|-----------|-------------|
| `pluginId` | Yes | Plugin identifier |
| `os` | Yes | OS: `WINDOWS`, `LINUX`, or `MACOS` |
| `arch` | No | Optional CPU architecture |
| `allowPrerelease` | No | Include prereleases (default: false) |

Example request:
```bash
curl "http://localhost:8080/resolve?pluginId=example.plugin&os=LINUX&arch=X86_64&allowPrerelease=false"
```
Example JSON response:
```json
{
  "pluginVersionid": "example.plugin-1.4.2",
  "pluginId": "example.plugin",
  "version": "1.4.2",
  "os": "LINUX",
  "arch": "X64",
  "yanked": false,
  "downloadUrl": "https://cdn.example.com/example.plugin-1.4.2-linux-x86_64.zip"
}
```

##  "CLI" Usage

```bash
 ./gradlew bootRun --args="save --pluginId formatter --version 1.2.0 --os MACOS --arch ARM64 --url https://cdn/formatter.zip"
```

---

##  Running the Tests

Run all test cases with:
```bash
./gradlew test
```

These tests verify:
- Version filtering logic  
- Correctness of `ResolverService` output  
- Repository behavior (`LocalPluginRepository`)

---

##  Built With

- [Kotlin](https://kotlinlang.org/)  
- [Spring Boot](https://spring.io/projects/spring-boot)  
- [Gradle](https://gradle.org/)  
- [JUnit 5](https://junit.org/junit5/)  

---

##  Authors

- **Maryna Redka** — Developer  
  [GitHub: Rina-redis](https://github.com/Rina-redis)
---
 · Made with ❤️ in Kotlin
