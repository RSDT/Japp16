package nl.rsdt.japp.application.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.rsdt.anl.WebRequest;
import com.rsdt.anl.WebRequestMethod;
import com.rsdt.anl.WebResponse;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.Japp;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.auth.Authentication;
import nl.rsdt.japp.jotial.data.builders.VosPostDataBuilder;
import nl.rsdt.japp.jotial.maps.deelgebied.Deelgebied;
import nl.rsdt.japp.jotial.maps.movement.MovementManager;
import nl.rsdt.japp.jotial.maps.pinning.PinningManager;
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

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    private OnMapReadyCallback callback;

    private PinningManager pinningManager = new PinningManager();

    private MovementManager movementManager = new MovementManager();

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
                                    VosPostDataBuilder builder = VosPostDataBuilder.getDefault();
                                    builder.setIcon(SightingIcon.HUNT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam(deelgebied.getName().substring(0, 1));
                                    builder.setInfo(optionalInfo);
                                    String data = builder.build();

                                                    /*--- Send a request to server ---*/
                                    WebRequest request = new WebRequest.Builder()
                                            .setUrl(new ApiUrlBuilder(false).append("vos").buildAsUrl())
                                            .setMethod(WebRequestMethod.POST)
                                            .setData(data)
                                            .create();
                                    request.executeAsync(new WebRequest.OnWebRequestCompletedCallback() {
                                        @Override
                                        public void onWebRequestCompleted(WebResponse response) {
                                            switch (response.getResponseCode())
                                            {
                                                case 200:
                                                    Snackbar.make(mapView, "Succesvol verzonden", Snackbar.LENGTH_LONG).show();
                                                    break;
                                                case 404:
                                                    Snackbar.make(mapView, "Verkeerde gegevens", Snackbar.LENGTH_LONG).show();
                                                    Authentication.startLoginActivity(JappMapFragment.this.getActivity());
                                                    break;
                                                default:
                                                    Snackbar.make(mapView, "Probleem bij verzenden: " + response.getResponseCode(), Snackbar.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    });
                                }
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
                        .setType(SightingSession.SIGHT_SPOT)
                        .setGoogleMap(googleMap)
                        .setTargetView(mapView)
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
                                    VosPostDataBuilder builder = VosPostDataBuilder.getDefault();
                                    builder.setIcon(SightingIcon.SPOT);
                                    builder.setLatLng(chosen);
                                    builder.setTeam(deelgebied.getName().substring(0, 1));
                                    builder.setInfo(optionalInfo);
                                    String data = builder.build();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("team", deelgebied.getName());
                                    bundle.putString("info", optionalInfo);
                                    bundle.putParcelable("location", chosen);
                                    Japp.getAnalytics().logEvent("EVENT_SPOT", bundle);



                                    /*--- Send a request to server ---*/
                                    WebRequest request = new WebRequest.Builder()
                                            .setUrl(new ApiUrlBuilder(false).append("vos").buildAsUrl())
                                            .setMethod(WebRequestMethod.POST)
                                            .setData(data)
                                            .create();

                                    request.executeAsync(new WebRequest.OnWebRequestCompletedCallback() {
                                        @Override
                                        public void onWebRequestCompleted(WebResponse response) {
                                            switch (response.getResponseCode())
                                            {
                                                case 200:
                                                    Snackbar.make(mapView, "Succesvol verzonden", Snackbar.LENGTH_LONG).show();
                                                    break;
                                                case 404:
                                                    Snackbar.make(mapView, "Verkeerde gegevens", Snackbar.LENGTH_LONG).show();
                                                    Authentication.startLoginActivity(JappMapFragment.this.getActivity());
                                                    break;
                                                default:
                                                    Snackbar.make(mapView, "Probleem bij verzenden: " + response.getResponseCode(), Snackbar.LENGTH_LONG).show();
                                                    break;
                                            }
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


        FloatingActionButton pinButton = (FloatingActionButton)v.findViewById(R.id.fab_mark);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getView();

                /*--- Hide the menu ---*/
                FloatingActionMenu menu = (FloatingActionMenu)v.findViewById(R.id.fab_menu);
                menu.hideMenu(true);


            }
        });

        FloatingActionButton followButton = (FloatingActionButton)v.findViewById(R.id.fab_follow);
        followButton.setOnClickListener(new View.OnClickListener() {

            MovementManager.FollowSession session;

            @Override
            public void onClick(View view) {
                View v = getView();

                googleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {

                    }
                });
                FloatingActionButton followButton = (FloatingActionButton)v.findViewById(R.id.fab_follow);

                /*--- Hide the menu ---*/
                FloatingActionMenu menu = (FloatingActionMenu)v.findViewById(R.id.fab_menu);

                /**
                 * TODO: use color to identify follow state?
                 * */
                if(session != null) {
                   // followButton.setColorNormal(Color.parseColor("#DA4336"));
                    followButton.setLabelText("Volg mij");
                    session.end();
                    session = null;
                }
                else {
                    menu.close(true);
                    //followButton.setColorNormal(Color.parseColor("#5cd65c"));
                    followButton.setLabelText("Stop volgen");
                    session = movementManager.newSession(googleMap.getCameraPosition(), JappPreferences.getFollowZoom(), JappPreferences.getFollowAngleOfAttack());
                }
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

    public void getMapAsync(OnMapReadyCallback callback) {
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
        googleMap.clear();

        movementManager.onMapReady(googleMap);

        Deelgebied[] all = Deelgebied.all();
        Deelgebied current;
        for(int i = 0; i < all.length; i++)
        {
            current = all[i];

            googleMap.addPolygon(new PolygonOptions()
                    .fillColor(current.alphaled(60))
                    .strokeWidth(0)
                    .addAll(current.getCoordinates()));
        }

        pinningManager.onMapReady(googleMap);

        if(callback != null)
        {
            callback.onMapReady(googleMap);
        }
    }
}
