package com.example.mandiexe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.example.mandiexe.utils.usables.UIUtils

class LocationHelper(context: Context) {

    lateinit var locationManager: LocationManager
    private lateinit var locationGPS: Location
    private lateinit var locationNetwork: Location

    var latGPS: Double = 0.0
    var longGPS: Double = 0.0
    var latNetwork: Double = 0.0
    var longNetwork: Double = 0.0


    @SuppressLint("MissingPermission")
    //Returns a location object to be used
    fun getLocation(context: Context): Location {

        try {


            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                LocationListener {

                override fun onLocationChanged(location: Location) {
                    locationGPS = location

                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String) {

                }

                override fun onProviderDisabled(provider: String) {

                }
            })

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                object :
                    LocationListener {

                    override fun onLocationChanged(location: Location) {
                        locationNetwork = location

                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }
                })


            //Check for the last location
            val localNetworkLocation =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (localNetworkLocation != null) {
                locationNetwork = localNetworkLocation

            }

            val localGpsLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null) {
                locationGPS = localGpsLocation
            }



            Log.e(
                "LocationHelper",
                "LocationHelper and locations are $locationNetwork and $locationGPS"
            )
            if (locationGPS.accuracy >= locationNetwork.accuracy) {
                return locationGPS
            } else {
                return locationNetwork
            }

        } catch (e: Exception) {
            UIUtils.logExceptions(e, "LocationHelper")
        }

        return locationNetwork

    }


}
