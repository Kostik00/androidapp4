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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.encodeStr
import ru.iskaskad.iskaskadapp.IsKaskadAPPVM
import ru.iskaskad.iskaskadapp.MainActivity
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.SkladFragmentAdapter
import ru.iskaskad.iskaskadapp.dto.SkladFragmentInfo
import ru.iskaskad.iskaskadapp.ui.BaseFragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import kotlin.collections.containsKey
import kotlin.collections.get

class SkladListOstatokFragment : BaseFragment() {

    override var logTAG = "SkladListOstatokFragment"

    private val AppVM: IsKaskadAPPVM by activityViewModels()


    private lateinit var mainActivity: MainActivity
    private lateinit var MoveOstItemSwitch: SwitchCompat
    private lateinit var ParamBundle: Bundle
    private lateinit var root: View


    private lateinit var rvFragmentList: RecyclerView
    private lateinit var adapter_FragmentList: SkladFragmentAdapter


    override fun onBarcode(barCode: String) {
        when {
            (barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE)) -> {
                val Key_Pasp_Place: String =
                    barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_PASP_PLACE, "")

                if (MoveOstItemSwitch.isChecked) {

                    var KeyNaclStrList = ""
                    var KeyNaclStrSostList = ""

                    adapter_FragmentList.items.forEach {
                        if (it.IsChecked) {
                            val Param = it.getParam("Key_Nacl_Str_Sost")
                            if (Param.ParamIsNull)
                                KeyNaclStrList =
                                    KeyNaclStrList + "," + it.getParam("Key_Nacl_Str").IntVal.toString()
                            else
                                KeyNaclStrSostList =
                                    KeyNaclStrSostList + "," + Param.IntVal.toString()
                        }
                    }

                    KeyNaclStrList = KeyNaclStrList.replaceFirst(",", "")
                    KeyNaclStrSostList = KeyNaclStrSostList.replaceFirst(",", "")

                    AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                } else {
                    ParamBundle = Bundle()
                    ParamBundle.putString("Key_Pasp_Place", Key_Pasp_Place)

                    runSearch()
                }


            }

            (barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR)) -> {
                val Key_Nacl_Str: String =
                    barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR, "")

                if (ParamBundle.containsKey("Key_Pasp_Place") && MoveOstItemSwitch.isChecked) {
                    val KeyNaclStrList: String = Key_Nacl_Str
                    val KeyNaclStrSostList = ""
                    val Key_Pasp_Place = ParamBundle.getString("Key_Pasp_Place") ?: ""

                    AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                } else {
                    ParamBundle = Bundle()
                    ParamBundle.putString("Key_Nacl_Str", Key_Nacl_Str)

                    runSearch()
                }

            }

            (barCode.startsWith(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST)) -> {
                val Key_Nacl_Str_Sost: String =
                    barCode.replace(ISKaskadAPP.BARCODE_DATA_KEY_NACL_STR_SOST, "")

                if (ParamBundle.containsKey("Key_Pasp_Place") && MoveOstItemSwitch.isChecked) {
                    val KeyNaclStrList = ""
                    val KeyNaclStrSostList: String = Key_Nacl_Str_Sost
                    val Key_Pasp_Place = ParamBundle.getString("Key_Pasp_Place")!!

                    AppVM.moveItems(KeyNaclStrList, KeyNaclStrSostList, Key_Pasp_Place)

                } else {
                    ParamBundle = Bundle()
                    ParamBundle.putString("Key_Nacl_Str_Sost", Key_Nacl_Str_Sost)

                    runSearch()
                }
            }

            else -> {
                super.onBarcode(barCode)
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.sklad_list_ostatok_fragment, container, false)

        mainActivity = activity as MainActivity

        ParamBundle = requireArguments()

        // Удалить setHasOptionsMenu(true)
        // setHasOptionsMenu(true)

        // Новый способ добавления меню
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.skladfragmentmenu, menu)
                val MoveSearchItem = menu.findItem(R.id.MoveOstItemSwitch)
                MoveOstItemSwitch =
                    MoveSearchItem.actionView?.findViewById<SwitchCompat>(R.id.switchid)!!
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Если требуется обработка выбора пунктов меню, реализуйте здесь
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        initList()

        return root
    }

    fun FillCaption() {
        mainActivity.setTitleBar (
            if (ParamBundle.containsKey("Key_Pasp_Place")) {
                if (adapter_FragmentList.items.isNotEmpty()) {
                    val P = adapter_FragmentList.items[0]
                    "Остатки на: ${P.getParam("Cod_Pasp_Place").StrValue}"
                } else {
                    "Остатки на: Данных не найдено"
                }
            } else {
                "Результаты поиска"
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppVM.getFragmentList().observe(
            viewLifecycleOwner,
            {
                it?.let {
                    //Cod_Pasp_Place.text = it.Cod_Pasp_Place()
                    adapter_FragmentList.setData(it)

                    FillCaption()
                }
            })

    }

    private var FirstTimeQRY: Boolean = true

    private fun initList() {
        rvFragmentList = root.findViewById(R.id.fragmentListRV)
        rvFragmentList.layoutManager = LinearLayoutManager(this.context)

        adapter_FragmentList = SkladFragmentAdapter(
            AppVM.getFragmentList().value ?: ArrayList<SkladFragmentInfo>(),
            object : SkladFragmentAdapter.Callback {
                override fun onItemClicked(item: SkladFragmentInfo) {
                    val bundle = Bundle()
                    bundle.putSerializable("ItemInfo", item)

                    AppVM.SelectedFragment = item

                    findNavController().navigate(
                        R.id.action_skladListOstatokFragment_to_skladInventOstatok,
                        bundle
                    )
                }
            })
        rvFragmentList.adapter = adapter_FragmentList
        if (FirstTimeQRY)
            runSearch()


        FirstTimeQRY = false
    }

    fun runSearch() {

        AppVM.loadSkladOstatokList(getSearchStr())

    }


    private fun getSearchStr(): String {

        var request = ""


        if (ParamBundle.containsKey("Key_Pasp_Place"))
            request += "&Key_Pasp_Place=" + encodeStr(ParamBundle.getString("Key_Pasp_Place"))

        if (ParamBundle.containsKey("StrMark"))
            request += "&StrMark=" + encodeStr(ParamBundle.getString("StrMark"))

        if (ParamBundle.containsKey("Key_Predm"))
            request += "&Key_Predm=" + encodeStr(ParamBundle.getString("Key_Predm"))

        if (ParamBundle.containsKey("Key_ID_Mat"))
            request += "&Key_ID_Mat=" + encodeStr(ParamBundle.getString("Key_ID_Mat"))

        if (ParamBundle.containsKey("RegN"))
            request += "&RegN=" + encodeStr(ParamBundle.getString("RegN"))

        if (ParamBundle.containsKey("N_Sert"))
            request += "&N_Sert=" + encodeStr(ParamBundle.getString("N_Sert"))

        if (ParamBundle.containsKey("Cod_Plavka"))
            request += "&Cod_Plavka=" + encodeStr(ParamBundle.getString("Cod_Plavka"))

        if (ParamBundle.containsKey("Party"))
            request += "&Party=" + encodeStr(ParamBundle.getString("Party"))

        if (ParamBundle.containsKey("Name_Predm"))
            request += "&Name_Predm=" + encodeStr(ParamBundle.getString("Name_Predm"))

        if (ParamBundle.containsKey("Sortam"))
            request += "&Sortam=" + encodeStr(ParamBundle.getString("Sortam"))

        if (ParamBundle.containsKey("Marka"))
            request += "&Marka=" + encodeStr(ParamBundle.getString("Marka"))

        if (ParamBundle.containsKey("Key_Nacl_Str"))
            request += "&Key_Nacl_Str=" + encodeStr(ParamBundle.getString("Key_Nacl_Str"))

        if (ParamBundle.containsKey("Key_Nacl_Str_Sost"))
            request += "&Key_Nacl_Str_Sost=" + encodeStr(ParamBundle.getString("Key_Nacl_Str_Sost"))

        if (ParamBundle.containsKey("Cod_Pasp_Place"))
            request += "&Cod_Pasp_Place=" + encodeStr(ParamBundle.getString("Cod_Pasp_Place"))

        return request
    }
}







