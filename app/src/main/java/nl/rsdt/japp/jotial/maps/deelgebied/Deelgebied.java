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

import nl.rsdt.japp.R;

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
    public static final Deelgebied Alpha = new Deelgebied("alpha", R.drawable.vos_a_4, R.drawable.vos_a_3, Color.argb(255, 255, 0, 0) );

    /**
     * Defines the Bravo Deelgebied.
     * */
    public static final Deelgebied Bravo = new Deelgebied("bravo", R.drawable.vos_b_4, R.drawable.vos_b_3, Color.argb(255, 0, 255, 0));

    /**
     * Defines the Charlie Deelgebied.
     * */
    public static final Deelgebied Charlie = new Deelgebied("charlie", R.drawable.vos_c_4, R.drawable.vos_c_3, Color.argb(255, 0, 0, 255));

    /**
     * Defines the Delta Deelgebied.
     * */
    public static final Deelgebied Delta = new Deelgebied("delta", R.drawable.vos_d_4, R.drawable.vos_d_3, Color.argb(255, 0, 255, 255));

    /**
     * Defines the Echo Deelgebied.
     * */
    public static final Deelgebied Echo = new Deelgebied("echo", R.drawable.vos_e_4, R.drawable.vos_e_3, Color.argb(255, 255, 0, 255));

    /**
     * Defines the Foxtrot Deelgebied.
     * */
    public static final Deelgebied Foxtrot = new Deelgebied("foxtrot", R.drawable.vos_f_4, R.drawable.vos_f_3, Color.argb(255, 255, 162, 0));

    /**
     * Defines the Xray Deelgebied.
     * */
    public static final Deelgebied Xray = new Deelgebied("xray", R.drawable.vos_x_4, R.drawable.vos_x_3, Color.argb(255, 0, 0, 0));

    /**
     * Gets a array of all the Deelgebieden.
     * */
    public static Deelgebied[] all() { return new Deelgebied[] { Alpha, Bravo, Charlie, Delta, Echo, Foxtrot, Xray }; }

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

    /**
     * Gets the list of coordinates that is the area of the Deelgebied.
     * */
    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

    /**
     * Initializes a new instance of Deelgebied.
     *
     * @param name The name of the Deelgebied.
     * @param drawable_hunt The hunt drawable of the Deelgebied.
     * @param drawable_spot The spot drawable of the Deelgebied.
     * @param color The color of the Deelgebied.
     * */
    private Deelgebied(String name, int drawable_hunt, int drawable_spot, int color)
    {
        this.name = name;
        this.drawable_hunt = drawable_hunt;
        this.drawable_spot = drawable_spot;
        this.color = color;
    }

    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     * */
    public boolean containsLocation(LatLng location)
    {
        return PolyUtil.containsLocation(location, coordinates, false);
    }

    /**
     * Checks if the DeelgebiedData contains a location.
     *
     * @param location The location to check.
     * @return Value indicating if the location is within the DeelgebiedData.
     * */
    public boolean containsLocation(Location location)
    {
        return PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), coordinates, false);
    }


    /**
     * Initializes the Deelgebieden, loading their coordinates from the Resources.
     * */
    public static void initialize(Resources resources)
    {
        Deelgebied[] gebieden = all();
        Deelgebied current;
        for(int g = 0; g < gebieden.length; g++)
        {
            current = gebieden[g];

            InputStream stream;
            switch (current.name)
            {
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
                case "foxtrot":
                    stream = resources.openRawResource(R.raw.foxtrot);
                    break;
                default:
                    stream = null;
                    break;
            }

            if(stream != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder total = new StringBuilder();
                String line;
                try {
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                } catch (IOException e)
                {
                    Log.e("Deelgebied", "Error occurred while reading stream", e);
                }

                String data = total.toString();

                current.coordinates = new Gson().fromJson(data, new TypeToken<ArrayList<LatLng>>() { }.getType());
            }
        }
    }

    /**
     * Resolves the Deelgebied on the location.
     *
     * @param location The location where the Deelgebied can be resolved on.
     * @return The Deelgebied resolved from the location, returns null if none.
     */
    public static Deelgebied resolveOnLocation(LatLng location)
    {
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

    public static Deelgebied parse(String name) {
        switch (name.toLowerCase()){
            case "alpha":
                return Alpha;
            case "bravo":
                return Bravo;
            case "charlie":
                return Charlie;
            case "delta":
                return Delta;
            case "echo":
                return Echo;
            case "foxtrot":
                return Foxtrot;
            case "xray":
                return Xray;
            default:
                return null;
        }
    }


}
