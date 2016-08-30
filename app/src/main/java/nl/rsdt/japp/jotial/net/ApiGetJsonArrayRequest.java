package nl.rsdt.japp.jotial.net;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;


import org.json.JSONArray;



/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-8-2016
 * Description...
 */
public class ApiGetJsonArrayRequest extends JsonArrayRequest {

    public ApiGetJsonArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }


}
