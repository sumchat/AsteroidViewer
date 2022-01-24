package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.Network.service
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val database = AsteroidDatabase.getDatabase(application)
    val asteroidRepository = AsteroidRepository(database)

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids:LiveData<List<Asteroid>>
     get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _displaySnackbarEvent = MutableLiveData<Boolean>()
    val displaySnackbarEvent: LiveData<Boolean>
        get() = _displaySnackbarEvent



    init{
        viewModelScope.launch {
            try{
                refreshPictureOfDay()
                asteroidRepository.refreshAsteroids()
                database.asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getSeventhDay())
                    .collect{
                            asteroids ->
                        _asteroids.value = asteroids

                    }

            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")

                _displaySnackbarEvent.value = true
            }
        }
    }

    suspend fun refreshPictureOfDay()
    {
        _pictureOfDay.value = service.getPictureOfTheDay()

    }

    fun onViewWeekAsteroidsClicked(){
        viewModelScope.launch{
            database.asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getSeventhDay())
                .collect{
                        asteroids ->
                    _asteroids.value = asteroids

                }
        }
    }

    fun onTodayAsteroidsClicked(){
        viewModelScope.launch{
            database.asteroidDao.getAsteroidsForToday(getToday())
                .collect{
                        asteroids ->
                    _asteroids.value = asteroids

                }
        }
    }

    fun onSavedAsteroidsClicked(){
        viewModelScope.launch{
            database.asteroidDao.getAllAsteroids()
                .collect{
                        asteroids ->
                    _asteroids.value = asteroids

                }
        }
    }


}


