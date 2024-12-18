package com.techteam.aqproad.AudioService

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(onInit: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("es", "ES"))

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "El idioma español no es compatible o falta datos.")
                    onInit(false)
                } else {
                    isInitialized = true
                    onInit(true)
                }
            } else {
                Log.e("TTS", "Inicialización del TTS fallida.")
                onInit(false)
            }
        }
    }

    fun speak(text: String) {
        if (isInitialized && textToSpeech != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            Log.e("TTS", "TTS no inicializado o texto no valido.")
        }
    }

    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}