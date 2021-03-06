package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/10/2016.
 */
public class SendRatingRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String SEARCH_REQUEST_URL = baseUrl.getBaseUrl() + "sendRating.php";
    private Map<String, String> params;


    public SendRatingRequest(int property_id, int rate_value, String device_name, Response.Listener<String> listener){
        super(Method.POST, SEARCH_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("property_id", property_id + "");
        params.put("rate_value", rate_value + "");
        params.put("device_name", device_name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
