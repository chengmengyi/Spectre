package com.demo.spectre.ui

import android.util.Log
import com.demo.spectre.R
import com.demo.spectre.manager.ConnectManager
import com.demo.spectre.manager.ConnectTimeManager
import com.demo.spectre.showad.LoopGetNativeAd
import com.demo.spectre.util.getFlagResId
import kotlinx.android.synthetic.main.layout_result0810.*

class Result0810Activity:AbsBaseActivity(), ConnectTimeManager.IConnectTimeCallback {
    private val nativeAd by lazy { LoopGetNativeAd(this,"sp_result") }


    override fun layoutId(): Int = R.layout.layout_result0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()

        iv_back.setOnClickListener { finish() }

        iv_server_flag.setImageResource(getFlagResId(ConnectManager.last.country_0810_bean))
        val connect = intent.getBooleanExtra("connect", false)
        tv_connect_status.text=if (connect) "Connected succeeded" else "Disconnected succeeded"
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
    }
}