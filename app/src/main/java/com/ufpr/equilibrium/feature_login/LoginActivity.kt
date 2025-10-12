package com.ufpr.equilibrium.feature_login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.ufpr.equilibrium.feature_paciente.HomePaciente
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.network.RetrofitClient
import android.util.Base64
import com.ufpr.equilibrium.network.Usuario
import org.json.JSONObject

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

        cpf.addMask("###.###.###-##")

        eyeIcon = findViewById(R.id.eye)
        errorBar = findViewById(R.id.error_bar)

        val btnLogin = findViewById<Button>(R.id.login_button)
        val retryButton = findViewById<Button>(R.id.retry_button)

        btnLogin.setOnClickListener {
            val cpfStr = cpf.text.toString().trim().replace(Regex("[^\\d]"), "")
            val senhaStr = senha.text.toString().trim()

            if (cpfStr.isNotEmpty() || senhaStr.isNotEmpty() || cpfStr.length != 14) {
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

    private fun authentication() {

        val cpfText = cpf.text.toString().trim().replace(Regex("[^\\d]"), "")
        val senhaText = senha.text.toString().trim()

        val api = RetrofitClient.instancePessoasAPI
        val login = Login(cpfText, senhaText)
        val call = api.authenticate(login)

        call.enqueue(object : Callback<LoginResult> {

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                println(response.body())

                if (response.isSuccessful) {
                    errorBar.visibility = View.GONE

                        SessionManager.token = response.body()?.access_token

                        val payloadJson = decodeJwtPayload(SessionManager.token.toString())

                        println(payloadJson)

                        if (payloadJson != null) {

                            val id = payloadJson.optString("sub")
                            val cpf = payloadJson.optString("cpf")
                            val fullName = payloadJson.optString("username")
                            val password = payloadJson.optString("password")
                            val phone = payloadJson.optString("phone")
                            val gender = payloadJson.optString("gender")
                            val role = payloadJson.optString("role")

                            val user = Usuario(id, cpf, fullName, password, phone, gender, role)

                            SessionManager.user = user

                            Toast.makeText(applicationContext, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                            if (SessionManager.user!!.role == "HEALTH_PROFESSIONAL" || SessionManager.user!!.role == "MANAGER") {
                                startActivity(Intent(this@LoginActivity, HomeProfissional::class.java))

                            } else if (SessionManager.user!!.role == "PATIENT") {
                                startActivity(Intent(this@LoginActivity, HomePaciente::class.java))
                            }
                        }

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

    fun EditText.addMask(mask: String) {
        this.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false
            var oldText: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                val unmasked = s.toString().replace(Regex("[^\\d]"), "")
                var maskedText = ""
                var i = 0

                for (m in mask.toCharArray()) {
                    if (m != '#' && unmasked.length > i) {
                        maskedText += m
                    } else {
                        try {
                            maskedText += unmasked[i]
                        } catch (_: Exception) { break }
                        i++
                    }
                }

                isUpdating = true
                this@addMask.setText(maskedText)
                this@addMask.setSelection(maskedText.length)
            }
        })
    }


    fun decodeJwtPayload(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            JSONObject(payload) // retorna como JSON
        } catch (e: Exception) {
            null
        }
    }
}
