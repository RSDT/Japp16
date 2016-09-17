package nl.rsdt.japp.jotial.maps;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import nl.rsdt.japp.R;

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
        layout.addView(createTextView(context, params, "hoi"));
        layout.addView(createTextView(context, params, "sfsf"));
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
