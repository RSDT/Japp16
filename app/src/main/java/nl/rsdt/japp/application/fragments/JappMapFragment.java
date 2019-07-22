package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.availability.StoragePermissionsChecker;
import nl.rsdt.japp.jotial.data.bodies.VosPostBody;
import nl.rsdt.japp.jotial.data.firebase.Location;
import nl.rsdt.japp.jotial.data.structures.area348.AutoInzittendeInfo;
import nl.rsdt.japp.jotial.maps.NavigationLocationManager;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;
import nl.rsdt.japp.jotial.maps.movement.MovementManager;
import nl.rsdt.japp.jotial.maps.pinning.Pin;
import nl.rsdt.japp.jotial.maps.pinning.PinningManager;
import nl.rsdt.japp.jotial.maps.pinning.PinningSession;
import nl.rsdt.japp.jotial.maps.sighting.SightingIcon;
import nl.rsdt.japp.jotial.maps.sighting.SightingSession;
import nl.rsdt.japp.jotial.maps.wrapper.IJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.IMarker;
import nl.rsdt.japp.jotial.maps.wrapper.IPolygon;
import nl.rsdt.japp.jotial.maps.wrapper.google.GoogleJotiMap;
import nl.rsdt.japp.jotial.maps.wrapper.osm.OsmJotiMap;
import nl.rsdt.japp.jotial.navigation.NavigationSession;
import nl.rsdt.japp.jotial.net.apis.AutoApi;
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
public class JappMapFragment extends Fragment implements IJotiMap.OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = "JappMapFragment";

    private static final String BUNDLE_MAP = "BUNDLE_MAP";

    private static final String BUNDLE_OSM_ACTIVE = "BUNDLE_OSM_ACTIVE_B";
    private static final String OSM_ZOOM = "OSM_ZOOM";
    private static final String OSM_LAT = "OSM_LAT";
    private static final java.lang.String OSM_LNG = "OSM_LNG";
    private static final String OSM_OR = "OSM_OR";
    private static final String OSM_BUNDLE = "OSM_BUNDLE";

    public static final int REQUEST_CHECK_SETTINGS = 32;

    private ServiceManager<LocationService, LocationService.LocationBinder> serviceManager = new ServiceManager<>(LocationService.class);

    private IJotiMap jotiMap;
    private MapView googleMapView;
    private NavigationLocationManager navigationLocationManager ;

    public IJotiMap getJotiMap() {
        return jotiMap;
    }

    private IJotiMap.OnMapReadyCallback callback;

    private PinningManager pinningManager = new PinningManager();

    private MovementManager movementManager = new MovementManager();

    private HashMap<String, IPolygon> areas = new HashMap<>();

    public MovementManager getMovementManager() {
        return movementManager;
    }

    private boolean osmActive = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLocationManager = new NavigationLocationManager();
        JappPreferences.getVisiblePreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pinningManager.intialize(getActivity());
        pinningManager.onCreate(savedInstanceState);

        movementManager.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return createMap(savedInstanceState, v);
    }

    private View createMap(Bundle savedInstanceState, View v){
        boolean useOSM = JappPreferences.useOSM();

        View view;
        if (useOSM){
            view = createOSMMap(savedInstanceState, v);
        }else {
            view = createGoogleMap(savedInstanceState, v);
        }

        FloatingActionMenu menu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        menu.bringToFront();

        return view;
    }

    private View createOSMMap(Bundle savedInstanceState, View v) {
        StoragePermissionsChecker.check(getActivity());
        osmActive = true;

        org.osmdroid.views.MapView osmView = new org.osmdroid.views.MapView(getActivity());
        ((ViewGroup)v).addView(osmView);

        Context ctx = getActivity().getApplication();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        switch (JappPreferences.getOsmMapSource()){
            case Mapnik:
                osmView.setTileSource(TileSourceFactory.MAPNIK);
                break;
            case OpenSeaMap:
                osmView.setTileSource(TileSourceFactory.OPEN_SEAMAP);
                break;
            case HikeBike:
                osmView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
                break;
            case OpenTopo:
                osmView.setTileSource(TileSourceFactory.OpenTopo);
                break;
            case Fiets_NL:
                osmView.setTileSource(TileSourceFactory.FIETS_OVERLAY_NL);
                break;
            case Default:
                osmView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                break;
            case CloudMade_Normal:
                osmView.setTileSource(TileSourceFactory.CLOUDMADESTANDARDTILES);
                break;
            case CloudMade_Small:
                osmView.setTileSource(TileSourceFactory.CLOUDMADESMALLTILES);
                break;
            case ChartBundle_ENRH:
                osmView.setTileSource(TileSourceFactory.ChartbundleENRH);
                break;
            case ChartBundle_ENRL:
                osmView.setTileSource(TileSourceFactory.ChartbundleENRH);
                break;
            case ChartBundle_WAC:
                osmView.setTileSource(TileSourceFactory.ChartbundleWAC);
                break;
            case USGS_Sat:
                osmView.setTileSource(TileSourceFactory.USGS_SAT);
                break;
            case USGS_Topo:
                osmView.setTileSource(TileSourceFactory.USGS_TOPO);
                break;
            case Public_Transport:
                osmView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
                break;
            case Road_NL:
                osmView.setTileSource(TileSourceFactory.ROADS_OVERLAY_NL);
                break;
            case Base_NL:
                osmView.setTileSource(TileSourceFactory.BASE_OVERLAY_NL);
                break;
            default:
                osmView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                break;
        }
        osmView.getController().setCenter(new GeoPoint(51.958852, 5.954517));
        osmView.getController().setZoom(11);
        osmView.setBuiltInZoomControls(true);
        osmView.setMultiTouchControls(true);
        osmView.setFlingEnabled(true);
        osmView.setTilesScaledToDpi(true);

        if (savedInstanceState != null) {
            Bundle osmbundle = savedInstanceState.getBundle(OSM_BUNDLE);
            if (osmbundle != null) {
                osmView.getController().setZoom(osmbundle.getInt(OSM_ZOOM));
                osmView.getController().setCenter(new GeoPoint(osmbundle.getDouble(OSM_LAT), osmbundle.getDouble(OSM_LNG)));
                osmView.setRotation(osmbundle.getFloat(OSM_OR));
            }
        }

        movementManager.setSnackBarView(osmView);
        setupHuntButton(v).setEnabled(true);
        setupSpotButton(v).setEnabled(true);
        setupPinButton(v).setEnabled(true);
        setupFollowButton(v);
        setupNavigationButton(v);
        jotiMap = OsmJotiMap.getJotiMapInstance(osmView);
        return v;
    }

    private View createGoogleMap(Bundle savedInstanceState, View v){
        osmActive = false;
        googleMapView = new MapView(getActivity());
        ((ViewGroup)v).addView(googleMapView);

        Context ctx = getActivity().getApplication();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey(BUNDLE_MAP)) {
                googleMapView.onCreate(savedInstanceState.getBundle(BUNDLE_MAP));
            } else {
                googleMapView.onCreate(null);
            }
        } else
        {
            googleMapView.onCreate(null);
        }
        movementManager.setSnackBarView(googleMapView);
        setupHuntButton(v);
        setupSpotButton(v);
        setupPinButton(v);
        setupFollowButton(v);
        setupNavigationButton(v);
        jotiMap = GoogleJotiMap.getJotiMapInstance(googleMapView);
        if(savedInstanceState != null) {
            Bundle osmbundle = savedInstanceState.getBundle(OSM_BUNDLE);
            if (osmbundle != null) {
                jotiMap.setPreviousZoom(osmbundle.getInt(OSM_ZOOM));
                jotiMap.setPreviousCameraPosition(osmbundle.getDouble(OSM_LAT), osmbundle.getDouble(OSM_LNG));
                jotiMap.setPreviousRotation(osmbundle.getFloat(OSM_OR));
            }
        }
        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        movementManager.onSaveInstanceState(savedInstanceState);
        pinningManager.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(BUNDLE_OSM_ACTIVE, osmActive);
        if (!osmActive) {
            Bundle mapBundle = new Bundle();
            googleMapView.onSaveInstanceState(mapBundle);

            savedInstanceState.putBundle(BUNDLE_MAP, mapBundle);
        }else if (jotiMap instanceof OsmJotiMap){
            org.osmdroid.views.MapView osmMap = ((OsmJotiMap) jotiMap).getOSMMap();
            Bundle osmMapBundle = new Bundle();
            osmMapBundle.putInt(OSM_ZOOM, osmMap.getZoomLevel());
            osmMapBundle.putDouble(OSM_LAT, osmMap.getMapCenter().getLatitude());
            osmMapBundle.putDouble(OSM_LNG, osmMap.getMapCenter().getLongitude());
            osmMapBundle.putFloat(OSM_OR, osmMap.getMapOrientation());
            savedInstanceState.putBundle(OSM_BUNDLE, osmMapBundle);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void getMapAsync(IJotiMap.OnMapReadyCallback callback) {
        jotiMap.getMapAsync(this);
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
        movementManager.setListener(new LocationService.OnResolutionRequiredListener() {
            @Override
            public void onResolutionRequired(Status status) {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            getActivity(),
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
            }
        });
        serviceManager.add(movementManager);
        movementManager.onResume();
        if(!serviceManager.isBound()) {
            serviceManager.bind(this.getActivity());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!osmActive && googleMapView != null) {
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

    public void onMapReady(IJotiMap jotiMap){
        this.jotiMap = jotiMap;
        jotiMap.clear();

        movementManager.onMapReady(jotiMap);

        setupDeelgebieden();

        pinningManager.onMapReady(jotiMap);
        if(callback != null)
        {
            callback.onMapReady(jotiMap);
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(0,0))
                .visible(false);
        Bitmap icon = null;
        final IMarker marker = jotiMap.addMarker(new Pair<MarkerOptions, Bitmap>(markerOptions, icon));
        navigationLocationManager.setCallback(new NavigationLocationManager.OnNewLocation() {
            @Override
            public void onNewLocation(Location location) {
                MarkerIdentifier.Builder identifier = new MarkerIdentifier.Builder();
                identifier.setType(MarkerIdentifier.TYPE_NAVIGATE_CAR);

                identifier.add("addedBy", location.createdBy);
                identifier.add("createdOn", String.valueOf(location.createdOn));
                marker.setTitle(new Gson().toJson(identifier.create()));
                marker.setVisible(true);
                marker.setPosition(new LatLng(location.lat,location.lon));
            }

            @Override
            public void onNotInCar() {
                marker.setVisible(false);
                marker.setPosition(new LatLng(0,0));
            }
        });
    }

    private void setupDeelgebieden() {
        Set<String> enabled = JappPreferences.getAreasEnabled();
        for(String area : enabled) {
            if(!areas.containsKey(area)) {
                setupDeelgebied(Deelgebied.parse(area));
            } else{ // vraag me niet hoe maar dit fixed #112
                IPolygon poly = areas.get(area);
                poly.remove();
                areas.remove(area);
                setupDeelgebied(Deelgebied.parse(area));
            }
        }
        if(jotiMap instanceof OsmJotiMap) {
            ((OsmJotiMap) jotiMap).getOSMMap().invalidate();
        }
    }

    public void setupDeelgebied(Deelgebied deelgebied) {
        if(!deelgebied.getCoordinates().isEmpty()) {
            PolygonOptions options = new PolygonOptions().addAll(deelgebied.getCoordinates());
            if(JappPreferences.getAreasColorEnabled()) {
                int alphaPercent = JappPreferences.getAreasColorAlpha();
                float alpha = ((float)(100 - alphaPercent))/100 * 255;
                options.fillColor(deelgebied.alphaled(Math.round(alpha)));
            } else {
                options.fillColor(Color.TRANSPARENT);
            }

            options.strokeColor(deelgebied.getColor());
            if(JappPreferences.getAreasEdgesEnabled()) {
                options.strokeWidth(JappPreferences.getAreasEdgesWidth());
            } else {
                options.strokeWidth(0);
            }
            options.visible(true);

            areas.put(deelgebied.getName(), jotiMap.addPolygon(options));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        IPolygon polygon;
        switch (key){
            case JappPreferences.USE_OSM:

                break;
            case JappPreferences.AREAS:
                if(jotiMap == null) break;
                Set<String> enabled = JappPreferences.getAreasEnabled();
                for(String area : enabled) {
                    if(!areas.containsKey(area)) {
                        setupDeelgebied(Deelgebied.parse(area));
                    }
                }

                List<String> toBeRemoved = new ArrayList<>();
                for(String area : areas.keySet()) {
                    if(!enabled.contains(area)) {
                        IPolygon poly = areas.get(area);
                        poly.remove();
                        toBeRemoved.add(area);
                    }
                }

                for(String area : toBeRemoved) {
                    areas.remove(area);
                }
                if(jotiMap instanceof OsmJotiMap) {
                    ((OsmJotiMap) jotiMap).getOSMMap().invalidate();
                }
                break;
            case JappPreferences.AREAS_EDGES:
                boolean edges = JappPreferences.getAreasEdgesEnabled();
                for(HashMap.Entry<String, IPolygon> pair : areas.entrySet()){
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
                for(HashMap.Entry<String, IPolygon> pair : areas.entrySet()){
                    polygon = pair.getValue();
                    if(edgesEnabled) {
                        polygon.setStrokeWidth(JappPreferences.getAreasEdgesWidth());
                    }
                }
                break;
            case JappPreferences.AREAS_COLOR:
                boolean color = JappPreferences.getAreasColorEnabled();
                for(HashMap.Entry<String, IPolygon> pair : areas.entrySet()){
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
                for(HashMap.Entry<String, IPolygon> pair : areas.entrySet()){
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
                    session = movementManager.newSession(jotiMap.getPreviousCameraPosition(), JappPreferences.getFollowZoom(), JappPreferences.getFollowAngleOfAttack());
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
                            .setJotiMap(jotiMap)
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

    private FloatingActionButton setupNavigationButton(View v) {
        FloatingActionButton navigationButton = (FloatingActionButton)v.findViewById(R.id.fab_nav);
        navigationButton.setOnClickListener(new View.OnClickListener() {

            NavigationSession session;

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

                    session = new NavigationSession.Builder()
                            .setJotiMap(jotiMap)
                            .setCallback(new NavigationSession.OnNavigationCompletedCallback() {
                                @Override
                                public void onNavigationCompleted(final LatLng navigateTo, boolean toNavigationPhone) {
                                    FloatingActionMenu menu = (FloatingActionMenu)getView().findViewById(R.id.fab_menu);
                                    menu.showMenu(true);

                                    session.end();
                                    session = null;
                                    if (navigateTo != null) {
                                        if (!toNavigationPhone) {
                                            try{
                                                switch(JappPreferences.navigationApp()){
                                                    case GoogleMaps:
                                                        String uristr = "google.navigation:q=" + Double.toString(navigateTo.latitude) + "," + Double.toString(navigateTo.longitude);
                                                        Uri gmmIntentUri = Uri.parse(uristr);
                                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                        mapIntent.setPackage("com.google.android.apps.maps");
                                                        startActivity(mapIntent);
                                                        break;
                                                    case Waze:
                                                        String uri = "waze://?ll="+Double.toString(navigateTo.latitude) +","+Double.toString(navigateTo.longitude) +"&navigate=yes";
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                                                        break;
                                                }
                                            } catch (ActivityNotFoundException e){
                                                System.out.println(e.toString());
                                                View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                                Snackbar.make(snackbarView, "De App: " + JappPreferences.navigationApp().toString() +" is niet geinstaleerd.", Snackbar.LENGTH_LONG).show(); //// TODO: 08/08/17 magic string
                                            }
                                        }else{
                                            int id = JappPreferences.getAccountId();
                                            if (id >= 0) {
                                                final AutoApi autoApi = Japp.getApi(AutoApi.class);
                                                autoApi.getInfoById(JappPreferences.getAccountKey(), id).enqueue(new Callback<AutoInzittendeInfo>() {
                                                    @Override
                                                    public void onResponse(Call<AutoInzittendeInfo> call, Response<AutoInzittendeInfo> response) {
                                                        if (response.code() == 200) {
                                                            AutoInzittendeInfo autoInfo = response.body();
                                                            if (autoInfo != null) {
                                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                DatabaseReference ref = database.getReference(NavigationLocationManager.FDB_NAME + "/" + autoInfo.autoEigenaar);
                                                                ref.setValue(new Location(navigateTo, JappPreferences.getAccountUsername()));
                                                            }
                                                        }
                                                        if (response.code() == 404){
                                                            View snackbarView = JappMapFragment.this.getActivity().findViewById(R.id.container);
                                                            Snackbar.make(snackbarView, "Fout: plaats jezelf eerst in een auto via telegram.", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<AutoInzittendeInfo> call, Throwable t) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                }
                            })
                            .setTargetView(JappMapFragment.this.getActivity().findViewById(R.id.container))
                            .setDialogContext(JappMapFragment.this.getActivity())
                            .create();
                    session.start();
                }

            }
        });
        return navigationButton;
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
