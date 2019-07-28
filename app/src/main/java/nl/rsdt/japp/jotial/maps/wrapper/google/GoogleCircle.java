package nl.rsdt.japp.jotial.maps.wrapper.google;

import nl.rsdt.japp.jotial.maps.wrapper.ICircle;

/**
 * Created by mattijn on 08/08/17.
 */

public class GoogleCircle implements ICircle{
    private final com.google.android.gms.maps.model.Circle googleCircle;


    public GoogleCircle(com.google.android.gms.maps.model.Circle circle) {
        googleCircle = circle;
    }

    @Override
    public void remove() {
        googleCircle.remove();
    }

    @Override
    public void setRadius(float radius) {
        googleCircle.setRadius(radius);
    }

    @Override
    public void setVisible(boolean visible) {
        googleCircle.setVisible(visible);
    }

    @Override
    public int getFillColor() {
        return googleCircle.getFillColor();
    }

    @Override
    public void setFillColor(int color) {
        googleCircle.setFillColor(color);
    }
}
