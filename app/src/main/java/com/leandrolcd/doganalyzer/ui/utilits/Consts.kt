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
const val IS_DEBUG = false


val Context.preferencesDataStore by preferencesDataStore(name = "DOG_ANALYZER")

val LANGUAGE: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    LocaleList.getDefault().get(0).language
} else {
        "en"
}
