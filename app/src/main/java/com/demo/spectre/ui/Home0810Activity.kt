package com.demo.spectre.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.util.Log
import android.view.Gravity
import android.view.animation.LinearInterpolator
import com.blankj.utilcode.util.ActivityUtils
import com.demo.spectre.R
import com.demo.spectre.loadad.PrepareLoadAd
import com.demo.spectre.manager.ConnectManager
import com.demo.spectre.manager.ConnectTimeManager
import com.demo.spectre.showad.LoopGetNativeAd
import com.demo.spectre.showad.ShowFullScreenAd
import com.demo.spectre.util.*
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.layout_home0810.*

class Home0810Activity:AbsBaseActivity(), ConnectTimeManager.IConnectTimeCallback,
    ConnectManager.IConnectedCallback {
    private var click=true
    private var connect=false
    private var havePermission = false
    private var valueAnimator:ValueAnimator?=null
    private val connectFullScreen by lazy { ShowFullScreenAd(this,"sp_connect"){ jumpResult() } }
    private val nativeAd by lazy { LoopGetNativeAd(this,"sp_home") }


    private val launcher = registerForActivityResult(StartService()) {
        if (!it && havePermission) {
            havePermission = false
            startConnect()
        } else {
            click=true
            toast("Connected fail")
        }
    }

    override fun layoutId(): Int = R.layout.layout_home0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()
        ConnectManager.init(this)
        ConnectTimeManager.addCallback(this)
        ConnectManager.setIConnectedCallback(this)
        setListener()
    }

    private fun setListener(){
        iv_set.setOnClickListener {
            if (!drawerLayout.isOpen&&click){
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }

        view_connect.setOnClickListener {
            if (!drawerLayout.isOpen&&click){
                preCall()
            }
        }

        view_choose_server.setOnClickListener {
            if (!drawerLayout.isOpen&&click){
                startActivityForResult(Intent(this,Server0810Activity::class.java),810)
            }
        }

        tv_update.setOnClickListener {
            if (drawerLayout.isOpen){
                jumpGooglePlay()
            }
        }

        tv_share.setOnClickListener {
            if (drawerLayout.isOpen){
                shareAppDownPath()
            }
        }

        tv_privacy.setOnClickListener {
            if (drawerLayout.isOpen){
                startActivity(Intent(this,Web0810Activity::class.java))
            }
        }
        tv_contact.setOnClickListener {
            if (drawerLayout.isOpen){
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data= Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL,InfoConfig.Email)
                    startActivity(intent)
                }catch (e:Exception){
                    toast("Contact us by email：${InfoConfig.Email}")
                }
            }
        }
        center_click_view.setOnClickListener {
            view_connect.performClick()
        }
    }

    private fun preCall(connected:Boolean=ConnectManager.connectSuccess()){
        click=false
        PrepareLoadAd.preLoadAd("sp_result")
        PrepareLoadAd.preLoadAd("sp_connect")
        if (connected){
            startDisconnect()
        }else{
            if(checkNetConnect()){
                if (VpnService.prepare(this) != null) {
                    havePermission = true
                    launcher.launch(null)
                    return
                }
                startConnect()
            }else{
                click=true
            }
        }
    }

    private fun startDisconnect(){
        ConnectManager.disconnect()
        refreshUI(BaseService.State.Stopping)
        checkAdmobResult(false)
    }

    private fun startConnect(){
        ConnectManager.connect()
        ConnectTimeManager.resetTime()
        refreshUI(BaseService.State.Connecting)
        checkAdmobResult(true)
    }

    private fun checkAdmobResult(connect: Boolean){
        this.connect=connect
        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                val duration = (10 * (pro / 100.0F)).toInt()
                if (duration in 2..9){
                    if (ConnectManager.isSuccess(connect)&&connectFullScreen.hasAdData()){
                        stopValueAnimator()
                        result(intentToResult = false)
                        connectFullScreen.show()
                    }
                }else if (duration>=10){
                    stopValueAnimator()
                    result()
                }
            }
            start()
        }
    }

    private fun result(intentToResult:Boolean=true){
        if (ConnectManager.isSuccess(connect)){
            if (connect){
                refreshUI(BaseService.State.Connected)
            }else{
                refreshUI(BaseService.State.Stopped)
                refreshConnectedFlag()
            }
            if (intentToResult){
                jumpResult()
            }
            click=true
        }else{
            toast(if (connect) "Connect Fail" else "Disconnect Fail")
            refreshUI(BaseService.State.Idle)
            click=false
        }
    }

    private fun refreshConnectedFlag(){
        iv_server_flag.setImageResource(getFlagResId(ConnectManager.current.country_0810_bean))
    }

    private fun jumpResult(){
        if (Acc0810.isFront){
            val intent = Intent(this, Result0810Activity::class.java)
            intent.putExtra("connect",connect)
            startActivity(intent)
        }
    }


    private fun refreshUI(state: BaseService.State){
        when(state){
            BaseService.State.Stopped,BaseService.State.Idle-> {
                connecting_lottie_view.showView(false)
                iv_connect_idle.showView(true)
                refreshConnectText("Connect")
                tv_connect_time.text="00:00:00"
                iv_connect_idle.setImageResource(R.drawable.connect_default_img)
                ConnectTimeManager.stopCountTime()
            }
            BaseService.State.Connecting,BaseService.State.Stopping-> {
                connecting_lottie_view.showView(true)
                iv_connect_idle.showView(false)
                refreshConnectText(if (state==BaseService.State.Connecting) "Connecting" else "Stopping")
            }
            BaseService.State.Connected-> {
                connecting_lottie_view.showView(false)
                iv_connect_idle.showView(true)
                refreshConnectText("Disconnect")
                ConnectTimeManager.startCountTime()
                iv_connect_idle.setImageResource(R.drawable.connected_img)
            }
        }
    }

    private fun refreshConnectText(string: String){
        tv_connect_text.text=string
    }

    private fun stopValueAnimator(){
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.cancel()
        valueAnimator=null
    }

    override fun connectTimeCallback(time: String) {
        tv_connect_time.text=time
    }

    override fun connectedCallback() {
        refreshConnectText("Connected")
        refreshConnectedFlag()
    }

    override fun stoppedCallback() {
        if (click){
            refreshUI(BaseService.State.Stopped)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==810){
            when(data?.getStringExtra("action")){
                "connect"->{
                    refreshConnectedFlag()
                    preCall(false)
                }
                "disconnect"->{
                    preCall(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Acc0810.refreshHomeNativeAd){
            nativeAd.loopGetNativeAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectManager.onDestroy()
        stopValueAnimator()
        ConnectTimeManager.removeCallback(this)
        Acc0810.refreshHomeNativeAd=true
        nativeAd.cancelJob()
        ConnectManager.removeIConnectedCallback(this)
    }
}