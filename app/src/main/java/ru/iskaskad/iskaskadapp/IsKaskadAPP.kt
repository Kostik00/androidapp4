package ru.iskaskad.iskaskadapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date


class ISKaskadAPP : Application() {


    companion object  {

        lateinit var sharedPreferences: SharedPreferences
        private lateinit var onPrefChanged: SharedPreferences.OnSharedPreferenceChangeListener

        const val SCAN_ACTION       = "scan.rcv.message"
        const val BARCODE_NAME      = "barcode"
        const val BARCODE_LENGTH    = "length"

        private const val APP_LOG_TAG = "iskaskadapp2546754."
        private const val LOG_TAG = "APP"

        private const val LOGIN_ID_STR = "?LoginID="


        const val REQUEST_PARAM_LOGIN_ID :String = "LOGIN_ID"
        const val REQUEST_PARAM_RUNMODE :String  = "RUNMODE"

        const val CHANNEL_ID = "ru.iskaskad.channel_1"

        const val URL_CHECKPASSWORD      = "checkpassword.php"
        const val URL_RESULT_SUCCESS      = "SUCCESS"

        const val URL_PASPINFO_FINDPASP  = "paspinfo_findpasp.php"
        const val URL_PASPINFO_GETINFO   = "paspinfo_getinfo.php"


        const val URL_PASPPLACE_GETINFO   = "place_get_info.php"

        const val URL_SKLAD_FRAGMENT_FIND   = "sklad_fragment_find.php"
        const val URL_SKLAD_FRAGMENT_INFO   = "sklad_fragment_info.php"
        const val URL_SKLAD_FRAGMENT_MOVE   = "sklad_fragment_move.php"
        const val URL_SKLAD_FRAGMENT_INVENT = "sklad_fragment_invent.php"

        const val URL_SKLAD_GRZAP_FIND      = "sklad_grzap_find.php"
        const val URL_SKLAD_GRZAP_INFO      = "sklad_grzap_info.php"
        const val URL_SKLAD_RUN_GRZAP       = "sklad_run_grzap.php"

        const val URL_SUBJ_FIND           = "subj_find.php"


        const val URL_MTASK_GETLIST       = "mtask_get_list.php"
        const val URL_MTASK_SETSTATUS     = "mtask_set_status.php"
        const val URL_MTASK_GETSTATISTICS = "mtask_get_statistics.php"

        const val BARCODE_DATA_KEY_PASP = "0 02 "
        const val BARCODE_DATA_KEY_PASP_PLACE = "1 4 "
        const val BARCODE_DATA_KEY_NACL_STR = "1 7 "
        const val BARCODE_DATA_KEY_NACL_STR_SOST = "1 8 "
        const val BARCODE_DATA_GR_ZAP = "0 7 "

        const val BARCODE_DATA_KEY_SUB_PODR = "1 1 "



        var LOGIN_ID:String = ""
        var UPDATE_TIMEOUT : Long = 20000
        var RunMode:Int=0
        var ConnectionStr:String = ""
        //var ShowScanButton :Boolean = false


     //   val SqlFormat     = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSSSSS")
     //   var UsrDateFormat = DateTimeFormatter.ofPattern("uuuu-MM-dd")

        private val SQLSdf =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        private val UsrSdf =  SimpleDateFormat("dd.MM.yyyy")


        fun convertSQLDateTimeToDate(StrDt:String): Date? {
            return SQLSdf.parse(StrDt)
        }

        fun myDateToStr(Dt:Date): String? {
             return UsrSdf.format(Dt)
        }

        fun sendLogMessage( AdTAG:String , Text:String )
        {
            Log.d( APP_LOG_TAG + AdTAG, Text)
        }

        fun makeURLStr(URL: String, requestParams: String, LOGIN_ID:String):String  {

            val resultStr: String = ConnectionStr + URL + LOGIN_ID_STR + encodeStr(LOGIN_ID) +  requestParams
            sendLogMessage(LOG_TAG+"_URL_GEN", resultStr)
            return resultStr
        }

        fun makeURLStr(URL: String, requestParams: String): String {
            //val ResultStr: String = ConnectionStr + URL + LOGIN_ID_STR + EncodeStr(LOGIN_ID) +  requestParams;
            //sendLogMessage(LOG_TAG+"_URL_GEN", ResultStr)
            return makeURLStr(URL,requestParams, LOGIN_ID )
        }


        fun encodeStr(Str: String?): String {
            var rslt = Str ?:""

            try {
                rslt = URLEncoder.encode(rslt, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                sendLogMessage(LOG_TAG+"_EncodeStr_ERROR", rslt)
                e.printStackTrace()
            }
            return rslt
        }

        fun readBarCode(intent: Intent?) :String {

            var barcode = intent!!.getStringExtra(BARCODE_NAME).toString()

                // это убираем последний символ контрольной суммы
            if (barcode.length > 2 )
                barcode = barcode.substring(0,barcode.length-1)


            return barcode
        }


    }

    private lateinit var urlKey :String
    private lateinit var syncPeriodKey:String
    private  var deflong :Long = 50

    private fun loadPref() {

        sendLogMessage(LOG_TAG, "LoadPref")

        ConnectionStr = sharedPreferences.getString(urlKey, getString(R.string.url_default)) ?: getString(R.string.url_default)

        val tmpUpdateTimeout = sharedPreferences.getString(syncPeriodKey,null)
        UPDATE_TIMEOUT=  (tmpUpdateTimeout?.toLongOrNull() ?: deflong) * 1000

        sendLogMessage(LOG_TAG, "LoadPref URL=$ConnectionStr")
        sendLogMessage(LOG_TAG, "LoadPref Timeout=$UPDATE_TIMEOUT")

    }



    override fun onCreate() {
        super.onCreate()

        urlKey = getString(  R.string.url_key )
        syncPeriodKey =  getString( R.string.mtask_sync_period_key )
        deflong = getString(R.string.mtask_sync_period_default).toLong()

        sendLogMessage(LOG_TAG, "APP onCreate")

        createNotificationChannels()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this )

        onPrefChanged = SharedPreferences.OnSharedPreferenceChangeListener { _ , key ->
            if ((key == urlKey) || (key == syncPeriodKey))
                loadPref()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(onPrefChanged)

        loadPref()
    }


    override fun onTerminate() {
        sendLogMessage(LOG_TAG, "APP onTerminate")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onPrefChanged)
        super.onTerminate()
    }



    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_ID,
                "ИС КАСКАД",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Канал оповещение ИС КАСКАД"
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel1)
        }
    }

//    override fun registerReceiver(
//        receiver: BroadcastReceiver?,
//        filter: IntentFilter?
//    ): Intent? {
//        if (Build.VERSION.SDK_INT >= 34 && applicationInfo.targetSdkVersion >= 34) {
//            return super.registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
//        } else {
//            return super.registerReceiver(receiver, filter)
//        }
//    }

}