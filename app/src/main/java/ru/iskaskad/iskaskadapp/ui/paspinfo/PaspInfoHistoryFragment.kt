package ru.iskaskad.iskaskadapp.ui.paspinfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.fragment_pasp_info_history.*

import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.PaspInfoHistoryAdapter
import ru.iskaskad.iskaskadapp.dto.PaspHistoryRecInfo
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoBinding
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoHistoryBinding


private const val ARG_PARAM1 = "Key_Pasport"



class PaspInfoHistiryFragment : Fragment() {

    private var _binding: FragmentPaspInfoHistoryBinding? = null
    private val binding get() = _binding!!



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

        _binding = FragmentPaspInfoHistoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    private lateinit var  myAdapter : PaspInfoHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()

    }

    private fun initList() {

        binding.HistoryRV.layoutManager = LinearLayoutManager(this.context)

        myAdapter = PaspInfoHistoryAdapter(  object : PaspInfoHistoryAdapter.Callback
        {
            override fun onItemClicked(item: PaspHistoryRecInfo)
            {

                //fpViewModel.SelectedPaspInfo=item


            }
        })

        binding.HistoryRV.adapter = myAdapter

        AppVM.getPaspHistory().observe(viewLifecycleOwner, {
            it?.let {
                myAdapter.setData(it)
            }
        })



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {

        @JvmStatic
        fun newInstance(Key_Pasport: String ) =
            PaspInfoHistiryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Key_Pasport)
                 }
            }
    }
}