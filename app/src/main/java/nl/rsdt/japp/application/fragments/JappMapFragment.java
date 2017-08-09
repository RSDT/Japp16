package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.data.bodies.VosPostBody;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.movement.MovementManager;
import nl.rsdt.japp.jotial.maps.pinning.Pin;
import nl.rsdt.japp.jotial.maps.pinning.PinningManager;
import nl.rsdt.japp.jotial.maps.pinning.PinningSession;
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon;
import nl.rsdt.japp.jotial.maps.sighting.SightingSession;
import nl.rsdt.japp.jotial.maps.wrapper.JotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.Polygon;
import nl.rsdt.japp.jotial.net.apis.VosApi;
import nl.rsdt.japp.service.LocationService;
import nl.rsdt.japp.service.ServiceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 8-7-2016
 * Description...
 */
public class JappMapFragment extends Fragment implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "JappMapFragment";

    private static final String BUNDLE_MAP = "BUNDLE_MAP";

    private static final String BUNDLE_OSM_ACTIVE = "BUNDLE_OSM_ACTIVE_B";

    private ServiceManager<LocationService, LocationService.LocationBinder> serviceManager = new ServiceManager<>(LocationService.class);

    private MapView googleMapView;
    private JotiMap jotiMap;

    public JotiMap getJotiMap() {
        return jotiMap;
    }

    private nl.rsdt.japp.jotial.maps.wrapper.OnMapReadyCallback callback;

    private PinningManager pinningManager = new PinningManager();

    private MovementManager movementManager = new MovementManager();

    private HashMap<String, Polygon> areas = new HashMap<>();

    private boolean osmActive = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pinningManager.intialize(getActivity());
        pinningManager.onCreate(savedInstanceState);

        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        boolean useOSM = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("pref_advanced_osm",false);
        if  (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_OSM_ACTIVE)) {
            if (useOSM != savedInstanceState.getBoolean(BUNDLE_OSM_ACTIVE)){
                savedInstanceState = null;
            }
        }

        if (useOSM){
            return createOSMMap(savedInstanceState, v);
        }else {
            return createGoogleMap(savedInstanceState, v);
        }
    }

    private View createOSMMap(Bundle savedInstanceState, View v) {
        //todo check
        osmActive = true;
        googleMapView = (MapView)v.findViewById(R.id.googleMap);
        googleMapView.setVisibility(View.GONE);
        org.osmdroid.views.MapView osmView = (org.osmdroid.views.MapView) v.findViewById(R.id.osmMap);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        osmView.setTileSource(TileSourceFactory.MAPNIK);
        osmView.getController().setCenter(new GeoPoint(51.958852, 5.954517));
        osmView.getController().setZoom(11);
        osmView.setBuiltInZoomControls(true);
        osmView.setMultiTouchControls(true);
        jotiMap = JotiMap.getJotiMapInstance(osmView);

        movementManager.setSnackBarView(osmView);
        setupHuntButton(v).setEnabled(false);
        setupSpotButton(v).setEnabled(false);
        setupPinButton(v).setEnabled(false);
        setupFollowButton(v);

        return v;
    }

    private View createGoogleMap(Bundle savedInstanceState, View v){
        osmActive = false;
        googleMapView = (MapView)v.findViewById(R.id.googleMap);
        org.osmdroid.views.MapView osmMapView = (org.osmdroid.views.MapView) v.findViewById(R.id.osmMap);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        osmMapView.setVisibility(View.GONE);
        if(savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_MAP))
        {
            googleMapView.onCreate(savedInstanceState.getBundle(BUNDLE_MAP));
        }
        else
        {
            googleMapView.onCreate(savedInstanceState);
        }
        movementManager.setSnackBarView(googleMapView);
        setupHuntButton(v);
        setupSpotButton(v);
        setupPinButton(v);
        setupFollowButton(v);

        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        pinningManager.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(BUNDLE_OSM_ACTIVE, osmActive);
        if (!osmActive) {
            Bundle mapBundle = new Bundle();
            googleMapView.onSaveInstanceState(mapBundle);

            savedInstanceState.putBundle(BUNDLE_MAP, mapBundle);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void getMapAsync(nl.rsdt.japp.jotial.maps.wrapper.OnMapReadyCallback callback) {
        if (osmActive){
            onMapReady(jotiMap);
        }else {
            MapView view = (MapView) getView().findViewById(R.id.googleMap);
            view.getMapAsync(this);
        }
        this.callback = callback;
    }

    public void onStart()  {
        super.onStart();
        if (!osmActive) {
            googleMapView.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        if (!osmActive) {
            googleMapView.onStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!osmActive) {
            googleMapView.onResume();
        }else {
            Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        }
        serviceManager.add(movementManager);
        movementManager.onResume();
        if(!serviceManager.isBound()) {
            serviceManager.bind(this.getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!osmActive) {
            googleMapView.onPause();
        }
        movementManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!osmActive) {
            googleMapView.onDestroy();
        }
        if(movementManager != null) {
            movementManager.onDestroy();
            serviceManager.remove(movementManager);
            movementManager = null;
        }

        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);

        serviceManager.unbind(getActivity());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!osmActive) {
            googleMapView.onLowMemory();
        }
    }

    public void onMapReady(JotiMap jotiMap){
        jotiMap.clear();

        movementManager.onMapReady(jotiMap);


        Deelgebied[] all = Deelgebied.all();
        Deelgebied current;
        for(int i = 0; i < all.length; i++)
        {
            current = all[i];

            PolygonOptions options = new PolygonOptions().addAll(current.getCoordinates());
            if(JappPreferences.getAreasColorEnabled()) {
                int alphaPercent = JappPreferences.getAreasColorAlpha();
                float alpha = ((float)(100 - alphaPercent))/100 * 255;
                options.fillColor(current.alphaled(Math.round(alpha)));
            } else {
                options.fillColor(Color.TRANSPARENT);
            }

            options.strokeColor(current.getColor());
            if(JappPreferences.getAreasEdgesEnabled()) {
                options.strokeWidth(JappPreferences.getAreasEdgesWidth());
            } else {
                options.strokeWidth(0);
            }

            areas.put(current.getName(), jotiMap.addPolygon(options));
        }

        pinningManager.onMapReady(jotiMap);
        if(callback != null)
        {
            callback.onMapReady(jotiMap);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.jotiMap = JotiMap.getJotiMapInstance(googleMap);
        onMapReady(jotiMap);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Polygon polygon;
        switch (key){
            case JappPreferences.AREAS_EDGES:
                boolean edges = JappPreferences.getAreasEdgesEnabled();
                for(HashMap.Entry<String, Polygon> pair : areas.entrySet()){
                    polygon = pair.getValue();
                    if(edges) {
                        polygon.setStrokeWidth(JappPreferences.getAreasEdgesWidth());
                    } else {
                        polygon.setStrokeWidth(0);
                    }
                }
                break;
            case JappPreferences.AREAS_EDGES_WIDTH:
                boolean edgesEnabled = JappPreferences.getAreasEdgesEnabled();
                for(HashMap.Entry<String, Polygon> pair : areas.entrySet()){
                    polygon = pair.getValue();
                    if(edgesEnabled) {
                        polygon.setStrokeWidth(JappPreferences.getAreasEdgesWidth());
                    }
                }
                break;
            case JappPreferences.AREAS_COLOR:
                boolean color = JappPreferences.getAreasColorEnabled();
                for(HashMap.Entry<String, Polygon> pair : areas.entrySet()){
                    polygon = pair.getValue();
                    if(color) {
                        int alphaPercent = JappPreferences.getAreasColorAlpha();
                        float alpha = ((float)(100 - alphaPercent))/100 * 255;
                        polygon.setFillColor(Deelgebied.parse(pair.getKey()).alphaled(Math.round(alpha)));
                    } else {
                        polygon.setFillColor(Color.TRANSPARENT);
                    }
                }
                break;
            case JappPreferences.AREAS_COLOR_ALPHA:
                boolean areasColorEnabled = JappPreferences.getAreasColorEnabled();
                for(HashMap.Entry<String, Polygon> pair : areas.entrySet()){
                    polygon = pair.getValue();
                    if(areasColorEnabled) {
                        int alphaPercent = JappPreferences.getAreasColorAlpha();
                        float alpha = ((float)(100 - alphaPercent))/100 * 255;
                        polygon.setFillColor(Deelgebied.parse(pair.getKey()).alphaled(Math.round(alpha)));
                    }
                }
                break;
        }
    }

    public FloatingActionButton setupSpotButton(View v) {
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
                        .setType(SightingSession.SIGHT_SPOT)
                        .setGoogleMap(jotiMap)
                        .setTargetView(JappMapFragment.this.getActivity().findViewById(R.id.container))
                        .setDialogContext(JappMapFragment.this.getActivity())
                        .setOnSightingCompletedCallback(new SightingSession.OnSightingCompletedCallback() {
                            @Override
                            public void onSightingCompleted(LatLng chosen, Deelgebied deelgebied, String optionalInfo) {

                                /*--- Show the menu ---*/
                                FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                menu.showMenu(true);

                                if(chosen != null)
                                {
                                    /*--- Construct a JSON string with the data ---*/
                                    VosPostBody builder = VosPostBody.getDefault();
                                    builder.setIcon(SightingIcon.SPOT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam(deelgebied.getName().substring(0, 1));
                                    builder.setInfo(optionalInfo);

                                    VosApi api = Japp.getApi(VosApi.class);
                                    api.post(builder).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                            switch (response.code()) {
                                                case 200:
                                                    Snackbar.make(snackbarView, "Succesvol verzonden", Snackbar.LENGTH_LONG).show();
                                                    break;
                                                case 404:
                                                    Snackbar.make(snackbarView, "Verkeerde gegevens", Snackbar.LENGTH_LONG).show();
                                                    break;
                                                default:
                                                    Snackbar.make(snackbarView, "Probleem bij verzenden: " + response.code(), Snackbar.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                            Snackbar.make(snackbarView, "Probleem bij verzenden: "  + t.toString() , Snackbar.LENGTH_LONG).show();
                                        }
                                    });

                                    /**
                                     * TODO: send details?
                                     * Log the spot in firebase.
                                     * */
                                    Japp.getAnalytics().logEvent("EVENT_SPOT", new Bundle());
                                }
                                session = null;
                            }
                        })
                        .create();
                session.start();
            }
        });
        return spotButton;
    }

    public FloatingActionButton setupFollowButton(View v) {
        FloatingActionButton followButton = (FloatingActionButton) v.findViewById(R.id.fab_follow);
        followButton.setOnClickListener(new View.OnClickListener() {

            MovementManager.FollowSession session;

            @Override
            public void onClick(View view) {
                View v = getView();

                FloatingActionButton followButton = (FloatingActionButton) v.findViewById(R.id.fab_follow);

                /*--- Hide the menu ---*/
                FloatingActionMenu menu = (FloatingActionMenu) v.findViewById(R.id.fab_menu);

                /**
                 * TODO: use color to identify follow state?
                 * */
                if (session != null) {
                    // followButton.setColorNormal(Color.parseColor("#DA4336"));
                    followButton.setLabelText("Volg mij");
                    session.end();
                    session = null;
                } else {
                    menu.close(true);
                    //followButton.setColorNormal(Color.parseColor("#5cd65c"));
                    followButton.setLabelText("Stop volgen");
                    session = movementManager.newSession(jotiMap.getCameraPosition(), JappPreferences.getFollowZoom(), JappPreferences.getFollowAngleOfAttack());
                }
            }

        });
        return followButton;
    }
    private FloatingActionButton setupPinButton(View v) {
        FloatingActionButton pinButton = (FloatingActionButton)v.findViewById(R.id.fab_mark);
        pinButton.setOnClickListener(new View.OnClickListener() {

            PinningSession session;

            @Override
            public void onClick(View view) {
                View v = getView();

                FloatingActionMenu menu = (FloatingActionMenu) v.findViewById(R.id.fab_menu);

                if(session != null) {
                    /*--- Show the menu ---*/
                    menu.showMenu(true);
                    session.end();
                    session = null;
                } else {
                    /*--- Hide the menu ---*/
                    menu.hideMenu(true);

                    session = new PinningSession.Builder()
                            .setGoogleMap(jotiMap)
                            .setCallback(new PinningSession.OnPinningCompletedCallback() {
                                @Override
                                public void onPinningCompleted(Pin pin) {
                                    if(pin != null) {
                                        pinningManager.add(pin);
                                    }

                                    FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                    menu.showMenu(true);

                                    session.end();
                                    session = null;
                                }
                            })
                            .setTargetView(JappMapFragment.this.getActivity().findViewById(R.id.container))
                            .setDialogContext(JappMapFragment.this.getActivity())
                            .create();
                    session.start();
                }

            }
        });
        return pinButton;
    }

    private FloatingActionButton setupHuntButton(View v){
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
                        .setGoogleMap(jotiMap)
                        .setTargetView(JappMapFragment.this.getActivity().findViewById(R.id.container))
                        .setDialogContext(JappMapFragment.this.getActivity())
                        .setOnSightingCompletedCallback(new SightingSession.OnSightingCompletedCallback() {
                            @Override
                            public void onSightingCompleted(LatLng chosen, Deelgebied deelgebied, String optionalInfo) {

                                /*--- Show the menu ---*/
                                FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                menu.showMenu(true);

                                if(chosen != null)
                                {
                                     /*--- Construct a JSON string with the data ---*/
                                    VosPostBody builder = VosPostBody.getDefault();
                                    builder.setIcon(SightingIcon.HUNT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam(deelgebied.getName().substring(0, 1));
                                    builder.setInfo(optionalInfo);

                                    VosApi api = Japp.getApi(VosApi.class);
                                    api.post(builder).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                            switch (response.code()) {
                                                case 200:
                                                    Snackbar.make(snackbarView, "Succesvol verzonden", Snackbar.LENGTH_LONG).show(); //// TODO: 08/08/17 magic string
                                                    break;
                                                case 404:
                                                    Snackbar.make(snackbarView, "Verkeerde gegevens", Snackbar.LENGTH_LONG).show(); //// TODO: 08/08/17 magic string
                                                    break;
                                                default:
                                                    Snackbar.make(snackbarView, "Probleem bij verzenden: " + response.code(), Snackbar.LENGTH_LONG).show(); //// TODO: 08/08/17 magic string
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                            Snackbar.make(snackbarView, "Probleem bij verzenden: "  + t.toString() , Snackbar.LENGTH_LONG).show();//// TODO: 08/08/17 magic string
                                        }
                                    });

                                    /**
                                     * TODO: send details?
                                     * Log the hunt in firebase.
                                     * */
                                    Japp.getAnalytics().logEvent("EVENT_HUNT", new Bundle()); //// TODO: 08/08/17 magic string
                                }
                            }
                        })
                        .create();
                session.start();
            }
        });
        return huntButton;
    }
}
