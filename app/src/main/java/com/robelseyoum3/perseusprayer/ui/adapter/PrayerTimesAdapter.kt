package com.robelseyoum3.perseusprayer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robelseyoum3.perseusprayer.R
import com.robelseyoum3.perseusprayer.data.model.PrayerTimes
import com.robelseyoum3.perseusprayer.ui.adapter.viewholder.PrayerTimesViewHolder
import java.text.SimpleDateFormat
import java.util.*

class PrayerTimesAdapter constructor(val prayerTimes: MutableList<PrayerTimes>) : RecyclerView.Adapter<PrayerTimesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerTimesViewHolder  =
        PrayerTimesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.prayer_times, parent, false))

    override fun getItemCount(): Int = prayerTimes.size

    override fun onBindViewHolder(holder: PrayerTimesViewHolder, position: Int) {
        holder.fajarTime.text = prayerTimes[position].mFajr
        holder.sunriseText.text = "SUNRISE at ${prayerTimes[position].mSunrise}"
        holder.dhuhrTime.text =  prayerTimes[position].mZuhr
        holder.asarTime.text = prayerTimes[position].mAsr
        holder.maghribTime.text = prayerTimes[position].mMaghrib
        holder.ishaTime.text = prayerTimes[position].mISHA
    }

    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)


}