package com.yeletskyiv.biorec.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _splashLiveData by lazy { MutableLiveData<Unit>() }
    val splashLiveData: LiveData<Unit> = _splashLiveData

    fun showLogo() = viewModelScope.launch {
        delay(SPLASH_DELAY)
        _splashLiveData.postValue(Unit)
    }

    companion object {

        private const val SPLASH_DELAY = 2000L
    }
}