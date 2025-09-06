package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.PaspHistoryRecInfo

class PaspInfoHistoryAdapter(private val callback:Callback ) : RecyclerView.Adapter<PaspInfoHistoryAdapter.MainHolder>()
{

    private var items: MutableList<PaspHistoryRecInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_pasp_info_history_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<PaspHistoryRecInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.FirstEv,"FirstEv") ,
            BindInfoItem(R.id.AdData,"AdData") ,
            BindInfoItem(R.id.UserHost,"UserHost"),
            BindInfoItem(R.id.LastHost,"LastHost"),
            BindInfoItem(R.id.Name_Sub,"Name_Sub")
        ))) {


        fun bind(item: PaspHistoryRecInfo,   callback:Callback, items: MutableList<PaspHistoryRecInfo>) {
            super.bind(item)

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[adapterPosition])
                }
            }

        }

    }

    interface Callback {
        fun onItemClicked(item: PaspHistoryRecInfo)
    }

}