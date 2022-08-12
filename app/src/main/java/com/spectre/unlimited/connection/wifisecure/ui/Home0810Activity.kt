package com.spectre.unlimited.connection.wifisecure.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.view.Gravity
import android.view.animation.LinearInterpolator
import com.spectre.unlimited.connection.wifisecure.R
import com.spectre.unlimited.connection.wifisecure.manager.ConnectManager
import com.spectre.unlimited.connection.wifisecure.manager.ConnectTimeManager
import com.spectre.unlimited.connection.wifisecure.util.Acc0810
import com.spectre.unlimited.connection.wifisecure.util.InfoConfig
import com.spectre.unlimited.connection.wifisecure.util.getFlagResId
import com.spectre.unlimited.connection.wifisecure.util.showView
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.layout_home0810.*

class Home0810Activity:AbsBaseActivity(), ConnectTimeManager.IConnectTimeCallback,
    ConnectManager.IConnectedCallback {
    private var click=true
    private var havePermission = false
    private var valueAnimator:ValueAnimator?=null

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
                    intent.putExtra(Intent.EXTRA_EMAIL, InfoConfig.Email)
                    startActivity(intent)
                }catch (e:Exception){
                    toast("Contact us by email：${InfoConfig.Email}")
                }
            }
        }
    }

    private fun preCall(connected:Boolean=ConnectManager.connectSuccess()){
        click=false
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
        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                val duration = (10 * (pro / 100.0F)).toInt()
                if (duration in 2..9){
                    if (ConnectManager.isSuccess(connect)){
                        stopValueAnimator()
                        result(connect)
                    }
                }else if (duration>=10){
                    stopValueAnimator()
                    result(connect)
                }
            }
            start()
        }
    }

    private fun result(connect: Boolean,intentToResult:Boolean=true){
        if (ConnectManager.isSuccess(connect)){
            if (connect){
                refreshUI(BaseService.State.Connected)
            }else{
                refreshUI(BaseService.State.Stopped)
                refreshConnectedFlag()
            }
            if (intentToResult){
                jumpResult(connect)
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

    private fun jumpResult(connect:Boolean){
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
                refreshConnectText("Connected")
                ConnectTimeManager.startCountTime()
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

    override fun onDestroy() {
        super.onDestroy()
        ConnectManager.onDestroy()
        stopValueAnimator()
        ConnectTimeManager.removeCallback(this)
    }
}