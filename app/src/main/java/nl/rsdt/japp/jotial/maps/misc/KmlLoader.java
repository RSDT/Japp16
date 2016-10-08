package nl.rsdt.japp.jotial.maps.misc;

import android.app.Activity;
import android.os.Environment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import nl.rsdt.japp.jotial.io.AppData;


/**
 * Created by Mattijn on 11/10/2015.
 */
public class KmlLoader {
    private final String DEELGEBIEDEN_OVERLAY_KEY = "pref_deelgebieden_overlay";
    private final int kmlfile;
    GoogleMap Gmap;
    public KmlLoader(GoogleMap gmap, int KmlFile){
        Gmap=gmap;
        this.kmlfile = KmlFile;
    }
    public void ReadKML(Activity activity){
        try {
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "deelgebieden");
            boolean created = directory.mkdir();
            KmlLayer kmllayer = new KmlLayer(Gmap, kmlfile, activity);
            for (KmlContainer temp : kmllayer.getContainers()){
                for (KmlContainer temp2: temp.getContainers()){
                    if (temp2.getProperty("name").equals("Deelgebieden")){
                        for (KmlPlacemark deelgebied : temp2.getPlacemarks()){
                            String name = String.valueOf(deelgebied.getProperty("name"));
                            KmlPolygon p = (KmlPolygon) deelgebied.getGeometry();
                            ArrayList<LatLng> coordinates = p.getOuterBoundaryCoordinates();
                            if(created) {
                                File file = new File(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS), "deelgebieden/" + name);
                                FileWriter fileWriter = new FileWriter(file);
                                fileWriter.write(new Gson().toJson(coordinates));
                                fileWriter.flush();
                                fileWriter.close();
                            }
                        }
                    }
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
