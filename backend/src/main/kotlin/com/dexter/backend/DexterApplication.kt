package com.dexter.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DexterApplication

fun main(args: Array<String>) {
	runApplication<DexterApplication>(*args)
}
