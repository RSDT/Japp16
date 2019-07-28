package nl.rsdt.japp.jotial.maps.wrapper;

/**
 * Created by mattijn on 08/08/17.
 */

public interface ICircle {

    void remove();

    void setRadius(float radius);

    void setVisible(boolean visible);

    int getFillColor();

    void setFillColor(int color);
}
