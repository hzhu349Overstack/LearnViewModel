package news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.noteviewmodel.R

class NewsActivity : AppCompatActivity() {
    private val mainViewModel1: MainViewModel by viewModels()
    private val mainViewModel2 by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        mainViewModel1.pageName = "Hello"

        Log.d("ViewModel","NewsActivity-mainViewModel1:${mainViewModel1}")
        Log.d("ViewModel","NewsActivity-mainViewModel2:${mainViewModel2}")
    }
}