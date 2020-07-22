package com.robelseyoum3.perseusprayer.data.repository

import androidx.lifecycle.LiveData
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.robelseyoum3.perseusprayer.data.model.PrayerMethods
import com.robelseyoum3.perseusprayer.data.model.PrayerTimes
import com.robelseyoum3.perseusprayer.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.security.interfaces.RSAKey
import java.util.*

object MainRepository {

    var job: CompletableJob? = null


    fun getPrayersTimes(_coordination: MutableMap<String, Double>, prayerBaseLoc: String): LiveData<Resource<PrayerTimes>> {
        job = Job()

        val prayerMethods = PrayerMethods(
                                    mutableMapOf(
                                        ("EGYPT_SURVEY" to "Egyptian General Authority of Survey" ),
                                        ("FIXED_ISHAA" to "Fixed Ishaa Angle Interval"),
                                        ("KARACHI_HANAF" to "University of Islamic Sciences, Karachi (Hanafi)"),
                                        ("MUSLIM_LEAGUE" to "Egyptian General Authority of Survey" ),
                                        ("NORTH_AMERICA" to "Islamic Society of North America"),
                                        ("UMM_ALQURRA" to "Om Al-Qurra University" )
                                    )
                             )

        val today = SimpleDate(GregorianCalendar())
        val location = Location(
            _coordination["latitude"] ?: 0.0,
            _coordination["longitude"] ?: 0.0,
            2.0,
            0
        )

        val prayerBasedMethod = prayerMethods.methodBased[prayerBaseLoc]?.let { checkPrayerBased(it) }

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

    private fun checkPrayerBased(methodType: String) : Method {
        return when (methodType) {
            "EGYPT_SURVEY" -> Method.EGYPT_SURVEY
            "FIXED_ISHAA" -> Method.FIXED_ISHAA
            "KARACHI_HANAF" -> Method.KARACHI_HANAF
            "MUSLIM_LEAGUE" -> Method.MUSLIM_LEAGUE
            "NORTH_AMERICA" -> Method.NORTH_AMERICA
            "UMM_ALQURRA" -> Method.UMM_ALQURRA
            else -> Method.NONE
        }
    }

    fun cancelJobs(){
        job?.cancel()
    }

}