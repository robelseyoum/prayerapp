package com.robelseyoum3.perseusprayer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.robelseyoum3.perseusprayer.data.model.PrayerMethods
import com.robelseyoum3.perseusprayer.data.model.PrayerTimes
import com.robelseyoum3.perseusprayer.utils.Constants
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._EGYPT_SURVEY
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._FIXED_ISHAA
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._KARACHI_HANAF
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._MUSLIM_LEAGUE
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._NORTH_AMERICA
import com.robelseyoum3.perseusprayer.utils.Constants.Companion._UMM_ALQURRA
import com.robelseyoum3.perseusprayer.utils.PreferenceKeys
import com.robelseyoum3.perseusprayer.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.security.interfaces.RSAKey
import java.util.*
import javax.inject.Inject

class MainRepository @Inject constructor(
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor) {


    var job: CompletableJob? = null

    fun getPrayersTimes(_coordination: MutableMap<String, Double>, prayerBaseLoc: String?): LiveData<Resource<PrayerTimes>> {
        job = Job()


//        val prayerMethods = PrayerMethods(
//            mutableMapOf(
//                ("EGYPT_SURVEY" to "Egyptian General Authority of Survey" ),
//                ("FIXED_ISHAA" to "Fixed Ishaa Angle Interval"),
//                ("KARACHI_HANAF" to "University of Islamic Sciences, Karachi (Hanafi)"),
//                ("MUSLIM_LEAGUE" to "Egyptian General Authority of Survey" ),
//                ("NORTH_AMERICA" to "Islamic Society of North America"),
//                ("UMM_ALQURRA" to "Om Al-Qurra University" )
//            )
//        )

        //store a method for shared preference
//        saveMethodOfCalculationToPrefs(prayerMethods)

//        sharedPrefsEditor.putString(PreferenceKeys.METHOD_CALCULATION, prayerBaseLoc)
//        sharedPrefsEditor.apply()

        val sharedValue = sharedPreferences.getString(PreferenceKeys.METHOD_CALCULATION, null)
        //FIXED_ISHAA
        Log.d("getPrayersTimes", sharedValue)



        val today = SimpleDate(GregorianCalendar())
        val location = Location(
            _coordination["latitude"] ?: 0.0,
            _coordination["longitude"] ?: 0.0,
            2.0,
            0
        )

//        val prayerBasedMethod = prayerMethods.methodBased[prayerBaseLoc]?.let { checkPrayerBased(it) }
        val prayerBasedMethod = checkPrayerBased(prayerBaseLoc)

        val azan = Azan(location, prayerBasedMethod)
        val prayerTimes = azan.getPrayerTimes(today)
        val imsaak = azan.getImsaak(today)


        return object : LiveData<Resource<PrayerTimes>>(){
            override fun onActive() {
                super.onActive()

                if(value?.data == null){
                    value = Resource.Loading(null)
                }

                job?.let { theJob ->
                    CoroutineScope(IO + theJob).launch {
                        withContext(Main){

                            if(_coordination.isEmpty()){
                                value = Resource.Error("No Prayer Data Found", null)
                            }else {

                                value = Resource.Success(
                                    PrayerTimes(
                                        mutableListOf(
                                            today.day.toString(),
                                            today.month.toString(),
                                            today.year.toString()
                                        ),
                                        imsaak.toString(),
                                        prayerTimes.fajr().toString(),
                                        prayerTimes.shuruq().toString(),
                                        prayerTimes.thuhr().toString(),
                                        prayerTimes.assr().toString(),
                                        prayerTimes.maghrib().toString(),
                                        prayerTimes.ishaa().toString()
                                    )
                                )
                            }

                            theJob.complete()
                        }
                    }
                }
            }
        }
    }

    /**
    val previousAuthUserEmail: String? =
    sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
     */
    //store the method into shared preference for methodsd
//    private fun saveMethodOfCalculationToPrefs(prayerMethods: PrayerMethods) {
//        prayerMethods.methodBased?.forEach { (key, value) ->
//            sharedPrefsEditor.putString(key, value)
//            sharedPrefsEditor.apply()
//        }
//    }




//    private fun checkPrayerBased(methodType: PrayerMethods) : Method {
//
//        return when (methodType) {
//            methodType["EGYPT_SURVEY"] -> { Method.EGYPT_SURVEY }
//            methodType["FIXED_ISHAA"] -> Method.FIXED_ISHAA
//            methodType["KARACHI_HANAF"] -> Method.KARACHI_HANAF
//            methodType["MUSLIM_LEAGUE"] -> Method.MUSLIM_LEAGUE
//            methodType["NORTH_AMERICA"] -> Method.NORTH_AMERICA
//            methodType["UMM_ALQURRA"] -> Method.UMM_ALQURRA
//            else -> Method.NONE
//        }
//    }

    private fun checkPrayerBased(methodType: String?) : Method {
        return when (methodType) {
            _EGYPT_SURVEY -> Method.EGYPT_SURVEY
            _FIXED_ISHAA -> Method.FIXED_ISHAA
            _KARACHI_HANAF -> Method.KARACHI_HANAF
            _MUSLIM_LEAGUE -> Method.MUSLIM_LEAGUE
            _NORTH_AMERICA -> Method.NORTH_AMERICA
            _UMM_ALQURRA -> Method.UMM_ALQURRA
            else -> Method.NONE
        }
    }

    fun cancelJobs(){
        job?.cancel()
    }

}