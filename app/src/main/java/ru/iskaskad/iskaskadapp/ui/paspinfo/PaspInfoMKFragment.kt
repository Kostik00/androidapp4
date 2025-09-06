package ru.iskaskad.iskaskadapp.ui.paspinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.android.synthetic.main.fragment_pasp_info_mk.*
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.PaspInfoMKAdapter
import ru.iskaskad.iskaskadapp.dto.PaspMKInfo
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoHistoryBinding
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoMkBinding


private const val ARG_PARAM1 = "Key_Pasport"



class PaspInfoMKFragment : Fragment() {


    private var _binding: FragmentPaspInfoMkBinding? = null
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

        _binding = FragmentPaspInfoMkBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private lateinit var  myAdapter : PaspInfoMKAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initList() {

        binding.pimkRV.layoutManager = LinearLayoutManager(this.context)

        myAdapter = PaspInfoMKAdapter(  object : PaspInfoMKAdapter.Callback
        {
            override fun onItemClicked(item: PaspMKInfo)
            {

                //fpViewModel.SelectedPaspInfo=item

                //findNavController().navigate(R.id.action_nav_findpasp_to_MKFragment)

            }
        })

        //mainActivity.FindPaspViewModel

        binding.pimkRV.adapter = myAdapter

        AppVM.getPaspMK().observe(viewLifecycleOwner, {
            it?.let {
                myAdapter.setData(it)
            }
        })



    }




    companion object {

        @JvmStatic
        fun newInstance(Key_Pasport: String ) =
            PaspInfoMKFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Key_Pasport)
                 }
            }
    }
}