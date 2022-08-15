package com.example.noteviewmodel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val commonData = CommonData()
    private val mainViewModel  by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity","onCreate")
        //todo 探究下： 这两行不加 达不到预期效果？？？
        tv1.text = "CommonData:${commonData.num}"
        tv2.text =  "ViewModelData:${mainViewModel.number}"

        button.setOnClickListener {
            tv1.text = "CommonData:${++commonData.num}"
            tv2.text =  "ViewModelData:${++mainViewModel.number}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity","onDestroy")
    }
}