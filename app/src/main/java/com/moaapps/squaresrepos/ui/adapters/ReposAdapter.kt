package com.moaapps.squaresrepos.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.moaapps.squaresrepos.R
import com.moaapps.squaresrepos.pojo.Repo
import com.moaapps.squaresrepos.ui.interfaces.OnRepoClickedListener
import kotlinx.android.synthetic.main.item_repo.view.*

class ReposAdapter(val listener:OnRepoClickedListener): RecyclerView.Adapter<ReposAdapter.Holder>() {
    var repos = ArrayList<Repo>()
    val stockList = ArrayList<Repo>()
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card:CardView = itemView.findViewById(R.id.card)
        val name:TextView = itemView.findViewById(R.id.name)
        val description:TextView = itemView.findViewById(R.id.description)
        val username:TextView = itemView.findViewById(R.id.user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val repo = repos[position]
        holder.name.text = repo.name
        holder.description.text = repo.description
        holder.username.text = repo.owner
        if (repo.fork == null || !repo.fork!!){
            holder.card.setCardBackgroundColor(Color.parseColor("#ccf6c8"))
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongClick(repo)
            true
        }
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    fun filter(query:String){
        repos.clear()
        if (query.isEmpty()){
            repos.addAll(stockList)
        }else{
            for (repo in stockList){
                if (repo.name.toLowerCase().contains(query.toLowerCase())){
                    repos.add(repo)
                }
            }
        }
        notifyDataSetChanged()
    }




    fun add(list:List<Repo>){
        repos.addAll(list)
        stockList.addAll(list)
        notifyDataSetChanged()
    }
    fun empty(){
        repos.clear()
        notifyDataSetChanged()
    }
}