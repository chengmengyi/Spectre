package com.spectre.unlimited.connection.wifisecure.ui

import com.spectre.unlimited.connection.wifisecure.R
import com.spectre.unlimited.connection.wifisecure.manager.ConnectManager
import com.spectre.unlimited.connection.wifisecure.manager.ConnectTimeManager
import com.spectre.unlimited.connection.wifisecure.showad.LoopGetNativeAd
import com.spectre.unlimited.connection.wifisecure.util.getFlagResId
import kotlinx.android.synthetic.main.layout_result0810.*

class Result0810Activity:AbsBaseActivity(), ConnectTimeManager.IConnectTimeCallback,
    ConnectManager.IConnectedCallback {
    private val nativeAd by lazy { LoopGetNativeAd(this,"sp_result") }


    override fun layoutId(): Int = R.layout.layout_result0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()
        ConnectManager.setIConnectedCallback(this)

        iv_back.setOnClickListener { finish() }

        iv_server_flag.setImageResource(getFlagResId(ConnectManager.last.country_0810_bean))
        val connect = intent.getBooleanExtra("connect", false)
        refreshUI(connect)
    }

    private fun refreshUI(connect:Boolean){
        tv_connect_status.text=if (connect) "Connected succeeded" else "Disconnected succeeded"
        tv_connect_time.isSelected=connect
        if (connect){
            ConnectTimeManager.addCallback(this)
        }else{
            tv_connect_time.text=ConnectTimeManager.transTime()
        }
    }
    override fun connectTimeCallback(time: String) {
        tv_connect_time.text=time
    }

    override fun onResume() {
        super.onResume()
        nativeAd.loopGetNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAd.cancelJob()
        ConnectTimeManager.removeCallback(this)
        ConnectManager.removeIConnectedCallback(this)
    }

    override fun connectedCallback() {

    }

    override fun stoppedCallback() {
        refreshUI(false)
    }
}