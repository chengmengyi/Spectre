package com.spectre.unlimited.connection.wifisecure.manager

import com.spectre.unlimited.connection.wifisecure.BuildConfig
import com.spectre.unlimited.connection.wifisecure.bean0810.Server0810Bean
import com.spectre.unlimited.connection.wifisecure.util.InfoConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject

object ReadFirebaseConfigManager {
    val configCityList= arrayListOf<String>()
    val configServerList= arrayListOf<Server0810Bean>()

    fun read(){
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful){
                parseCityJson(remoteConfig.getString("sp_city_0810"))
                parseServerJson(remoteConfig.getString("sp_server"))
                saveAdJson(remoteConfig.getString("sp_ad"))
            }
        }
    }

    private fun parseCityJson(string: String){
        try {
            configCityList.clear()
            val jsonArray = JSONObject(string).getJSONArray("sp_city_0810")
            for (index in 0 until jsonArray.length()){
                configCityList.add(jsonArray.optString(index))
            }
        }catch (e:Exception){}
    }

    private fun parseServerJson(string: String){
        if (configServerList.isEmpty()){
            configServerList.addAll(ServerManager.parseServerJson(string))
        }
    }

    private fun saveAdJson(string: String){
        MMKV.defaultMMKV().encode("ad",string)
    }
}