package nl.rsdt.japp.jotial.maps.pinning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
public class PinningSession extends Snackbar.Callback implements OnMapReadyCallback, GoogleMap.OnMapClickListener, DialogInterface.OnClickListener, View.OnClickListener {

    /**
     * The GoogleMap used to create markers.
     * */
    private GoogleMap googleMap;

    /**
     * The Marker that indicates the location.
     * */
    private Marker marker;

    /**
     * The callback that gets invoked when the pinning is completed.
     * */
    private OnPinningCompletedCallback callback;

    /**
     * The Snackbar that informs the user.
     * */
    private Snackbar snackbar;

    /**
     * The MaterialDialog that asks for the users confirmation and for entering details.
     * */
    private AlertDialog dialog;


    private void initialize() {
        snackbar.setText("Markeer een positie op de kaart. Swipe dit weg om te annuleren");
        snackbar.setAction("Klaar!", this);
        snackbar.setCallback(this);

        marker = googleMap.addMarker(new MarkerOptions()
                .visible(false)
                .position(new LatLng(0, 0)));
    }

    public void start() {
        googleMap.setOnMapClickListener(this);
        snackbar.show();
    }

    public void end() {
        onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(!marker.isVisible()) marker.setVisible(true);
        marker.setPosition(latLng);
    }

    @Override
    public void onClick(View view) {
        if(marker.isVisible()) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    if(dialog != null) {
                        dialog.show();
                        ((TextView)dialog.findViewById(R.id.pinning_dialog_title)).setText("Bevestig de markering");
                    }
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
    public void onDismissed(Snackbar snackbar, int event) {
        super.onDismissed(snackbar, event);

        switch (event)
        {
            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                if(callback != null) {
                    callback.onPinningCompleted(null);
                }
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i)
        {
            case DialogInterface.BUTTON_POSITIVE:
                if(callback != null) {
                    String title = ((TextView)dialog.findViewById(R.id.pinning_dialog_title_edit)).getText().toString();
                    String description = ((TextView)dialog.findViewById(R.id.pinning_dialog_description_edit)).getText().toString();
                    callback.onPinningCompleted(Pin.create(googleMap, new Pin.Data(title, description, marker.getPosition(), R.drawable.ic_action_place)));
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:l:
                snackbar.show();
                break;
        }
    }

    private void onDestroy() {

        if(marker != null) {
            marker.remove();
            marker = null;
        }

        if(googleMap != null) {
            googleMap.setOnMapClickListener(null);
            googleMap = null;
        }

        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        if(snackbar != null)  {
            snackbar.dismiss();
            snackbar = null;
        }


        callback = null;
    }

    public static class Builder {

        PinningSession buffer = new PinningSession();

        /**
         * Sets the GoogleMap of the SightingSession.
         * */
        public Builder setGoogleMap(GoogleMap googleMap){
            buffer.googleMap = googleMap;
            return this;
        }

        /**
         * Sets the callback of the SightingSession.
         * */
        public Builder setCallback(OnPinningCompletedCallback callback) {
            buffer.callback = callback;
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
            View view = inflater.inflate(R.layout.pinning_input_dialog, null);
            buffer.dialog = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setPositiveButton("Bevestigen", buffer)
                    .setNegativeButton("Annuleren", buffer)
                    .setView(view)
                    .create();
            return this;
        }

        /**
         * Creates the PinningSession.
         * */
        public PinningSession create() {
            buffer.initialize();
            return buffer;
        }

    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 8-9-2016
     * Description...
     */
    public interface OnPinningCompletedCallback {
        void onPinningCompleted(Pin pin);
    }

}
