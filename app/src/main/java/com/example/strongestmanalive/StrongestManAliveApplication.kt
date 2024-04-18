package com.example.strongestmanalive

import android.app.Application
import com.example.strongestmanalive.data.AppContainer
import com.example.strongestmanalive.data.AppdataContainer

class StrongestManAliveApplication: Application() {

    var container: AppContainer = AppdataContainer(this)

    override fun onCreate() {
        super.onCreate()
        container = AppdataContainer(this)
        println("Application Created")
    }
}