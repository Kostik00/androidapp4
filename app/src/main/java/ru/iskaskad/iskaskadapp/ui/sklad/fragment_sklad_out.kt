package ru.iskaskad.iskaskadapp.ui.sklad

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.SkladGrZapAdapter
import ru.iskaskad.iskaskadapp.databinding.FragmentSkladOutBinding
import ru.iskaskad.iskaskadapp.dto.SkladGrZapInfo
import ru.iskaskad.iskaskadapp.ui.BaseFragment


class fragment_sklad_out : BaseFragment() {

    override var logTAG = "SkladOMOut"

    private var _binding: FragmentSkladOutBinding? = null
    private val binding get() = _binding!!

    private val AppVM : IsKaskadAPPVM by activityViewModels()
    private lateinit var GrZapAdapter: SkladGrZapAdapter



    override fun onBarcode(barCode: String) {
        super.onBarcode(barCode)
        when {
            barCode.startsWith(ISKaskadAPP.BARCODE_DATA_GR_ZAP) -> {
                val Key_GrZap:String = barCode.replace(ISKaskadAPP.BARCODE_DATA_GR_ZAP, "")
                runSearchByKey(Key_GrZap.toInt())

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
        _binding = FragmentSkladOutBinding.inflate(inflater, container, false)

        binding.SearchGrZapBtn.setOnClickListener {
            try {
                runSearchByKey(binding.SearchGrZap.text.toString().toInt())
            }
            catch (e: NumberFormatException){
                ISKaskadAPP.sendLogMessage(logTAG , "Ошибка при поиске по ключу (${e.toString()})"  )
            }
        }

        initList()

        // Новый способ работы с меню
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.skladfragmentmenu, menu)
                // Если требуется инициализация элементов меню, добавьте здесь
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Если требуется обработка выбора пунктов меню, реализуйте здесь
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun runSearch(   ) {
        val SearchStr = ""
        AppVM.loadGrZapList(SearchStr)
    }
    private fun runSearchByKey( Key_GrZap :Int    ) {
        val SearchStr = "&Key_GrZap=$Key_GrZap"
        AppVM.loadGrZapList(SearchStr)
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        ISKaskadAPP.sendLogMessage(logTAG, "OnResume")

        if (ISKaskadAPP.LOGIN_ID == "")
        {
            val navController = findNavController()
            val Params= Bundle()
            Params.putInt(ISKaskadAPP.REQUEST_PARAM_RUNMODE, R.id.radio_Sklad)
            navController.navigate(R.id.nav_login, Params)
        }

    }

    override fun onPause() {
        ISKaskadAPP.sendLogMessage(logTAG, "OnPause")



        super.onPause()
    }



    private fun initList() {

        binding.GrZapRV.layoutManager = LinearLayoutManager(this.context)

        GrZapAdapter = SkladGrZapAdapter(  object : SkladGrZapAdapter.Callback
        {
            override fun onItemClicked(item: SkladGrZapInfo)
            {
                val Key_GrZap = item.getStrParam("Key_GrZap").toInt()

                val bundle = Bundle()
                bundle.putInt("Key_GrZap", Key_GrZap)

                findNavController().navigate(R.id.action_nav_sklad_om_to_fragment_sklad_out_dt, bundle)


            }
        })

        binding.GrZapRV.adapter = GrZapAdapter

        AppVM.getGrZapList().observe(viewLifecycleOwner,
            {
                it?.let {
                    GrZapAdapter.setData(it)
                }
            }
        )

        runSearch()
    }




}