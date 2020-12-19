package com.nir.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import com.nir.library.ISensorService
import com.nir.library.SensorService
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    private lateinit var sensorDataText: TextView
    private var sensorService: ISensorService? = null
    private var serviceConnection: ServiceConnection? = null
    private var serviceIntent: Intent? = null
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorDataText = findViewById(R.id.sensorTV)
        bindSensorService()
    }

    /**
     * Reads sensor data after every 8 milliseconds
     */
    private fun registerForSensorData() {
        timer = fixedRateTimer("SENSOR_TIMER", false, initialDelay = 100, period = 8) {
            sensorService?.sensorData?.let {
                this@MainActivity.runOnUiThread {
                    // Show updated sensor data inside text view
                    sensorDataText.text = it
                }
            }
        }
    }

    private fun bindSensorService() {
        serviceIntent = Intent(this, SensorService::class.java)

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                // Retrieves sensor service
                sensorService = ISensorService.Stub.asInterface(binder)
                registerForSensorData()
            }
            override fun onServiceDisconnected(componentName: ComponentName?) {
                sensorService = null
            }
        }

        serviceIntent?.let {
            serviceConnection?.let {
                bindService(
                    serviceIntent,
                    it,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let { unbindService(it) }
        timer.cancel()
    }
}