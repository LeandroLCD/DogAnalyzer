package com.leandrolcd.doganalyzer.ui.admob

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.leandrolcd.doganalyzer.R
import com.leandrolcd.doganalyzer.ui.utilits.IS_DEBUG
import javax.inject.Inject

class RewardAdView @Inject constructor() {
    companion object{
        var rewardedAd: RewardedAd? = null
    }

    fun load(context: Activity){
        val unitId = context.getString(if (IS_DEBUG) R.string.reward_id else R.string.reward_id_release)

        RewardedAd.load(
            context,
            unitId,
            AdManagerAdRequest.Builder().build(),
            object: RewardedAdLoadCallback() {

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.e("Error Admob", p0.message)
                    rewardedAd = null
                }

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    p0.setImmersiveMode(true)

                    p0.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            load(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            load(context)
                        }
                    }
                    rewardedAd = p0

                }
            }
        )
    }

    fun show(context: Activity, onReward:(Int)->Unit){
        rewardedAd?.show(context){ rewardItem ->
            rewardedAd = null
            load(context)
            if (rewardItem.amount > 0){
                    onReward(rewardItem.amount)

            }
        }
    }
}