package com.ufpr.equilibrium.feature_professional

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.widget.ViewPager2
import com.ufpr.equilibrium.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CadastroPacienteActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = CadastroPagerAdapter(this)
        val formViewModel = ViewModelProvider(this)[FormViewModel::class.java]

        viewPager.adapter = adapter
    }


}