package com.demo.spectre.loadad

import com.demo.spectre.util.InfoConfig
import com.demo.spectre.util.printLog
import com.tencent.mmkv.MMKV
import org.json.JSONObject

object PrepareLoadAd:AbsLoadAd() {
    var isShowingFullScreenAd=false

    fun preLoadAd(type:String,loadOpenAgain:Boolean=true){
        if (isLoadingAd(type)||hasCache(type)){
            return
        }
        val adList = getAdList(type)
        if (adList.isEmpty()){
            return
        }
        loadingList.add(type)
        loopLoadAd(type,adList.iterator(),loadOpenAgain)
    }

    private fun loopLoadAd(type: String, iterator: Iterator<ConfigAd0810Bean>,loadOpenAgain:Boolean){
        val configAd0810Bean = iterator.next()
        printLog("start load $type,${configAd0810Bean.toString()}")
        startLoadAd(
            configAd0810Bean,
            success = {
                printLog("load $type success")
                loadingList.remove(type)
                if (null!=it.adData){
                    adDataMap[type]=it
                }
            },
            fail = {
                printLog("load $type fail,$it")
                if (iterator.hasNext()){
                    loopLoadAd(type,iterator,loadOpenAgain)
                }else{
                    loadingList.remove(type)
                    if (type=="sp_open"&&loadOpenAgain){
                        preLoadAd(type,loadOpenAgain = false)
                    }
                }
            }
        )
    }

    private fun getAdList(type: String):List<ConfigAd0810Bean>{
        val list= arrayListOf<ConfigAd0810Bean>()
        try {
            val jsonArray = JSONObject(getAdJson()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    ConfigAd0810Bean(
                        jsonObject.optString("sp_source"),
                        jsonObject.optString("sp_id"),
                        jsonObject.optString("sp_type"),
                        jsonObject.optInt("sp_sort"),
                    )
                )
            }
        }catch (e:Exception){}
        return list.filter { it.source_0810 == "admob" }.sortedByDescending { it.sort_0810 }
    }

    private fun getAdJson():String{
        val ad = MMKV.defaultMMKV().decodeString("ad")
        return if (ad.isNullOrEmpty()) InfoConfig.AD_0810 else ad
    }

    fun getAdDataByType(type: String) = adDataMap[type]?.adData
}