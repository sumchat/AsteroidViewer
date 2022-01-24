package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class DeleteDataWorker (appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {

        const val DELETE_WORK_NAME = "DeleteDataWorker"
    }

    /**
     * A coroutine-friendly method to do your work.
     * Note: In recent work version upgrade, 1.0.0-alpha12 and onwards have a breaking change.
     * The doWork() function now returns Result instead of Payload because they have combined Payload into Result.
     * Read more here - https://developer.android.com/jetpack/androidx/releases/work#1.0.0-alpha12
     */
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.deletePreviousDayAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}