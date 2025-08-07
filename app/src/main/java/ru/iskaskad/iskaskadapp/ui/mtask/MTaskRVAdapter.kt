package ru.iskaskad.iskaskadapp.ui.mtask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.adapters.BindInfoArray
import ru.iskaskad.iskaskadapp.adapters.BindInfoItem
import ru.iskaskad.iskaskadapp.adapters.MasterHolder
import ru.iskaskad.iskaskadapp.dto.MTaskInfo


class MTaskAdapter(private val callback: Callback) : RecyclerView.Adapter<MTaskAdapter.MainHolder>()
{

    private var items: MutableList<MTaskInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)  = MainHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fragment_mtask_item, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION)
            holder.bind(items[position], callback)
    }

    fun setData(newitems: MutableList<MTaskInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(
        itemView,
        BindInfoArray(
            arrayListOf(
                BindInfoItem(R.id.Cod_Predm, "Cod_Predm"),
                BindInfoItem(R.id.Name_Predm, "Name_Predm"),
                BindInfoItem(R.id.Cod_Pasp_Place, "Cod_Pasp_Place"),
                BindInfoItem(R.id.Cod_Pln_Pasp_Place, "Cod_Pln_Pasp_Place"),
                BindInfoItem(R.id.Cod_Pasp, "Cod_Pasp"),
                BindInfoItem(R.id.Name_PredmMat, "Name_PredmMat"),
                BindInfoItem(R.id.Sortam_Mat, "Sortam_Mat"),
                BindInfoItem(R.id.Marka_Mat, "Marka_Mat"),
                BindInfoItem(R.id.MassSm, "MassSm"),
                BindInfoItem(R.id.Massa, "Massa"),
                BindInfoItem(R.id.Name_Sub, "Name_Sub"),
                BindInfoItem(R.id.PaspKolvo, "PaspKolvo")
            )
        )
    ) {


        private val taskDetails     : LinearLayout = itemView.findViewById(R.id.taskDetails)
        private val ExpandCheckBox: CheckBox =itemView.findViewById(R.id.ExpandCheckBox)
        private val statusImage: ImageView = itemView.findViewById(R.id.statusImage)


        private fun setDetailVisibility(){
            if (ExpandCheckBox.isChecked)
                taskDetails.visibility=View.VISIBLE
            else
                taskDetails.visibility=View.GONE
        }


        fun bind(item: MTaskInfo, callback: Callback) {
            super.bind(item)

            ExpandCheckBox.setOnClickListener {
                if (getBindingAdapterPosition()  != RecyclerView.NO_POSITION) {
                    item.ShowAdInfo = ExpandCheckBox.isChecked
                    //  items[adapterPosition].ShowAdInfo = ExpandCheckBox.isChecked

                    setDetailVisibility()
                }
            }

            ExpandCheckBox.isChecked=item.ShowAdInfo

            setDetailVisibility()

            when (item.getParam("Key_MTask_Status").IntVal) {
                0 -> statusImage.setBackgroundResource(R.mipmap.checkbox_gray)
                1 -> statusImage.setBackgroundResource(R.mipmap.checkbox_yellow)
                2 -> statusImage.setBackgroundResource(R.mipmap.checkbox_green)
                else ->
                    statusImage.setBackgroundResource(R.mipmap.checkbox_gray)
            }

            itemView.setOnClickListener {
                if (getBindingAdapterPosition () != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(item)
                    //callback.onItemClicked(items[adapterPosition])
                }
            }

        }
    }

    interface Callback {
        fun onItemClicked(item: MTaskInfo)

    }

}