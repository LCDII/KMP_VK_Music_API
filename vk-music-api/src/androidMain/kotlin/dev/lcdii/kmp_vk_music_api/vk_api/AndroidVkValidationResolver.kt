package dev.lcdii.kmp_vk_music_api.vk_api

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import dev.lcdii.kmp_vk_music_api.core.DataError
import dev.lcdii.kmp_vk_music_api.core.Result
import dev.lcdii.kmp_vk_music_api.core.VkApiError
import dev.lcdii.kmp_vk_music_api.vk_api.domain.VkValidationResolver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class AndroidVkValidationResolver(
    private val activity: Activity
) : VkValidationResolver {
    override suspend fun resolve(
        error: VkApiError.Auth.Validation
    ): Result<String, DataError> =
        suspendCancellableCoroutine { continuation ->
            activity.runOnUiThread {
                val input = EditText(activity).apply {
                    hint = "VK code"
                    inputType = InputType.TYPE_CLASS_NUMBER
                }

                val container = LinearLayout(activity).apply {
                    setPadding(48, 0, 48, 0)
                    addView(input)
                }

                var completed = false
                val dialog = AlertDialog.Builder(activity)
                    .setTitle("VK confirmation code")
                    .setMessage(error.description ?: "Enter the code sent by VK")
                    .setView(container)
                    .setNegativeButton("Cancel") { _, _ ->
                        if (!completed) {
                            completed = true
                            continuation.resume(Result.Error(DataError.Remote.Unknown))
                        }
                    }
                    .setPositiveButton("Continue", null)
                    .create()

                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        val code = input.text.toString().trim()
                        if (code.isEmpty()) {
                            input.error = "Enter the VK code"
                            return@setOnClickListener
                        }

                        if (!completed) {
                            completed = true
                            dialog.dismiss()
                            continuation.resume(Result.Success(code))
                        }
                    }
                }

                dialog.setOnCancelListener {
                    if (!completed) {
                        completed = true
                        continuation.resume(Result.Error(DataError.Remote.Unknown))
                    }
                }

                dialog.show()
                input.requestFocus()
                dialog.window?.setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                )

                continuation.invokeOnCancellation {
                    activity.runOnUiThread {
                        if (dialog.isShowing) dialog.dismiss()
                    }
                }
            }
        }
}
