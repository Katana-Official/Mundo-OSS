package lu.die.mundo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import es.mundosoft.sample.BuildCompatUtils

class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        try{
            if(BuildCompatUtils.isAtLeastU())
            {
                startForeground(
                    12580,
                    getNotification(this, "App Is Running", "This is a guardian farm."),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            }
            else startForeground(
                12580,
                getNotification(this, "App Is Running", "This is a guardian farm.")
            )
        }catch (e : Throwable)
        {
            e.printStackTrace()
        }
    }

    private fun getNotification(context: Context, title: String, text: String): Notification {
        val isOngoing = true //是否持续(为不消失的常驻通知)
        val channelName = "App Daemon"
        val channelId = "geth"
        val category = Notification.CATEGORY_SERVICE
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nfIntent = Intent(context, AppTestCloneActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, nfIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setContentIntent(pendingIntent) //设置PendingIntent
            .setSmallIcon(android.R.mipmap.sym_def_app_icon) //设置状态栏内的小图标
            .setContentTitle(title) //设置标题
            .setContentText(text) //设置内容
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //设置通知公开可见
            .setOngoing(isOngoing) //设置持续(不消失的常驻通知)
            .setCategory(category) //设置类别
            .setPriority(NotificationCompat.PRIORITY_MAX) //优先级为：重要通知
        if (BuildCompatUtils.isAtLeastO()) { //安卓8.0以上系统要求通知设置Channel,否则会报错
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET //锁屏显示通知
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(channelId)
        }
        return builder.build()
    }
}