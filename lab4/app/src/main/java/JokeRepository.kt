package com.example.lab4

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class JokeRepository(val scope: CoroutineScope, val dao: JokeDAO) {

    val currentJoke = dao.latestJoke().asLiveData()

    val allJoke = dao.allJoke().asLiveData()
    fun checkJoke(joke: String){
        scope.launch {
            delay(1000)
            dao.addJokeData(
                JokeData(Date(), joke))
        }
    }
}