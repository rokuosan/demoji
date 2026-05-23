package org.yaken.demoji.infrastructure.config

fun interface Environment {
    fun get(name: String): String?
}

object SystemEnvironment : Environment {
    override fun get(name: String): String? = System.getenv(name)
}
