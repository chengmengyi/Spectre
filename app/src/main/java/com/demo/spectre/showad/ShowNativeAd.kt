package com.demo.spectre.showad

import android.content.Context
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.spectre.R
import com.demo.spectre.loadad.PrepareLoadAd
import com.demo.spectre.ui.AbsBaseActivity
import com.demo.spectre.util.Acc0810
import com.demo.spectre.util.showView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

abstract class ShowNativeAd {

    protected fun showNativeAd(context:AbsBaseActivity,type:String,ad:NativeAd){
        val nativeAdView=context.findViewById<NativeAdView>(R.id.native_ad_view)
        nativeAdView.mediaView=context.findViewById(R.id.iv_native_cover)
        if (null!=ad.mediaContent){
            nativeAdView.mediaView?.apply {
                setMediaContent(ad.mediaContent)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(10F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }
        nativeAdView.headlineView=context.findViewById(R.id.tv_native_title)
        (nativeAdView.headlineView as AppCompatTextView).text=ad.headline

        nativeAdView.bodyView=context.findViewById(R.id.tv_native_desc)
        (nativeAdView.bodyView as AppCompatTextView).text=ad.body

        nativeAdView.iconView=context.findViewById(R.id.iv_native_logo)
        (nativeAdView.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        nativeAdView.callToActionView=context.findViewById(R.id.tv_native_btn)
        (nativeAdView.callToActionView as AppCompatTextView).text=ad.callToAction

        nativeAdView.setNativeAd(ad)
        showHideView(context,nativeAdView)
        preLoadAd(type)
    }

    private fun showHideView(context: AbsBaseActivity,nativeAdView: NativeAdView){
        nativeAdView.showView(true)
        context.findViewById<AppCompatImageView>(R.id.iv_native_default).showView(false)
    }

    private fun preLoadAd(type: String){
        if (type=="sp_home"){
            Acc0810.refreshHomeNativeAd=false
        }
        PrepareLoadAd.clearAdCache(type)
        PrepareLoadAd.preLoadAd(type)
    }
}