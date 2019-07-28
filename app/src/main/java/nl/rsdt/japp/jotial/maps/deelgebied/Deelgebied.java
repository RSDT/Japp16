package nl.rsdt.japp.jotial.maps.deelgebied;

import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo;
import nl.rsdt.japp.jotial.maps.kml.KmlDeelgebied;
import nl.rsdt.japp.jotial.maps.kml.KmlFile;
import nl.rsdt.japp.jotial.maps.kml.KmlLocation;
import nl.rsdt.japp.jotial.maps.kml.KmlReader;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 13-7-2016
 * Class for the Deelgebieden,
 * Deelgebied.initialize(Resources) must be called in order for this class to function.
 */
public final class Deelgebied {

    /**
     * Defines the Alpha Deelgebied.
     * */
    private static final Deelgebied Alpha = new Deelgebied("alpha", JappPreferences.getColorName("a"));

    /**
     * Defines the Bravo Deelgebied.
     * */
    private static final Deelgebied Bravo = new Deelgebied("bravo", JappPreferences.getColorName("b"));

    /**
     * Defines the Charlie Deelgebied.
     * */
    private static final Deelgebied Charlie = new Deelgebied("charlie", JappPreferences.getColorName("c"));

    /**
     * Defines the Delta Deelgebied.
     * */
    private static final Deelgebied Delta = new Deelgebied("delta", JappPreferences.getColorName("d"));

    /**
     * Defines the Echo Deelgebied.
     * */
    private static final Deelgebied Echo = new Deelgebied("echo", JappPreferences.getColorName("e"));

    /**
     * Defines the Foxtrot Deelgebied.
     * */

    private static final Deelgebied Foxtrot = new Deelgebied("foxtrot", JappPreferences.getColorName("f"));

    /**
     * Defines the Xray Deelgebied.
     * */
    public static final Deelgebied Xray = new Deelgebied("xray", JappPreferences.getColorName("x"));
    private static final String TAG = "Deelgebied";

    /**
     * Gets a array of all the Deelgebieden.
     * */
    public static Deelgebied[] all() {
        return new Deelgebied[] { Alpha, Bravo, Charlie, Delta, Echo, Foxtrot, Xray };
    }
    public static volatile boolean deelgebiedenInitialized = false;

    /**
     * The name of this Deelgebied.
     * */
    private String name;

    /**
     * The hunt drawable associated with this Deelgebied.
     * */
    private int drawable_hunt;

    /**
     * The spot drawable associated with this Deelgebied.
     * */
    private int drawable_spot;

    /**
     * The color of this Deelgebied.
     * */
    private int color;

    /**
     * Gets the hunt drawable associated with this Deelgebied.
     * */
    public int getDrawableHunt() {
        return drawable_hunt;
    }

    /**
     * Gets the spot drawable associated with this Deelgebied.
     * */
    public int getDrawableSpot() {
        return drawable_spot;
    }

    /**
     * Gets the color of the Deelgebied.
     * */
    public int getColor() {
        return color;
    }

    /**
     * Gets the name of the Deelgebied.
     * */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of the Deelgebied, with a given alpha.
     *
     * @param alpha The alpha from 0 - 255.
     * @return The color of the Deelgebied with a given alpha.
     * */
    public int alphaled(int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * The list of coordinates that is the area of the Deelgebied.
     * */
    private ArrayList<LatLng> coordinates = new ArrayList<>();
    private static Map<String,LinkedList<OnInitialized>> onInitializedList = new HashMap<>();
    /**
     * Gets the list of coordinates that is the area of the Deelgebied.
     * */
    public ArrayList<LatLng> getCoordinates() {
        return this.coordinates;
    }

    public void getDeelgebiedAsync(OnInitialized onInitialized){
        if (deelgebiedenInitialized){
            onInitialized.onInitialized(this);
        }
        else{
            if (!onInitializedList.containsKey(this.name)) onInitializedList.put(this.name, new LinkedList<>());
            onInitializedList.get(this.name).add(onInitialized);
        }
    }

    /**
     * Initializes a new instance of Deelgebied.
     *
     * @param name The name of the Deelgebied.
     * */
    private Deelgebied(String name,String colorName)
    {
        this.name = name;
        switch(MetaColorInfo.ColorNameInfo.DeelgebiedColor.valueOf(colorName)){

            case Groen:
                this.drawable_hunt = R.drawable.vos_groen_4;
                this.drawable_spot = R.drawable.vos_groen_3;
                this.color = Color.argb(255, 0, 255, 0);
                break;
            case Rood:
                this.drawable_hunt = R.drawable.vos_rood_4;
                this.drawable_spot = R.drawable.vos_rood_3;
                this.color = Color.argb(255, 255, 0, 0);
                break;
            case Paars:
                this.drawable_hunt = R.drawable.vos_paars_4;
                this.drawable_spot = R.drawable.vos_paars_3;
                this.color = Color.argb(255, 255, 0, 255);
                break;
            case Oranje:
                this.drawable_hunt = R.drawable.vos_oranje_4;
                this.drawable_spot = R.drawable.vos_oranje_3;
                this.color = Color.argb(255, 255, 162, 0);
                break;
            case Blauw:
                this.drawable_hunt = R.drawable.vos_oranje_4;
                this.drawable_spot = R.drawable.vos_oranje_3;
                this.color = Color.argb(255, 0, 0, 255);
                break;
            default:
            case Onbekend:
            case Zwart:
                this.drawable_hunt = R.drawable.vos_zwart_4;
                this.drawable_spot = R.drawable.vos_zwart_3;
                this.color = Color.argb(255, 0, 0, 0);
                break;
            case Turquoise:
                this.drawable_hunt = R.drawable.vos_groen_4;
                this.drawable_spot = R.drawable.vos_groen_3;
                this.color = Color.argb(255, 0, 255, 255);
                break;
        }

    }

    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     * */
    public boolean containsLocation(LatLng location)
    {
        return PolyUtil.containsLocation(location, getCoordinates(), false);
    }

    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     * */
    public boolean containsLocation(Location location)
    {
        return PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), getCoordinates(), false);
    }


    /**
     * Initializes the Deelgebieden, loading their coordinates from the Resources.
     * */
    public static synchronized void initialize(Resources resources)
    {
        KmlReader.parseFromMeta(new KmlReader.Callback() {
            @Override
            public void onException(Throwable e) {
                Log.e(Deelgebied.TAG,e.toString());
                Deelgebied[] gebieden = all();
                Deelgebied current;
                for(int g = 0; g < gebieden.length; g++) {
                    current = gebieden[g];

                    InputStream stream;
                    switch (current.name) {
                        case "alpha":
                            stream = resources.openRawResource(R.raw.alpha);
                            break;
                        case "bravo":
                            stream = resources.openRawResource(R.raw.bravo);
                            break;
                        case "charlie":
                            stream = resources.openRawResource(R.raw.charlie);
                            break;
                        case "delta":
                            stream = resources.openRawResource(R.raw.delta);
                            break;
                        case "echo":
                            stream = resources.openRawResource(R.raw.echo);
                            break;
                        //case "foxtrot":
                            //stream = resources.openRawResource(R.raw.foxtrot);
                            //break;
                        default:
                            stream = null;
                            Log.i("Deelgebied", "No polygon data was found for " + current.name);
                            break;
                    }

                    if (stream != null) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder total = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                total.append(line).append('\n');
                            }
                        } catch (IOException err) {
                            Log.e("Deelgebied", "Error occurred while reading stream", err);
                        }

                        String data = total.toString();

                        current.coordinates = new Gson().fromJson(data, new TypeToken<ArrayList<LatLng>>() {
                        }.getType());
                    }
                }
                deelgebiedenInitialized = true;
                for (Map.Entry<String, LinkedList<OnInitialized>> entry: onInitializedList.entrySet()){
                    for (OnInitialized oi : entry.getValue()){
                        oi.onInitialized(Objects.requireNonNull(Deelgebied.parse(entry.getKey())));
                    }
                }
            }

            @Override
            public void onSucces(KmlFile kml) {
                Deelgebied[] gebieden = all();
                Deelgebied current;
                for(int g = 0; g < gebieden.length; g++) {
                    current = gebieden[g];

                    KmlDeelgebied kmldg;
                    switch (current.name) {
                        case "alpha":
                            kmldg = kml.getAlpha();
                            break;
                        case "bravo":
                            kmldg = kml.getBravo();
                            break;
                        case "charlie":
                            kmldg = kml.getCharlie();
                            break;
                        case "delta":
                            kmldg = kml.getDelta();
                            break;
                        case "echo":
                            kmldg = kml.getEcho();
                            break;
                        case "foxtrot":
                            kmldg = kml.getFoxtrot();
                            break;
                        default:
                            kmldg = null;
                            break;
                    }

                    if (kmldg != null) {

                        ArrayList<LatLng> tmp = new ArrayList<>(kmldg.getBoundry().size());
                        for (int i = 0; i < kmldg.getBoundry().size(); i++) {
                            KmlLocation kmldgLoc = kmldg.getBoundry().get(i);
                            LatLng tmploc = new LatLng(kmldgLoc.lat, kmldgLoc.lon);
                            while (tmp.size()-1 < i){
                                tmp.add(null);
                            }
                            tmp.set(i, tmploc);
                        }
                        current.coordinates = tmp;
                    } else {
                        Log.i("Deelgebied", "No polygon data was found for " + current.name);
                    }
                }
                deelgebiedenInitialized = true;
                for (Map.Entry<String, LinkedList<OnInitialized>> entry: onInitializedList.entrySet()){
                    for (OnInitialized oi : entry.getValue()){
                        oi.onInitialized(Objects.requireNonNull(Deelgebied.parse(entry.getKey())));
                    }
                }
            }
        });
        waitUntilInitzialized();
    }

    /**
     * Resolves the Deelgebied on the location.
     *
     * @param location The location where the Deelgebied can be resolved on.
     * @return The Deelgebied resolved from the location, returns null if none.
     */
    public static Deelgebied resolveOnLocation(LatLng location)
    {
        waitUntilInitzialized();
        Deelgebied[] data = all();
        Deelgebied current;

        for(int i = 0; i < data.length; i++)
        {
            current = data[i];
            if(current.containsLocation(location))
            {
                return current;
            }
        }
        return null;
    }

    /**
     * Resolves the Deelgebied on the location.
     *
     * @param location The location where the Deelgebied can be resolved on.
     * @return The Deelgebied resolved from the location, returns null if none.
     */
    public static Deelgebied resolveOnLocation(Location location)
    {
        waitUntilInitzialized();
        Deelgebied[] data = all();
        Deelgebied current;

        for(int i = 0; i < data.length; i++)
        {
            current = data[i];
            if(current.containsLocation(location))
            {
                return current;
            }
        }
        return null;
    }
    public static synchronized void waitUntilInitzialized(){
        /*
        while (!deelgebiedenInitialized){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
    public static Deelgebied parse(String name) {
        waitUntilInitzialized();
        switch (name.toLowerCase()){
            case "alpha":
            case "Alpha":
            case "a":
            case "A":
                return Alpha;
            case "bravo":
            case "Bravo":
            case "b":
            case "B":
                return Bravo;
            case "charlie":
            case "Charlie":
            case "c":
            case "C":
                return Charlie;
            case "delta":
            case "Delta":
            case "d":
            case "D":
                return Delta;
            case "echo":
            case "Echo":
            case "e":
            case "E":
                return Echo;
            case "foxtrot":
            case "Foxtrot":
            case "f":
            case "F":
                return Foxtrot;
            case "xray":
            case "Xray":
            case "X-ray":
            case "x-ray":
            case "x":
            case "X":
                return Xray;
            default:
                return null;
        }
    }
    public interface OnInitialized {
        void onInitialized(Deelgebied deelgebied);
    }
}
