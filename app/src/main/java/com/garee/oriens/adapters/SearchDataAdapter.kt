package com.garee.oriens.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.garee.oriens.R
import com.garee.oriens.databases.Database
import com.garee.oriens.databinding.ListDataBinding
import com.garee.oriens.views.activity.DetailDataActivity

class SearchDataAdapter(
    var c: Context, var searchList:ArrayList<Database>
): ListAdapter<Database, SearchDataAdapter.DataViewHolder>(COMPARATOR) {

    private object COMPARATOR: DiffUtil.ItemCallback<Database>(){
        override fun areItemsTheSame(oldItem: Database, newItem: Database): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Database, newItem: Database): Boolean {
            return oldItem.id == newItem.id
        }

    }

    inner class DataViewHolder(var v: ListDataBinding): RecyclerView.ViewHolder(v.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflter = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<ListDataBinding>(
            inflter, R.layout.list_data,parent,
            false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val newList = searchList[position]
        holder.v.isData = searchList[position]
        holder.v.root.setOnClickListener {
            val date = newList.date
            val name = newList.name
            val rate = newList.rate
            val split = newList.split
            val total = newList.total
            val value = newList.value


            /**set Data*/
            val mIntent = Intent(c, DetailDataActivity::class.java)
            mIntent.putExtra("date",date)
            mIntent.putExtra("name",name)
            mIntent.putExtra("rate",rate)
            mIntent.putExtra("split",split)
            mIntent.putExtra("total",total)
            mIntent.putExtra("value",value)

            c.startActivity(mIntent)
        }
    }
    override fun getItemCount(): Int {
        return  searchList.size
    }
}