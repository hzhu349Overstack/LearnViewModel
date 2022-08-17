package news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.noteviewmodel.R

class MenuFragment : Fragment() {

    private val menuViewModel1: MainViewModel by viewModels()
    private val menuViewModel2 by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ViewModel","MenuFragment-menuViewModel1:${menuViewModel1}")
        Log.d("ViewModel","MenuFragment-menuViewModel2:${menuViewModel2}")
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }
}