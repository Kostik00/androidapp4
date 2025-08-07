package ru.iskaskad.iskaskadapp.adapters

import android.view.View
import android.widget.TextView
import ru.iskaskad.iskaskadapp.ISKaskadAPP
import ru.iskaskad.iskaskadapp.dto.MasterDTO

class BindInfoArray (private var List: ArrayList<BindInfoItem>) {

    fun bindToView(v: View) {
        List.forEach {
            it.BindedView = v.findViewById(it.BindID)
        }
    }

    fun setViewData (data : MasterDTO) {
        List.forEach {

            if (data.hasParam(it.BindName)) {

                val Param = data.LastFound

                if (! Param.ParamIsNull) {
                    when (Param.PType) {
                        MasterDTO.ParamType.PT_INT ->
                            (it.BindedView as TextView).text = Param.IntVal.toString()
                        MasterDTO.ParamType.PT_FLOAT ->
                        {

                            (it.BindedView as TextView).text = String.format("%.3f",Param.DoubleVal)

                        }
                        MasterDTO.ParamType.PT_DATA ->
                        {
                            (it.BindedView as TextView).text =
                                ISKaskadAPP.myDateToStr(Param.DataVal)
                        }
                        else ->
                            (it.BindedView as TextView).text = Param.StrValue
                    }
                } else {
                    (it.BindedView as TextView).text = "-"
                }
            }
        }
    }


}