package dev.lcdii.kmp_vk_music_api

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import dev.lcdii.kmp_vk_music_api.di.vkApiModule
import dev.lcdii.kmp_vk_music_api.vk_api.AndroidAuth
import dev.lcdii.kmp_vk_music_api.vk_api.ApiSource
import dev.lcdii.kmp_vk_music_api.vk_api.VkApiResponse
import dev.lcdii.kmp_vk_music_api.vk_api.core.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidAuthTestActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val koin = startKoin {
                androidContext(applicationContext)
                modules(vkApiModule)
            }.koin


        val density = resources.displayMetrics.density
        fun dp(value: Int): Int = (value * density).toInt()

        val statusBarHeight = run {
            val id = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (id > 0) resources.getDimensionPixelSize(id) else dp(24)
        }
        val topPadding = statusBarHeight + dp(48)

        val loginLabel = TextView(this).apply {
            text = "VK login"
            setTextColor(Color.BLACK)
            textSize = 16f
            setPadding(0, 0, 0, dp(4))
        }
        val loginInput = EditText(this).apply {
            hint = "Введите логин"
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)
            setBackgroundColor(Color.WHITE)
            minHeight = dp(56)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            textSize = 16f
        }

        val passwordLabel = TextView(this).apply {
            text = "VK password"
            setTextColor(Color.BLACK)
            textSize = 16f
            setPadding(0, dp(16), 0, dp(4))
        }
        val passwordInput = EditText(this).apply {
            hint = "Введите пароль"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setTextColor(Color.BLACK)
            setHintTextColor(Color.GRAY)
            setBackgroundColor(Color.WHITE)
            minHeight = dp(56)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            textSize = 16f
        }

        val authenticateButton = Button(this).apply {
            text = "Authenticate"
            isAllCaps = false
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        val resultText = TextView(this).apply {
            text = "Enter credentials"
            setTextColor(Color.BLACK)
            textSize = 14f
            setPadding(0, dp(24), 0, 0)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setBackgroundColor(Color.parseColor("#EEEEEE"))
            // ВАЖНО: верхний отступ = статус-бар + запас, чтобы первое поле было видно
            setPadding(dp(24), topPadding, dp(24), dp(24))
            addView(loginLabel, inputLayoutParams())
            addView(loginInput, inputLayoutParams())
            addView(passwordLabel, inputLayoutParams())
            addView(passwordInput, inputLayoutParams())
            addView(authenticateButton, inputLayoutParams())
            addView(resultText, inputLayoutParams())
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#EEEEEE"))
            fitsSystemWindows = true
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
        setContentView(scroll)

        scope.launch {
            try {
                when (val userIdResult = koin.get<ApiSource>().getUserId()) {
                    is Result.Success -> {
                        loginInput.isEnabled = false
                        passwordInput.isEnabled = false
                        authenticateButton.isEnabled = false
                        resultText.text = "Already authorized\nuser_id: ${userIdResult.data}"
                    }

                    is Result.Error -> Unit
                }
            } catch (_: Throwable) {
                resultText.text = "Not authorized"
            }
        }

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
                    when (val result = koin.get<AndroidAuth>().auth(this@AndroidAuthTestActivity, login, password)) {
                        is Result.Success -> {
                            val authResponse = result.data as? VkApiResponse.AuthResponse
                            resultText.text = "Success\nuser_id: ${authResponse?.userId ?: "not returned"}"
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
    ).apply {
        bottomMargin = (8 * resources.displayMetrics.density).toInt()
    }
}
