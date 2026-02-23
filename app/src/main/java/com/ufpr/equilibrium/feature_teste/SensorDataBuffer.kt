package com.ufpr.equilibrium.feature_teste

import org.json.JSONObject
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Singleton que armazena dados de sensores coletados durante a contagem regressiva
 * para serem consumidos pelo Timer.kt quando ele iniciar.
 */
object SensorDataBuffer {

    val accelQueue = ConcurrentLinkedQueue<JSONObject>()
    val gyroQueue = ConcurrentLinkedQueue<JSONObject>()
    val linearQueue = ConcurrentLinkedQueue<JSONObject>()

    /** Timestamp (millis) de quando a coleta real de sensores começou na contagem */
    var collectionStartTime: Long = 0L

    /** Drena todos os dados do buffer para as filas de destino e limpa o buffer */
    fun drainTo(
        targetAccel: ConcurrentLinkedQueue<JSONObject>,
        targetGyro: ConcurrentLinkedQueue<JSONObject>,
        targetLinear: ConcurrentLinkedQueue<JSONObject>
    ) {
        var item: JSONObject?

        item = accelQueue.poll()
        while (item != null) {
            targetAccel.add(item)
            item = accelQueue.poll()
        }

        item = gyroQueue.poll()
        while (item != null) {
            targetGyro.add(item)
            item = gyroQueue.poll()
        }

        item = linearQueue.poll()
        while (item != null) {
            targetLinear.add(item)
            item = linearQueue.poll()
        }
    }

    /** Limpa todo o buffer */
    fun clear() {
        accelQueue.clear()
        gyroQueue.clear()
        linearQueue.clear()
        collectionStartTime = 0L
    }
}
