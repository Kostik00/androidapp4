package ru.iskaskad.iskaskadapp.dto

import org.json.JSONObject

class SubjInfo(jsonfrom: JSONObject) : MasterDTO(
        jsonfrom,

        mutableListOf(
                ParamInfo("Key_sub", ParamType.PT_INT),
                ParamInfo("Name_Sub", ParamType.PT_STRING),
                ParamInfo("Key_Class", ParamType.PT_INT),


                ParamInfo("cod_sub", ParamType.PT_STRING),
                ParamInfo("Key_Sub_Ver", ParamType.PT_INT),
                ParamInfo("F_Name_Sub", ParamType.PT_STRING),
                ParamInfo("Sub_Version", ParamType.PT_INT),
                ParamInfo("Fl", ParamType.PT_INT)

)
) {


        fun getF_Name_Sub(): String = ParamValues[5].StrValue

}
