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


    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationGPS: Location? = null
    private var locationNetwork: Location? = null


    @SuppressLint("MissingPermission")
    //Returns a location object to be used
    fun getLocation(context: Context): Location? {

        try {


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
            if (locationGPS != null && locationNetwork == null) {
                return locationGPS
            } else if (locationGPS == null && locationNetwork != null) {
                return locationNetwork
            } else if (locationNetwork != null && locationGPS != null) {
                if (locationGPS!!.accuracy >= locationNetwork!!.accuracy) {
                    return locationGPS
                } else {
                    return locationNetwork
                }
            }

        } catch (e: Exception) {
            UIUtils.logExceptions(e, "Location Helper")
        }

        return locationNetwork

    }

}
