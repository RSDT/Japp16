package nl.rsdt.japp.jotial.data.structures.area348

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-10-2016
 * Description...
 */

class VosStatusInfo {

    @SerializedName("data")
    var status = ArrayList<Status>()
        protected set

    class Status {

        @SerializedName("team")
        var team: String? = null
            protected set

        @SerializedName("status")
        var status: State? = null
            protected set
    }

    enum class State {
        @SerializedName("rood")
        RED,

        @SerializedName("oranje")
        ORANGE,

        @SerializedName("groen")
        GREEN
    }


}
