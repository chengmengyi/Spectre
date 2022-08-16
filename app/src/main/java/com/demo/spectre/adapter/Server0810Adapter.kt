package com.demo.spectre.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.spectre.R
import com.demo.spectre.bean0810.Server0810Bean
import com.demo.spectre.manager.ConnectManager
import com.demo.spectre.manager.ServerManager
import com.demo.spectre.util.getFlagResId
import kotlinx.android.synthetic.main.layout_server_item0810.view.*

class Server0810Adapter(private val context: Context):RecyclerView.Adapter<Server0810Adapter.MyView>() {
    private val list= arrayListOf<Server0810Bean>()
    private var chooseServer=ConnectManager.current

    fun getChooseServer()=chooseServer

    init {
        list.add(ServerManager.createFastServer())
        list.addAll(ServerManager.getServerList())
    }

    inner class MyView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                chooseServer=list[layoutPosition]
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView =
        MyView(LayoutInflater.from(context).inflate(R.layout.layout_server_item0810,parent,false))

    override fun onBindViewHolder(holder: MyView, position: Int) {
        val server0810Bean = list[position]
        with(holder.itemView){
            item_layout.isSelected=server0810Bean.host_0810_bean==chooseServer.host_0810_bean
            tv_server_name.text=server0810Bean.country_0810_bean
            iv_server_flag.setImageResource(getFlagResId(server0810Bean.country_0810_bean))
        }
    }

    override fun getItemCount(): Int = list.size
}