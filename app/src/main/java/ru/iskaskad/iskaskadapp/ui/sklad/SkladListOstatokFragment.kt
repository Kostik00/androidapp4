package ru.iskaskad.iskaskadapp.ui.sklad

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.encodeStr
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.SkladFragmentAdapter
import ru.iskaskad.iskaskadapp.dto.SkladFragmentInfo
import java.util.*

//@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SkladListOstatokFragment : Fragment() {

    val LogTAG="SkladListOstatokFragment"

    private val AppVM: IsKaskadAPPVM by activityViewModels()


    private lateinit var mainActivity : MainActivity
    private lateinit var MoveOstItemSwitch: SwitchCompat
    private lateinit var ParamBundle :Bundle
    private lateinit var root :View



    private lateinit var rvFragmentList: RecyclerView
    private lateinit var adapter_FragmentList: SkladFragmentAdapter


    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            val barcode = ISKaskadAPP.readBarCode(intent)

            ISKaskadAPP.sendLogMessage(LogTAG, "PARSE BARCODE $barcode")

            when {
                (barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE)) -> {
                    val Key_Pasp_Place: String =
                        barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")

                    if (MoveOstItemSwitch.isChecked) {

                        var KeyNaclStrList = ""
                        var KeyNaclStrSostList = ""

                        adapter_FragmentList.items.forEach {
                            if (it.IsChecked) {
                                val Param = it.getParam("Key_Nacl_Str_Sost")
                                if (Param.ParamIsNull)
                                    KeyNaclStrList =
                                        KeyNaclStrList + "," + it.getParam("Key_Nacl_Str").IntVal.toString()
                                else
                                    KeyNaclStrSostList =
                                        KeyNaclStrSostList + "," + Param.IntVal.toString()
                            }
                        }

                        KeyNaclStrList = KeyNaclStrList.replaceFirst(",", "")
                        KeyNaclStrSostList = KeyNaclStrSostList.replaceFirst(",", "")

                        AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                    }
                    else {
                        ParamBundle = Bundle()
                        ParamBundle.putString("Key_Pasp_Place", Key_Pasp_Place)

                        runSearch()
                    }


                }
                (barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR)) -> {
                    val Key_Nacl_Str: String =
                        barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR, "")

                    if (ParamBundle.containsKey("Key_Pasp_Place") && MoveOstItemSwitch.isChecked) {
                        val KeyNaclStrList: String = Key_Nacl_Str
                        val KeyNaclStrSostList = ""
                        val Key_Pasp_Place  =  ParamBundle.getString("Key_Pasp_Place") ?:""

                        AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                    }
                    else {
                        ParamBundle = Bundle()
                        ParamBundle.putString("Key_Nacl_Str", Key_Nacl_Str)

                        runSearch()
                    }

                }
                (barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST)) -> {
                    val Key_Nacl_Str_Sost: String =
                        barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST, "")

                    if (ParamBundle.containsKey("Key_Pasp_Place") && MoveOstItemSwitch.isChecked) {
                        val KeyNaclStrList = ""
                        val KeyNaclStrSostList: String = Key_Nacl_Str_Sost
                        val Key_Pasp_Place=ParamBundle.getString("Key_Pasp_Place")!!

                        AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                    }
                    else {
                        ParamBundle = Bundle()
                        ParamBundle.putString("Key_Nacl_Str_Sost", Key_Nacl_Str_Sost)

                        runSearch()
                    }
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


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        root  = inflater.inflate(R.layout.sklad_list_ostatok_fragment, container, false)

        mainActivity = activity as MainActivity

        ParamBundle = requireArguments()

        setHasOptionsMenu(true)




        initList()

        return root
    }

    fun FillCaption()  {
        // -- TODO:  Исправить caption
//        if (ParamBundle.containsKey("Key_Pasp_Place") ) {
//            if (adapter_FragmentList.items.count() > 0) {
//                val P = adapter_FragmentList.items[0]
//                mainActivity.TBTitle = P.getParam("Cod_Pasp_Place").StrValue
//            }
//            else
//            {
//                mainActivity.TBTitle = "Данных не найдено"
//
//            }
//        }
//        else
//            mainActivity.TBTitle = "Результаты поиска"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppVM.getFragmentList().observe( viewLifecycleOwner,
            {
                it?.let {
                    //Cod_Pasp_Place.text = it.Cod_Pasp_Place()
                    adapter_FragmentList.setData(it)

                    FillCaption()
                }
            })

    }

    private var FirstTimeQRY:Boolean=true

    private fun initList() {
        rvFragmentList = root.findViewById(R.id.fragmentListRV)
        rvFragmentList.layoutManager = LinearLayoutManager(this.context)

        adapter_FragmentList = SkladFragmentAdapter( AppVM.getFragmentList().value ?: ArrayList<SkladFragmentInfo> () ,  object :  SkladFragmentAdapter.Callback
        {
            override fun onItemClicked(item: SkladFragmentInfo)
            {
                val bundle = Bundle()
                bundle.putSerializable("ItemInfo",item)

                AppVM.SelectedFragment = item

                findNavController().navigate(R.id.action_skladListOstatokFragment_to_skladInventOstatok, bundle)
            }
        })
        rvFragmentList.adapter = adapter_FragmentList
        if (FirstTimeQRY)
            runSearch()


        FirstTimeQRY = false
    }

    fun runSearch(  ) {

        AppVM.loadSkladOstatokList( getSearchStr() )

    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.skladfragmentmenu, menu)

        val MoveSearchItem = menu.findItem(R.id.MoveOstItemSwitch)
        // -- todo Исправить
        MoveOstItemSwitch =  MoveSearchItem.actionView?.findViewById(R.id.switchid) as SwitchCompat
    }

    private fun getSearchStr():String
    {

        var request = ""


        if (ParamBundle.containsKey("Key_Pasp_Place"))
            request += "&Key_Pasp_Place=" + encodeStr(ParamBundle.getString("Key_Pasp_Place"))

        if (ParamBundle.containsKey("StrMark"))
            request += "&StrMark=" + encodeStr(ParamBundle.getString("StrMark"))

        if (ParamBundle.containsKey("Key_Predm"))
            request += "&Key_Predm=" + encodeStr(ParamBundle.getString("Key_Predm"))

        if (ParamBundle.containsKey("Key_ID_Mat"))
            request += "&Key_ID_Mat=" + encodeStr(ParamBundle.getString("Key_ID_Mat"))

        if (ParamBundle.containsKey("RegN"))
            request += "&RegN=" + encodeStr(ParamBundle.getString("RegN"))

        if (ParamBundle.containsKey("N_Sert"))
            request += "&N_Sert=" + encodeStr(ParamBundle.getString("N_Sert"))

        if (ParamBundle.containsKey("Cod_Plavka"))
            request += "&Cod_Plavka=" + encodeStr(ParamBundle.getString("Cod_Plavka"))

        if (ParamBundle.containsKey("Party"))
            request += "&Party=" + encodeStr(ParamBundle.getString("Party"))

        if (ParamBundle.containsKey("Name_Predm"))
            request += "&Name_Predm=" + encodeStr(ParamBundle.getString("Name_Predm"))

        if (ParamBundle.containsKey("Sortam"))
            request += "&Sortam=" + encodeStr(ParamBundle.getString("Sortam"))

        if (ParamBundle.containsKey("Marka"))
            request += "&Marka=" + encodeStr(ParamBundle.getString("Marka"))

        if (ParamBundle.containsKey("Key_Nacl_Str"))
            request += "&Key_Nacl_Str=" + encodeStr(ParamBundle.getString("Key_Nacl_Str"))

        if (ParamBundle.containsKey("Key_Nacl_Str_Sost"))
            request += "&Key_Nacl_Str_Sost=" + encodeStr(ParamBundle.getString("Key_Nacl_Str_Sost"))

        if (ParamBundle.containsKey("Cod_Pasp_Place"))
            request += "&Cod_Pasp_Place=" + encodeStr(ParamBundle.getString("Cod_Pasp_Place" ))

        return  request
    }





    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        ISKaskadAPP.sendLogMessage(LogTAG, "OnResume")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION),
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION)
            )
        }
    }


    override fun onPause() {
        ISKaskadAPP.sendLogMessage(LogTAG, "OnPause")

        try {
            requireContext().unregisterReceiver(broadCastReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver не был зарегистрирован или уже удалён
        }

        super.onPause()
    }


}