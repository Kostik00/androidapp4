package ru.iskaskad.iskaskadapp.ui.sklad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.iskaskad.iskaskadapp.ui.BaseFragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

class fragment_sklad_out_dt : BaseFragment() {
    override var logTAG = "SkladOMOutDt"

    private var _binding: FragmentSkladOutDtBinding? = null
    private val binding get() = _binding!!

    private val AppVM : IsKaskadAPPVM by activityViewModels()
    private lateinit var Adapter: SkladOutMTabAdapter
    private val mainActivity get() =  activity  as MainActivity


    private lateinit var ParamBundle :Bundle

    override fun onBarcode(barCode: String) {
        when {
            barCode.startsWith(ISKaskadAPP.BARCODE_DATA_GR_ZAP) -> {
                val Key_GrZap:String = barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")
                runSearchByKey(Key_GrZap.toInt())

            }
            barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_SUB_PODR) -> {
                val Key_Sub_Ver:String = barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_SUB_PODR, "")
                AppVM.loadSubjList("&Key_Sub_Ver=$Key_Sub_Ver")

            }
            else -> {
                super.onBarcode(barCode)
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
        val Key_GrZap = ParamBundle.getInt("Key_GrZap")
        initTabs(Key_GrZap)

        binding.SelSubjBtn.setOnClickListener {
            val Params= Bundle()
            Params.putString("Filter","&Key_Class=1&Cod_Sub=1")
            findNavController().navigate(R.id.action_fragment_sklad_out_dt_to_subjFindFragment, Params)
        }

        // Новый способ работы с меню
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.skladoutdtmenu, menu)
                val m: MenuItem = menu.findItem(R.id.MakeNaclItem)
                m.setOnMenuItemClickListener {
                    if (AppVM.GetSelectedSubjInfo().value != null) {
                        val JsonRoot = JSONObject()
                        JsonRoot.put("Key_Sub_Ver", AppVM.GetSelectedSubjInfo().value?.GetIntParam("Key_Sub_Ver"))
                        AppVM.getGrZapInfo().value?.let {
                            JsonRoot.put("Key_GrZap", it.GetIntParam("Key_GrZap"))
                            val arr = JSONArray()
                            it.IdMatInfo.forEach { matInfo ->
                                matInfo.IdMatItems.forEach { item ->
                                    if (item.GetIntParam("Cnt") > 0) {
                                        val elementi = JSONObject()
                                        elementi.put("Key_Nacl_Str_Sost", item.GetIntParam("Key_Nacl_Str_Sost"))
                                        elementi.put("Cnt", item.GetIntParam("Cnt"))
                                        arr.put(elementi)
                                    }
                                }
                            }
                            if (arr.length() > 0) {
                                JsonRoot.put("sostav", arr)
                                val RunStr = "&JSONStr=${ISKaskadAPP.encodeStr(JsonRoot.toString())}"
                                AppVM.SkladRunGrZap(RunStr)
                                Toast.makeText(
                                    context,
                                    "Запущен процесс создания накладных",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigateUp()
                            }
                        }
                    } else {
                        val Params = Bundle()
                        Params.putString("Filter", "&Key_Class=1")
                        findNavController().navigate(R.id.action_fragment_sklad_out_dt_to_subjFindFragment, Params)
                    }
                    true
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Если требуется обработка других пунктов меню, реализуйте здесь
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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





    private fun initTabs(Key_GrZap :Int ) {
        //AppVM.clearGrZapInfo()

        AppVM.GetSelectedSubjInfo().observe(viewLifecycleOwner
        ) {
            it?.let{
                binding.SubSkladName.text = it.getF_Name_Sub()

            } ?: run    {
                binding.SubSkladName.text = "Не выбран"
            }

        }


        Adapter = SkladOutMTabAdapter (
            {
                fun onItemClicked(item: SkladIdMatInfo)        {
                                val toast = Toast.makeText(
                                    getActivity(),
                                    "onItemClicked ${item.getStrParam("Key_IdMat")}!",
                                    Toast.LENGTH_SHORT
                                )
                                toast.show()
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


}