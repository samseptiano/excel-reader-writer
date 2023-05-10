package com.example.assetmanagement.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.assetmanagement.utils.showSnackBar
import javax.inject.Inject

abstract class BaseFragment<VB: ViewBinding> : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // ViewBinding
    protected lateinit var binding: VB
    abstract val inflateLayout: (LayoutInflater, ViewGroup?, Boolean) -> VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateLayout.invoke(layoutInflater, container, false)
        return binding.root
    }


    fun showSnackBar(view: View, message:String, action: String = "", actionListener: () -> Unit = {}){
        view.showSnackBar(message, action, actionListener)
    }
}