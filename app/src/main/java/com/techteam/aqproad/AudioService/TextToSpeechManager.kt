package com.techteam.aqproad.AudioService

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log

class TextToSpeechManager(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(onInit: (Boolean) -> Unit) {
        /*textToSpeechManager.initialize { isInitialized ->
            if (isInitialized) {

                // TTS Listo, puedes comenzar a usar speak() aqui
                Log.d("TTS", "TTS Inicializado")
                val textToSpeak = description
                textToSpeechManager.speak(textToSpeak)

            } else {
                // Manejar el fallo de inicialización
                Log.e("TTS", "Fallo la inicializacion")
            }
        }*/
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                onInit(true)
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