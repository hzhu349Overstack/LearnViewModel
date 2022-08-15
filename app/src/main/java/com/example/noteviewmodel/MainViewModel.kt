package com.example.noteviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * Create by SunnyDay /08/15 21:48:36
 */
class MainViewModel:ViewModel() {
    var number:Int = 0
    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel","onCleared")
    }
}