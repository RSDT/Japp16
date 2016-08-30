package nl.rsdt.japp.application.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import java.net.URL;
import java.util.ArrayList;

import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.availability.GooglePlayServicesChecker;
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;
import nl.rsdt.japp.jotial.io.AppData;
import nl.rsdt.japp.jotial.maps.MapDataLoader;
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController;
import nl.rsdt.japp.jotial.maps.management.transformation.AbstractTransducerResult;
import nl.rsdt.japp.jotial.maps.management.transformation.async.OnTransduceCompletedCallback;
import nl.rsdt.japp.jotial.availability.LocationPermissionsChecker;
import nl.rsdt.japp.service.LocationService;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class SplashActivity extends Activity implements OnTransduceCompletedCallback {

    public static final String LOAD_ID = "LOAD_RESULTS";

    Bundle bundle = new Bundle();

    MapDataLoader mapDataLoader = new MapDataLoader();

    int count = 0;

    int permission_check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Check if we have the permissions we need.
         * */
        permission_check = LocationPermissionsChecker.check(this);

        /**
         * Load the MapData.
         * */
        mapDataLoader.load(this);

    }

    @Override
    public void onTransduceCompleted(AbstractTransducerResult result) {
        if(result != null)
        {
            bundle.putParcelable(result.getBundleId(), result);
        }
        count++;

        if(count == mapDataLoader.getNumOfControllers()) {
            ArrayList<ScoutingGroepInfo> list = AppData.getObject(ScoutingGroepController.STORAGE_ID, new TypeToken<ArrayList<ScoutingGroepInfo>>(){}.getType());
            if(list != null) {
                bundle.putParcelableArrayList(ScoutingGroepController.BUNDLE_ID, list);
            }
            continueToNext();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(LocationPermissionsChecker.hasPermissionOfPermissionRequestResult(requestCode, permissions, grantResults)) {
            if(GooglePlayServicesChecker.check(this) != GooglePlayServicesChecker.FAILURE) {
                determineAndStartNewActivity();
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Locatie permissie")
                    .setMessage("De app heeft de locatie permissie nodig om goed te kunnen functioneren")
                    .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            permission_check = LocationPermissionsChecker.check(SplashActivity.this);
                        }
                    })
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                }
            });
            dialog.show();
        }
    }

    public void continueToNext() {
        if(permission_check != LocationPermissionsChecker.PERMISSIONS_REQUEST_REQUIRED) {
            determineAndStartNewActivity();
        }
    }

    public void determineAndStartNewActivity() {
        /**
         * Start the LocationService.
         * */
        Intent intentService = new Intent(this, LocationService.class);
        startService(intentService);

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
