package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.structures.area348.FotoOpdrachtInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 2-9-2016
 * Description...
 */
interface FotoApi {

    @GET("/foto/{key}/{id}")
    fun getById(@Path("key") key: String, @Path("id") id: Int): Call<FotoOpdrachtInfo>

    @GET("/foto/{key}/all")
    fun getAll(@Path("key") key: String): Call<ArrayList<FotoOpdrachtInfo>>

}
