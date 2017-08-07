package nl.rsdt.japp.jotial.maps.clustering;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo;

/**
 * Created by mattijn on 07/08/17.
 * this clustermanager does absolutely nothing
 * necessary for maptypes that do not support clustering
 */
public class NoneClusterManager implements ClusterManagerInterface {
    @Override
    public void addItems(ArrayList<ScoutingGroepInfo> buffer) {

    }

    @Override
    public void cluster() {

    }

    @Override
    public Collection<ScoutingGroepInfo> getItems() {
        return new Collection<ScoutingGroepInfo>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<ScoutingGroepInfo> iterator() {
                return new Iterator<ScoutingGroepInfo>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public ScoutingGroepInfo next() {
                        return null;
                    }
                };
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return a;
            }

            @Override
            public boolean add(ScoutingGroepInfo scoutingGroepInfo) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends ScoutingGroepInfo> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
    }

    @Override
    public void clearItems() {

    }
}
