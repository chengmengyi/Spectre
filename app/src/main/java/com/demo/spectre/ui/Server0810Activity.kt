package com.demo.spectre.ui

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.spectre.R
import com.demo.spectre.adapter.Server0810Adapter
import com.demo.spectre.bean0810.Server0810Bean
import com.demo.spectre.loadad.PrepareLoadAd
import com.demo.spectre.manager.ConnectManager
import com.demo.spectre.showad.ShowFullScreenAd
import kotlinx.android.synthetic.main.layout_server0810.*

class Server0810Activity:AbsBaseActivity() {
    private val back by lazy { ShowFullScreenAd(this,"sp_back"){ finish() } }

    override fun layoutId(): Int = R.layout.layout_server0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()
        PrepareLoadAd.preLoadAd("sp_back")

        recycler_view.apply {
            layoutManager=LinearLayoutManager(this@Server0810Activity)
            adapter=Server0810Adapter(this@Server0810Activity){
                click(it)
            }
        }
        iv_back.setOnClickListener { onBackPressed() }
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