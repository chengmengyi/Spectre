package com.spectre.unlimited.connection.wifisecure.util

import android.app.ActivityManager
import android.app.Application
import android.util.Log
import android.view.View
import com.spectre.unlimited.connection.wifisecure.R

fun getCurrentProcessName(applicationContext: Application): String {
    val pid = android.os.Process.myPid()
    var processName = ""
    val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
    for (process in manager.runningAppProcesses) {
        if (process.pid === pid) {
            processName = process.processName
        }
    }
    return processName
}

fun getFlagResId(country:String) = when(country){
    "United Kingdom"->R.drawable.flag_uk
    "United States"->R.drawable.flag_usa
    "Japan"->R.drawable.flag_japan
    "Canada"->R.drawable.flag_canada
    else-> R.drawable.flag_default
}

fun View.showView(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}



fun printLog(string: String){

}

