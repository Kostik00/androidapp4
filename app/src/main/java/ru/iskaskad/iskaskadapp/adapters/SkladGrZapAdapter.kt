package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.SkladGrZapInfo

class SkladGrZapAdapter(private val callback:Callback ) : RecyclerView.Adapter<SkladGrZapAdapter.MainHolder>()
{

    private var items: MutableList<SkladGrZapInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.sklad_grzap_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<SkladGrZapInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.Key_GrZap,"Key_GrZap"),
            BindInfoItem(R.id.GrZapFIO,"Name_Sub"),
            BindInfoItem(R.id.GrZapData,"DateGrZap"),
            BindInfoItem(R.id.GrZapText,"GrZapText")
        ))) {


        fun bind(item: SkladGrZapInfo,   callback:Callback, items: MutableList<SkladGrZapInfo>) {
            super.bind(item)

            itemView.setOnClickListener {
                val   adapterPosition = getBindingAdapterPosition()
                if (adapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[adapterPosition])
                }
            }

        }
    }

    interface Callback {
        fun onItemClicked(item: SkladGrZapInfo)
    }

}