package ru.iskaskad.iskaskadapp.ui.sklad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.FindPaspAdapter
import ru.iskaskad.iskaskadapp.adapters.SkladGrZapAdapter
import ru.iskaskad.iskaskadapp.databinding.FragmentSkladOutBinding
import ru.iskaskad.iskaskadapp.dto.PaspInfo
import ru.iskaskad.iskaskadapp.dto.SkladGrZapInfo


class fragment_sklad_out : Fragment() {

    private var _binding: FragmentSkladOutBinding? = null
    private val binding get() = _binding!!

    private val AppVM : IsKaskadAPPVM by activityViewModels()
    private lateinit var GrZapAdapter: SkladGrZapAdapter

    val LogTAG = "SkladOMOut"

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val barcode = ISKaskadAPP.readBarCode(intent)

            when {
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_GR_ZAP) -> {
                    val Key_GrZap:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_GR_ZAP, "")
                    runSearchByKey(Key_GrZap.toInt())

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSkladOutBinding.inflate(inflater, container, false)
      //  val view = binding.root

        binding.SearchGrZapBtn.setOnClickListener {
            try {
                runSearchByKey(binding.SearchGrZap.text.toString().toInt())
            }
            catch (e: NumberFormatException){

            }
        }

        initList()


        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun runSearch(   ) {
        var SearchStr = ""
        AppVM.loadGrZapList(SearchStr)
    }
    private fun runSearchByKey( Key_GrZap :Int    ) {
        var SearchStr = "&Key_GrZap=$Key_GrZap"
        AppVM.loadGrZapList(SearchStr)
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



    private fun initList() {

        binding.GrZapRV.layoutManager = LinearLayoutManager(this.context)

        GrZapAdapter = SkladGrZapAdapter(  object : SkladGrZapAdapter.Callback
        {
            override fun onItemClicked(item: SkladGrZapInfo)
            {
                val Key_GrZap = item.getStrParam("Key_GrZap").toInt() ;

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