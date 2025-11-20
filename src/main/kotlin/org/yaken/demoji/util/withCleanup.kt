package org.yaken.demoji.util


suspend fun <T> T.withCleanup(cleanup: (T) -> Unit, block: suspend (T) -> Unit) {
    return try {
        block(this)
    } finally {
        cleanup(this)
    }
}
