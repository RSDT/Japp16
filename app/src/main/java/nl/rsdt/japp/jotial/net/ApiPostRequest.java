package nl.rsdt.japp.jotial.net;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 18-8-2016
 * Description...
 */
public class ApiPostRequest extends JsonObjectRequest {

    public ApiPostRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, JSONObject input) {
        super(url, input, listener, errorListener);
    }

}

