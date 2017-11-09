package ionsoft.endahquotes

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.*

/**
 * A helper class for easily call TextToSpeech from various android versions
 *
 * @author hendrawd on 11/22/16
 */

class TextToSpeechHelper {

    /**
     * Started at android 2.3 we will use "in_ID" as the android standard
     * although we can use "in" for java 5 and 6, and "id" as java >7
     * https://web.archive.org/web/20120814113314/http://colincooper.net/blog/2011/02/17/android-supported-language-and-locales/
     */
    val ID = "in_ID"
    val US = "en_US"

    private var textToSpeech: TextToSpeech? = null
    private var speakCallback: SpeakCallback? = null

    interface SpeakCallback {
        fun onSpeak()
    }

    @JvmOverloads
    fun speak(context: Context, stringToSpeak: String, locale: Locale? = null) {
        if (textToSpeech == null) {
            textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
                if (locale != null) {
                    textToSpeech!!.language = locale
                } else {
                    textToSpeech!!.language = Locale(ID)
                }
                speakCompat(stringToSpeak)
                if (speakCallback != null) {
                    speakCallback!!.onSpeak()
                }
            })
        } else {
            //negate speak command if it is currently speaking
            if (!textToSpeech!!.isSpeaking) {
                speakCompat(stringToSpeak)
            }
            if (speakCallback != null) {
                speakCallback!!.onSpeak()
            }
        }
    }

    private fun speakCompat(stringToSpeak: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeechGreater21(stringToSpeak)
        } else {
            textToSpeechUnder20(stringToSpeak)
        }
    }

    fun stop() {
        textToSpeech!!.stop()
    }

    private fun textToSpeechUnder20(text: String) {
        val map = HashMap<String, String>()
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId")
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, map)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun textToSpeechGreater21(text: String) {
        val utteranceId = this.hashCode().toString() + ""
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun setCallback(speakCallback: SpeakCallback) {
        this.speakCallback = speakCallback
    }
}
