package ru.iskaskad.iskaskadapp.dto


import org.json.JSONException
import org.json.JSONObject
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.convertSQLDateTimeToDate

import java.util.*

open class MasterDTO(jsonfrom: JSONObject, NewParamList: MutableList<ParamInfo>) {


//val tz = jsonfrom.getJSONObject(it.ParamName).getString("timezone")
//val tzt = jsonfrom.getJSONObject(it.ParamName).getString("timezone_type")
//val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSSSSS");

    val LogTAG="master_dto_"

    data class ValueHolder(
        val Name: String,
        var ParamIsNull: Boolean,
        var StrValue:String,
        var IntVal  :Int,
        var DataVal :Date,
        val DoubleVal:Double,
        val PType:ParamType
    )

    enum class ParamType {
        PT_INT,
        PT_DATA,
        PT_STRING,
        PT_FLOAT
    }

    data class ParamInfo(
        val ParamName :String,
        val ParamType :ParamType
    )


    val ParamValues    =  mutableListOf<ValueHolder> ()

    var  LastFound : ValueHolder


    init {
        NewParamList.forEach {

                var ParamIsNull = true
                var StrValue = ""
                var IntVal = 0
                var DataVal = Date(0)
                var DoubleVal = 0.0

                if (jsonfrom.has(it.ParamName) ) {
                        try {
                            ParamIsNull = jsonfrom.isNull(it.ParamName)
                            StrValue = jsonfrom.getString(it.ParamName)

                            if (!ParamIsNull)
                                when (it.ParamType) {

                                    ParamType.PT_INT -> IntVal = jsonfrom.getInt(it.ParamName)

                                    ParamType.PT_FLOAT -> DoubleVal =
                                        jsonfrom.getDouble(it.ParamName)

                                    ParamType.PT_DATA -> {

                                        StrValue =
                                            jsonfrom.getJSONObject(it.ParamName).getString("date")
                                        //val tz = jsonfrom.getJSONObject(it.ParamName).getString("timezone")
                                        //val tzt = jsonfrom.getJSONObject(it.ParamName).getString("timezone_type")
                                        //val formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSSSSS");
                                        DataVal = convertSQLDateTimeToDate(StrValue)!!
                                    }
                                    ParamType.PT_STRING -> {

                                    }

                                }
                        }
                        catch (e: JSONException){
                            ISKaskadAPP.sendLogMessage(LogTAG,"Exception Param not found '${it.ParamName}'")
                        }

                }
                else {
                    ISKaskadAPP.sendLogMessage(LogTAG,"Column not found '${it.ParamName}'")
                }

                ParamValues.add(ValueHolder(it.ParamName, ParamIsNull, StrValue, IntVal, DataVal, DoubleVal, it.ParamType))

        }
        LastFound=ParamValues[0]
    }

    fun hasParam(ParamName:String):Boolean {
        ParamValues.forEach{
            if (it.Name==ParamName) {
                LastFound = it
                return true
            }
        }
        return false
    }

    fun getParam (ParamName:String): ValueHolder {
        return   ParamValues.first { it.Name==ParamName }
    }

    fun getStrParam(ParamName: String):String{
        return   ParamValues.first { it.Name==ParamName }.StrValue

    }

    fun SetStrParam(ParamName: String, NewParamValue:String){
        val VH=ParamValues.first { it.Name==ParamName }!!
        VH.StrValue = NewParamValue

    }
    fun SetIntParam(ParamName: String, NewParamValue:Int){
        val VH=ParamValues.first { it.Name==ParamName }!!
        VH.IntVal = NewParamValue
        VH.StrValue = NewParamValue.toString()
    }

    fun GetIntParam(ParamName: String):Int {
        return   ParamValues.first { it.Name==ParamName }.IntVal
    }

}