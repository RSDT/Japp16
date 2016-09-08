package nl.rsdt.japp.jotial.maps.pinning;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import nl.rsdt.japp.jotial.Recreatable;
import nl.rsdt.japp.jotial.io.AppData;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-9-2016
 * Description...
 */
public class PinningManager implements OnMapReadyCallback, Recreatable {

    protected static final String STORAGE_ID = "PinData";

    protected static final String BUNDLE_KEY = "PinningManager";

    protected GoogleMap googleMap;

    protected ArrayList<Pin> pins = new ArrayList<>();

    protected ArrayList<Pin.Data> buffer = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState)
    {
        ArrayList<Pin.Data> pins;
        if(savedInstanceState != null) {
            pins = savedInstanceState.getParcelableArrayList(BUNDLE_KEY);
        } else {
            pins = AppData.getObject(STORAGE_ID, new TypeToken<ArrayList<Pin.Data>>(){}.getType());
        }

        if(pins != null && !pins.isEmpty()) {
            if(googleMap != null) {
                process(pins);
            } else {
                buffer = pins;
            }
        }
    }

    public void onSaveInstanceState(Bundle state)
    {
        ArrayList<Pin.Data> list = new ArrayList<>();
        Pin current;
        for(int i = 0; i < pins.size(); i++) {
            current = pins.get(i);

            if(current != null) {
                list.add(current.data);
            }

        }
        state.putParcelableArrayList(BUNDLE_KEY, list);
    }

    public void add(Pin pin) {
        pins.add(pin);
        save(true);
    }

    public void remove(Pin pin) {
        pins.remove(pin);
        save(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if(buffer != null) {
            process(buffer);
            buffer.clear();
            buffer = null;
        }
    }

    private void process(ArrayList<Pin.Data> input) {
        if(googleMap != null) {
            Pin.Data buffer;
            for(int i = 0; i < input.size(); i++) {
                buffer = input.get(i);

                if(buffer != null) {
                    pins.add(Pin.create(googleMap, buffer));
                }
            }
        }
    }

    private void save(boolean background) {
        /**
         * Save the pins
         * */
        ArrayList<Pin.Data> list = new ArrayList<>();
        Pin current;
        for(int i = 0; i < pins.size(); i++) {
            current = pins.get(i);

            if(current != null) {
                list.add(current.data);
            }
        }
        if(background) {
            AppData.saveObjectAsJsonInBackground(list, STORAGE_ID);
        } else {
            AppData.saveObjectAsJson(list, STORAGE_ID);
        }

    }

    public void onDestroy() {
        save(false);

        if(googleMap != null) {
            googleMap = null;
        }

        if(pins != null){
            pins.clear();
            pins = null;
        }

        if(buffer != null) {
            buffer.clear();
            buffer = null;
        }

    }

}
