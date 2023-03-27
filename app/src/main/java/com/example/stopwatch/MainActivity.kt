package com.example.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stopwatch.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isStarted = false
    private lateinit var servIntent : Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            run()
        }

        binding.btnReset.setOnClickListener {
            reset()
        }

        servIntent = Intent(applicationContext, StopwatchService::class.java)
        registerReceiver(updateTime, IntentFilter(StopwatchService.UPDATED_TIME))
    }

    private fun run() {
        if (isStarted) {
            stop()
        } else {
            start()
        }
    }

    private fun start() {
        servIntent.putExtra(StopwatchService.CURRENT_TIME, time)
        startService(servIntent)
        binding.btnStart.text = "Stop"
        isStarted = true
    }

    private fun stop() {
        stopService(servIntent)
        binding.btnStart.text = "Start"
        isStarted = false
    }

    private fun reset() {
        stop()
        time = 0.0
        binding.tvStopwatch.text = getFormattedTime(0.0)
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent) {
            time = intent.getDoubleExtra(StopwatchService.UPDATED_TIME, 0.0)
            binding.tvStopwatch.text = getFormattedTime(time)
        }
    }

    private fun getFormattedTime(time: Double): String {
        val timeInt = time.roundToInt()
        val day = 86400
        val hour = 3600
        val hours = timeInt % day / hour
        val minutes = timeInt % day % hour / 60
        val seconds = timeInt % day % hour % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}