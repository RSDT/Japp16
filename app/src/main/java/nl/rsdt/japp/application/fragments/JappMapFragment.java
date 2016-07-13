package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.PolygonOptions;
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.builders.VosPostDataBuilder;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon;
import nl.rsdt.japp.jotial.maps.sighting.SightingSession;
import nl.rsdt.japp.jotial.net.ApiUrlBuilder;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class JappMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "JappMapFragment";

    private static final String BUNDLE_MAP = "BUNDLE_MAP";

    private MapView mapView;

    private GoogleMap googleMap;

    private OnMapReadyCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView)v.findViewById(R.id.map);

        if(savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_MAP))
        {
            mapView.onCreate(savedInstanceState.getBundle(BUNDLE_MAP));
        }
        else
        {
            mapView.onCreate(savedInstanceState);
        }


        FloatingActionButton huntButton = (FloatingActionButton)v.findViewById(R.id.fab_hunt);
        huntButton.setOnClickListener(new View.OnClickListener() {

            SightingSession session;

            @Override
            public void onClick(View view) {

                /*--- Hide the menu ---*/
                View v = getView();
                FloatingActionMenu menu = (FloatingActionMenu)v.findViewById(R.id.fab_menu);
                menu.hideMenu(true);

                /*--- Build a SightingSession and start it ---*/
                session = new SightingSession.Builder()
                        .setType(SightingSession.SIGHT_HUNT)
                        .setGoogleMap(googleMap)
                        .setTargetView(mapView)
                        .setOnSightingCompletedCallback(new SightingSession.OnSightingCompletedCallback() {
                            @Override
                            public void onSightingCompleted(LatLng chosen) {

                                /*--- Show the menu ---*/
                                FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                menu.showMenu(true);

                                if(chosen != null)
                                {
                                    /*--- Construct a JSON string with the data ---*/
                                    VosPostDataBuilder builder = VosPostDataBuilder.getDefault();
                                    builder.setIcon(SightingIcon.HUNT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam("a");
                                    builder.setInfo("testsfsf");
                                    String data = builder.build();

                                    /*--- Send a request to server ---*/
                                    WebRequest request = new WebRequest.Builder()
                                            .setUrl(new ApiUrlBuilder(false).append("vos").build())
                                            .setMethod(WebRequestMethod.POST)
                                            .setData(data)
                                            .create();
                                    request.executeAsync(new WebRequest.OnWebRequestCompletedCallback() {
                                        @Override
                                        public void onWebRequestCompleted(WebResponse response) {
                                            Log.i("", response.getData());
                                        }
                                    });
                                }

                                session = null;
                            }
                        })
                        .create();
                session.start();
            }
        });

        FloatingActionButton spotButton = (FloatingActionButton)v.findViewById(R.id.fab_spot);
        spotButton.setOnClickListener(new View.OnClickListener() {

            SightingSession session;

            @Override
            public void onClick(View view) {
                 /*--- Hide the menu ---*/
                View v = getView();
                FloatingActionMenu menu = (FloatingActionMenu)v.findViewById(R.id.fab_menu);
                menu.hideMenu(true);

                /*--- Build a SightingSession and start it ---*/
                session = new SightingSession.Builder()
                        .setType(SightingSession.SIGHT_HUNT)
                        .setGoogleMap(googleMap)
                        .setTargetView(mapView)
                        .setOnSightingCompletedCallback(new SightingSession.OnSightingCompletedCallback() {
                            @Override
                            public void onSightingCompleted(LatLng chosen) {

                                /*--- Show the menu ---*/
                                FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                menu.showMenu(true);

                                if(chosen != null)
                                {
                                    /*--- Construct a JSON string with the data ---*/
                                    VosPostDataBuilder builder = VosPostDataBuilder.getDefault();
                                    builder.setIcon(SightingIcon.SPOT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam("a");
                                    builder.setInfo("test");
                                    String data = builder.build();

                                    /*--- Send a request to server ---*/
                                    WebRequest request = new WebRequest.Builder()
                                            .setUrl(new ApiUrlBuilder(false).append("vos").build())
                                            .setMethod(WebRequestMethod.POST)
                                            .setData(data)
                                            .create();
                                    request.executeAsync(null);
                                }
                                session = null;
                            }
                        })
                        .create();
                session.start();
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Bundle mapBundle = new Bundle();
        mapView.onSaveInstanceState(mapBundle);

        savedInstanceState.putBundle(BUNDLE_MAP, mapBundle);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void getMapAsync(OnMapReadyCallback callback)
    {
        MapView view = (MapView)getView().findViewById(R.id.map);
        view.getMapAsync(this);
        this.callback = callback;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Deelgebied[] all = Deelgebied.all();
        Deelgebied current;
        for(int i = 0; i < all.length; i++)
        {
            current = all[i];

            googleMap.addPolygon(new PolygonOptions()
                    .fillColor(current.alphaled(90))
                    .strokeWidth(0)
                    .addAll(current.getCoordinates()));
        }

        if(callback != null)
        {
            callback.onMapReady(googleMap);
        }
    }
}
