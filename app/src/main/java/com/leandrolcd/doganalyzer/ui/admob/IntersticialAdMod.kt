package com.leandrolcd.doganalyzer.ui.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.leandrolcd.doganalyzer.ui.utilits.INTERSTICIAL_ID_DEBUG
import com.leandrolcd.doganalyzer.ui.utilits.INTERSTICIAL_ID_RELEASE
import com.leandrolcd.doganalyzer.ui.utilits.IS_DEBUG
import javax.inject.Inject


class InterstitialAdMod @Inject constructor(){
    companion object{
        var mInterstitialAd: InterstitialAd? = null

    }


    fun load(context: Context) {
    InterstitialAd.load(
        context,
        if(IS_DEBUG) INTERSTICIAL_ID_DEBUG else INTERSTICIAL_ID_RELEASE, //Change this with your own AdUnitID!
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("TAG", "onAdFailedToLoad: $adError")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        }
    )
}

    fun show(activity: Activity, onAdDismissed: ()->Unit) {
    if (mInterstitialAd != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null
            }
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                load(activity)
                onAdDismissed()
            }
        }
        mInterstitialAd?.show(activity)
    }
}

}
fun removeInterstitial() {
    InterstitialAdMod.mInterstitialAd?.fullScreenContentCallback = null
    InterstitialAdMod.mInterstitialAd = null
}


