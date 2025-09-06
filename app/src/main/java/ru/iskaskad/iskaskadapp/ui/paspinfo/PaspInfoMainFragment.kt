package ru.iskaskad.iskaskadapp.ui.paspinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.BindInfoArray
import ru.iskaskad.iskaskadapp.adapters.BindInfoItem
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM


private const val ARG_PARAM1 = "Key_Pasport"



class PaspInfoMainFragment : Fragment() {

    private var Key_Pasport: String? = null

    //private val mainActivity get() =  activity !! as MainActivity
    private val AppVM: IsKaskadAPPVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Key_Pasport = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pasp_info_main, container, false)
    }

    private val BindInfo= BindInfoArray (arrayListOf(

        BindInfoItem(R.id.Name_Pasp_Status, "Name_Pasp_Status")
        , BindInfoItem(R.id.Cod_Zak, "Cod_Zak")
        , BindInfoItem(R.id.Key_Pasp, "Key_Pasport")

        , BindInfoItem(R.id.Name_PredmMat, "Name_PredmMat")
        , BindInfoItem(R.id.Sortam_Mat, "Sortam_Mat")
        , BindInfoItem(R.id.Marka_Mat, "Marka_Mat")

    ))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BindInfo.bindToView(view)

        AppVM.getSelectedPaspInfo().observe(viewLifecycleOwner, {it?.let {
            BindInfo.setViewData(it)
        }
        })
    }




    companion object {

        @JvmStatic
        fun newInstance(Key_Pasport: String ) =
            PaspInfoMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Key_Pasport)
                 }
            }
    }
}