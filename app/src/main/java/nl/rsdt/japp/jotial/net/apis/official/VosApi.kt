package nl.rsdt.japp.jotial.net.apis.official

import nl.rsdt.japp.jotial.data.structures.area348.VosStatusInfo
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 14-10-2016
 * Description...
 */

interface VosApi {
    @get:GET("/api/1.0/vossen")
    val status: Call<VosStatusInfo>
}
