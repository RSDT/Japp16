package nl.rsdt.japp.jotial.maps.sighting;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Description...
 */
public class SightingSession extends Snackbar.Callback implements View.OnClickListener {


    public static final String SIGHT_HUNT = "HUNT";

    public static final String SIGHT_SPOT = "SPOT";

    public static final String SIGHT_MARK = "MARK";

    private String type;

    private GoogleMap googleMap;

    private Marker marker;

    private Snackbar snackbar;

    private LatLng lastLatLng;

    private OnSightingCompletedCallback callback;

    private Deelgebied deelgebied;

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

        callback.onSightingCompleted(lastLatLng);
        destroy();
    }

    public void start()
    {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lastLatLng = latLng;

                if(!marker.isVisible()) marker.setVisible(true);
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
                    switch (type)
                    {
                        case SIGHT_HUNT:
                            marker.setIcon(BitmapDescriptorFactory.fromResource(deelgebied.getDrawableHunt()));
                            break;
                        case SIGHT_SPOT:
                            marker.setIcon(BitmapDescriptorFactory.fromResource(deelgebied.getDrawableSpot()));
                            break;
                    }
                }
            }
        });

        snackbar.show();
    }

    @Override
    public void onClick(View view) {
        callback.onSightingCompleted(lastLatLng);
        destroy();
    }

    public void destroy()
    {
        type = null;

        googleMap.setOnMapClickListener(null);
        googleMap = null;

        if(marker != null)
        {
            marker.remove();
            marker = null;
        }

        snackbar = null;

        lastLatLng = null;

        callback = null;

    }

    public static class Builder
    {

        private SightingSession buffer = new SightingSession();

        public Builder setGoogleMap(GoogleMap googleMap){
            buffer.googleMap = googleMap;
            return this;
        }

        public Builder setType(String type)
        {
            buffer.type = type;
            return this;
        }

        public Builder setTargetView(View view)
        {
            buffer.snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
            return this;
        }

        public Builder setOnSightingCompletedCallback(OnSightingCompletedCallback callback)
        {
            buffer.callback = callback;
            return this;
        }


        public SightingSession create()
        {
            buffer.initialize();
            return buffer;
        }


    }

    public interface OnSightingCompletedCallback
    {
        void onSightingCompleted(LatLng chosen);
    }

}
