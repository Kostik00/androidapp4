package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable

class PaspHistoryRecInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,

        mutableListOf(
                ParamInfo("Key_Pasp_Ev", ParamType.PT_INT),
                ParamInfo("Key_Pasport", ParamType.PT_INT),
                ParamInfo("UserName", ParamType.PT_STRING),
                ParamInfo("UserHost", ParamType.PT_STRING),
                ParamInfo("AdData", ParamType.PT_STRING),
                ParamInfo("FirstEv", ParamType.PT_DATA),
                ParamInfo("LastEv", ParamType.PT_DATA),
                ParamInfo("LastHost", ParamType.PT_STRING),
                ParamInfo("Name_Sub", ParamType.PT_STRING)
        )
), Serializable
