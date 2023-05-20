package com.leandrolcd.doganalyzer.ui.utilits

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.datastore.preferences.preferencesDataStore

const val MAX_RECOGNITION_DOG_RESULTS = 5

const val MODEL_PATH = "model.tflite"
const val LABEL_PATH = "labels.txt"
const val COUNTER_DETAIL_PREFS = "detail_click"
const val COUNTER_RECOGNITION_REFS = "recognition_click"
const val COUNTER_AD_REWARD = "adreward"
const val DATE_AD_REWARD = "dateadreward"
const val IS_DEBUG = false

const val SERVER_CLIENT_ID = "920293101468-534pgc84fvcqh30pude095p1eej9fs0d.apps.googleusercontent.com"
const val BANNER_ID_RELEASE ="ca-app-pub-2089536578261112/6373721447"
const val BANNER_ID_DEBUG= "ca-app-pub-3940256099942544/6300978111"
const val INTERSTICIAL_ID_DEBUG = "ca-app-pub-3940256099942544/1033173712"
const val INTERSTICIAL_ID_RELEASE = "ca-app-pub-2089536578261112/3034114376"



val Context.preferencesDataStore by preferencesDataStore(name = "DOG_ANALYZER")

val LANGUAGE: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    LocaleList.getDefault().get(0).language
} else {
        "en"
}
