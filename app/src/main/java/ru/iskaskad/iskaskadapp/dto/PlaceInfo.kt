package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable

class PlaceInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,

        mutableListOf(
                ParamInfo("Key_Pasp_Place", ParamType.PT_INT),
                ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING),
                ParamInfo("Name_Pasp_Place", ParamType.PT_STRING)
        )
), Serializable {

        fun getKey_Pasp_Place(): Int = ParamValues[0].IntVal

        fun getCod_Pasp_Place(): String = ParamValues[1].StrValue

}
