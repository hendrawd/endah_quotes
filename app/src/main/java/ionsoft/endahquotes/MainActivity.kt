package ionsoft.endahquotes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class MainActivity : AppCompatActivity() {

    private var textToSpeechHelper: TextToSpeechHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUi().setContentView(this)
    }

    override fun onStop() {
        super.onStop()
        stopSpeak()
    }

    fun stopSpeak() {
        textToSpeechHelper?.stop()
    }

    fun speak(text: String) {
        if (!TextUtils.isEmpty(text)) {
            if (textToSpeechHelper == null) {
                textToSpeechHelper = TextToSpeechHelper()
            }
            textToSpeechHelper!!.speak(this, text)
        }
    }
}

class MainActivityUi : AnkoComponent<MainActivity> {

    private val customStyle = { view: Any ->
        when (view) {
            is Button -> view.textSize = 18f
            is TextView -> view.textSize = 18f
        }
    }

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        scrollView {
            verticalLayout {
                val dip8 = dip(8)
                padding = dip8

                val imageView = imageView()
                imageView.setImageResource(R.drawable.ic_foto_endah)

                val randomQuoteTextView = textView("Random quote will appear here")
                val layoutParams = randomQuoteTextView.layoutParams as LinearLayout.LayoutParams
                layoutParams.setMargins(dip8, dip8, dip8, dip8)
                randomQuoteTextView.gravity = Gravity.CENTER

                button("Generate Random Quote!") {
                    onClick {
                        val quoteArray = getContext().resources.getStringArray(R.array.quotes)
                        val random = Random()
                        var randomIndex = random.nextInt(quoteArray.size)
                        var randomQuote = quoteArray[randomIndex]
                        while (randomQuote == randomQuoteTextView.text) {
                            randomIndex = random.nextInt(quoteArray.size)
                            randomQuote = quoteArray[randomIndex]
                        }
                        randomQuoteTextView.text = randomQuote
                        val mainActivity = getContext() as MainActivity
                        mainActivity.stopSpeak()
                        mainActivity.speak(randomQuote)
                    }
                }
            }
        }.applyRecursively(customStyle)
    }

}