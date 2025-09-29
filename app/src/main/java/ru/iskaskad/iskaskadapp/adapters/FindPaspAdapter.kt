package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.PaspInfo

class FindPaspAdapter(private val callback:Callback ) : RecyclerView.Adapter<FindPaspAdapter.MainHolder>()
{

    private var items: MutableList<PaspInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_find_pasp_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<PaspInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.Key_Pasport,"Key_Pasport") ,
            BindInfoItem(R.id.Cod_Pasp,"Cod_Pasp") ,
            BindInfoItem(R.id.Cod_Predm,"Cod_Predm"),
            BindInfoItem(R.id.Name_Predm,"Name_Predm"),
            BindInfoItem(R.id.Cod_Pasp_Place,"Cod_Pasp_Place")
            , BindInfoItem(R.id.PaspMaster,"PaspMaster")
        ))) {

//        val Card     : LinearLayout = itemView.findViewById(R.id.FragmentKolvoInfoGr)

        fun bind(item: PaspInfo,   callback:Callback, items: MutableList<PaspInfo>) {
            super.bind(item)

            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[bindingAdapterPosition])
                }
            }

        }
    }

    interface Callback {
        fun onItemClicked(item: PaspInfo)
    }

}