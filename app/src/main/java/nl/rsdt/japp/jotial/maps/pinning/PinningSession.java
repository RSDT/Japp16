package nl.rsdt.japp.jotial.maps.pinning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.Marker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
public class PinningSession extends Snackbar.Callback implements GoogleMap.OnMapClickListener, DialogInterface.OnClickListener, View.OnClickListener, GoogleMap.CancelableCallback {

    /**
     * The GoogleMap used to create markers.
     * */
    private JotiMap jotiMap;

    /**
     * The Marker that indicates the location.
     * */
    private Marker marker;

    /**
     * The callback that gets invoked when the pinning is completed.
     * */
    private OnPinningCompletedCallback callback;

    /**
     * The view where the Snackbar is going to be made on.
     * */
    private View targetView;

    /**
     * The Snackbar that informs the user.
     * */
    private Snackbar snackbar;

    /**
     * The MaterialDialog that asks for the users confirmation and for entering details.
     * */
    private AlertDialog dialog;


    private void initialize() {
        snackbar = Snackbar.make(targetView, "Markeer een positie op de kaart. Swipe dit weg om te annuleren", Snackbar.LENGTH_INDEFINITE);;
        snackbar.setAction("Klaar!", this);
        snackbar.setCallback(this);

        marker = jotiMap.addMarker(new Pair<MarkerOptions, Bitmap>(new MarkerOptions()
                .visible(false)
                .position(new LatLng(0, 0)),null));
    }

    public void start() {
        jotiMap.setOnMapClickListener(this);
        snackbar.show();
    }

    public void end() {
        onDestroy();
    }


    public void onMapReady(JotiMap jotiMap) {
        this.jotiMap = jotiMap;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(!marker.isVisible()) marker.setVisible(true);
        marker.setPosition(latLng);
    }

    @Override
    public void onClick(View view) {
        if(marker.isVisible()) {
            jotiMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12), this);
        } else {
            if(snackbar != null){
                snackbar.dismiss();
                snackbar = null;
            }
            snackbar = Snackbar.make(targetView, "Selecteer een geldige locatie!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setCallback(this);
            snackbar.setAction("Klaar!", this);
            snackbar.show();
        }
    }


    @Override
    public void onFinish() {
        if(dialog != null) {
            dialog.show();
            ((TextView)dialog.findViewById(R.id.pinning_dialog_title)).setText("Bevestig de markering");
        }
    }

    @Override
    public void onCancel() {
        if(dialog != null) {
            dialog.show();
            ((TextView)dialog.findViewById(R.id.pinning_dialog_title)).setText("Bevestig de markering");
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
                    callback.onPinningCompleted(Pin.create(jotiMap, new Pin.Data(title, description, marker.getPosition(), R.drawable.ic_place_white_48dp)));
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                snackbar.setText("Markeer een positie op de kaart. Swipe dit weg om te annuleren");
                snackbar.show();
                break;
        }
    }

    private void onDestroy() {

        if(marker != null) {
            marker.remove();
            marker = null;
        }

        if(jotiMap != null) {
            jotiMap.setOnMapClickListener(null);
            jotiMap = null;
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
        public Builder setGoogleMap(JotiMap jotiMap){
            buffer.jotiMap = jotiMap;
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
        public Builder setTargetView(View view) {
            buffer.targetView = view;
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
