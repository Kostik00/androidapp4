package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.SkladFragmentHistoryInfo
import java.util.*

class SkladFragmentHistoryAdapter (private var items: ArrayList<SkladFragmentHistoryInfo>, private val callback:Callback ) : RecyclerView.Adapter<SkladFragmentHistoryAdapter.MainHolder>()
{



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.sklad_invent_ostatok_fragment_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: ArrayList<SkladFragmentHistoryInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.h_Cod_Pasp_Place,"Cod_Pasp_Place"),
            BindInfoItem(R.id.h_DataPlace,"DTH")

        ))) {

        private val Kolvo: TextView = itemView.findViewById(R.id.h_Kolvo)
        private val Edizm: TextView = itemView.findViewById(R.id.h_Edizm)



        fun bind(item: SkladFragmentHistoryInfo, callback:Callback, items: ArrayList<SkladFragmentHistoryInfo>) {
            super.bind(item)

            var Param = item.getParam("Key_Nacl_Str_Sost")

            Param = if (Param.ParamIsNull) {
                item.getParam("KolvoByStr")
            } else {
                item.getParam("KolvoZag")
            }
            Edizm.text = item.Name_K_Ed
            if (Param.ParamIsNull)
                Kolvo.text="-"
            else
                Kolvo.text = Param.StrValue



            itemView.setOnClickListener {
                val adapterPosition = getBindingAdapterPosition()
                if (adapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[adapterPosition])
                }
            }

        }
    }

    interface Callback {
        fun onItemClicked(item: SkladFragmentHistoryInfo)
    }

}