package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable

class PaspInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,
        mutableListOf(
                ParamInfo("Key_Pasport", ParamType.PT_INT),
                ParamInfo("Cod_Pasp", ParamType.PT_STRING),
                ParamInfo("Cod_Zak", ParamType.PT_STRING),
                ParamInfo("Cod_Predm", ParamType.PT_STRING),
                ParamInfo("Name_Predm", ParamType.PT_STRING),
                ParamInfo("Name_PredmMat", ParamType.PT_STRING),
                ParamInfo("Sortam_Mat", ParamType.PT_STRING),
                ParamInfo("Marka_Mat", ParamType.PT_STRING),
                ParamInfo("Name_Pasp_Status", ParamType.PT_STRING),
                ParamInfo("PaspKolvo", ParamType.PT_INT),
                ParamInfo("PaspMaster", ParamType.PT_STRING),
                ParamInfo("PaspUser", ParamType.PT_STRING),
                ParamInfo("Date_Pasp_Status", ParamType.PT_DATA),
                ParamInfo("Key_Pasp_Place", ParamType.PT_INT),
                ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING)
        )
), Serializable
