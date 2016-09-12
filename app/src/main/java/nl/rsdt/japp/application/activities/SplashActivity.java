package nl.rsdt.japp.application.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.net.UnknownHostException;

import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.auth.Authentication;
import nl.rsdt.japp.jotial.availability.GooglePlayServicesChecker;
import nl.rsdt.japp.jotial.maps.clustering.ScoutingGroepController;
import nl.rsdt.japp.jotial.maps.management.MapItemController;
import nl.rsdt.japp.jotial.maps.management.transformation.async.AsyncBundleTransduceTask;
import nl.rsdt.japp.jotial.availability.LocationPermissionsChecker;
import nl.rsdt.japp.jotial.net.apis.AuthApi;
import nl.rsdt.japp.service.LocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class SplashActivity extends Activity implements AsyncBundleTransduceTask.OnBundleTransduceCompletedCallback {

    public static final String TAG = "SplashActivity";

    public static final String LOAD_ID = "LOAD_RESULTS";

    Bundle bundle;

    int permission_check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Check if we have the permissions we need.
         * */
        permission_check = LocationPermissionsChecker.check(this);

        new AsyncBundleTransduceTask(this).execute(MapItemController.getAll());
    }

    @Override
    public void onTransduceCompleted(Bundle bundle) {
        this.bundle = bundle;
        ScoutingGroepController.loadAndPutToBundle(bundle);
        continueToNext();
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(LocationPermissionsChecker.hasPermissionOfPermissionRequestResult(requestCode, permissions, grantResults)) {
            if(GooglePlayServicesChecker.check(this) != GooglePlayServicesChecker.FAILURE) {
                validate();
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
            validate();
        }
    }

    public void validate() {
        AuthApi api = Japp.getApi(AuthApi.class);
        api.validateKey(JappPreferences.getAccountKey()).enqueue(new Callback<Authentication.ValidateObject>() {
            @Override
            public void onResponse(Call<Authentication.ValidateObject> call, Response<Authentication.ValidateObject> response) {
                if(response.code() == 200) {
                    Authentication.ValidateObject object = response.body();
                    if(object != null) {
                        if(!object.exists()) {
                            Authentication.startLoginActivity(SplashActivity.this);
                        } else {
                            determineAndStartNewActivity();
                        }
                    }
                } else {
                    Authentication.startLoginActivity(SplashActivity.this);
                }

            }

            @Override
            public void onFailure(Call<Authentication.ValidateObject> call, Throwable t) {
                if(t instanceof UnknownHostException) {
                    determineAndStartNewActivity();
                } else {
                    new AlertDialog.Builder(SplashActivity.this)
                            .setTitle("Fout tijdens vertificatie")
                            .setMessage(t.toString())
                            .setPositiveButton("Opnieuw proberen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    validate();
                                }
                            })
                            .create()
                            .show();
                }
                Log.e(TAG, t.toString(), t);
            }
        });
    }

    public void determineAndStartNewActivity() {

        if(false) {
            Intent intenti = new Intent(this, IntroActivity.class);
            startActivity(intenti);
            finish();
            return;
        }


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
