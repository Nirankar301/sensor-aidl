package com.nir.library

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder

/**
 * Service responsible for providing sensor data
 */
class SensorService : Service(), SensorEventListener {

    private val samplingPeriod = 8 * 1000 // 8 milliseconds
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    // Stores data of TYPE_ROTATION_VECTOR sensor
    private lateinit var data: String

    private fun initSensorManager() {
        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            sensor?.let { addSensorListener() }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initSensorManager()
    }

    private fun addSensorListener() {
        sensorManager?.registerListener(
            this,
            sensor,
            samplingPeriod
        )
    }

    private val myBinder: ISensorService.Stub = object : ISensorService.Stub() {
        override fun getSensorData(): String {
            return data
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                data = it.values.contentToString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this, sensor)
    }
}