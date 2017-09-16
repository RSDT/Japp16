package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import nl.rsdt.japp.application.Japp;

/**
 * Created by mattijn on 08/08/17.
 */

public interface IMarker {

    public void showInfoWindow();

    public void remove();

    public String getTitle();

    public LatLng getPosition();

    public void setPosition(LatLng latLng);

    public void setOnClickListener(IMarker.OnClickListener onClickListener);

    public void setIcon(int drawableHunt);

    public void setIcon(Bitmap bitmap);

    public void setTitle(String title) ;

    public boolean isVisible() ;

    public void setVisible(boolean visible) ;

    public void setRotation(float rotation) ;

    public String getId() ;


    public interface OnClickListener{
        public boolean OnClick(IMarker m);
    }
}
