package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable
import java.util.ArrayList


class SkladIdMatInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,
        mutableListOf(
                ParamInfo("Key_ID_Mat", ParamType.PT_INT),
                ParamInfo("Key_Predm", ParamType.PT_INT),
                ParamInfo("VidSert", ParamType.PT_STRING),
                ParamInfo("N_Sert", ParamType.PT_STRING),
                ParamInfo("RegN", ParamType.PT_STRING),
                ParamInfo("Cod_Plavka", ParamType.PT_STRING),
                ParamInfo("Party", ParamType.PT_STRING),
                ParamInfo("Cod_Predm", ParamType.PT_STRING),
                ParamInfo("Name_Predm", ParamType.PT_STRING),
                ParamInfo("Sortam", ParamType.PT_STRING),
                ParamInfo("Type_NTD_Sortam", ParamType.PT_STRING),
                ParamInfo("Name_NTD_Sortam", ParamType.PT_STRING),
                ParamInfo("Marka", ParamType.PT_STRING),
                ParamInfo("Type_NTD_Marka", ParamType.PT_STRING),
                ParamInfo("Name_NTD_Marka", ParamType.PT_STRING)
        )
) , Serializable
{

        public var IdMatItems : MutableList<SkladIDMatDTInfo> = ArrayList<SkladIDMatDTInfo>()

        val DataTag = "Android_SostOst"

        init {
                if (jsonfrom.has(DataTag)) {
                        val items = jsonfrom.getJSONArray(DataTag)
                        for (i in 0 until items.length()) {
                                val item: JSONObject = items.getJSONObject(i)
                                val DataStr = SkladIDMatDTInfo(item)
                                IdMatItems.add(DataStr)
                        }
                }
        }


}
