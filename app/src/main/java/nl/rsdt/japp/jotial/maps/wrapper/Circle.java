package nl.rsdt.japp.jotial.maps.wrapper;

/**
 * Created by mattijn on 08/08/17.
 */

public class Circle {
    public static final int GOOGLE_CIRCLE= 0;
    public static final int OSM_CIRCLE = 1;
    private final com.google.android.gms.maps.model.Circle googleCircle;
    private final int circleType;

    public Circle(com.google.android.gms.maps.model.Circle circle) {
        googleCircle = circle;
        circleType = GOOGLE_CIRCLE;
    }

    public void remove() {
        if (circleType == GOOGLE_CIRCLE){
            googleCircle.remove();
        }
    }

    public void setRadius(float radius) {
        if (circleType == GOOGLE_CIRCLE) {
            googleCircle.setRadius(radius);
        }
    }
}
