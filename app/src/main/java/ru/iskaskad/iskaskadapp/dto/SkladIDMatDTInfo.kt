package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable


class SkladIDMatDTInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,
        mutableListOf(
                ParamInfo("Key_GrZap", ParamType.PT_INT),
                ParamInfo("Key_Sub_Ver", ParamType.PT_INT),
                ParamInfo("Name_Sub", ParamType.PT_STRING),
                ParamInfo("Key_Nacl_Str_Sost", ParamType.PT_INT),
                ParamInfo("Kolvo", ParamType.PT_INT),
                ParamInfo("Rashod", ParamType.PT_INT),

                ParamInfo("Kolvo", ParamType.PT_INT),
                ParamInfo("Ost", ParamType.PT_INT),
                ParamInfo("Name_K_Zagotov", ParamType.PT_STRING),
                ParamInfo("ProfRazm", ParamType.PT_STRING),
                ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING),
                ParamInfo("Cnt", ParamType.PT_INT)
        )
), Serializable
