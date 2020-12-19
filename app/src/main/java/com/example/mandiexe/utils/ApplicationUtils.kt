package com.example.mandiexe.utils

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ApplicationUtils : Application() {

    companion object {

        lateinit var instance: ApplicationUtils


        fun getApplication(): ApplicationUtils {
            return instance
        }

        fun getContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        //Firebase init also
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)


    }
}