package org.yaken.demoji.infrastructure.config

class MapEnvironment(
    private val values: Map<String, String?>,
) : Environment {
    constructor(vararg values: Pair<String, String?>) : this(values.toMap())

    override fun get(name: String): String? = values[name]
}
