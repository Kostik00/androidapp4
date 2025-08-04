package ru.iskaskad.iskaskadapp.dto

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import java.io.Serializable
import java.util.ArrayList


class SkladGrZapDetailedInfo(jsonfrom: JSONObject) : MasterDTO(
   jsonfrom,
   mutableListOf(
      ParamInfo("Key_GrZap", ParamType.PT_INT),
      ParamInfo("DateGrZap", ParamType.PT_DATA),
      ParamInfo("GrZapText", ParamType.PT_STRING),
      ParamInfo("Key_GrZap_Status", ParamType.PT_INT),
      ParamInfo("Key_Sub_Ver_Z", ParamType.PT_INT),
      ParamInfo("Name_Sub", ParamType.PT_STRING)
   )
)
{
   var isLoaded: Boolean = false;

   var IdMatInfo :   MutableList<SkladIdMatInfo>  = ArrayList<SkladIdMatInfo>()

   public var SearchStr = ""


   val IdMatInfoTag = "IdMatInfo"

   init {
       if (jsonfrom.has(IdMatInfoTag)) {
//          val NewDataArray: ArrayList<SkladIdMatInfo> = ArrayList<SkladIdMatInfo>()

          val items = jsonfrom.getJSONArray(IdMatInfoTag)
          for (i in 0 until items.length()) {
             val item: JSONObject = items.getJSONObject(i)
             val DataStr = SkladIdMatInfo(item)
             IdMatInfo.add(DataStr)
          }
          isLoaded=true;
             //IdMatInfo .postValue(NewDataArray)
       }
   }
}
