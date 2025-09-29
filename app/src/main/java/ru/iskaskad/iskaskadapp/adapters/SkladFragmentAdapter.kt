package ru.iskaskad.iskaskadapp.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion.myDateToStr
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.dto.SkladFragmentInfo



class SkladFragmentAdapter( var items: MutableList<SkladFragmentInfo>, private val callback: Callback) : RecyclerView.Adapter<SkladFragmentAdapter.MainHolder>()
{




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)  = MainHolder(
        LayoutInflater.from(parent.context).inflate( R.layout.sklad_list_ostatok_fragment_item, parent, false)
    )


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position], callback, items)
    }

    fun setData(newitems: MutableList<SkladFragmentInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(itemView: View) : MasterHolder(
        itemView,
        BindInfoArray(
            arrayListOf(


            BindInfoItem(R.id.fr_NamePredm, "FNP"),

            BindInfoItem(R.id.fr_Cod_Pasp_PlaceStr, "Cod_Pasp_Place_Str"),
            BindInfoItem(R.id.fr_StrMark, "StrMark"),
            BindInfoItem(R.id.fr_OstKolvo, "OstByStr"),
            BindInfoItem(R.id.fr_Edizm, "Name_K_Ed"),


            BindInfoItem(R.id.Fr_ProfRazm, "ProfRazm"),
            BindInfoItem(R.id.fr_OstZag, "OstZag"),

            BindInfoItem(R.id.fr_Plavka, "Cod_Plavka"),
            BindInfoItem(R.id.fr_Party, "Party"),
            BindInfoItem(R.id.frTOInfo, "TOInfo"),

            BindInfoItem(R.id.FragmentVidSertMax,"VidSert"),
            BindInfoItem(R.id.FragmentVidSertMin,"VidSert"),


            BindInfoItem(R.id.fr_Cod_Pasp_Place_Zag, "Cod_Pasp_Place_Zag")

        )))
    {

        private val FragmentKolvoInfoGr     : LinearLayout = itemView.findViewById(R.id.FragmentKolvoInfoGr)
        private val FragmentProfRazmInfoGr  : LinearLayout = itemView.findViewById(R.id.FragmentZagotovInfoGr)

        private val FragmentSertInfoMinGr   : LinearLayout = itemView.findViewById(R.id.FragmentSertInfoMinGr)
        private val FragmentSertInfoMaxGr   : LinearLayout = itemView.findViewById(R.id.FragmentSertInfoMaxGr)
        private val FragmentTOInfoGr        : LinearLayout = itemView.findViewById(R.id.frTOInfoGr)
        private val FragmentPaspInfoGr      : LinearLayout = itemView.findViewById(R.id.frPaspInfoGr)

        private val SelectedCheckBox        : CheckBox =itemView.findViewById(R.id.frcheckBox)

        private val SertInfoMin             :TextView = itemView.findViewById(R.id.fr_SertInfoMin)
        private val SertInfoMax             :TextView = itemView.findViewById(R.id.fr_SertInfoMax)

        private val Key_Id_Mat              :TextView = itemView.findViewById(R.id.fr_Key_Id_Mat)

        private val ExpandSertInfoBtn       :ImageView = itemView.findViewById(R.id.fr_ExpandSertInfoBtn)
        private val CollapseSertInfoBtn     :ImageView = itemView.findViewById(R.id.fr_CollapseSertInfoBtn)

        fun bind(item: SkladFragmentInfo, callback: Callback, items: MutableList<SkladFragmentInfo>) {

            super.bind(item)

            Key_Id_Mat.text =  itemView.context.resources.getString(R.string.KeyIDMatKeyPredm, item.getStrParam("Key_ID_Mat") , item.getStrParam("Key_Predm") )


            val  Key_Nacl_Str_Sost  = item.getParam("Key_Nacl_Str_Sost")

            if ( Key_Nacl_Str_Sost.ParamIsNull)
            {
                FragmentKolvoInfoGr.visibility = View.VISIBLE
                FragmentProfRazmInfoGr.visibility = View.GONE
            }
            else
            {
                FragmentKolvoInfoGr.visibility = View.GONE
                FragmentProfRazmInfoGr.visibility = View.VISIBLE
            }

            FragmentTOInfoGr.visibility = View.GONE
            FragmentPaspInfoGr.visibility = View.GONE


            val  Key_Sert  = item.getParam("Key_Sert")
            when {
                Key_Sert.ParamIsNull -> {
                    FragmentSertInfoMinGr.visibility = View.GONE
                    FragmentSertInfoMaxGr.visibility = View.GONE
                }
                item.SetInfoExpanded -> {
                    FragmentSertInfoMinGr.visibility = View.GONE
                    FragmentSertInfoMaxGr.visibility = View.VISIBLE
                }
                else -> {
                    FragmentSertInfoMinGr.visibility = View.VISIBLE
                    FragmentSertInfoMaxGr.visibility = View.GONE

                }
            }


            val DtStr = myDateToStr(item.getParam("Date_Sert").DataVal)


            SertInfoMin.text = itemView.context.resources.getString(R.string.NSertDate,item.getParam("N_Sert").StrValue,  DtStr )

            SertInfoMax.text = itemView.context.resources.getString(R.string.NSertDate,item.getParam("N_Sert").StrValue,  DtStr )

            SelectedCheckBox.isChecked =   item.IsChecked


            SelectedCheckBox.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    items[bindingAdapterPosition].IsChecked = SelectedCheckBox.isChecked
                }
            }

            itemView.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION)
                {
                    callback.onItemClicked(items[bindingAdapterPosition])
                }
            }
            CollapseSertInfoBtn.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    items[bindingAdapterPosition].SetInfoExpanded = false
                    FragmentSertInfoMinGr.visibility = View.VISIBLE
                    FragmentSertInfoMaxGr.visibility = View.GONE
                }
            }
            ExpandSertInfoBtn.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    items[bindingAdapterPosition].SetInfoExpanded = true
                    FragmentSertInfoMinGr.visibility = View.GONE
                    FragmentSertInfoMaxGr.visibility = View.VISIBLE
                }
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: SkladFragmentInfo)
    }

}