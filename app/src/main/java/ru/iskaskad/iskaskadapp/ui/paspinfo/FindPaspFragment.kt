package ru.iskaskad.iskaskadapp.ui.paspinfo


//import kotlinx.android.synthetic.main.fragment_find_pasp.*
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.LOGIN_ID
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.FindPaspAdapter
import ru.iskaskad.iskaskadapp.databinding.FragmentFindPaspBinding
import ru.iskaskad.iskaskadapp.dto.PaspInfo
import ru.iskaskad.iskaskadapp.ui.BaseFragment
import java.util.Calendar
import java.util.Date
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion as GlobalApp

class FindPaspFragment : BaseFragment() {
    override var logTAG="FindPaspFragment"

    private var _binding: FragmentFindPaspBinding? = null
    private val binding get() = _binding!!



    private val AppVM : IsKaskadAPPVM by activityViewModels()

    private lateinit var adapter_FindPasp: FindPaspAdapter


    private var TS :Date  =  Calendar.getInstance().time


    override fun onBarcode(barCode: String) {
        when {
            barCode.startsWith(GlobalApp.BARCODE_DATA_KEY_PASP) -> {
                val Key_Pasp: String = barCode.replace(GlobalApp.BARCODE_DATA_KEY_PASP, "")

                runSearchByKey(Key_Pasp)
            }

            barCode.startsWith(GlobalApp.BARCODE_DATA_KEY_PASP_PLACE) -> {
                val Key_PaspPlace: String =
                    barCode.replace(GlobalApp.BARCODE_DATA_KEY_PASP_PLACE, "")
                runSearchByPlace(Key_PaspPlace)

            }

            else -> {
                super.onBarcode(barCode)
            }
        }

    }





    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        GlobalApp.sendLogMessage(logTAG, "onCreateView in")


        _binding = FragmentFindPaspBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.SearchBtn.setOnClickListener {
            runSearch(binding.SearchPasp.text.toString())
        }

        binding.SearchPasp.setText(AppVM.SearchStr )

        binding.SearchPasp.setOnEditorActionListener  { _, _, _ ->   // v, keyCode, event ->
            runSearch(binding.SearchPasp.text.toString())
            true
        }
        initList()



        GlobalApp.sendLogMessage(logTAG, "onCreateView out")

        return view

    }

    override fun onResume() {
        super.onResume()
        GlobalApp.sendLogMessage(logTAG, "OnResume")


        TS   = Calendar.getInstance().time

        if (LOGIN_ID == "")
        {
            val navController = findNavController()
            val Params= Bundle()
            Params.putInt(ISKaskadAPP.REQUEST_PARAM_RUNMODE, R.id.radio_FindPasp)
            navController.navigate(R.id.nav_login, Params)
        }

    }






    private fun initList() {

        binding.rvPaspList.layoutManager = LinearLayoutManager(this.context)

        adapter_FindPasp = FindPaspAdapter(  object : FindPaspAdapter.Callback
        {
            override fun onItemClicked(item: PaspInfo)
            {
                AppVM.SelectedPaspInfo=item
                    // todo action_nav_findpasp
                    //findNavController().navigate(R.id.action_nav_findpasp_to_MKFragment)
            }
        })

        binding.rvPaspList.adapter = adapter_FindPasp

        AppVM.getPaspList().observe(viewLifecycleOwner,
        {
            it?.let {
                adapter_FindPasp.setData(it)
            }
        })

        runSearch(binding.SearchPasp.text.toString())
    }


    private fun closeKeyBoard(view : View) {
        view.let { v ->
            val imm = context?.getSystemService( Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
        view.clearFocus()
    }



    private fun runSearch(SearchStr:String   ) {

        context?.let { closeKeyBoard( binding.SearchPasp) }

        if (AppVM.SearchStr == SearchStr)  return

        AppVM.SearchStr = SearchStr

        val URLParams =GlobalApp.encodeStr(SearchStr)

        AppVM.loadPaspList("&FindStr=$URLParams")

    }

    fun runSearchByKey( Key_Pasp:String ) {
        AppVM.loadPaspList("&Key_Pasport=$Key_Pasp")
    }

    fun runSearchByPlace(Key_Pasp_Place:String ) {
        AppVM.loadPaspList("&Key_Pasp_Place=$Key_Pasp_Place")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


