package nl.rsdt.japp.jotial.maps.wrapper;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mattijn on 08/08/17.
 */

public interface IMarker {

    void showInfoWindow();

    void remove();

    String getTitle();

    void setTitle(String title);

    LatLng getPosition();

    void setPosition(LatLng latLng);

    void setOnClickListener(IMarker.OnClickListener onClickListener);

    void setIcon(int drawableHunt);

    void setIcon(Bitmap bitmap);

    boolean isVisible();

    void setVisible(boolean visible);

    void setRotation(float rotation);

    String getId();


    interface OnClickListener {
        boolean OnClick(IMarker m);
    }
}
