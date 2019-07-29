package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.structures.area348.MetaColorInfo
import nl.rsdt.japp.jotial.data.structures.area348.MetaInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
interface MetaApi {

    @GET("/meta/{key}")
    fun getMetaInfo(@Path("key") key: String): Call<MetaInfo>

    @GET("/meta/{key}/color")
    fun getMetaColor(@Path("key") key: String): Call<MetaColorInfo>
}
