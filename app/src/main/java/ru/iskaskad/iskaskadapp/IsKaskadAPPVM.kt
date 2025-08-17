package ru.iskaskad.iskaskadapp

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import ru.iskaskad.iskaskadapp.dto.*
import java.net.URL
import java.util.*


class IsKaskadAPPVM(application: Application) : AndroidViewModel(application) {

    class MyDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle("Важное сообщение!")
                    .setMessage("Покормите кота!")
                    //.setIcon(R.drawable.hungrycat)
                    .setPositiveButton("ОК, иду на кухню") {
                            dialog, id ->  dialog.cancel()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }



    val LogTAG = "iskaskadappViewModel"

    init {
        ISKaskadAPP.sendLogMessage(LogTAG, "Init")
    }

    var RunProgress: MutableLiveData<Int> = MutableLiveData(-1)
    var ErrorText: MutableLiveData<String> = MutableLiveData("")
    var ErrorMessage:MutableLiveData<String> = MutableLiveData("")

    var AdvSearchChecked: Boolean = false

//    private val myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())

    var SearchStr: String = ""

    var NeedStatistics: Boolean = false

    private var data_PaspPlace: MutableLiveData<PlaceInfo> = MutableLiveData()
    fun getPaspPlace() = data_PaspPlace

    private var PaspPlaceJob: Job = Job()
    private var PaspInfoJob: Job = Job()
    private var MTaskJob: Job = Job()
    private var UpdateMTaskJob: Job = Job()
    private var PaspListJob: Job = Job()
    private var SkladJob:Job= Job()
    private var SkladTaskJob:Job=Job()
    private var SkladGrZapJob:Job=Job()
    private var SkladGrZapInfoJob:Job=Job()
    private var SelectSubjJob:Job= Job()
    private var SkladRunGrZapJob:Job= Job()


    fun SkladRunGrZap(RunStr: String) {

        SkladRunGrZapJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_RUN_GRZAP, RunStr,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    try {
                        val V = getGrZapList().value
                         V?.let { if (it.count() > 0)
                                      loadGrZapList(it[0].SearchStr)
                                  else
                                      loadGrZapList("")
                                }


                        val JsonData = JSONObject(Result)
                        val Items = JsonData.getJSONArray("resultinfo")
                        val frinfo = Items.getJSONObject(0)
                        val ErrText = frinfo.getString("ErrorMessage")

                        if (frinfo.getString("RESULT") == "SUCCESS")
                            Toast.makeText(getApplication(), ErrText, Toast.LENGTH_LONG).show()
                        else {
                            ErrorMessage.postValue(ErrText)

                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        showError("Ошибка данных при отправке запроса на создание накладных: $Result")
                    }



                }
            }, SkladRunGrZapJob
        )
    }





    fun clearGrZapInfo() {
         getGrZapInfo().postValue(null)
    }
    fun loadGrZapInfo(SearchStr: String) {

        getGrZapInfo().value?.let {
            if (SearchStr== it.SearchStr) return
        }

        clearGrZapInfo()


        SkladGrZapInfoJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_GRZAP_INFO, SearchStr,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseGrZapInfo(Result, SearchStr)

                }
            }, SkladGrZapJob
        )
    }
    fun parseGrZapInfo(JSonStr: String, SearchStr:String) {
        try {

            val JsonData = JSONObject(JSonStr)
            val Items = JsonData.getJSONArray("grzapinfo")
            val frinfo = SkladGrZapDetailedInfo(Items.getJSONObject(0))
            frinfo.SearchStr = SearchStr
            getGrZapInfo().postValue(frinfo)
        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (задание на запуск)")
        }


    }




    private var data_GrZapInfo: MutableLiveData<SkladGrZapDetailedInfo?> = MutableLiveData()
    fun getGrZapInfo() = data_GrZapInfo



    fun moveItems(KeyNaclStrList:String, KeyNaclStrSostList:String, Key_Pasp_Place:String){

        if ((KeyNaclStrList=="") && (KeyNaclStrSostList==""))
        {
            Toast.makeText(getApplication(), "Для перемещения необходимо выбрать перемещаемые позиции", Toast.LENGTH_SHORT).show()
            return
        }

        var request = ""


        request += "&Key_Pasp_Place=" + ISKaskadAPP.encodeStr(Key_Pasp_Place)

        if (KeyNaclStrList != "")
            request += "&KeyNaclStrList=" + ISKaskadAPP.encodeStr(KeyNaclStrList)

        if (KeyNaclStrSostList != "")
            request += "&KeyNaclStrSostList=" + ISKaskadAPP.encodeStr(KeyNaclStrSostList)


        SkladTaskJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_FRAGMENT_MOVE, request,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseSkladTask(Result)
                }
            }, SkladTaskJob
        )

    }

    fun parseSkladTask(StrResult: String) {
        if (StrResult==ISKaskadAPP.URL_RESULT_SUCCESS)
        {
            Toast.makeText(getApplication(), "Перемещение произошло успешно", Toast.LENGTH_LONG).show()
            loadSkladOstatokList(LastSkladSearchStr)
        }
        else
        {
            Toast.makeText(getApplication(), "Ошибка перемещения: $StrResult", Toast.LENGTH_LONG).show()
        }
    }

    fun updateMTask(New_Key_Pasp_Place: String, Key_Pasport: String, NewState: String) {
        val URLParams =
            "&New_Key_Pasp_Place=" + New_Key_Pasp_Place + "&Key_Pasport=" + Key_Pasport +
                    "&NewState=" + NewState

        UpdateMTaskJob = runHTTPQry(0, ISKaskadAPP.URL_MTASK_SETSTATUS, URLParams,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseUpdateMTask(Result)
                }
            }, UpdateMTaskJob
        )
    }

    private var data_FragmentList:MutableLiveData<MutableList<SkladFragmentInfo>> = MutableLiveData()
    fun getFragmentList() = data_FragmentList


    private var  sklad_fragment_history: MutableLiveData< ArrayList<SkladFragmentHistoryInfo>> = MutableLiveData()
    fun getSelectedFragmentHistory() = sklad_fragment_history

    private var f_SelectedFragment: MutableLiveData<SkladFragmentInfo?> = MutableLiveData(null)
    fun getSelectedFragment() = f_SelectedFragment

    var SelectedFragment: SkladFragmentInfo?
        get() = f_SelectedFragment.value
        set(value) {
            if (value != f_SelectedFragment.value) {
                f_SelectedFragment.postValue(value)

                value?.let {

                    val  Key_Nacl_Str_Sost  = it.getParam("Key_Nacl_Str_Sost")
                    val  Key_Nacl_Str       = it.getParam("Key_Nacl_Str")

                    val URLParams = if ( Key_Nacl_Str_Sost.ParamIsNull)
                        "&Key_Nacl_Str="+Key_Nacl_Str.IntVal.toString()
                    else
                        "&Key_Nacl_Str_Sost="+Key_Nacl_Str_Sost.IntVal.toString()

                    loadSkladOstatokDetail(URLParams)
                }

            }
        }



    private var data_FindGrZap: MutableLiveData<MutableList<SkladGrZapInfo>> = MutableLiveData()
    fun getGrZapList() = data_FindGrZap


    private var data_SubjList: MutableLiveData<MutableList<SubjInfo>> = MutableLiveData()
    fun getSubjList() = data_SubjList

    private var data_SelectedSubjInfo: MutableLiveData<SubjInfo?> = MutableLiveData(null)
    fun GetSelectedSubjInfo() = data_SelectedSubjInfo



    private var data_FindPasp: MutableLiveData<MutableList<PaspInfo>> = MutableLiveData()
    fun getPaspList() = data_FindPasp

    private var data_PaspMK: MutableLiveData<MutableList<PaspMKInfo>> = MutableLiveData()
    fun getPaspMK() = data_PaspMK

    private var data_PaspHistory: MutableLiveData<MutableList<PaspHistoryRecInfo>> =  MutableLiveData()
    fun getPaspHistory() = data_PaspHistory

    private var f_SelectedPaspInfo: MutableLiveData<PaspInfo?> = MutableLiveData(null)
    fun getSelectedPaspInfo() = f_SelectedPaspInfo


    var SelectedPaspInfo: PaspInfo?
        get() = f_SelectedPaspInfo.value
        set(value) {
            if (value != f_SelectedPaspInfo.value) {
                f_SelectedPaspInfo.postValue(value)
                data_PaspMK.value = ArrayList<PaspMKInfo>()
                data_PaspHistory.value = ArrayList<PaspHistoryRecInfo>()
                loadPaspInfo(value!!.getStrParam("Key_Pasport"))
            }
        }



    private var data_mtask: MutableLiveData<MutableList<MTaskInfo>> = MutableLiveData()
    fun getMTaskList() = data_mtask

    fun loadGrZapList(SearchStr: String) {
        SkladGrZapJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_GRZAP_FIND, SearchStr,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseGrZapList(Result, SearchStr)
                }
            }, SkladGrZapJob
        )
    }


     fun loadPaspList(SearchStr: String) {
        PaspListJob = runHTTPQry(0, ISKaskadAPP.URL_PASPINFO_FINDPASP, SearchStr,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parsePaspList(Result)
                }
            }, PaspListJob
        )
    }



    fun loadPaspPlaceInfo(Key_Pasp_Place: String) {
        val URLParams = "&Key_Pasp_Place=$Key_Pasp_Place"

        PaspPlaceJob = runHTTPQry(0, ISKaskadAPP.URL_PASPPLACE_GETINFO, URLParams,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parsePlaceInfo(Result)
                }
            }, PaspPlaceJob
        )
    }



    fun loadPaspInfo(Key_Pasport: String) {
        val URLParams = "&Key_Pasport=$Key_Pasport"

        PaspInfoJob = runHTTPQry(0, ISKaskadAPP.URL_PASPINFO_GETINFO, URLParams,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parsePaspInfo(Result)
                }
            }, PaspInfoJob
        )
    }

    fun loadMTaskList() {
        MTaskJob = runHTTPQry(0, ISKaskadAPP.URL_MTASK_GETLIST, "",
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseMTaskList(Result)
                }
            }, MTaskJob
        )

    }

    private var LastSkladSearchStr:String=""

    fun loadSkladOstatokList(SearchStr: String) {
        LastSkladSearchStr=SearchStr
        SelectSubjJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_FRAGMENT_FIND, SearchStr,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseSkladOstatokList(Result)
                }
            }, SkladJob
        )
    }

    fun loadSkladOstatokDetail(URLParams: String) {

        SkladJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_FRAGMENT_INFO, URLParams,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    parseSkladOstatokDetail(Result)
                }
            }, SkladJob
        )
    }

    fun inventOstatok(Key_Pasp_Place:String, Key_Nacl_Str:String, Kolvo:String, Key_Nacl_Str_Sost:String, KolvoZag:String){

        var URLParams  = "&Key_Pasp_Place=$Key_Pasp_Place&Key_Nacl_Str=$Key_Nacl_Str"

        URLParams = if ( Key_Nacl_Str_Sost != "")  {
            "$URLParams&Key_Nacl_Str_Sost=$Key_Nacl_Str_Sost&KolvoZag=${ISKaskadAPP.encodeStr(KolvoZag.replace(",","."))}"
        }
        else  {
            "$URLParams&Kolvo=${ISKaskadAPP.encodeStr(Kolvo.replace(",","."))}"
        }

        SkladJob = runHTTPQry(0, ISKaskadAPP.URL_SKLAD_FRAGMENT_INVENT, URLParams,
            object : ParseResult {
                override fun parseResult(Result: String, QryID: Int) {
                    if (Result== ISKaskadAPP.URL_RESULT_SUCCESS) {
                        Toast.makeText(getApplication(), "Обновление данных прошло успешно", Toast.LENGTH_LONG).show()



                        val URLParams1 =  if (Key_Nacl_Str_Sost=="")
                            "&Key_Nacl_Str=$Key_Nacl_Str"
                        else
                            "&Key_Nacl_Str_Sost=$Key_Nacl_Str_Sost"

                        loadSkladOstatokDetail(URLParams1)


                    }
                    else
                        Toast.makeText(getApplication(), "Ошибка инвентаризации: $Result", Toast.LENGTH_LONG).show()
                }
            }, SkladJob
        )

    }


    var LastSubjListSearchStr:String = ""

    fun loadSubjList(SearchStr: String) {
        if ((SearchStr!=LastSubjListSearchStr)  || (1==1))
        {
            LastSubjListSearchStr = SearchStr

            SelectSubjJob = runHTTPQry(
                0, ISKaskadAPP.URL_SUBJ_FIND, SearchStr,
                object : ParseResult {
                    override fun parseResult(Result: String, QryID: Int) {
                        parseSubjList(Result)
                    }
                }, SelectSubjJob
            )
        }
    }


    fun parseSubjList(ResultStr: String) {
        val SearchResult: MutableList<SubjInfo> = mutableListOf()
        try {
            val data = JSONObject(ResultStr)
            val items = data.getJSONArray("subjinfo")
            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val frinfo = SubjInfo(item)
                SearchResult.add(frinfo)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (поиск группы запуска)")
        }
        getSubjList().postValue(SearchResult)
        if (SearchResult.count()==1)
            GetSelectedSubjInfo().postValue(SearchResult[0])
    }




    fun parsePaspInfo(JSonStr: String) {

        try {

            val data = JSONObject(JSonStr)

            val ItemInfo = data.getJSONArray("paspinfo")
            val itemData: JSONObject = ItemInfo.getJSONObject(0)

            val NewPaspInfo = PaspInfo(itemData)

            f_SelectedPaspInfo.value = NewPaspInfo

            if (NewPaspInfo.getStrParam("Key_Pasport") == SelectedPaspInfo?.getStrParam("Key_Pasport"))
            if (true)
            {

                val NewDataArray: ArrayList<PaspMKInfo> = ArrayList<PaspMKInfo>()

                val items = data.getJSONArray("paspmk")

                for (i in 0 until items.length()) {
                    val item: JSONObject = items.getJSONObject(i)
                    val DataStr = PaspMKInfo(item)

                    NewDataArray.add(DataStr)
                }
                getPaspMK().postValue(NewDataArray)
            }

            if (NewPaspInfo.getStrParam("Key_Pasport") == SelectedPaspInfo?.getStrParam("Key_Pasport"))

            if (true)
            {

                val NewDataArray: ArrayList<PaspHistoryRecInfo> = ArrayList<PaspHistoryRecInfo>()

                val items = data.getJSONArray("history")

                for (i in 0 until items.length()) {
                    val item: JSONObject = items.getJSONObject(i)
                    val DataStr = PaspHistoryRecInfo(item)

                    NewDataArray.add(DataStr)
                }
                getPaspHistory().postValue(NewDataArray)
            }


        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (Детализация паспорта)")
        }

    }

    fun parseMTaskList(JSonStr: String) {
        try {

            val data = JSONObject(JSonStr)

            val NewDataArray: ArrayList<MTaskInfo> = ArrayList<MTaskInfo>()

            val items = data.getJSONArray("data")

            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val DataStr = MTaskInfo(item)

                NewDataArray.add(DataStr)
            }
            getMTaskList().postValue(NewDataArray)

        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (Список задач)")

        }
    }

    fun parsePlaceInfo(JSonStr: String) {
        try {

            val data = JSONObject(JSonStr)
            val items = data.getJSONArray("placeinfo")
            val item: JSONObject = items.getJSONObject(0)
            val DataStr = PlaceInfo(item)
            getPaspPlace().postValue(DataStr)
        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (поиск места хранения): ${e.toString()}")
        }
    }

    fun parseUpdateMTask(ResultStr: String) {
        if (ResultStr != "")
            loadMTaskList()
    }

    fun parsePaspList(ResultStr: String) {
        val SearchResult: MutableList<PaspInfo> = mutableListOf()
        try {
            val data = JSONObject(ResultStr)
            val items = data.getJSONArray("paspinfo")
            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val frinfo = PaspInfo(item)
                SearchResult.add(frinfo)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (поиск паспорта)")
        }
        getPaspList().postValue(SearchResult)
    }

    fun parseGrZapList(ResultStr: String, SearchStr: String) {
        val SearchResult: MutableList<SkladGrZapInfo> = mutableListOf()
        try {
            val data = JSONObject(ResultStr)
            val items = data.getJSONArray("grzapinfo")
            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val frinfo = SkladGrZapInfo(item, SearchStr)
                SearchResult.add(frinfo)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            showError("Ошибка данных (поиск группы запуска)")
        }
        getGrZapList().postValue(SearchResult)
    }



    fun parseSkladOstatokList(ResultStr: String) {
        val New_FragmentList : ArrayList<SkladFragmentInfo> = ArrayList<SkladFragmentInfo> ()
        try {
            New_FragmentList.clear()
            val data = JSONObject(ResultStr)
            val items=data.getJSONArray("data")

            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val frinfo = SkladFragmentInfo(item)
                New_FragmentList.add(frinfo)
            }
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        data_FragmentList.postValue(New_FragmentList)

    }


    fun parseSkladOstatokDetail(jsonText:String) {

        val DataArray : ArrayList<SkladFragmentHistoryInfo> = ArrayList<SkladFragmentHistoryInfo> ()

        try {

            val data = JSONObject(jsonText)

            val ItemInfo=data.getJSONArray("data")
            val itemData: JSONObject = ItemInfo.getJSONObject(0)
            val SklFrInfo=SkladFragmentInfo(itemData)

            f_SelectedFragment.value =  SklFrInfo

            val items=data.getJSONArray("detail")

            for (i in 0 until items.length()) {
                val item: JSONObject = items.getJSONObject(i)
                val frinfo = SkladFragmentHistoryInfo(item)

                if (frinfo.getParam("Key_Nacl_Str_Sost").ParamIsNull)
                    frinfo.Name_K_Ed = SklFrInfo.getStrParam("Name_K_Ed")

                DataArray.add(frinfo)
            }
            sklad_fragment_history.value = DataArray
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private var LastProgress: Job = Job()

    private fun runProgress() {
        if (LastProgress.isActive) LastProgress.cancel()

        LastProgress = GlobalScope.launch(Dispatchers.IO) {
            try {
                for (i in 0..100) {
                    RunProgress.postValue(i)
                    delay(200)
                }
            } catch (e: java.lang.Exception) {
                RunProgress.postValue(-1)
            }

        }
    }

    private fun stopProgress() {
        if (LastProgress.isActive) LastProgress.cancel()
    }

    private fun showError(Text: String) {
        ErrorText.value = Text
        ISKaskadAPP.sendLogMessage(LogTAG, "ErrorText=$Text")

    }


    interface ParseResult {
        fun parseResult(Result: String, QryID: Int)
    }


    private   fun runHTTPQry(
        QryID: Int, URLStr: String, URLParams: String,
        callback: ParseResult, PrevJob: Job
    ): Job {

        var Result = PrevJob
        if (Result.isActive) Result.cancel()

        runProgress()
        ErrorText.value = ""


        Result = GlobalScope.launch(Dispatchers.IO) {

            val UrlStr = ISKaskadAPP.makeURLStr(URLStr, URLParams)

            ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY START ID=$QryID URLStr=$UrlStr")

            try {
                val ResultStr = URL(UrlStr).readText()

                delay(3000)

                ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY COMPLETE ID=$QryID")

                if (isActive)
                    launch(Dispatchers.Main) {
                        stopProgress()
                        callback.parseResult(ResultStr, QryID)
                    }
            } catch (e: Exception) {
                ISKaskadAPP.sendLogMessage(LogTAG, "URL QRY Error ID=$QryID URLStr=$UrlStr")
                if (isActive)
                    launch(Dispatchers.Main) {
                        stopProgress()
                        showError(e.toString())
                    }
            }
        }
        return Result
    }


    override fun onCleared() {

        ISKaskadAPP.sendLogMessage(LogTAG, "onCleared")

        val mySaver = ISKaskadAPP.sharedPreferences.edit()
        mySaver.putString("SearchStr", SearchStr)

        mySaver.apply()

        super.onCleared()
    }


}
