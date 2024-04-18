package com.example.strongestmanalive.data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import com.example.strongestmanalive.sensors.AndroidSensor
import com.example.strongestmanalive.sensors.MainSensor

interface AppContainer {
    val accelerationSensor: MainSensor
    val gyroSensor: MainSensor
}

class AppdataContainer(private val context: Context) : AppContainer {

    override val accelerationSensor: MainSensor by lazy {
        AndroidSensor(
            context,
            sensorFeature = PackageManager.FEATURE_SENSOR_ACCELEROMETER,
            sensorType = Sensor.TYPE_LINEAR_ACCELERATION
        )
    }

    override val gyroSensor: MainSensor by lazy {
        AndroidSensor(
            context,
            sensorFeature = PackageManager.FEATURE_SENSOR_STEP_COUNTER,
            sensorType = Sensor.TYPE_STEP_COUNTER
        )
    }

}