package ru.iskaskad.iskaskadapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

import ru.iskaskad.iskaskadapp.dto.MasterDTO

open class MasterHolder(itemView: View, BindInfo: BindInfoArray) :
    RecyclerView.ViewHolder(itemView) {

    private var BindList : BindInfoArray = BindInfo

    init {
        BindList.bindToView(itemView)
    }

    open fun bind(item: MasterDTO) {
        BindList.setViewData(item)
    }

    /*
    fun GetBindItem (BindID:Int): BindInfoItem  {
        return   BindList.List.first { it.BindID==BindID }
    }
    */


}