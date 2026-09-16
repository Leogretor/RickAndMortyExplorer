package com.example.rickandmortyexplorer.presentation.characters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.Closeable
import java.util.concurrent.Executors

class TestCoroutineEnvironment : Closeable {

    private val executor = Executors.newSingleThreadExecutor()
    val dispatcher: ExecutorCoroutineDispatcher = executor.asCoroutineDispatcher()
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    override fun close() {
        dispatcher.close()
        executor.shutdown()
    }
}
