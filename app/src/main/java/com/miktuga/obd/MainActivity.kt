package com.miktuga.obd

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.miktuga.design.feedback.FeedbackLauncher
import com.miktuga.design.settings.TugaSetting
import com.miktuga.design.settings.TugaSettingsClient
import com.miktuga.design.settings.UnitsSpeedValue
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var demoMode = true
    private var startTime = System.currentTimeMillis()

    private lateinit var textSpeed: TextView
    private lateinit var textSpeedUnit: TextView
    private lateinit var textRpm: TextView
    private lateinit var textCoolant: TextView
    private lateinit var textFuel: TextView
    private lateinit var textVoltage: TextView
    private lateinit var textConnState: TextView

    private val updater = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textSpeed = findViewById(R.id.textSpeed)
        textSpeedUnit = findViewById(R.id.textSpeedUnit)
        textRpm = findViewById(R.id.textRpm)
        textCoolant = findViewById(R.id.textCoolant)
        textFuel = findViewById(R.id.textFuel)
        textVoltage = findViewById(R.id.textVoltage)
        textConnState = findViewById(R.id.textConnState)

        findViewById<Button>(R.id.buttonConnect).setOnClickListener {
            showConnectDialog()
        }
        findViewById<View>(R.id.buttonOverflow).setOnClickListener(::showOverflowMenu)
    }

    private fun showOverflowMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add("Обратная связь")
        popup.setOnMenuItemClickListener {
            FeedbackLauncher.launch(this, packageName, appVersionName())
            true
        }
        popup.show()
    }

    private fun appVersionName(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "0.0.0"
    }.getOrDefault("0.0.0")

    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()
        handler.post(updater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updater)
    }

    private fun tick() {
        if (!demoMode) return
        val t = (System.currentTimeMillis() - startTime) / 1000.0
        // Smooth oscillating values for demo
        val speedKmh = (50 + 40 * sin(t * 0.3)).toInt().coerceIn(0, 220)
        val rpm = (1500 + 1200 * abs(sin(t * 0.45)) + Random.nextInt(-80, 80))
            .toInt().coerceIn(700, 6000)
        val coolant = (85 + 8 * sin(t * 0.1)).toInt().coerceIn(60, 110)
        val fuel = (60 + 20 * sin(t * 0.05)).toInt().coerceIn(5, 100)
        val volts = 13.8 + 0.4 * sin(t * 0.7)

        val (displaySpeed, speedLabel) = when (TugaSettingsClient.get(this, TugaSetting.UnitsSpeed)) {
            UnitsSpeedValue.MPH -> (speedKmh * 0.621371f).toInt() to "mph"
            UnitsSpeedValue.KMH -> speedKmh to "км/ч"
        }
        textSpeed.text = "$displaySpeed"
        textSpeedUnit.text = speedLabel
        textRpm.text = "$rpm"
        textCoolant.text = "$coolant"
        textFuel.text = "$fuel"
        textVoltage.text = "Напряжение АКБ: %.1f В".format(volts)
    }

    private fun showConnectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Подключение к OBD-II")
            .setMessage(
                "Сейчас поддерживается только DEMO-режим.\n\n" +
                "Для подключения к реальному адаптеру (ELM327 / ELS27) " +
                "понадобится Bluetooth-сопряжение и реализация AT-команд. " +
                "Будет добавлено в v0.2.0."
            )
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .show()
    }
}
