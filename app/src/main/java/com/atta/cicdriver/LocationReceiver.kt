package com.atta.cicdriver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.atta.cicdriver.ui.home.HomeFragment
import com.google.android.gms.location.LocationResult
import java.lang.Exception


class LocationReceiver : BroadcastReceiver(){
    companion object{
        const val ACTION_LOCATION_UPDATE = "com.atta.cicdriver.updateLocation"
    }
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent != null){
            if (intent.action.equals(ACTION_LOCATION_UPDATE)){
                val result =  LocationResult.extractResult(intent)
                if (result != null){
                    val location = result.lastLocation
                    val locationString = "${location.latitude} , ${location.longitude}"
                    try {
                        HomeFragment.getHomeInstance()?.updateLocationOnMap(location)
                    }catch (e: Exception){
                        if(HomeFragment.getHomeInstance() != null){
                            HomeFragment.getHomeInstance()?.updateLocationOnMap(location)
                        }else{
                            Toast.makeText(context, locationString, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }


}