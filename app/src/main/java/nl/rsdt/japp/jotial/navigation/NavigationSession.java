package nl.rsdt.japp.jotial.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * Created by mattijn on 16/08/17.
 */

public class NavigationSession extends Snackbar.Callback implements IJotiMap.OnMapClickListener, DialogInterface.OnClickListener,
        View.OnClickListener, IJotiMap.CancelableCallback, IJotiMap.OnMarkerClickListener {

    /**
     * The GoogleMap used to create markers.
     */
    private IJotiMap jotiMap;

    /**
     * The Marker that indicates the location.
     */
    private IMarker marker;

    /**
     * The callback that gets invoked when the navigation is completed.
     */
    private NavigationSession.OnNavigationCompletedCallback callback;

    /**
     * The view where the Snackbar is going to be made on.
     */
    private View targetView;

    /**
     * The Snackbar that informs the user.
     */
    private Snackbar snackbar;

    /**
     * The MaterialDialog that asks for the users confirmation and for entering details.
     */
    private android.app.AlertDialog dialog;
    private Navigator navigator;
    private long lastmoved;

    public NavigationSession(){

    }
    private void initialize() {
        snackbar = Snackbar.make(targetView, "Markeer een positie op de kaart om naar toe te navigeren. Swipe dit weg om te annuleren", Snackbar.LENGTH_INDEFINITE);
        ;
        snackbar.setAction("Klaar!", this);
        snackbar.setCallback(this);

        marker = jotiMap.addMarker(new Pair<MarkerOptions, Bitmap>(new MarkerOptions()
                .visible(true)
                .position(new LatLng(0, 0)), null));
    }

    public void start() {
        jotiMap.setMarkerOnClickListener(this);
        //navigator.start();
        jotiMap.setOnMapClickListener(this);
        snackbar.show();
    }

    public void end() {
        jotiMap.setMarkerOnClickListener(null);
        navigator.clear();
        onDestroy();
    }


    @Override
    public boolean onMapClick(LatLng latLng) {
        moveMarker(latLng, false);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (marker.isVisible()) {
            jotiMap.animateCamera(marker.getPosition(), 12, this);
        } else {
            if (snackbar != null) {
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
        if (dialog != null) {
            dialog.show();
            ((TextView) dialog.findViewById(R.id.navigation_dialog_title)).setText("Open navigatie in Google maps ");
        }
    }

    @Override
    public void onCancel() {
        if (dialog != null) {
            dialog.show();
            ((TextView) dialog.findViewById(R.id.navigation_dialog_title)).setText("Open navigatie in google maps");
        }
    }

    @Override
    public void onDismissed(Snackbar snackbar, int event) {
        super.onDismissed(snackbar, event);

        switch (event) {
            case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                if (callback != null) {
                    callback.onNavigationCompleted(null, false);
                }
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case DialogInterface.BUTTON_POSITIVE:
                if (callback != null) {
                    //String title = ((TextView) dialog.findViewById(R.id.navigation_dialog_title_edit)).getText().toString();
                    //String description = ((TextView) dialog.findViewById(R.id.navigation_dialog_description_edit)).getText().toString();
                    if (marker != null) {
                        LatLng pos = marker.getPosition();
                        marker.remove();
                        callback.onNavigationCompleted(pos, false);
                    }
                }
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                if (callback != null) {
                    //String title = ((TextView) dialog.findViewById(R.id.navigation_dialog_title_edit)).getText().toString();
                    //String description = ((TextView) dialog.findViewById(R.id.navigation_dialog_description_edit)).getText().toString();
                    if (marker != null) {
                        LatLng pos = marker.getPosition();
                        marker.remove();
                        callback.onNavigationCompleted(pos, true); // // TODO: 30/09/17
                    }
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                snackbar.setText("Markeer een positie op de kaart. Swipe dit weg om te annuleren");
                snackbar.show();
                break;
        }
    }

    private void onDestroy() {

        if (marker != null) {
            marker.remove();
            marker = null;
        }

        if (jotiMap != null) {
            jotiMap.setOnMapClickListener(null);
            jotiMap = null;
        }

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }


        callback = null;
    }

    private void moveMarker(LatLng latLng, boolean priority){
        if (priority || System.currentTimeMillis() - lastmoved > 800) {
            navigator.setEndLocation(latLng);
            if (!marker.isVisible()) marker.setVisible(true);
            marker.setPosition(latLng);
            this.lastmoved= System.currentTimeMillis();
        }
    }

    @Override
    public boolean OnClick(IMarker m) {
        m.showInfoWindow();
        moveMarker(m.getPosition(), true);
        return false;
    }

    public static class Builder {

        NavigationSession buffer = new NavigationSession();

        /**
         * Sets the GoogleMap of the SightingSession.
         */
        public NavigationSession.Builder setJotiMap(IJotiMap jotiMap) {
            buffer.jotiMap = jotiMap;
            buffer.navigator = new Navigator(jotiMap);
            return this;
        }

        /**
         * Sets the callback of the SightingSession.
         */
        public NavigationSession.Builder setCallback(NavigationSession.OnNavigationCompletedCallback callback) {
            buffer.callback = callback;
            return this;
        }

        /**
         * Sets the TargetView of the SightingSession.
         */
        public NavigationSession.Builder setTargetView(View view) {
            buffer.targetView = view;
            return this;
        }

        /**
         * Sets the Context for the Dialog of the SightingSession.
         */
        public NavigationSession.Builder setDialogContext(Context context) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.navigation_input_dialog, null);
            buffer.dialog = new android.app.AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setPositiveButton("Zelf navigeren", buffer)
                    //.setNeutralButton("Op de Navigatie Navigeren", buffer)
                    .setNegativeButton("Annuleren", buffer)
                    .setView(view)
                    .create();
            return this;
        }

        /**
         * Creates the NavigationSession.
         */
        public NavigationSession create() {
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
    public interface OnNavigationCompletedCallback {
        void onNavigationCompleted(LatLng navigateTo, boolean toNavigationPhone);
    }
}