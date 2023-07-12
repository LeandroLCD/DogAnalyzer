package com.leandrolcd.doganalyzer.core.camera

import android.content.Context
import androidx.annotation.Keep
import com.leandrolcd.doganalyzer.utility.LABEL_PATH
import com.leandrolcd.doganalyzer.utility.MODEL_PATH
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer

@Keep
@Module
@InstallIn(SingletonComponent::class)
object ClassifierSetup {
    @Provides
    fun providesClassifierModel(context: Context): MappedByteBuffer =
        FileUtil.loadMappedFile(context, MODEL_PATH)

    @Provides
    fun providesClassifierLabels(context: Context):List<String> =
        FileUtil.loadLabels(context, LABEL_PATH)

}