package com.demo.spectre.manager

import com.demo.spectre.bean0810.Server0810Bean
import com.demo.spectre.util.InfoConfig
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object ServerManager {
    private val localServerList= arrayListOf<Server0810Bean>()

    fun initLocalServerList(){
        if (localServerList.isEmpty()){
            localServerList.addAll(parseServerJson(InfoConfig.SERVER_0810))
        }
    }

    fun getServerList():ArrayList<Server0810Bean>{
        if (ReadFirebaseConfigManager.configServerList.isEmpty()){
            return localServerList
        }
        return ReadFirebaseConfigManager.configServerList
    }

    fun createFastServer()= Server0810Bean(country_0810_bean = "Faster server")

    fun isFast(server0810Bean: Server0810Bean)=server0810Bean.country_0810_bean=="Faster server"

    fun getServerId(server0810Bean: Server0810Bean):Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==server0810Bean.host_0810_bean&&it.remotePort==server0810Bean.port_0810_bean){
                return it.id
            }
        }
        return 0L
    }

    fun getFastServer():Server0810Bean?{
        val serverList = getServerList()
        if (!ReadFirebaseConfigManager.configCityList.isNullOrEmpty()){
            val filter = serverList.filter { ReadFirebaseConfigManager.configCityList.contains(it.city_0810_bean) }
            if (!filter.isNullOrEmpty()){
                return filter.randomOrNull()
            }
        }
        return serverList.randomOrNull()
    }

    fun write(server0810Bean: Server0810Bean){
        val profile = Profile(
            id = 0L,
            name = "${server0810Bean.country_0810_bean} - ${server0810Bean.city_0810_bean}",
            host = server0810Bean.host_0810_bean,
            remotePort = server0810Bean.port_0810_bean,
            password = server0810Bean.pwd_0810_bean,
            method = server0810Bean.method_0810_bean
        )

        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }

    fun parseServerJson(json:String):ArrayList<Server0810Bean>{
        val list= arrayListOf<Server0810Bean>()
        try {
            val jsonArray = JSONObject(json).getJSONArray("sp_server")
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    Server0810Bean(
                        jsonObject.optString("pwd_0810_bean"),
                        jsonObject.optString("method_0810_bean"),
                        jsonObject.optInt("port_0810_bean"),
                        jsonObject.optString("country_0810_bean"),
                        jsonObject.optString("city_0810_bean"),
                        jsonObject.optString("host_0810_bean"),
                    )
                )
            }
            GlobalScope.launch {
                list.forEach {
                    write(it)
                }
            }
        }catch (e:Exception){ }
        return list
    }
}