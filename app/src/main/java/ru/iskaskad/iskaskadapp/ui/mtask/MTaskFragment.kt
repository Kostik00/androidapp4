package ru.iskaskad.iskaskadapp.ui.mtask

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.databinding.FragmentMtaskListBinding
import ru.iskaskad.iskaskadapp.dto.MTaskInfo
import ru.iskaskad.iskaskadapp.service.MTaskService

/**
 * A fragment representing a list of Items.
 */
class MTaskFragment : Fragment() {
    val LogTAG = "MTaskFragment"

    private var _binding: FragmentMtaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var myAdapter :MTaskAdapter
    private val AppVM: IsKaskadAPPVM by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMtaskListBinding.inflate(inflater, container, false)
        val view = binding.root

        initList()

        AppVM.getPaspPlace().observe(viewLifecycleOwner,
            {
                it?.let {
                    binding.CodPaspPlace.text = it.getCod_Pasp_Place()
                }
            })


        return view
    }



    private fun initList() {

        myAdapter = MTaskAdapter(object : MTaskAdapter.Callback {
            override fun onItemClicked(item: MTaskInfo) {

                // showDialog("Test")

                val builder = AlertDialog.Builder(requireContext())

                with(builder) {
                    val statuses = arrayOf(
                        "Принять к исполнению",
                        "Начать перемещение",
                        "Перемещение выполнено"
                    )

                    setTitle("Выберите статус задания")
                    setItems(statuses) { _, which ->
                        when (which) {
                            0 -> markitem0(item)
                            1 -> markitem1(item)
                            else -> markitem2(item)
                        }

                    }
                    show()
                }


            }
        })

        binding.list.layoutManager = LinearLayoutManager(this.context)
        binding.list.adapter = myAdapter

        AppVM.getMTaskList().observe(viewLifecycleOwner, {
            it?.let {
                myAdapter.setData(it)
            }
        })



    }


    fun markitem0(item: MTaskInfo) {
        //Toast.makeText(context, "Нажата  \"Принять к исполнению\"", Toast.LENGTH_SHORT).show()
        AppVM.updateMTask("-1",item.getStrParam("Key_Pasport"),"1")

    }
    fun markitem1(item: MTaskInfo) {
        //Toast.makeText(context, "Нажата  \"Начать перемещение\"", Toast.LENGTH_SHORT).show()
        AppVM.updateMTask("-1",item.getStrParam("Key_Pasport"),"2")
    }
    fun markitem2(item: MTaskInfo) {
        val NewPlace=AppVM.getPaspPlace().value
        NewPlace?.let {
            val NewCod=NewPlace.getCod_Pasp_Place()
            val NeedPlace=item.getStrParam("Cod_Pln_Pasp_Place")

            if (NewCod.startsWith(NeedPlace))
                AppVM.updateMTask(NewPlace.getKey_Pasp_Place().toString() ,item.getStrParam("Key_Pasport"),"3")
            else {
                playWarning()
                Toast.makeText(
                    context,
                    "Место назначеия не соотвествует требуемому",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        ISKaskadAPP.sendLogMessage(LogTAG, "Resume")

        val filter = IntentFilter(ISKaskadAPP.SCAN_ACTION)

        registerReceiver(this.requireContext() , broadCastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)

        ISKaskadAPP.sendLogMessage(LogTAG, "loadMTaskList")
        AppVM.loadMTaskList()

        kotlin.runCatching {
            requireContext().stopService(Intent(activity, MTaskService::class.java))
        }



    }

    override fun onPause() {

        this.requireContext().unregisterReceiver(broadCastReceiver)
        ISKaskadAPP.sendLogMessage(LogTAG, "Pause")

        val intent = Intent(activity, MTaskService::class.java)

        requireContext().startService(intent)

        super.onPause()
    }



    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            /*
                        val barcodeByteArray = intent!!.getByteArrayExtra(IsKaskadAPP.BARCODE_NAME)
                        val barcodeLength = intent.getIntExtra(IsKaskadAPP.BARCODE_LENGTH, 0)
                        var barcode = String(barcodeByteArray, 0, barcodeLength)
            */
            val barcode = ISKaskadAPP.readBarCode(intent)


            ISKaskadAPP.sendLogMessage(LogTAG, "PARSE BARCODE $barcode")


            when {
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE) -> {
                    val Key_Pasp_Place:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")

                    AppVM.loadPaspPlaceInfo(Key_Pasp_Place)
                }
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP) -> {
                    val Key_Pasp:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP, "")

                    checkRunPasp(Key_Pasp)
                }
                else -> {
                    Toast.makeText(
                        context,
                        "Данный тип штрихкода не поддерживается:'$barcode'",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }


    fun checkRunPasp(Key_Pasp: String){

        val TaskItem  = AppVM.getMTaskList().value?.firstOrNull { it.getParam("Key_Pasport").StrValue ==  Key_Pasp }
        val PlaceItem = AppVM.getPaspPlace().value

        if ((TaskItem != null) && (PlaceItem != null ))
        {
            val Cod_Pasp_Place     = PlaceItem.getCod_Pasp_Place()
            val Cod_Pln_Pasp_Place = TaskItem.getParam("Cod_Pln_Pasp_Place").StrValue

            if (Cod_Pasp_Place.startsWith(Cod_Pln_Pasp_Place)){

                Toast.makeText(context, "Требуется $Cod_Pln_Pasp_Place ложим на $Cod_Pasp_Place", Toast.LENGTH_LONG).show()
                AppVM.updateMTask( PlaceItem.getKey_Pasp_Place().toString() , TaskItem.getParam("Key_Pasport").StrValue, "3")

            }
            else
            {

                playWarning()
            }
        }
        else playWarning()

    }

    private fun playWarning() {
        try {
            val notification: Uri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
            ISKaskadAPP.sendLogMessage(LogTAG, "Error (playWarning): ${e.toString()}")
        }

    }



}