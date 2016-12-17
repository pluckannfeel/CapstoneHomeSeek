package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/30/2016.
 */
public class GetAddressRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String GETADDRESS_REQUEST_URL = baseUrl.getBaseUrl() + "getAddress.php";
    private Map<String, String> params;

    public GetAddressRequest(int property_id, Response.Listener<String> listener){
        super(Method.POST, GETADDRESS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("property_id", property_id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
