package news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.noteviewmodel.R

class ListFragment : Fragment() {
    private val listViewModel1: MainViewModel by activityViewModels() // viewModel() 不可否则会产生新的对象
    private val listViewModel3: MainViewModel by activityViewModels()
    private val listViewModel2 by lazy { //不可用这种方案 会产生新的对象
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ViewModel","ListFragment-listViewModel1:${listViewModel1}")
        Log.d("ViewModel","ListFragment-listViewModel2:${listViewModel2}")
        Log.d("ViewModel","ListFragment-listViewModel3:${listViewModel3}")
        return inflater.inflate(R.layout.fragment_list, container, false)
    }
}