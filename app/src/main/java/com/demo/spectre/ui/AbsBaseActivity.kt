package com.demo.spectre.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class AbsBaseActivity :AppCompatActivity(){
    var resume0810=false
    protected var immersionBar:ImmersionBar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        density()
        setContentView(layoutId())
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        initView()
    }

    abstract fun layoutId():Int

    abstract fun initView()

    protected fun toast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    fun showDialog(sure:()->Unit){
        AlertDialog.Builder(this).apply {
            setMessage("You are currently connected and need to disconnect before manually connecting to the server.")
            setPositiveButton("sure", { dialog, which ->
                sure.invoke()
            })
            setNegativeButton("cancel",null)
            show()
        }
    }

    fun shareAppDownPath() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=${getPackInfo().packageName}"
        )
        startActivity(Intent.createChooser(intent, "share"))
    }

    fun jumpGooglePlay() {
        val packName = getPackInfo().packageName
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=$packName"
            )
        }
        startActivity(intent)
    }

    private fun getPackInfo(): PackageInfo {
        val pm = packageManager
        return pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    }

    fun checkNetConnect():Boolean{
        if (getNetWorkStatus(this) ==1){
            AlertDialog.Builder(this).apply {
                setMessage("You are not currently connected to the network")
                setPositiveButton("sure", null)
                show()
            }
            return false
        }
        return true
    }

    private fun density(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    private fun getNetWorkStatus(context: Context): Int {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return 2
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return 0
            }
        } else {
            return 1
        }
        return 1
    }

    override fun onResume() {
        super.onResume()
        resume0810=true
    }

    override fun onPause() {
        super.onPause()
        resume0810=false
    }

    override fun onStop() {
        super.onStop()
        resume0810=false
    }
}