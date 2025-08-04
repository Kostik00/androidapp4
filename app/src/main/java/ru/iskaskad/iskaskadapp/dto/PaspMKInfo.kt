package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable

class PaspMKInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,

        mutableListOf(
                ParamInfo("Key_Pasp_Rows", ParamType.PT_INT),
                ParamInfo("N_Oper_Sm", ParamType.PT_STRING),
                ParamInfo("Cod_Oper", ParamType.PT_STRING),
                ParamInfo("Name_Oper", ParamType.PT_STRING),
                ParamInfo("Name_K_Oper", ParamType.PT_STRING),
                ParamInfo("Tpz", ParamType.PT_FLOAT),
                ParamInfo("Tsht", ParamType.PT_FLOAT),
                ParamInfo("Tsm", ParamType.PT_FLOAT),
                ParamInfo("Name_Status_Oper", ParamType.PT_STRING),
                ParamInfo("Status_Oper", ParamType.PT_INT)
        )
), Serializable {
        var IsChecked:Boolean = false

}
