package nl.rsdt.japp.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String key = JappPreferences.getAccountKey();
        if(key.isEmpty())
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            if(JappPreferences.isFirstRun())
            {

                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
