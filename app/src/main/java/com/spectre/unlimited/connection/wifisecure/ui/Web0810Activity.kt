package com.spectre.unlimited.connection.wifisecure.ui

import com.spectre.unlimited.connection.wifisecure.R
import com.spectre.unlimited.connection.wifisecure.util.InfoConfig
import kotlinx.android.synthetic.main.layout_web0810.*

class Web0810Activity:AbsBaseActivity() {
    override fun layoutId(): Int = R.layout.layout_web0810

    override fun initView() {
        immersionBar?.statusBarView(status_view)?.init()

        web_view.apply {
            settings.javaScriptEnabled=true
            loadUrl(InfoConfig.PrivacyPolicy)
        }

        iv_back.setOnClickListener { finish() }
    }
}