package nl.rsdt.japp.jotial.maps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import java.util.Map;

import nl.rsdt.japp.R;
import nl.rsdt.japp.jotial.data.structures.area348.BaseInfo;
import nl.rsdt.japp.jotial.data.structures.area348.VosInfo;
import nl.rsdt.japp.jotial.maps.management.MarkerIdentifier;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Context context = inflater.getContext();
        View view = inflater.inflate(R.layout.custom_info_window, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.custom_info_window_text_fields);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.rightMargin = 10;

        ImageView indicator = (ImageView) view.findViewById(R.id.custom_info_window_indicator_image);

        MarkerIdentifier identifier = new Gson().fromJson(marker.getTitle(), MarkerIdentifier.class);
        HashMap<String, String> properties = identifier.getProperties();
        switch (identifier.getType()) {
            case MarkerIdentifier.TYPE_VOS:
                layout.addView(createTextView(context, params, properties.get("note")));
                layout.addView(createTextView(context, params, properties.get("extra")));
                layout.addView(createTextView(context, params, properties.get("time")));
                indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                break;
            case MarkerIdentifier.TYPE_FOTO:
                layout.addView(createTextView(context, params, properties.get("info")));
                layout.addView(createTextView(context, params, properties.get("extra")));
                indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                break;
            case MarkerIdentifier.TYPE_HUNTER:
                layout.addView(createTextView(context, params, properties.get("hunter")));
                layout.addView(createTextView(context, params, properties.get("time")));
                indicator.setImageDrawable(ContextCompat.getDrawable(context, Integer.parseInt(properties.get("icon"))));
                break;
            case MarkerIdentifier.TYPE_SC:
                layout.addView(createTextView(context, params, properties.get("name")));
                layout.addView(createTextView(context, params, properties.get("adres")));
                indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22));
                break;
            case MarkerIdentifier.TYPE_SC_CLUSTER:
                layout.addView(createTextView(context, params, properties.get("size")));
                indicator.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.scouting_groep_icon_30x22));
                break;
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

}
