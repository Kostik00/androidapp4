/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.iskaskad.iskaskadapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.iskaskad.iskaskadapp.R
import ru.iskaskad.iskaskadapp.databinding.SkladIdmatTabItemBinding
import ru.iskaskad.iskaskadapp.dto.SkladIDMatDTInfo
import ru.iskaskad.iskaskadapp.dto.SkladIdMatInfo

class SkladOutMTabAdapter(private val callback: () -> Unit) : RecyclerView.Adapter<SkladOutMTabAdapter.MainHolder>() {

    private   var FDtInfo :  MutableList<SkladIdMatInfo>  = ArrayList<SkladIdMatInfo>()
    fun GetData() = FDtInfo

    fun SetData(NewDtInfo : MutableList<SkladIdMatInfo> )
    {
        FDtInfo = NewDtInfo
        notifyDataSetChanged()
    }

    interface Callback {
        fun onItemClicked(item: SkladIdMatInfo)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
       = MainHolder(  SkladIdmatTabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)  )

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        if (GetData().count() >= position) {
            holder.bind(FDtInfo[position])

        }

    }


    override fun getItemCount() = FDtInfo.count()

    class MainHolder(binding: SkladIdmatTabItemBinding) : MasterHolder(binding.root ,
        BindInfoArray(
            arrayListOf(
                BindInfoItem(R.id.IdMat_Key_Id_Mat,"Key_ID_Mat"),
                BindInfoItem(R.id.IdMat_Key_Predm,"Key_Predm"),
                BindInfoItem(R.id.IdMat_Name_Predm,"Name_Predm"),
                BindInfoItem(R.id.IdMat_Sortam,"Sortam")
            )))
    {



        var adapter = SkladIdMatDTAdapter(  object : SkladIdMatDTAdapter.Callback
        {
            override fun onItemClicked(item: SkladIDMatDTInfo)
            {
                //
            }
        })

       init  {
            binding.idmatdtrv.layoutManager = LinearLayoutManager(binding.root.context)
            binding.idmatdtrv.adapter =   adapter
        }


        fun bind (Item : SkladIdMatInfo)
        {
            super.bind(Item)
            adapter.setData(Item.IdMatItems)

        }



    }


}

