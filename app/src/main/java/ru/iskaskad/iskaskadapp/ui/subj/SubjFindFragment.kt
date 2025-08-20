package ru.iskaskad.iskaskadapp.ui.subj


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.LOGIN_ID
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.SubjFindAdapter
import java.util.*
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion as GlobalApp
import ru.iskaskad.iskaskadapp.databinding.FragmentFindSubjBinding
import ru.iskaskad.iskaskadapp.dto.SubjInfo

class SubjFindFragment : Fragment() {

    private var _binding: FragmentFindSubjBinding? = null
    private val binding get() = _binding!!

    private var StrFilter:String = ""


    val LogTAG="SubjFindFragment"

    private val AppVM : IsKaskadAPPVM by activityViewModels()

    private lateinit var adapter_FindSub: SubjFindAdapter


    private var TS :Date  =  Calendar.getInstance().time



    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            val barcode = ISKaskadAPP.readBarCode(intent)

            GlobalApp.sendLogMessage(LogTAG, "PARSE BARCODE $barcode")

            when {
                barcode.startsWith(GlobalApp.BARCODE_DATA_KEY_SUB_PODR) -> {
                    val Key_Pasp:String = barcode.replace(GlobalApp.BARCODE_DATA_KEY_PASP, "")

                    runSearchByKey(  Key_Pasp )
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




    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        GlobalApp.sendLogMessage(LogTAG, "onCreateView in")


        _binding = FragmentFindSubjBinding.inflate(inflater, container, false)
        val view = binding.root

        val ParamBundle = requireArguments()

        if (ParamBundle.containsKey("Filter"))
            StrFilter= ParamBundle.getString("Filter").toString()



        binding.SearchSubjBtn.setOnClickListener {
            runSearch(binding.SearchSub.text.toString())
        }

        //binding.SearchSub.setText(AppVM.Se )

        binding.SearchSub.setOnEditorActionListener  { _, _, _ ->   // v, keyCode, event ->
            runSearch(binding.SearchSub.text.toString())
            true
        }
        initList()



        GlobalApp.sendLogMessage(LogTAG, "onCreateView out")

        return view

    }

    override fun onResume() {
        super.onResume()
        GlobalApp.sendLogMessage(LogTAG, "OnResume")

        context?.registerReceiver(broadCastReceiver, IntentFilter(GlobalApp.SCAN_ACTION))

        TS   = Calendar.getInstance().time

        if (LOGIN_ID == "")
        {
            val navController = findNavController()
            val Params= Bundle()
            Params.putInt(ISKaskadAPP.REQUEST_PARAM_RUNMODE, R.id.radio_FindPasp)
            navController.navigate(R.id.nav_login, Params)
        }

    }

    override fun onPause() {
        GlobalApp.sendLogMessage(LogTAG, "OnPause")

        context?.unregisterReceiver(broadCastReceiver)

        super.onPause()
    }





    private fun initList() {

        binding.rvSubjList.layoutManager = LinearLayoutManager(this.context)

        adapter_FindSub = SubjFindAdapter(  object : SubjFindAdapter.Callback
        {
            override fun onItemClicked(item: SubjInfo)
            {
                AppVM.GetSelectedSubjInfo().postValue(item)
                findNavController().navigateUp()
            }
        })

        binding.rvSubjList.adapter = adapter_FindSub

        AppVM.getSubjList().observe(viewLifecycleOwner,
        {
            it?.let {
                adapter_FindSub.setData(it)
            }
        })

        runSearch(binding.SearchSub.text.toString())
    }


    private fun closeKeyBoard(view : View) {
        view.let { v ->
            val imm = context?.getSystemService( Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        view.clearFocus()
    }



    private fun runSearch(SearchStr:String   ) {

        context?.let { closeKeyBoard( binding.SearchSub) }

        val URLParams =GlobalApp.encodeStr(SearchStr)

        AppVM.loadSubjList("$StrFilter&FindStr=$URLParams")

    }

    fun runSearchByKey( Key_Sub_Ver:String ) {
        AppVM.loadSubjList("$StrFilter&Key_Sub_Ver=$Key_Sub_Ver")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


