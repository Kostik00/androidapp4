package ru.iskaskad.iskaskadapp.ui.sklad


//import kotlinx.android.synthetic.main.sklad_invent_ostatok_fragment.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.BindInfoArray
import ru.iskaskad.iskaskadapp.adapters.BindInfoItem
import ru.iskaskad.iskaskadapp.adapters.SkladFragmentHistoryAdapter
import ru.iskaskad.iskaskadapp.databinding.SkladInventOstatokFragmentBinding
import ru.iskaskad.iskaskadapp.dto.SkladFragmentHistoryInfo
import ru.iskaskad.iskaskadapp.dto.SkladFragmentInfo
import ru.iskaskad.iskaskadapp.ui.BaseFragment


class SkladInventOstatok : BaseFragment() {
    override var logTAG = "SkladInventOstatok"


    private var _binding: SkladInventOstatokFragmentBinding? = null
    private val binding get() = _binding!!



    private val AppVM: IsKaskadAPPVM by activityViewModels()

    private lateinit var mainActivity : MainActivity
//    private lateinit var root :View
    private lateinit var ParamBundle :Bundle


    private lateinit var dataItem: SkladFragmentInfo


    private lateinit var HistorytList_rv: RecyclerView
    private lateinit var historyList_adapter: SkladFragmentHistoryAdapter
    private var historyList_data : ArrayList<SkladFragmentHistoryInfo> = ArrayList<SkladFragmentHistoryInfo> ()


    override fun onBarcode(barCode: String) {
        if ( barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE)) {
            val Key_Pasp_Place:String = barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")
            val KolZag=binding.frInventKolvoZag.text
            val KolStr=binding.frInventKolvoStr.text

            val  Key_Nacl_Str       = dataItem.getParam("Key_Nacl_Str")
            val  Key_Nacl_Str_Sost  = dataItem.getParam("Key_Nacl_Str_Sost")

            if ( Key_Nacl_Str_Sost.ParamIsNull)
            {
                Toast.makeText(context, "01 Инвентаризируем остаток на Месте:$Key_Pasp_Place в количестве=$KolStr", Toast.LENGTH_LONG).show()
                AppVM.inventOstatok(Key_Pasp_Place , Key_Nacl_Str.IntVal.toString(), KolStr.toString(), "",  "")
            }
            else
            {
                Toast.makeText(context, "01 Инвентаризируем кусочек на Место:$Key_Pasp_Place  в количестве=$KolZag", Toast.LENGTH_LONG).show()
                AppVM.inventOstatok(Key_Pasp_Place , Key_Nacl_Str.IntVal.toString(), "", Key_Nacl_Str_Sost.IntVal.toString(),  KolZag.toString())
            }
            BindInfo.setViewData(dataItem)
        }
        else {
            super.onBarcode(barCode)
        }


    }




    val BindInfo= BindInfoArray (arrayListOf(

         BindInfoItem(R.id.fr_NamePredm2, "FNP")
        ,BindInfoItem(R.id.fr_StrMark2, "StrMark")

        ,BindInfoItem(R.id.fr_Key_Id_Mat, "Key_ID_Mat")
        ,BindInfoItem(R.id.fr_Key_Predm, "Key_Predm")

        ,BindInfoItem(R.id.fr_Vid_Sert, "VidSert")

        ,BindInfoItem(R.id.fr_Plavka, "Cod_Plavka")
        ,BindInfoItem(R.id.fr_Party, "Party")

        ,BindInfoItem(R.id.fr_Key_Nacl_Str_Sost, "Key_Nacl_Str")

        ,BindInfoItem(R.id.fr_Cod_Pasp_PlaceStr, "Cod_Pasp_Place_Str")
        ,BindInfoItem(R.id.fr_InventKolvo_Str,"OstByStr")
        ,BindInfoItem(R.id.fr_Edizm,"Name_K_Ed")

        ,BindInfoItem(R.id.fr_Cod_Pasp_Place_Zag, "Cod_Pasp_Place_Zag")
        ,BindInfoItem(R.id.Fr_ProfRazm,"ProfRazm")
        ,BindInfoItem(R.id.fr_InventKolvo_Zag,"OstZag")
        ,BindInfoItem(R.id.fr_InventMass_Zag,"M_Zag")
        ))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SkladInventOstatokFragmentBinding.inflate(inflater, container, false)
        val view = binding.root




        mainActivity = activity as MainActivity


        ParamBundle = requireArguments()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dataItem = ParamBundle.getSerializable("ItemInfo", SkladFragmentInfo::class.java)!!
            }
//            else
//                dataItem =  (ParamBundle.getSerializable("ItemInfo") as SkladFragmentInfo)



        BindInfo.bindToView(binding.root)

        initList()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        AppVM.getSelectedFragment().observe( viewLifecycleOwner, {
            it?.let {
                dataItem = it
                showDataInfo(  )
            }
        } )

        AppVM.getSelectedFragmentHistory().observe(viewLifecycleOwner, {
            it?.let {
                historyList_adapter.setData(it)
            }
        })


    }

    private fun showDataInfo ( ) {
        BindInfo.setViewData(dataItem)

        val DtStr = ISKaskadAPP.myDateToStr(dataItem.getParam("Date_Sert").DataVal)

        binding.frNSert.text = getString(R.string.SertInfoNumSert, dataItem.getParam("N_Sert").StrValue, DtStr)



        val  Key_Nacl_Str_Sost  = dataItem.getParam("Key_Nacl_Str_Sost")

        if ( Key_Nacl_Str_Sost.ParamIsNull)
        {
            binding.FragmentKolvoInfoGr.visibility = View.VISIBLE
            binding.FragmentZagotovInfoGr.visibility = View.GONE
        }
        else
        {
            binding.FragmentKolvoInfoGr.visibility = View.GONE
            binding.FragmentZagotovInfoGr.visibility = View.VISIBLE
        }

        val  key_Sert  = dataItem.getParam("Key_Sert")

        if (key_Sert.ParamIsNull)
            binding.FragmentSertInfoMaxGr.visibility = View.GONE
        else
            binding.FragmentSertInfoMaxGr.visibility = View.VISIBLE

    }

    private fun initList() {

        HistorytList_rv = binding.skladHistoryRv

        HistorytList_rv.layoutManager = LinearLayoutManager(this.context)


        historyList_adapter = SkladFragmentHistoryAdapter(
            historyList_data,
            object : SkladFragmentHistoryAdapter.Callback {
                override fun onItemClicked(item: SkladFragmentHistoryInfo) {

                    Toast.makeText(context, "Нажали на историю", Toast.LENGTH_SHORT).show()
                }
            })

        HistorytList_rv.adapter = historyList_adapter

    }






}