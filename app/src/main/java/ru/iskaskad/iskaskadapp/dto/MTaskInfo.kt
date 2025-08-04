package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject
import java.io.Serializable

class MTaskInfo(jsonfrom: JSONObject) : MasterDTO(
    jsonfrom,

    mutableListOf(
        ParamInfo("Key_MTask", ParamType.PT_INT),
        ParamInfo("Key_Pasport", ParamType.PT_INT),
        ParamInfo("Key_Pasp_Place", ParamType.PT_INT),
        ParamInfo("Key_Pln_Pasp_Place", ParamType.PT_INT),
        ParamInfo("Usr_PlnPlace", ParamType.PT_STRING),
        ParamInfo("Key_MTask_Status", ParamType.PT_INT),
        ParamInfo("DtCompl", ParamType.PT_DATA),
        ParamInfo("DtZayavka", ParamType.PT_DATA),
        ParamInfo("Key_Sub", ParamType.PT_INT),
        ParamInfo("DateMStatus", ParamType.PT_DATA),
        ParamInfo("WTime", ParamType.PT_FLOAT),
        ParamInfo("Name_Sub_Isp", ParamType.PT_STRING),
        ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING),
        ParamInfo("Cod_Pln_Pasp_Place", ParamType.PT_STRING),
        ParamInfo("Cod_Pasp", ParamType.PT_STRING),
        ParamInfo("Cod_Predm", ParamType.PT_STRING),
        ParamInfo("Name_Predm", ParamType.PT_STRING),
        ParamInfo("Massa", ParamType.PT_FLOAT),
        ParamInfo("PaspKolvo", ParamType.PT_INT),
        ParamInfo("MassSm", ParamType.PT_FLOAT),
        ParamInfo("Name_Sub", ParamType.PT_STRING),
        ParamInfo("FlCompl", ParamType.PT_INT),
        ParamInfo("Name_PredmMat", ParamType.PT_STRING),
        ParamInfo("Sortam_Mat", ParamType.PT_STRING),
        ParamInfo("Marka_Mat", ParamType.PT_STRING)
    )
), Serializable {

        var ShowAdInfo:Boolean = false

}
