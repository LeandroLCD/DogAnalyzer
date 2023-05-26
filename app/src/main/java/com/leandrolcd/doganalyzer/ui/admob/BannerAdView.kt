package com.leandrolcd.doganalyzer.ui.admob

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.leandrolcd.doganalyzer.utility.BANNER_ID_DEBUG
import com.leandrolcd.doganalyzer.utility.BANNER_ID_RELEASE
import com.leandrolcd.doganalyzer.utility.IS_DEBUG

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView(onVisibleBanner:()->Unit) {
    val unitId = if (IS_DEBUG) BANNER_ID_DEBUG else BANNER_ID_RELEASE

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = unitId
                loadAd(AdRequest.Builder().build())
                onVisibleBanner()
            }
        }
    )
}