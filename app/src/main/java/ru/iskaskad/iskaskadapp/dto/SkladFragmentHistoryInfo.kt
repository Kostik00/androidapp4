package ru.iskaskad.iskaskadapp.dto


import org.json.JSONObject
import java.io.Serializable

class SkladFragmentHistoryInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,
        mutableListOf(
                ParamInfo("Key_Nacl_Str", ParamType.PT_INT),
                ParamInfo("Key_Nacl_Str_Sost", ParamType.PT_INT),
                ParamInfo("Key_Pasp_Place", ParamType.PT_INT),
                ParamInfo("KolvoByStr", ParamType.PT_FLOAT),
                ParamInfo("KolvoZag", ParamType.PT_INT),
                ParamInfo("DTH", ParamType.PT_DATA),
                ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING)
        )
), Serializable {
        var Name_K_Ed :String="ШТ"

}
