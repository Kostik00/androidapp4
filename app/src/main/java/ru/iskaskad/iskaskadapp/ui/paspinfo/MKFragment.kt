package ru.iskaskad.iskaskadapp.ui.paspinfo


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
//import kotlinx.android.synthetic.main.fragment_pasp_info.*
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
//import ru.iskaskad.iskaskadapp.databinding.FragmentFindPaspBinding
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoBinding
import ru.iskaskad.iskaskadapp.ui.BaseFragment


class MKFragment : BaseFragment() {
    override var logTAG = "MKFragment"

    private var _binding: FragmentPaspInfoBinding? = null
    private val binding get() = _binding!!



    private val mainActivity get() =  activity  as MainActivity

    private val AppVM: IsKaskadAPPVM by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaspInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // todo исправить
//        AppVM.getSelectedPaspInfo().observe(viewLifecycleOwner,
//            {it?.let {
//                        binding.DseTV.text= getString(R.string.DSECodPredmNamePredm, it.getStrParam("Cod_Predm") , it.getStrParam("Name_Predm") )
//                        mainActivity.TBTitle = it.getParam("Cod_Pasp").StrValue
//                    }
//            })

        val PageAdapter = PaspInfoPageAdapter(this)

        binding.ViewPager.adapter = PageAdapter

        TabLayoutMediator( binding.tabs ,  binding.ViewPager) { tab, position ->
            tab.text = context?.resources?.getString(PageAdapter.TAB_TITLES[position])
        }.attach()

    }


    override fun onBarcode(barCode: String) {
        when {
            barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP) -> {
                val Key_Pasp:String = barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP, "")


                AppVM.loadPaspInfo(Key_Pasp)
            }

            else -> {
                super.onBarcode(barCode)
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
