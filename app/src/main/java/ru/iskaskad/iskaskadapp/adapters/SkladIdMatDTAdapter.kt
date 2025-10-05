package ru.iskaskad.iskaskadapp.adapters

import android.icu.lang.UCharacter
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.databinding.SkladIdmatDtItemBinding
import ru.iskaskad.iskaskadapp.dto.SkladIDMatDTInfo


class SkladIdMatDTAdapter(private val callback:Callback ) : RecyclerView.Adapter<SkladIdMatDTAdapter.MainHolder>()
{

    private var items: MutableList<SkladIDMatDTInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = SkladIdmatDtItemBinding.inflate(LayoutInflater.from(parent.context))


        return MainHolder(view)
    }




    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind( items[position], callback, items)
    }

    fun setData( newitems: MutableList<SkladIDMatDTInfo>) {
        items = newitems
        notifyDataSetChanged()
    }

    class MainHolder(var binding: SkladIdmatDtItemBinding) : MasterHolder(binding.root,
        BindInfoArray(
        arrayListOf(
            BindInfoItem(R.id.IdMatDT_Name_K_Zagotov,"Name_K_Zagotov"),
            BindInfoItem(R.id.IdMatDT_ProfRazm,"ProfRazm"),
            BindInfoItem(R.id.IdMatDT_Cod_Pasp_Place,"Cod_Pasp_Place"),
            BindInfoItem(R.id.IdMatDT_Name_Sub,"Name_Sub"),
            BindInfoItem(R.id.IdMatDT_Ost,"Ost"),
            BindInfoItem(R.id.IdMatDT_Cnt,"Cnt")
        ))) {


        fun bind(item: SkladIDMatDTInfo, callback:Callback, items: MutableList<SkladIDMatDTInfo>) {
            super.bind(item)
            binding.IdMatDTCnt.filters = arrayOf( InputFilterMinMax("0", item.getParam("Ost").StrValue  ))

            fun SaveData() {
                item.SetIntParam("Cnt",  Integer.parseInt(binding.IdMatDTCnt.getText().toString())    )

            }

            binding.addBtn.setOnClickListener {
                try {
                    val NewVal = (Integer.parseInt(binding.IdMatDTCnt.getText().toString()) + 1)
                    if (NewVal <= item.getParam("Ost").IntVal)
                        binding.IdMatDTCnt.setText(NewVal.toString())
                }
                catch(nfe: NumberFormatException   ) {
                    binding.IdMatDTCnt.setText("0")
                }
                SaveData()
            }


            binding.removeBtn.setOnClickListener {
                try {

                    val NewVal =  (Integer.parseInt(binding.IdMatDTCnt.getText().toString()) - 1 )
                    if (NewVal >= 0)
                        binding.IdMatDTCnt.setText(  NewVal.toString() )

                }
                catch (nfe: NumberFormatException) {
                    binding.IdMatDTCnt.setText("0")
                }
                SaveData()
            }


            binding.IdMatDTCnt.setOnClickListener {
                val adapterPosition = getBindingAdapterPosition()
                if (adapterPosition != RecyclerView.NO_POSITION)
                {
                    SaveData()
                    //item.SetIntParam("Cnt",  Integer.parseInt(binding.IdMatDTCnt.getText().toString())    )
                }


            }

        }
    }

    interface Callback {
        fun onItemClicked(item: SkladIDMatDTInfo)
    }


    internal class InputFilterMinMax : InputFilter {
        private var min: Int
        private var max: Int

        constructor(min: Int, max: Int) {
            this.min = min
            this.max = max
        }

        constructor(min: String, max: String) {
            this.min = min.toInt()
            this.max = max.toInt()
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input = (dest.toString() + source.toString()).toInt()
                if (isInRange(min, max, input)) return  null
            } catch (nfe: NumberFormatException) {
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c >= a && c <= b else c >= b && c <= a
        }
    }


}