package nl.rsdt.japp.jotial.maps.window;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.HashMap;

import nl.rsdt.japp.R;
import nl.rsdt.japp.application.JappPreferences;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowCloseListener {

    private LayoutInflater inflater;

    private GoogleMap googleMap;

    private ScoutingGroepClickSession session;

    public CustomInfoWindowAdapter(LayoutInflater inflater, GoogleMap googleMap) {
        this.inflater = inflater;
        this.googleMap = googleMap;
        this.googleMap.setOnInfoWindowCloseListener(this);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Context context = inflater.getContext();
        View view = inflater.inflate(R.layout.custom_info_window, null);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.custom_info_window_text_fields);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.rightMargin = 10;

        ImageView indicator = (ImageView) view.findViewById(R.id.custom_info_window_indicator_image);

        boolean create = false;
        boolean end = false;

        MarkerIdentifier identifier = new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class);
        if(identifier != null) {

            StringBuilder text = new StringBuilder();

            TextView buffer = new TextView(context);
            buffer.setTextColor(Color.DKGRAY);
            buffer.setLayoutParams(params);

            HashMap<String, String> properties = identifier.getProperties();
            switch (identifier.getType()) {
                case MarkerIdentifier.TYPE_VOS:
                    text.append(properties.get("note"));
                    text.append("\n");
                    text.append(properties.get("extra"));
                    text.append("\n");
                    text.append(properties.get("time"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
                case MarkerIdentifier.TYPE_FOTO:
                    text.append(properties.get("info"));
                    text.append("\n");
                    text.append(properties.get("extra"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
                case MarkerIdentifier.TYPE_HUNTER:
                    text.append(properties.get("hunter"));
                    text.append("\n");
                    text.append(properties.get("time"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
                case MarkerIdentifier.TYPE_SC:
                    text.append(properties.get("name"));
                    text.append("\n");
                    text.append(properties.get("adres"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22));

                    if(session != null) {
                        if(!session.getType().equals(MarkerIdentifier.TYPE_SC)) {
                            end = true;
                            create = true;
                        } else {
                            end = true;
                        }
                    } else {
                        create = true;
                    }
                    break;
                case MarkerIdentifier.TYPE_SC_CLUSTER:
                    text.append(properties.get("size"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22));
                    break;
                case MarkerIdentifier.TYPE_SIGHTING:
                    text.append(properties.get("text"));
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
                case MarkerIdentifier.TYPE_PIN:
                    text.append(properties.get("title"));
                    text.append("\n");
                    text.append(properties.get("description"));
                    text.append("\n");
                    text.append(R.string.keep_pressed_to_remove);
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
                case MarkerIdentifier.TYPE_NAVIGATE:
                    text.append(R.string.navigate_to_here);
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.binoculars));
                    break;
                case MarkerIdentifier.TYPE_NAVIGATE_CAR:
                    text.append(R.string.navigation_phone_navigates_here);
                    text.append("\n");
                    text.append("geplaatst door: ");
                    text.append(properties.get("addedBy"));
                    //todo voeg leuk icoon toe
                    break;
                case MarkerIdentifier.TYPE_ME:
                    String name = JappPreferences.getHuntname();
                    if(name.isEmpty()) {
                        name = JappPreferences.getAccountUsername();
                    }

                    text.append(name);
                    text.append("\n");

                    indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                    break;
            }

            buffer.setText(text.toString());
            layout.addView(buffer);

            if(end) {
                if(session != null) {
                    session.end();
                    session = null;
                }
            }

            if(create) {
                session = new ScoutingGroepClickSession.Builder()
                        .setMarker(marker)
                        .setGoogleMap(googleMap)
                        .create();
                session.start();
            }
        }

        return view;
    }

    private TextView createTextView(Context context, ViewGroup.LayoutParams params, String text) {
        TextView buffer = new TextView(context);
        buffer.setText(text);
        buffer.setTextColor(Color.DKGRAY);
        buffer.setLayoutParams(params);
        return buffer;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        if(session != null) {
            session.end();
            session = null;
        }
    }
}
