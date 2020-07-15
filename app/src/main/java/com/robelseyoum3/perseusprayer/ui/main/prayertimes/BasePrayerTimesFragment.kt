package com.robelseyoum3.perseusprayer.ui.main.prayertimes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.robelseyoum3.perseusprayer.ui.DataStateChangeListener
import com.robelseyoum3.perseusprayer.ui.main.MainViewModel
import com.robelseyoum3.perseusprayer.viewmodel.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.ClassCastException
import java.lang.Exception
import javax.inject.Inject

abstract class BasePrayerTimesFragment : DaggerFragment() {

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: PrayerTimesViewModel

    lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(PrayerTimesViewModel::class.java)
        }?: throw Exception("Invalid activity")

        mainViewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(MainViewModel::class.java)
        }?: throw Exception("Invalid activity")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }


}