package com.ufpr.equilibrium.feature_teste

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ufpr.equilibrium.R
import java.util.Locale

class Contagem : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    // Contagem de 10 até 1 e depois "JÁ!"
    private val countdownList = listOf("5","4","3","2","1","JÁ!")
    private var countdownRunning = false

    companion object {
        private const val INTRO_UTTERANCE_ID = "INTRO_TTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contagem)

        textToSpeech = TextToSpeech(this, this)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ContagemAdapter(countdownList)

        // Não inicia a contagem aqui; esperamos o TTS inicial terminar.
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale("pt", "BR")

            // Ouvir quando a fala introdutória terminar para então iniciar a contagem
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (utteranceId == INTRO_UTTERANCE_ID) {
                        runOnUiThread { startCountdown() }
                    }
                }
                override fun onError(utteranceId: String?) {
                    // Em caso de erro no TTS, ainda assim começamos a contagem
                    if (utteranceId == INTRO_UTTERANCE_ID) {
                        runOnUiThread { startCountdown() }
                    }
                }
            })

            // Fala introdutória antes da contagem de 10 s
            speakText(
                "Cinco segundos para começar o teste! Faça os ajustes necessários",
                INTRO_UTTERANCE_ID
            )
        }
    }

    private fun startCountdown() {
        if (countdownRunning) return
        countdownRunning = true
        currentPage = 0

        // Exibe e fala a cada 1 segundo
        handler.post(object : Runnable {
            override fun run() {
                if (currentPage < countdownList.size) {
                    viewPager.setCurrentItem(currentPage, true)

                    val text = countdownList[currentPage]
                    speakText(text)

                    currentPage++

                    // “JÁ!” fica menos tempo (300 ms) e já troca de tela
                    val delay = if (currentPage == countdownList.size) 300L else 1000L
                    handler.postDelayed(this, delay)
                } else {
                    startTimerActivity()
                }
            }
        })
    }

    private fun speakText(text: String, utteranceId: String? = null) {
        // Usamos QUEUE_FLUSH para garantir que cada fala substitua a anterior
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId ?: System.nanoTime().toString())
    }

    private fun startTimerActivity() {
        val teste = intent.getStringExtra("teste")
        val selectedUnitId = intent.getStringExtra("id_unidade")

        val newIntent = Intent(this, Timer::class.java).apply {
            putExtra("teste", teste)
            putExtra("id_unidade", selectedUnitId)
        }

        startActivity(newIntent)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}



