import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized: Boolean = false

    fun initialize(callback: (Boolean) -> Unit) {
        if (isInitialized) {
            callback(true) // Ya inicializado
            return
        }

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Lenguaje no soportado")
                    callback(false)
                } else {
                    isInitialized = true
                    callback(true)
                    Log.d("TTS", "TTS Inicializado")
                }
            } else {
                Log.e("TTS", "Fallo la inicialización")
                callback(false)
            }
        }
    }


    fun speak(text: String) {
        if (isInitialized) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TTS", "No inicializado, no puede hablar.")
        }
    }

    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.shutdown()
    }
}