package com.sensis.inyectlistffx1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "sensis_good"
    private val SHIZUKU_CODE = 200
    lateinit var prefs: SharedPreferences
    lateinit var ui: MainUI
    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        prefs = getSharedPreferences("SensisPrefs", Context.MODE_PRIVATE)
        createNotificationChannel()
        ui = MainUI(this)
        setContentView(ui.rootView)
        setupShizuku()
    }

    private fun setupShizuku() {
        Shizuku.addRequestPermissionResultListener { code, result ->
            if (code == SHIZUKU_CODE)
                ui.setShizukuStatus(result == PackageManager.PERMISSION_GRANTED)
        }
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
                ui.setShizukuStatus(true)
            else Shizuku.requestPermission(SHIZUKU_CODE)
        } catch (e: Exception) { ui.setShizukuStatus(false) }
    }

    fun runInjection(type: String) {
        Thread {
            val commands = CommandBlocks.get(type)
            val total = commands.size
            var ok = 0; var fail = 0
            commands.forEachIndexed { i, cmd ->
                handler.post { ui.logLine(cmd, i + 1, total) }
                try { execCmd(cmd); ok++ }
                catch (e: Exception) { handler.post { ui.logError(cmd) }; fail++ }
                Thread.sleep(10)
            }
            handler.post {
                ui.onDone(type, ok, fail)
                sendNotif("✅ SENSIS GOOD", "$type → $ok OK · $fail errores")
            }
        }.start()
    }

    fun execCmd(cmd: String) {
        val proc = Runtime.getRuntime().exec(arrayOf("sh", "-c", cmd))
        proc.waitFor()
        proc.destroy()
    }

    fun sendNotif(title: String, msg: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title).setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true)
            .also { NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), it.build()) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "SENSIS GOOD", NotificationManager.IMPORTANCE_HIGH)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }
}
