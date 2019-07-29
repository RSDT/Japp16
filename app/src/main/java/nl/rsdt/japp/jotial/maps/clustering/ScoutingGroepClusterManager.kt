package nl.rsdt.japp.jotial.maps.clustering

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
import java.util.ArrayList

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-8-2016
 * Description...
 */
class ScoutingGroepClusterManager (protected var context: Context, protected var map: GoogleMap) : ClusterManager<ScoutingGroepInfo>(context, map), ClusterManagerInterface {


    /**
     * The algorithm used for the clustering.
     */
    internal var algorithm = ScoutingGroepAlgorithm()

    /**
     * The renderer used for the clustering.
     */
    internal var renderer: ScoutingGroepRenderer

    /**
     * Gets the items.
     */
    override val items: Collection<ScoutingGroepInfo>
        get() = algorithm.items

    init {
        setAlgorithm(algorithm)
        renderer = ScoutingGroepRenderer(context, map, this)
        setRenderer(renderer)
        map.setOnCameraIdleListener(this)
        map.setOnMarkerClickListener(this)
    }

    fun reRender() {
        renderer = ScoutingGroepRenderer(context, map, this)
        setRenderer(renderer)
    }

    override fun addItems(buffer: ArrayList<ScoutingGroepInfo>) {
        algorithm.items.addAll(buffer)
    }

}

