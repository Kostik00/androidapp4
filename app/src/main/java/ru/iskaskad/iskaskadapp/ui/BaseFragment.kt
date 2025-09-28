package ru.iskaskad.iskaskadapp.ui

import android.annotation.SuppressLint
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




    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()

        ISKaskadAPP.sendLogMessage(logTAG, "Попытка установить broadCastReceiver")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION),
                Context.RECEIVER_NOT_EXPORTED
            )
            ISKaskadAPP.sendLogMessage(logTAG, "Использована новая версия registerReceiver")

        } else {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION)
            )
            ISKaskadAPP.sendLogMessage(logTAG, "Использована старая версия registerReceiver")
        }

        ISKaskadAPP.sendLogMessage(logTAG, "Завершена попытка установки broadCastReceiver")


    }

    override fun onPause() {
        try {
            ISKaskadAPP.sendLogMessage(logTAG, "Попытка удалить broadCastReceiver")
            requireContext().unregisterReceiver(broadCastReceiver)
            ISKaskadAPP.sendLogMessage(logTAG, "Попытка удалить broadCastReceiver завершена")
        } catch (e: IllegalArgumentException) {
            ISKaskadAPP.sendLogMessage(logTAG, "Ошибка при удалении broadCastReceiver: ${e.toString()}")
        }

        super.onPause()
    }


    fun playWarning() {
        try {
            ISKaskadAPP.sendLogMessage(logTAG, "Попытка запустить WARNING")
            val notification: Uri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
            ISKaskadAPP.sendLogMessage(logTAG, "Завершена попытка запустить WARNING")
        } catch (e: Exception) {
            e.printStackTrace()
            ISKaskadAPP.sendLogMessage(logTAG, "Error (playWarning): ${e.toString()}")
        }

    }


}