package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable


class SkladGrZapInfo(jsonfrom: JSONObject, var SearchStr:String) : MasterDTO(
        jsonfrom,
        mutableListOf(
                ParamInfo("Key_GrZap", ParamType.PT_INT),
                ParamInfo("DateGrZap", ParamType.PT_DATA),
                ParamInfo("GrZapText", ParamType.PT_STRING),
                ParamInfo("Key_GrZap_Status", ParamType.PT_INT),
                ParamInfo("Key_Sub_Ver_Z", ParamType.PT_INT),
                ParamInfo("Name_Sub", ParamType.PT_STRING)
        )
), Serializable
