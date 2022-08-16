package com.example.noteviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * Create by SunnyDay /08/15 21:48:36
 */
class MainViewModel:ViewModel() {
    var number:Int = 0
    /**
     * ViewModel 提供的唯一一个可重写的方法。默认空实现。
     * 方法生命周期独立于Activity，当ViewModel于应用进程都不在使用时这个方法回调。可以简单理解为进程死了，这个方法就回调。
     * */
    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel","onCleared")
    }
}