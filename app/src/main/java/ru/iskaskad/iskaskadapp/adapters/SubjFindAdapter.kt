package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.SubjInfo

class SubjFindAdapter(private val callback:Callback ) : RecyclerView.Adapter<SubjFindAdapter.MainHolder>()
{

    private var items: MutableList<SubjInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.subj_item_fragment, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<SubjInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.subjItem_F_Name_Sub,"F_Name_Sub"),
            BindInfoItem(R.id.subjItem_Name_Sub,"Name_Sub")
        ))) {

//        val Card     : LinearLayout = itemView.findViewById(R.id.FragmentKolvoInfoGr)

        fun bind(item: SubjInfo,   callback:Callback, items: MutableList<SubjInfo>) {
            super.bind(item)

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
        fun onItemClicked(item: SubjInfo)
    }

}