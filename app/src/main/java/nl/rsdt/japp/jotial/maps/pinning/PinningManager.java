package nl.rsdt.japp.jotial.maps.pinning;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;


/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 20-7-2016
 * Description...
 */
public class PinningManager implements OnMapReadyCallback {

    private static final String BUNDLE_KEY = "PinningManager";

    private ArrayList<Pin> pins = new ArrayList<>();

    private GoogleMap googleMap;

    public void onCreate(Bundle savedInstanceState)
    {
        if(savedInstanceState != null)
        {
            pins = savedInstanceState.getParcelableArrayList(BUNDLE_KEY);
        }
    }


    public void onSaveInstanceState(Bundle state)
    {
        state.putParcelableArrayList(BUNDLE_KEY, pins);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Pin current;
        for(int i = 0; i < pins.size(); i++)
        {
            current = pins.get(i);
            if(current != null)
            {
                current.addToGoogleMap(googleMap);
            }
        }
    }

    public void addPin(Pin pin)
    {
        pins.add(pin);
    }

    public void removePin(Pin pin)
    {
        pins.remove(pin);
    }




}
