package com.ufpr.equilibrium

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class Contagem : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contagem)

        viewPager = findViewById(R.id.viewPager)

        val countdownList = listOf("1", "2", "3", "JÁ!")
        viewPager.adapter = ContagemAdapter(countdownList)

        startCountdown()
    }

    private fun startCountdown() {

        handler.postDelayed(object : Runnable {

            override fun run() {
                if (currentPage < 3) {
                    currentPage++
                    viewPager.setCurrentItem(currentPage, false)
                    handler.postDelayed(this, 1000)

                } else {
                    playSound()
                    startTimerActivity()
                }
            }
        }, 1000)
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.go)
        mediaPlayer.start()
    }

    private fun startTimerActivity() {
        val intent = Intent(this, Timer::class.java)
        startActivity(intent)
    }
}
