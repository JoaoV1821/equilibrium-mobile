package com.ufpr.equilibrium.feature_professional

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class CadastroPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DadosPessoaisFragment()
            1 -> InfoAddFragment()
            else -> EnderecoFragment()
        }
    }
}
