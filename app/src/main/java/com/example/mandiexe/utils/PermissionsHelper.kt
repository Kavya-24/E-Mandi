package com.example.mandiexe.utils

import android.Manifest
import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

object PermissionsHelper {

    fun requestMapsPermissions(activity: Activity): Observable<Boolean> {
        val rxPermissions = RxPermissions(activity)

        return Observable.create { emitter ->
            rxPermissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,


                )
                .compose(
                    rxPermissions.ensureEachCombined(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE
                    )
                )
                .subscribeBy { permission ->
                    emitter.onNext(permission.granted)
                }
        }
    }

    fun requestCallsAndMessagePermissions(activity: Activity): Observable<Boolean> {
        val rxPermissions = RxPermissions(activity)

        return Observable.create { emitter ->
            rxPermissions.request(
                Manifest.permission.CALL_PHONE)
                .compose(
                    rxPermissions.ensureEachCombined(

                        Manifest.permission.CALL_PHONE)
                )
                .subscribeBy { permission ->
                    emitter.onNext(permission.granted)
                }
        }
    }

}