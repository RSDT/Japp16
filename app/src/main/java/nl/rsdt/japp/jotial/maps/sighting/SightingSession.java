package nl.rsdt.japp.jotial.maps.sighting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.Snackbar;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.misc.CameraUtils;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for Sighting
 */
public class SightingSession extends Snackbar.Callback implements View.OnClickListener, DialogInterface.OnClickListener, GoogleMap.SnapshotReadyCallback {

    /**
     * Defines the SightingSession type HUNT.
     * */
    public static final String SIGHT_HUNT = "HUNT";

    /**
     * Defines the SightingSession type SPOT.
     * */
    public static final String SIGHT_SPOT = "SPOT";

    /**
     * The type of the sighting.
     * */
    private String type;

    /**
     * The GoogleMap used to create markers.
     * */
    private GoogleMap googleMap;

    /**
     * The Marker that indicates the location.
     * */
    private Marker marker;

    /**
     * The Snackbar that informs the user.
     * */
    private Snackbar snackbar;

    /**
     * The AlertDialog that asks for the users confirmation.
     * */
    private AlertDialog dialog;

    /**
     * The last LatLng that was selected.
     * */
    private LatLng lastLatLng;

    /**
     * The callback for when the sighting is completed;
     * */
    private OnSightingCompletedCallback callback;

    /**
     * The Deelgebied where the lastLatLng is in, null if none.
     * */
    private Deelgebied deelgebied;

    /**
     * Initializes the SightingSession.
     * */
    private void initialize()
    {
        BitmapDescriptor descriptor;
        switch (type)
        {
            case SIGHT_HUNT:
                snackbar.setText("Markeer de positie van de vossen op de kaart. Swipe dit weg om te annuleren");
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.vos_x_4);
                break;
            case SIGHT_SPOT:
                snackbar.setText("Markeer de positie van de vossen op de kaart. Swipe dit weg om te annuleren");
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.vos_x_3);
                break;
            default:
                snackbar.setText("");
                descriptor = BitmapDescriptorFactory.defaultMarker();
                break;
        }
        snackbar.setAction("Klaar!", this);
        snackbar.setCallback(this);


        marker = googleMap.addMarker(new MarkerOptions()
                .visible(false)
                .position(new LatLng(0,0))
                .icon(descriptor));
    }

    @Override
    public void onDismissed(Snackbar snackbar, int event) {
        super.onDismissed(snackbar, event);
        switch (event)
        {
            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                if(callback != null) {
                    callback.onSightingCompleted(null, null, null);
                }
                destroy();
                break;
        }
    }

    /**
     * Starts the SightingSession.
     * */
    public void start()
    {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lastLatLng = latLng;

                marker.setPosition(latLng);

                boolean updateMarker = false;

                if(deelgebied == null)
                {
                    deelgebied = Deelgebied.resolveOnLocation(latLng);
                    updateMarker = true;
                }
                else
                {
                    if(!deelgebied.containsLocation(latLng))
                    {
                        deelgebied = null;
                        deelgebied = Deelgebied.resolveOnLocation(latLng);
                        updateMarker = true;
                    }
                }

                if(deelgebied != null && updateMarker)
                {
                    String icon = null;
                    switch (type)
                    {
                        case SIGHT_HUNT:
                            marker.setIcon(BitmapDescriptorFactory.fromResource(deelgebied.getDrawableHunt()));
                            icon = String.valueOf(deelgebied.getDrawableHunt());
                            break;
                        case SIGHT_SPOT:
                            marker.setIcon(BitmapDescriptorFactory.fromResource(deelgebied.getDrawableSpot()));
                            icon = String.valueOf(deelgebied.getDrawableSpot());
                            break;
                    }

                    MarkerIdentifier identifier = new MarkerIdentifier.Builder()
                            .setType(MarkerIdentifier.TYPE_SIGHTING)
                            .add("text", type)
                            .add("icon", icon)
                            .create();
                    marker.setTitle(new Gson().toJson(identifier));
                }

                if(!marker.isVisible()) {
                    marker.setVisible(true);
                }

            }
        });

        snackbar.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i)
        {
            case AlertDialog.BUTTON_POSITIVE:
                if(callback != null) {
                    callback.onSightingCompleted(lastLatLng, deelgebied, ((TextView)dialog.findViewById(R.id.sighting_dialog_info_edit)).getText().toString());
                    destroy();
                }
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                snackbar.show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(deelgebied != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 12), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if(dialog != null) {
                        dialog.show();
                        ((TextView)dialog.findViewById(R.id.sighting_dialog_title)).setText("Bevestig de " + type);
                        ((TextView)dialog.findViewById(R.id.sighting_dialog_team_label)).setText("Deelgebied: " + deelgebied.getName());
                    }
                    googleMap.snapshot(SightingSession.this);
                }

                @Override
                public void onCancel() {

                }
            });

        } else {
            snackbar.setText("Selecteer een geldige locatie!");
            snackbar.show();
        }

    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        if(dialog != null) {
            ((ImageView)dialog.findViewById(R.id.sighting_dialog_snapshot)).setImageDrawable(new BitmapDrawable(Japp.getAppResources(), bitmap));
        }
    }

    /**
     * Destroys the SightingSession.
     * */
    public void destroy()
    {
        type = null;

        if(googleMap != null)
        {
            googleMap.setOnMapClickListener(null);
            googleMap = null;
        }

        if(marker != null)
        {
            marker.remove();
            marker = null;
        }

        if(dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }

        if(snackbar != null)
        {
            snackbar.dismiss();
            snackbar = null;
        }

        lastLatLng = null;

        callback = null;

        deelgebied = null;

    }


    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-7-2016
     * Builder for the SightingSession.
     */
    public static class Builder
    {

        /**
         * Buffer to hold the SightingSession.
         * */
        private SightingSession buffer = new SightingSession();

        /**
         * Sets the GoogleMap of the SightingSession.
         * */
        public Builder setGoogleMap(GoogleMap googleMap){
            buffer.googleMap = googleMap;
            return this;
        }

        /**
         * Sets the Type of the SightingSession.
         * */
        public Builder setType(String type)
        {
            buffer.type = type;
            return this;
        }

        /**
         * Sets the TargetView of the SightingSession.
         * */
        public Builder setTargetView(View view)
        {
            buffer.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
            return this;
        }

        /**
         * Sets the Context for the Dialog of the SightingSession.
         * */
        public Builder setDialogContext(Context context) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.sighting_input_dialog, null);
            buffer.dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setPositiveButton("Bevestigen", buffer)
                    .setNegativeButton("Annuleren", buffer)
                    .setView(view)
                    .create();
            return this;
        }

        /**
         * Sets the callback of the SightingSession.
         * */
        public Builder setOnSightingCompletedCallback(OnSightingCompletedCallback callback)
        {
            buffer.callback = callback;
            return this;
        }

        /**
         * Creates the SightingSession.
         * */
        public SightingSession create()
        {
            buffer.initialize();
            return buffer;
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 13-7-2016
     * Callback for when a SightingSession is completed.
     */
    public interface OnSightingCompletedCallback
    {
        /**
         * Gets invoked when a SightingSession has been completed.
         *
         * @param chosen The chosen LatLng.
         * @param deelgebied The Deelgebied where the LatLng is in, null if none.
         * @param optionalInfo The optional info the user can provide.
         * */
        void onSightingCompleted(LatLng chosen, Deelgebied deelgebied, String optionalInfo);
    }

}
