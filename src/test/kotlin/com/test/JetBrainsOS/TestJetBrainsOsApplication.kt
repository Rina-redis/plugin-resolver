package com.test.JetBrainsOS

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<JetBrainsOsApplication>().with(TestcontainersConfiguration::class).run(*args)
}
