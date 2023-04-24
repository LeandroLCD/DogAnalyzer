package com.leandrolcd.dogedexmvvm

import android.os.LocaleList

const val MAX_RECOGNITION_DOG_RESULTS = 5

const val MODEL_PATH = "model.tflite"
const val LABEL_PATH = "labels.txt"

val LANGUAGE: String = LocaleList.getDefault().get(0).language