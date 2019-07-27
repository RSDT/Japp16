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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
public class PinningSession extends Snackbar.Callback implements IJotiMap.OnMapClickListener, DialogInterface.OnClickListener,
        View.OnClickListener, IJotiMap.CancelableCallback {

    /**
     * The GoogleMap used to create markers.
     * */
    private IJotiMap jotiMap;

    /**
     * The Marker that indicates the location.
     * */
    private IMarker marker;

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
        snackbar = Snackbar.make(targetView, R.string.swipe_or_cancle, Snackbar.LENGTH_INDEFINITE);;
        snackbar.setAction(R.string.done, this);
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


    public void onMapReady(IJotiMap jotiMap) {
        this.jotiMap = jotiMap;
    }

    @Override
    public boolean onMapClick(LatLng latLng) {
        if(!marker.isVisible()) marker.setVisible(true);
        marker.setPosition(latLng);
        return false;
    }

    @Override
    public void onClick(View view) {
        if(marker.isVisible()) {
            jotiMap.animateCamera(marker.getPosition(), 12, this);
        } else {
            if(snackbar != null){
                snackbar.dismiss();
                snackbar = null;
            }
            snackbar = Snackbar.make(targetView, R.string.select_valid_location, Snackbar.LENGTH_INDEFINITE);
            snackbar.setCallback(this);
            snackbar.setAction(R.string.done, this);
            snackbar.show();
        }
    }


    @Override
    public void onFinish() {
        if(dialog != null) {
            dialog.show();
            ((TextView)dialog.findViewById(R.id.pinning_dialog_title)).setText(R.string.confirm_mark);
        }
    }

    @Override
    public void onCancel() {
        if(dialog != null) {
            dialog.show();
            ((TextView)dialog.findViewById(R.id.pinning_dialog_title)).setText(R.string.confirm_mark);
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
                snackbar.setText(R.string.swipe_or_cancle);
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
        public Builder setJotiMap(IJotiMap jotiMap){
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
                    .setPositiveButton(R.string.confirm, buffer)
                    .setNegativeButton(R.string.cancel, buffer)
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
