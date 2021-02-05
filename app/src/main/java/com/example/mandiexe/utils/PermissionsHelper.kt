package com.example.mandiexe.utils

import android.Manifest
import android.app.Activity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

object PermissionsHelper {

    fun requestStoragePermissions(activity: Activity): Observable<Boolean> {
        val rxPermissions = RxPermissions(activity)

        return Observable.create { emitter ->
            rxPermissions.request(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE

            )
                .compose(
                    rxPermissions.ensureEachCombined(

                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE                    )
                )
                .subscribeBy { permission ->
                    emitter.onNext(permission.granted)
                }
        }
    }

}