package com.ufpr.equilibrium.feature_teste

import android.content.Context
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class HARClassifier(context: Context) {

    companion object {
        init {
            System.loadLibrary("tensorflow_inference")
        }

        private const val MODEL_FILE = "file:///android_asset/frozen_HAR.pb";
        private const val INPUT_NODE = "LSTM_1_input"
        private val OUTPUT_NODES = arrayOf("Dense_2/Softmax")
        private const val OUTPUT_NODE = "Dense_2/Softmax"
        private val INPUT_SIZE = longArrayOf(1, 100, 12)
        private const val OUTPUT_SIZE = 7
    }

    private val inferenceInterface = TensorFlowInferenceInterface(context.assets, MODEL_FILE)

    fun predictProbabilities(data: FloatArray): FloatArray {
        val result = FloatArray(OUTPUT_SIZE)
        inferenceInterface.feed(INPUT_NODE, data, *INPUT_SIZE)
        inferenceInterface.run(OUTPUT_NODES)
        inferenceInterface.fetch(OUTPUT_NODE, result)

        return result
    }
}
