package com.spectre.unlimited.connection.wifisecure.ui

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.spectre.unlimited.connection.wifisecure.R
import com.spectre.unlimited.connection.wifisecure.adapter.Server0810Adapter
import com.spectre.unlimited.connection.wifisecure.bean0810.Server0810Bean
import com.spectre.unlimited.connection.wifisecure.loadad.PrepareLoadAd
import com.spectre.unlimited.connection.wifisecure.manager.ConnectManager
import com.spectre.unlimited.connection.wifisecure.showad.ShowFullScreenAd
import kotlinx.android.synthetic.main.layout_server0810.*

class Server0810Activity:AbsBaseActivity() {
    private val back by lazy { ShowFullScreenAd(this,"sp_back"){ finish() } }
    private val myAdapter by lazy { Server0810Adapter(this@Server0810Activity) }

    override fun layoutId(): Int = R.layout.layout_server0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()
        PrepareLoadAd.preLoadAd("sp_back")

        recycler_view.apply {
            layoutManager=LinearLayoutManager(this@Server0810Activity)
            adapter=myAdapter
        }
        iv_back.setOnClickListener { onBackPressed() }
        iv_connect_btn.setOnClickListener {
            click(myAdapter.getChooseServer())
        }
    }

    private fun click(bean:Server0810Bean){
        var action=""
        val current = ConnectManager.current
        val connectSuccess = ConnectManager.connectSuccess()
        if (current.host_0810_bean==bean.host_0810_bean){
            if (!connectSuccess){
                action="connect"
            }
        }else{
            action=if (connectSuccess) "disconnect" else "connect"
        }
        if (action=="disconnect"){
            showDialog { result(action,bean) }
        }else{
            result(action,bean)
        }
    }


    private fun result(action:String,bean:Server0810Bean){
        ConnectManager.current=bean
        val intent = Intent()
        intent.putExtra("action",action)
        setResult(810,intent)
        finish()
    }

    override fun onBackPressed() {
        if (back.hasAdData()){
            back.show()
            return
        }
        finish()
    }
}