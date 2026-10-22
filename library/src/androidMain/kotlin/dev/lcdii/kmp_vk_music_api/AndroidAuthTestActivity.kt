package dev.lcdii.kmp_vk_music_api

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import dev.lcdii.kmp_vk_music_api.di.vkApiModule
import dev.lcdii.kmp_vk_music_api.vk_api.AndroidAuth
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import dev.lcdii.kmp_vk_music_api.vk_api.core.TokenKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class AndroidAuthTestActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                modules(vkApiModule)
            }
        }

        val loginInput = EditText(this).apply {
            hint = "VK login"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        val passwordInput = EditText(this).apply {
            hint = "VK password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val authenticateButton = Button(this).apply {
            text = "Authenticate"
        }

        val resultText = TextView(this).apply {
            text = "Enter credentials"
            setPadding(0, 24, 0, 0)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            addView(loginInput, inputLayoutParams())
            addView(passwordInput, inputLayoutParams())
            addView(authenticateButton, inputLayoutParams())
            addView(resultText, inputLayoutParams())
        }

        setContentView(content)

        authenticateButton.setOnClickListener {
            val login = loginInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                resultText.text = "Login and password are required"
                return@setOnClickListener
            }

            authenticateButton.isEnabled = false
            resultText.text = "Authenticating..."

            scope.launch {
                try {
                    when (val result = AndroidAuth.authenticate(this@AndroidAuthTestActivity, login, password)) {
                        is Result.Success -> {
                            resultText.text = "Success\nToken: ${TokenKeeper.token ?: "not saved"}"
                        }

                        is Result.Error -> {
                            resultText.text = "Authorization error:\n${result.error}"
                        }
                    }
                } catch (error: Throwable) {
                    resultText.text = "Authorization exception:\n${error.message ?: error::class.simpleName}"
                } finally {
                    authenticateButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun inputLayoutParams() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}
