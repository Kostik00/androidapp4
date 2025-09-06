package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.PaspMKInfo

class PaspInfoMKAdapter(private val callback:Callback ) : RecyclerView.Adapter<PaspInfoMKAdapter.MainHolder>()
{

    private var items: MutableList<PaspMKInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {

        return  MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_pasp_info_mk_item, parent, false))
    }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<PaspMKInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(itemView,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.N_Oper_SM,"N_Oper_Sm") ,
            BindInfoItem(R.id.Name_K_Oper,"Name_Oper") ,
            BindInfoItem(R.id.TPz,"Tpz"),
            BindInfoItem(R.id.TSht,"Tsht"),
            BindInfoItem(R.id.TSm,"Tsm")
        ))) {

        private val ColorView     : View = itemView.findViewById(R.id.ColorView)
        private val SelectedCheckBox: CheckBox =itemView.findViewById(R.id.OperCheckBox)

        fun bind(item: PaspMKInfo,   callback:Callback, items: MutableList<PaspMKInfo>) {
            super.bind(item)

            when (item.getParam("Status_Oper").IntVal) {
                1->
                    ColorView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorOperInWork))
                2->
                    ColorView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorOperReady))
                else ->
                    ColorView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorOperWaiting))
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[adapterPosition])
                }
            }

            SelectedCheckBox.isChecked =   item.IsChecked

            SelectedCheckBox.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    items[adapterPosition].IsChecked = SelectedCheckBox.isChecked
                }
            }
        }

    }

    interface Callback {
        fun onItemClicked(item: PaspMKInfo)
    }

}