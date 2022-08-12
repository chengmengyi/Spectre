package com.demo.spectre.loadad

class AdData0810Bean(
    val time:Long=0,
    val adData:Any?=null
) {
    fun getIsExpired()=(System.currentTimeMillis() - time) >= 60L * 60L * 1000L
}