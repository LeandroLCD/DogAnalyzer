package com.leandrolcd.doganalyzer.ui.admob

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.utilits.IS_DEBUG

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView(onVisibleBanner:()->Unit) {
    val unitId = if (IS_DEBUG) stringResource(id = R.string.banner_id_debug) else stringResource(
        id = R.string.banner_id_release
    )

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