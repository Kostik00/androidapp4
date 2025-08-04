package ru.iskaskad.iskaskadapp.dto


import org.json.JSONObject
import java.io.Serializable

class SkladFragmentInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,

        mutableListOf(

                ParamInfo("Key_Sub_Ver_SKL", ParamType.PT_INT),
                ParamInfo("Name_Skl", ParamType.PT_STRING),
                ParamInfo("Key_ID_Mat", ParamType.PT_INT),
                ParamInfo("Cod_ID_Mat", ParamType.PT_STRING),
                ParamInfo("Key_Sert", ParamType.PT_INT),
                ParamInfo("StrMark", ParamType.PT_STRING),
                ParamInfo("VidSert", ParamType.PT_STRING),
                ParamInfo("N_Sert", ParamType.PT_STRING),
                ParamInfo("Date_Sert", ParamType.PT_DATA),
                ParamInfo("Key_Sert_Pos", ParamType.PT_INT),
                ParamInfo("N_SertPos", ParamType.PT_STRING),
                ParamInfo("Cod_Plavka", ParamType.PT_STRING),
                ParamInfo("Party", ParamType.PT_STRING),
                ParamInfo("Pokovka", ParamType.PT_STRING),
                ParamInfo("SrokGodn", ParamType.PT_DATA),
                ParamInfo("RegN", ParamType.PT_STRING),
                ParamInfo("Key_Pasport", ParamType.PT_INT),
                ParamInfo("Cod_Pasp", ParamType.PT_STRING),
                ParamInfo("Name_Sub_Pr", ParamType.PT_STRING),
                ParamInfo("Key_Predm", ParamType.PT_INT),
                ParamInfo("Cod_Predm", ParamType.PT_STRING),
                ParamInfo("Name_Predm", ParamType.PT_STRING),
                ParamInfo("Sortam", ParamType.PT_STRING),
                ParamInfo("Marka", ParamType.PT_STRING),
                ParamInfo("Type_NTD_Sortam", ParamType.PT_STRING),
                ParamInfo("Name_NTD_Sortam", ParamType.PT_STRING),
                ParamInfo("Type_NTD_Marka", ParamType.PT_STRING),
                ParamInfo("Name_NTD_Marka", ParamType.PT_STRING),
                ParamInfo("PrintFlag", ParamType.PT_INT),
                ParamInfo("Name_K_Ed", ParamType.PT_STRING),
                ParamInfo("Cod_Ed_Izm", ParamType.PT_STRING),
                ParamInfo("Key_Nacl_Str", ParamType.PT_INT),
                ParamInfo("OstByStr", ParamType.PT_FLOAT),
                ParamInfo("Cod_Pasp_Place_Str", ParamType.PT_STRING),
                ParamInfo("Key_Pasp_Place_Str", ParamType.PT_INT),
                ParamInfo("DatePlace_Str", ParamType.PT_DATA),
                ParamInfo("Key_Nacl_Str_Sost", ParamType.PT_INT),
                ParamInfo("Cod_Zagotov", ParamType.PT_STRING),
                ParamInfo("Name_Zagotov", ParamType.PT_STRING),
                ParamInfo("ProfRazm", ParamType.PT_STRING),
                ParamInfo("M_Zag", ParamType.PT_FLOAT),
                ParamInfo("KolvoZag", ParamType.PT_INT),
                ParamInfo("OstZag", ParamType.PT_INT),
                ParamInfo("Key_Pasp_Place_Zag", ParamType.PT_INT),
                ParamInfo("DatePlace_Zag", ParamType.PT_DATA),
                ParamInfo("Cod_Pasp_Place_Zag", ParamType.PT_STRING),
                ParamInfo("Cod_Pasp_Place", ParamType.PT_STRING),
                ParamInfo("TOInfo", ParamType.PT_STRING),
                ParamInfo("FNP", ParamType.PT_STRING)

        )
), Serializable {

        var IsChecked:Boolean = false
        var SetInfoExpanded: Boolean=false

}
