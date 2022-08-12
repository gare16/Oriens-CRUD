package com.garee.oriens.adapters

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.garee.oriens.R
import com.garee.oriens.databases.Database
import com.garee.oriens.views.activity.DetailDataActivity
import com.garee.oriens.views.activity.EditDataActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DataAdapter(var c: Context, var dataList: ArrayList<Database>): RecyclerView.Adapter<DataAdapter.HolderData>(){


    inner class HolderData(itemView : View): RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.nameTv)
        val date : TextView = itemView.findViewById(R.id.dateTv)
        val moreBtn : ImageView = itemView.findViewById(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.list_data,parent,false)
        return HolderData(inflater)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val newList = dataList[position]

        holder.name.text = newList.name
        holder.date.text = newList.date
        holder.moreBtn.setOnClickListener {
            moreOptionDialogs(newList, holder)
        }

        holder.itemView.setOnClickListener { v ->
            val dataId = newList.id
            val date = newList.date
            val name = newList.name
            val rate = newList.rate.toString()
            val split = newList.split.toString()
            val total = newList.total.toString()
            val value = newList.value.toString()

            val intent = Intent(v.context, DetailDataActivity::class.java)
            intent.putExtra("dataId",dataId)
            intent.putExtra("date",date)
            intent.putExtra("name",name)
            intent.putExtra("rate",rate)
            intent.putExtra("split",split)
            intent.putExtra("total",total)
            intent.putExtra("value",value)
            v.context.startActivity(intent)
        }
    }
    private fun moreOptionDialogs(newList: Database, holder: DataAdapter.HolderData) {

        val dataId = newList.id
        val name = newList.name
        val rate = newList.rate.toString()
        val split = newList.split.toString()
        val value = newList.value.toString()

        val popupMenu = PopupMenu(c.applicationContext, holder.moreBtn)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Ubah")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0){
                val intent = Intent(c, EditDataActivity::class.java)
                intent.putExtra("dataId", dataId)
                intent.putExtra("name", name)
                intent.putExtra("rate", rate)
                intent.putExtra("split", split)
                intent.putExtra("value", value)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                c.applicationContext.startActivity(intent)
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

}