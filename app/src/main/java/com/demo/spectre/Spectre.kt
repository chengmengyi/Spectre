package com.demo.spectre

import android.app.Application
import com.demo.spectre.manager.ReadFirebaseConfigManager
import com.demo.spectre.manager.ServerManager
import com.demo.spectre.ui.Home0810Activity
import com.demo.spectre.util.Acc0810
import com.demo.spectre.util.getCurrentProcessName
import com.github.shadowsocks.Core
import com.tencent.mmkv.MMKV

lateinit var mSpectre:Spectre
class Spectre:Application() {
    override fun onCreate() {
        super.onCreate()
        mSpectre=this
        Core.init(this,Home0810Activity::class)
        if (!packageName.equals(getCurrentProcessName(this))){
            return
        }
        MMKV.initialize(this)
        Acc0810.register(this)
        ServerManager.initLocalServerList()
        ReadFirebaseConfigManager.read()
    }

}