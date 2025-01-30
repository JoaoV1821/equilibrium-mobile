package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.login_button);

        btnLogin.setOnClickListener {
            val intent = Intent(this, Testes::class.java)

            startActivity(intent);
        }
    }

}