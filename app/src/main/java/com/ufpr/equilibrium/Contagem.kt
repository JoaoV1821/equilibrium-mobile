package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.util.Locale

class Contagem : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    private val countdownList = listOf("1", "2", "3", "JÁ!")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contagem)

        textToSpeech = TextToSpeech(this, this)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ContagemAdapter(countdownList)

        startCountdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale("pt", "BR") // Configura idioma para português do Brasil
        }
    }

    private fun startCountdown() {

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentPage < countdownList.size) {
                    viewPager.setCurrentItem(currentPage, true)

                    speakText(countdownList[currentPage])

                    currentPage++
                    handler.postDelayed(this, 1000)

                } else {
                    startTimerActivity()
                }
            }
        }, 1000)
    }

    private fun speakText(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

    }

    private fun startTimerActivity() {
        val intent = Intent(this, Timer::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}


