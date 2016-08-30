package nl.rsdt.japp.application.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import nl.rsdt.japp.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welkom", "Welkom bij de Jotihunt app voor de RP", R.drawable.rp_logo_500x500, Color.parseColor("#993333")));
        addSlide(AppIntroFragment.newInstance("Kaart", "Bekijk de vossen, scouting-groepen, foto-opdrachten en de andere hunters op de kaart", R.drawable.ic_menu_camera, Color.parseColor("#66ccff")));
        addSlide(AppIntroFragment.newInstance("Waarnemen & melden", "Spot of hunt een vos via de app", R.drawable.ic_menu_camera, Color.parseColor("#99cc00")));
        addSlide(AppIntroFragment.newInstance("Zoeken", "Zoek een Scoutinggroeg op of een andere hunter", R.drawable.ic_menu_camera, Color.parseColor("#99cc00")));
        addSlide(AppIntroFragment.newInstance("Automatische Updates", "Wanneer de app actief is wordt alles automatisch bijgewerkt! Verander voorkeuren in de Instellingen onder updates", R.drawable.ic_menu_camera, Color.parseColor("#99cc00")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#996633"));

        setFadeAnimation();

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }



}
