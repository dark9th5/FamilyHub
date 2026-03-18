package com.family.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FamilyBackendApplication

fun main(args: Array<String>) {
    runApplication<FamilyBackendApplication>(*args)
}
