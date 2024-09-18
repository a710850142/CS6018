package com.example.lab4

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class JokeApplication : Application() {

    val scope = CoroutineScope(SupervisorJob())
    val db by lazy {JokeDatabase.getDatabase(applicationContext)}
    val jokeRepository by lazy {JokeRepository(scope, db.jokeDao())}
}