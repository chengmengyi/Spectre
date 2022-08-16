package com.spectre.unlimited.connection.wifisecure.manager

import kotlinx.coroutines.*
import java.lang.Exception

object ConnectTimeManager {
    private var connectTime=0L
    private var job:Job?=null
    private var callbackList= arrayListOf<IConnectTimeCallback>()

    fun resetTime(){
        connectTime=0L
    }

    fun stopCountTime(){
        job?.cancel()
        job=null
    }

    fun startCountTime(){
        job=GlobalScope.launch(Dispatchers.Main) {
            while (true){
                callbackList.forEach { it.connectTimeCallback(transTime()) }
                connectTime++
                delay(1000L)
            }
        }
    }

    fun addCallback(iConnectTimeCallback:IConnectTimeCallback){
        callbackList.add(iConnectTimeCallback)
    }

    fun removeCallback(iConnectTimeCallback:IConnectTimeCallback){
        callbackList.remove(iConnectTimeCallback)
    }

    fun transTime():String{
        try {
            val shi=connectTime/3600
            val fen= (connectTime % 3600) / 60
            val miao= (connectTime % 3600) % 60
            val s=if (shi<10) "0${shi}" else shi
            val f=if (fen<10) "0${fen}" else fen
            val m=if (miao<10) "0${miao}" else miao
            return "${s}:${f}:${m}"
        }catch (e:Exception){}
        return "00:00:00"
    }


    interface IConnectTimeCallback{
        fun connectTimeCallback(time:String)
    }
}