package ru.iskaskad.iskaskadapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.iskaskad.iskaskadapp.ISKaskadAPP

open class BaseFragment : Fragment() {

    open var logTAG: String  = "BaseFragment"

    open fun onBarcode(barCode: String) {
        Toast.makeText(
            context,
            "Данный тип штрихкода не поддерживается:'$barCode'",
            Toast.LENGTH_LONG
        ).show()
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val barcode = ISKaskadAPP.readBarCode(intent)
            ISKaskadAPP.sendLogMessage(logTAG, "PARSE BARCODE $barcode start")
            onBarcode(barcode)
            ISKaskadAPP.sendLogMessage(logTAG, "PARSE BARCODE $barcode finish")
        }

    }




    override fun onResume() {
        super.onResume()

        ISKaskadAPP.sendLogMessage(logTAG, "OnResume Trying to install broadCastReceiver")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION),
                Context.RECEIVER_NOT_EXPORTED
            )
            ISKaskadAPP.sendLogMessage(logTAG, "OnResume new version installed")

        } else {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION)
            )
            ISKaskadAPP.sendLogMessage(logTAG, "OnResume old version installed")
        }

        ISKaskadAPP.sendLogMessage(logTAG, "OnResume end of Trying to install broadCastReceiver")


    }

    override fun onPause() {
        try {
            requireContext().unregisterReceiver(broadCastReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver не был зарегистрирован или уже удалён
        }

        super.onPause()
    }


    fun playWarning() {
        try {
            val notification: Uri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
            ISKaskadAPP.sendLogMessage(logTAG, "Error (playWarning): ${e.toString()}")
        }

    }


}