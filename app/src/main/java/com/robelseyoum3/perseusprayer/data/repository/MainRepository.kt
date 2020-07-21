package com.robelseyoum3.perseusprayer.data.repository

import androidx.lifecycle.LiveData
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.robelseyoum3.perseusprayer.data.model.PrayerTimes
import com.robelseyoum3.perseusprayer.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.security.interfaces.RSAKey
import java.util.*

object MainRepository {

    var job: CompletableJob? = null


    fun getPrayersTimes(_coordination: MutableMap<String, Double>): LiveData<Resource<PrayerTimes>> {
        job = Job()

        val today = SimpleDate(GregorianCalendar())
        val location = Location(
            _coordination["latitude"] ?: 0.0,
            _coordination["longitude"] ?: 0.0,
            2.0,
            0
        )

        val azan = Azan(location, Method.EGYPT_SURVEY)
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

    fun cancelJobs(){
        job?.cancel()
    }

}