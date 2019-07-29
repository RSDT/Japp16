package nl.rsdt.japp.application.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment

import nl.rsdt.japp.R

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(AppIntroFragment.newInstance(getString(R.string.welkom), getString(R.string.welkom_sub), R.drawable.rp_logo_500x500, Color.parseColor("#993333")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.kaart), getString(R.string.kaart_desc), R.drawable.map_example, Color.parseColor("#66ccff")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.waarnemen), getString(R.string.waarnemen_desc), R.drawable.spot_example, Color.parseColor("#99cc00")))
        addSlide(AppIntroFragment.newInstance(getString(R.string.auto_updates), getString(R.string.auto_updates_desc), R.drawable.ic_action_refresh, Color.parseColor("#993399")))

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#996633"))

        setFadeAnimation()

        // Hide Skip/Done button.
        showSkipButton(true)
        isProgressButtonEnabled = true

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true)
        setVibrateIntensity(30)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.


        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }


}
