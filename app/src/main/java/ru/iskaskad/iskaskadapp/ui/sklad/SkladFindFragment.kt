package ru.iskaskad.iskaskadapp.ui.sklad


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
//import kotlinx.android.synthetic.main.sklad_find_fragment.*
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.databinding.FragmentPaspInfoMkBinding
import ru.iskaskad.iskaskadapp.databinding.SkladFindFragmentBinding

class SkladFindFragment : Fragment() {

    private var _binding: SkladFindFragmentBinding? = null
    private val binding get() = _binding!!


    val LogTAG = "SkladFindFragment"

    private  var AdvSearchSwitch: SwitchCompat? = null
    private lateinit var AdvSearchItem  : MenuItem

    //private val mainActivity get() =  activity !! as MainActivity
    private val AppVM: IsKaskadAPPVM by activityViewModels()

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            val barcode = ISKaskadAPP.readBarCode(intent)

            ISKaskadAPP.sendLogMessage(LogTAG, "PARSE BARCODE $barcode")

            when {
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE) -> {
                    val Key_Pasp_Place:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")

                    val bundle = Bundle()
                    bundle.putString("Key_Pasp_Place", Key_Pasp_Place)
                    findNavController().navigate(R.id.action_nav_sklad_to_skladListOstatokFragment, bundle)

                }
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR) -> {
                    val Key_Nacl_Str:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR, "")

                    val bundle = Bundle()
                    bundle.putString("Key_Nacl_Str", Key_Nacl_Str)

                    findNavController().navigate(R.id.action_nav_sklad_to_skladListOstatokFragment, bundle)

                }
                barcode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST) -> {
                    val Key_Nacl_Str_Sost:String = barcode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST, "")

                    val bundle = Bundle()
                    bundle.putString("Key_Nacl_Str_Sost", Key_Nacl_Str_Sost)

                    findNavController().navigate(R.id.action_nav_sklad_to_skladListOstatokFragment, bundle)

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SkladFindFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        //setHasOptionsMenu(true)
        return view

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchButton.setOnClickListener {

            val bundle = Bundle()

            if (binding.frStrMark.text.toString() != "") bundle.putString(
                "StrMark",
                binding.frStrMark.text.toString()
            )
            if (binding.KeyPredm.text.toString() != "") bundle.putString(
                "Key_Predm",
                binding.KeyPredm.text.toString()
            )
            if (binding.KeyIdMat.text.toString() != "") bundle.putString(
                "Key_ID_Mat",
                binding.KeyIdMat.text.toString()
            )
            if (binding.RegN.text.toString() != "") bundle.putString("RegN", binding.RegN.text.toString())
            if (binding.nSert.text.toString() != "") bundle.putString("N_Sert", binding.nSert.text.toString())
            if (binding.codPlavka.text.toString() != "") bundle.putString(
                "Cod_Plavka",
                binding.codPlavka.text.toString()
            )
            if (binding.party.text.toString() != "") bundle.putString("Party", binding.party.text.toString())
            if (binding.namePredm.text.toString() != "") bundle.putString(
                "Name_Predm",
                binding.namePredm.text.toString()
            )
            if (binding.sortam.text.toString() != "") bundle.putString("Sortam", binding.sortam.text.toString())
            if (binding.marka.text.toString() != "") bundle.putString("Marka", binding.marka.text.toString())
            if (binding.place.text.toString() != "") bundle.putString(
                "Cod_Pasp_Place",
                binding.place.text.toString()
            )

            findNavController().navigate(R.id.action_nav_sklad_to_skladListOstatokFragment, bundle)

        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.skladfindfragmentmenu, menu)
                AdvSearchItem = menu.findItem(R.id.AdvSearchSwitch)
                AdvSearchSwitch = AdvSearchItem.actionView?.findViewById(R.id.switchid)
                AdvSearchItem.isVisible = true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // обработка нажатий, если нужно
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

//    override fun onCreateContextMenu(
//        menu: ContextMenu,
//        v: View,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//
//        super.onCreateOptionsMenu(menu, inflater)
//
//        inflater.inflate(R.menu.skladfindfragmentmenu, menu)
//
//        AdvSearchItem = menu.findItem(R.id.AdvSearchSwitch)
//        AdvSearchSwitch = AdvSearchItem.actionView?.findViewById(R.id.switchid)
//
//
//        AdvSearchItem.isVisible = true
//
//    }


//    override fun onPrepareOptionsMenu(menu: Menu) {
//        super.onPrepareOptionsMenu(menu)
//
//        AdvSearchSwitch?.isChecked = AppVM.AdvSearchChecked
//
//        return
//    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        ISKaskadAPP.sendLogMessage(LogTAG, "OnResume")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION),
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireContext().registerReceiver(
                broadCastReceiver,
                IntentFilter(ISKaskadAPP.SCAN_ACTION)
            )
        }
    }

    override fun onPause() {
        ISKaskadAPP.sendLogMessage(LogTAG, "OnPause")

        // Используйте try/catch для unregisterReceiver, чтобы избежать IllegalArgumentException
        try {
            requireContext().unregisterReceiver(broadCastReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver не был зарегистрирован или уже удалён
        }
        AdvSearchSwitch?.let{
            AppVM.AdvSearchChecked = AdvSearchSwitch!!.isChecked

        }
        super.onPause()
    }




}