package nl.rsdt.japp.application.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;

import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.jotial.maps.management.transformation.TransduceMode;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransducePackage;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncTransduceTask;
import nl.rsdt.japp.jotial.maps.management.transformation.async.OnTransduceCompletedCallback;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class SplashActivity extends Activity implements OnTransduceCompletedCallback {

    public static final String LOAD_ID = "LOAD_RESULTS";

    Bundle bundle = new Bundle();

    int count = 0;

    int numOfControllers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            int pCount = 0;
            MapItemController[] controllers = MapItemController.getAll();
            numOfControllers = controllers.length;
            AsyncTransducePackage[] packages = new AsyncTransducePackage[controllers.length];
            MapItemController controller;
            for(int i = 0; i < controllers.length; i++) {
                controller = controllers[i];

                if(controller != null) {
                    packages[pCount] =  new AsyncTransducePackage.Builder<>()
                            .setMode(TransduceMode.STORAGE_MODE)
                            .setTransducer(controller.getTransducer())
                            .setCallback(this)
                            .create();
                    pCount++;
                }
            }
            new AsyncTransduceTask().execute(packages);
        }
        catch(Exception ex)
        {
            FirebaseCrash.report(ex);
        }
    }

    @Override
    public void onTransduceCompleted(AbstractTransducerResult result) {
        if(result != null)
        {
            bundle.putParcelable(result.getBundleId(), result);
        }
        count++;

        if(count == numOfControllers)
        {
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
                    intent.putExtra(LOAD_ID, bundle);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}
