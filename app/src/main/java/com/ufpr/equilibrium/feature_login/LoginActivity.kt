package com.ufpr.equilibrium.feature_login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.OnBackPressedCallback
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.network.Login
import com.ufpr.equilibrium.network.LoginResult
import com.ufpr.equilibrium.network.RetrofitClient

class LoginActivity : AppCompatActivity() {
    private lateinit var cpf: EditText
    private lateinit var senha: EditText
    private lateinit var errorBar: LinearLayout
    private lateinit var eyeIcon: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        cpf = findViewById(R.id.cpf)
        senha = findViewById(R.id.password)

        eyeIcon = findViewById(R.id.eye)
        errorBar = findViewById(R.id.error_bar)

        val btnLogin = findViewById<Button>(R.id.login_button)
        val retryButton = findViewById<Button>(R.id.retry_button)

        btnLogin.setOnClickListener {
            val cpfStr = cpf.text.toString().trim()
            val senhaStr = senha.text.toString().trim()

            if (cpfStr.isNotEmpty() || senhaStr.isNotEmpty() || isValidCPF(cpf.text.toString())) {
                authentication()

            } else {
                println("Erro CPF");
                errorBar.visibility = View.VISIBLE
            }
        }

        retryButton.setOnClickListener {
            errorBar.visibility = View.GONE
            cpf.text.clear()
            senha.text.clear()

        }

        eyeIcon.setOnClickListener {
            togglePasswordVisibility()
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

    }


    private fun isValidCPF(cpf: String): Boolean {
        val cleanedCPF = cpf.replace("\\D".toRegex(), "") // Remove caracteres não numéricos

        if (cleanedCPF.length != 11 || cleanedCPF.all { it == cleanedCPF[0] }) return false

        fun calculateDigit(cpfSlice: String, weights: IntProgression): Int {
            val sum = cpfSlice.mapIndexed { index, c -> c.digitToInt() * weights.elementAt(index) }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }

        val digit1 = calculateDigit(cleanedCPF.substring(0, 9), 10 downTo 2)
        val digit2 = calculateDigit(cleanedCPF.substring(0, 10), 11 downTo 2)

        println(cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2)

        return cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2
    }

    private fun authentication() {
        val cpfText = cpf.text.toString().trim()
        val senhaText = senha.text.toString().trim()

        val api = RetrofitClient.instancePessoasAPI
        val login = Login(cpfText, senhaText)
        val call = api.authenticate(login)

        call.enqueue(object : Callback<LoginResult> {

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                if (response.isSuccessful && response.body()?.user?.profile == "healthProfessional") {
                    errorBar.visibility = View.GONE

                        SessionManager.token = response.body()?.token
                        SessionManager.user = response.body()?.user

                        Toast.makeText(applicationContext, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this@LoginActivity, HomeProfissional::class.java))

                } else {
                    println(response)
                    errorBar.visibility = View.VISIBLE

                }
            }

            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                Log.e("Erro", "Falha no login", t)
                Toast.makeText(applicationContext, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun togglePasswordVisibility() {

        if (isPasswordVisible) {
            senha.transformationMethod = PasswordTransformationMethod.getInstance()
            eyeIcon.setImageResource(R.drawable.eye)

        } else {
            senha.transformationMethod = HideReturnsTransformationMethod.getInstance()
            eyeIcon.setImageResource(R.drawable.openeye)
        }

        isPasswordVisible = !isPasswordVisible
        senha.setSelection(senha.text.length)
    }


}
