package nl.rsdt.japp.jotial.net.apis

import nl.rsdt.japp.jotial.data.structures.area348.ScoutingGroepInfo
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
interface ScoutingGroepApi {

    @GET("/sc/{key}/all")
    fun getAll(@Path("key") key: String?): Call<ArrayList<ScoutingGroepInfo>>

}
