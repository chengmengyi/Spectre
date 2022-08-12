package com.spectre.unlimited.connection.wifisecure

import android.app.Application
import com.spectre.unlimited.connection.wifisecure.manager.ServerManager
import com.spectre.unlimited.connection.wifisecure.ui.Home0810Activity
import com.spectre.unlimited.connection.wifisecure.util.Acc0810
import com.spectre.unlimited.connection.wifisecure.util.getCurrentProcessName
import com.github.shadowsocks.Core
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

class Spectre:Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this,Home0810Activity::class)
        if (!packageName.equals(getCurrentProcessName(this))){
            return
        }
        Firebase.initialize(this)
        MMKV.initialize(this)
        Acc0810.register(this)
        ServerManager.initLocalServerList()
    }

}