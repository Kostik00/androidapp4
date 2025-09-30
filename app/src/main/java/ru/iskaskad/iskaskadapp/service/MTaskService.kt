package ru.iskaskad.iskaskadapp.service


import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.UPDATE_TIMEOUT
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.R
import java.net.URL


class MTaskService : Service() {

    lateinit var nm : NotificationManager

    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ISKaskadAPP.sendLogMessage(LogTAG, "Service created")
    }

    val LogTAG = "MTaskService"

    override fun onBind(intent: Intent): IBinder? {
        TODO("Return the communication channel to the service.")
    }

    private var mTaskJob:Job = Job()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        mTaskJob.cancel()

        mTaskJob = GlobalScope.launch  @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
            ISKaskadAPP.sendLogMessage(LogTAG, "Job is started ")
            while (isActive) {

                try {
                    ISKaskadAPP.sendLogMessage(LogTAG, "Waiting for delay $UPDATE_TIMEOUT")
                    delay( UPDATE_TIMEOUT )
                    ISKaskadAPP.sendLogMessage(LogTAG, "Delay complete ")
                    val notifyStr = loadMTaskStatistic()
                    if (notifyStr != "") {
                        ISKaskadAPP.sendLogMessage(LogTAG, "Notification string not empty, sending notification")
                        sendOnChannel1(notifyStr)
                    } else {
                        ISKaskadAPP.sendLogMessage(LogTAG, "Notification string empty, no notification sent")
                        // Не вызываем startForeground с null-уведомлением
                        // Можно добавить nm.cancel(1) если нужно убрать старое уведомление
                        nm.cancel(1)
                    }
                } catch (e:Exception) {
                    ISKaskadAPP.sendLogMessage(LogTAG, "Exception  $e")
                }

            }
            ISKaskadAPP.sendLogMessage(LogTAG, "Job is stoped ")
        }
        ISKaskadAPP.sendLogMessage(LogTAG, "Service started ")

        return START_NOT_STICKY
    }

    private fun loadMTaskStatistic () :String  {
        ISKaskadAPP.sendLogMessage(LogTAG, "Load Statistics ")

        var notificationOutput  = ""

        val UrlStr = ISKaskadAPP.makeURLStr(ISKaskadAPP.URL_MTASK_GETSTATISTICS, "")

        val ResultStr = URL(UrlStr).readText()
        val item = JSONObject(ResultStr)

        var SkipNotify = true
        notificationOutput += "Ожидают:" + item.getInt("Status0")
        if (item.getInt("Status0") > 0)
            SkipNotify = false

        notificationOutput += "  Принято:" + item.getInt("Status1")
        if (item.getInt("Status1") > 0)
            SkipNotify = false

        notificationOutput += "  В работе:" + item.getInt("Status2")
        if (item.getInt("Status2") > 0) // Исправлено: было Status1
            SkipNotify = false

        if (SkipNotify)
            notificationOutput = ""

        ISKaskadAPP.sendLogMessage(LogTAG, "Load Statistics OK")

        return notificationOutput
    }



    override fun onDestroy() {
        runBlocking {
            mTaskJob.cancelAndJoin()
        }
        ISKaskadAPP.sendLogMessage(LogTAG, "Service destroyed ")
        super.onDestroy()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendOnChannel1(Txt: String) {

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        //val bitmap = AppCompatResources.getDrawable(this, R.mipmap.ic_launcher)?.toBitmap()

        val goToAppIntent = Intent(this, MainActivity::class.java)

        goToAppIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        goToAppIntent.putExtra(ISKaskadAPP.REQUEST_PARAM_LOGIN_ID, ISKaskadAPP.LOGIN_ID)
        // todo Подправить
            //   goToAppIntent.putExtra(ISKaskadAPP.REQUEST_PARAM_RUNMODE, R.id.radio_MTask)

        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            goToAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, ISKaskadAPP.CHANNEL_ID)
            .setContentText(Txt)
            .setContentTitle("Заявки")
            .setWhen(System.currentTimeMillis())
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_logo)
            .setVibrate(longArrayOf(0, 300, 0, 300, 0, 300))
            .build()

        try {
            nm.notify( 1, notification)
            ISKaskadAPP.sendLogMessage(LogTAG, "Notification send ")
        } catch (e:Exception) {
            ISKaskadAPP.sendLogMessage(LogTAG, "Notify Exception  $e")
        }
    }

    override fun stopService(name: Intent?): Boolean {
        mTaskJob.cancel()

        return super.stopService(name)
    }



}
