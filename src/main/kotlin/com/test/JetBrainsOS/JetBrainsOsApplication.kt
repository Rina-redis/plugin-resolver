package com.test.JetBrainsOS

import com.test.JetBrainsOS.cli.Root
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import picocli.CommandLine
import kotlin.system.exitProcess

@SpringBootApplication
class JetBrainsOsApplication

fun main(args: Array<String>) {
	runApplication<JetBrainsOsApplication>(*args)
}

@Component
class PicoRunner(
	private val root: Root,
	private val factory: CommandLine.IFactory
) : CommandLineRunner {

	private val cmds = setOf("plugins", "save", "resolve", "list", "-h", "--help")

	override fun run(vararg args: String) {
		//if no arguments -> Web, if arguments -> CLI
		if (args.isEmpty()) return

		if (args[0] in cmds) {
			val exit = CommandLine(root, factory).execute(*args)
			exitProcess(exit)
		}
	}
}