package com.demo.spectre.manager

import com.demo.spectre.ui.AbsBaseActivity
import com.demo.spectre.util.printLog
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectManager:ShadowsocksConnection.Callback {
    private var context:AbsBaseActivity?=null
    var current=ServerManager.createFastServer()
    var last=ServerManager.createFastServer()
    private var state=BaseService.State.Idle
    private val sc=ShadowsocksConnection(true)
    private var iConnectedCallback:IConnectedCallback?=null

    fun setIConnectedCallback(iConnectedCallback:IConnectedCallback){
        this.iConnectedCallback=iConnectedCallback
    }

    fun connectSuccess()= state==BaseService.State.Connected

    private fun disconnectSuccess()= state==BaseService.State.Stopped

    fun isSuccess(connect:Boolean)=if (connect) connectSuccess() else disconnectSuccess()

    fun disconnect(){
        changeState(BaseService.State.Stopping)
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun connect(){
        changeState(BaseService.State.Connecting)
        GlobalScope.launch {
            if (ServerManager.isFast(current)){
                val fastServer = ServerManager.getFastServer()
                if (null!=fastServer){
                    DataStore.profileId = ServerManager.getServerId(fastServer)
                    Core.startService()
                }
            }else{
                DataStore.profileId = ServerManager.getServerId(current)
                Core.startService()
            }
        }
    }

    fun init(context:AbsBaseActivity){
        this.context=context
        sc.connect(context,this)
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        printLog("=stateChanged=${state}==")
        changeState(state)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        printLog("=onServiceConnected=${state}==")
        changeState(state)
        if (connectSuccess()){
            iConnectedCallback?.connectedCallback()
        }
    }

    private fun changeState(state: BaseService.State){
        this.state=state
        if (connectSuccess()){
            last = current
        }
    }

    override fun onBinderDied() {
        context?.run {
            sc.disconnect(this)
        }
    }

    fun onDestroy(){
        onBinderDied()
        context=null
    }

    interface IConnectedCallback{
        fun connectedCallback()
    }
}