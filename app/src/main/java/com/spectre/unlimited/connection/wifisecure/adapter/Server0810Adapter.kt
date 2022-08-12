package com.spectre.unlimited.connection.wifisecure.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spectre.unlimited.connection.wifisecure.R
import com.spectre.unlimited.connection.wifisecure.bean0810.Server0810Bean
import com.spectre.unlimited.connection.wifisecure.manager.ServerManager
import com.spectre.unlimited.connection.wifisecure.util.getFlagResId
import kotlinx.android.synthetic.main.layout_server_item0810.view.*

class Server0810Adapter(
    private val context: Context,
    private val click:(bean:Server0810Bean)->Unit
):RecyclerView.Adapter<Server0810Adapter.MyView>() {
    private val list= arrayListOf<Server0810Bean>()
    init {
        list.add(ServerManager.createFastServer())
        list.addAll(ServerManager.getServerList())
    }

    inner class MyView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView =
        MyView(LayoutInflater.from(context).inflate(R.layout.layout_server_item0810,parent,false))

    override fun onBindViewHolder(holder: MyView, position: Int) {
        val server0810Bean = list[position]
        with(holder.itemView){
            tv_server_name.text=server0810Bean.country_0810_bean
            iv_server_flag.setImageResource(getFlagResId(server0810Bean.country_0810_bean))
        }
    }

    override fun getItemCount(): Int = list.size
}