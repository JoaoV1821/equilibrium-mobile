package com.ufpr.equilibrium.feature_login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.HomePaciente
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var cpf: EditText
    private lateinit var senha: EditText
    private lateinit var errorBar: LinearLayout
    private lateinit var loadingOverlay: View
    private lateinit var eyeIcon: ImageView
    private var isPasswordVisible = false

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        cpf = findViewById(R.id.cpf)
        senha = findViewById(R.id.password)

        cpf.addMask("###.###.###-##")

        eyeIcon = findViewById(R.id.eye)
        errorBar = findViewById(R.id.error_bar)
        loadingOverlay = findViewById(R.id.loading_overlay)

        val btnLogin = findViewById<Button>(R.id.login_button)
        val retryButton = findViewById<Button>(R.id.retry_button)

        btnLogin.setOnClickListener {
            val cpfStr = cpf.text.toString().trim().replace(Regex("[^\\d]"), "")
            val senhaStr = senha.text.toString().trim()
            if (cpfStr.isNotEmpty() && senhaStr.isNotEmpty()) {
                viewModel.login(cpfStr, senhaStr)
            } else {
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
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is LoginUiState.Idle -> Unit
                    is LoginUiState.Loading -> {
                        loadingOverlay.visibility = View.VISIBLE
                        errorBar.visibility = View.GONE
                    }
                    is LoginUiState.Error -> {
                        loadingOverlay.visibility = View.GONE
                        errorBar.visibility = View.VISIBLE
                        val errorMessage = findViewById<TextView>(R.id.error_message)

                        if (state.message.contains("401") || state.message.contains("Unauthorized")) {
                            errorMessage.text = getString(R.string.error_invalid_credentials)
                        } else {
                            errorMessage.text = getString(R.string.login_error_generic)
                        }

                    }

                    is LoginUiState.Success -> {
                        loadingOverlay.visibility = View.GONE
                        
                        // Verifica se o usuário é um HEALTH_PROFESSIONAL
                        if (state.session.role != "HEALTH_PROFESSIONAL") {
                            // Se não for, mostra o errorBar com a mensagem apropriada
                            errorBar.visibility = View.VISIBLE
                            val errorMessage = findViewById<TextView>(R.id.error_message)
                            errorMessage.text = getString(R.string.error_participant_access)
                            return@collectLatest
                        }
                        
                        errorBar.visibility = View.GONE


                        SessionManager.token = state.session.token
                        SessionManager.user = com.ufpr.equilibrium.network.Usuario(
                            id = state.session.id ?: "",
                            cpf = state.session.cpf ?: "",
                            fullName = state.session.fullName ?: "",
                            password = "",
                            phone = state.session.phone ?: "",
                            gender = state.session.gender ?: "",
                            role = state.session.role ?: ""
                        )

                        if (state.session.role == "HEALTH_PROFESSIONAL") {
                            startActivity(Intent(this@LoginActivity, HomeProfissional::class.java))

                        } else {

                        }
                    }
                }
            }
        }
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


}
