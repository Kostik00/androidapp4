package ru.iskaskad.iskaskadapp.ui.sklad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray
import org.json.JSONObject
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.SkladOutMTabAdapter
import ru.iskaskad.iskaskadapp.databinding.FragmentSkladOutDtBinding
import ru.iskaskad.iskaskadapp.dto.SkladIdMatInfo
import java.net.URLEncoder


class fragment_sklad_out_dt : Fragment() {

    private var _binding: FragmentSkladOutDtBinding? = null
    private val binding get() = _binding!!

    private val AppVM : IsKaskadAPPVM by activityViewModels()
    private lateinit var Adapter: SkladOutMTabAdapter;
    private val mainActivity get() =  activity  as MainActivity


    private lateinit var ParamBundle :Bundle

    val LogTAG = "SkladOMOutDt"

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val barcode = ISKaskadAPP.readBarCode(intent)

            when {
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_GR_ZAP) -> {
                    val Key_GrZap:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")
                    runSearchByKey(Key_GrZap.toInt())

                }
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_SUB_PODR) -> {
                    val Key_Sub_Ver:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_SUB_PODR, "")
                    AppVM.loadSubjList("&Key_Sub_Ver=$Key_Sub_Ver")

                }                else -> {
                    Toast.makeText(
                        context,
                        "Данный тип штрихкода не поддерживается:'$barcode'",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSkladOutDtBinding.inflate(inflater, container, false)
        val view = binding.root


        ParamBundle = requireArguments()

        var Key_GrZap = ParamBundle.getInt("Key_GrZap")

        initTabs(Key_GrZap)

        binding.SelSubjBtn.setOnClickListener {
            val Params= Bundle()
            Params.putString("Filter","&Key_Class=1&Cod_Sub=1")
            findNavController().navigate(R.id.action_fragment_sklad_out_dt_to_subjFindFragment, Params)

        }


        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun runSearchByKey( Key_GrZap :Int    ) {
        var SearchStr = "&Key_GrZap=$Key_GrZap"

        AppVM.loadGrZapInfo(SearchStr)
    }

    override fun onResume() {
        super.onResume()
        ISKaskadAPP.sendLogMessage(LogTAG, "OnResume")

        if (ISKaskadAPP.LOGIN_ID == "")
        {
            val navController = findNavController()
            val Params= Bundle()
            Params.putInt(ISKaskadAPP.REQUEST_PARAM_RUNMODE, R.id.radio_Sklad)
            navController.navigate(R.id.nav_login, Params)
        }

        context?.registerReceiver(broadCastReceiver, IntentFilter(ISKaskadAPP.SCAN_ACTION))
    }

    override fun onPause() {
        ISKaskadAPP.sendLogMessage(LogTAG, "OnPause")

        context?.unregisterReceiver(broadCastReceiver)

        super.onPause()
    }



    private fun initTabs(Key_GrZap :Int ) {
        //AppVM.clearGrZapInfo()

        AppVM.GetSelectedSubjInfo().observe(viewLifecycleOwner
        ) {
            it?.let{
                binding.SubSkladName.setText(it.getF_Name_Sub())

            } ?: run    {
                binding.SubSkladName.setText("Не выбран")
            }

        }


        Adapter = SkladOutMTabAdapter (
            {
                fun onItemClicked(item: SkladIdMatInfo)        {

                }
            }
        )

        binding.pager.adapter = Adapter



        AppVM.getGrZapInfo().observe(viewLifecycleOwner
        ) {

            it?.let{
//                        val cnt = it.IdMatInfo.count()
//                        val toast = Toast.makeText(
//                            getActivity(),
//                            "Материалов: ($cnt)!", Toast.LENGTH_SHORT
//                        )
//                        toast.show()

                        Adapter.SetData(it.IdMatInfo)
                // --todo  title
                       // mainActivity.TBTitle = "Зап.№${it.getStrParam("Key_GrZap")} выдача ОМ"
            } ?: run {
                Adapter.SetData( ArrayList<SkladIdMatInfo>() )
            }




        }

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = "Поз. $position"

            if (Adapter.GetData().count() >= position) {
                tab.text = Adapter.GetData()[position].getStrParam("RegN")
            }
        }.attach()

        runSearchByKey(Key_GrZap)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.skladoutdtmenu, menu)

        var m: MenuItem =    menu.findItem(R.id.MakeNaclItem)

        m.setOnMenuItemClickListener {
            if (AppVM.GetSelectedSubjInfo().value != null) {
                     val JsonRoot = JSONObject()
                     JsonRoot.put("Key_Sub_Ver",AppVM.GetSelectedSubjInfo().value?.GetIntParam("Key_Sub_Ver"))

                     AppVM.getGrZapInfo().value?.let {
                         JsonRoot.put("Key_GrZap", it.GetIntParam("Key_GrZap"))

                         val arr = JSONArray()

                         it.IdMatInfo.forEach {
                             it.IdMatItems.forEach {
                                 if (it.GetIntParam("Cnt")>0 ) {
                                     val elementi = JSONObject()
                                     elementi.put("Key_Nacl_Str_Sost",it.GetIntParam("Key_Nacl_Str_Sost"))
                                     elementi.put("Cnt",it.GetIntParam("Cnt"))
                                     arr.put(elementi)
                                 }
                             }

                         }

                         if (arr.length()>0)
                         {

                             JsonRoot.put("sostav", arr)


                             var RunStr = "&JSONStr=${ISKaskadAPP.encodeStr(JsonRoot.toString())}"
                             AppVM.SkladRunGrZap(RunStr)

                             Toast.makeText(
                                 this.context,
                                 "Запущен процесс создания накладных",
                                 Toast.LENGTH_SHORT
                             ).show()

                             findNavController().navigateUp()
                         }

                }
            }
            else
            {
                //Toast.makeText( context, "Запускаем выбор получателя", Toast.LENGTH_SHORT).show()

                val Params= Bundle()
                Params.putString("Filter","&Key_Class=1")
                findNavController().navigate(R.id.action_fragment_sklad_out_dt_to_subjFindFragment, Params)
            }
           true
        }
        false
    }


}