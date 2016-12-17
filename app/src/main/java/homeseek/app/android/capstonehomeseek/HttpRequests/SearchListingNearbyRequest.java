package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/17/2016.
 */

public class SearchListingNearbyRequest extends StringRequest{
    static BaseUrl baseUrl;
    private static final String LOGIN_REQUEST_URL = baseUrl.getBaseUrl() + "searchNearbyListing.php";
    private Map<String, String> params;

    public SearchListingNearbyRequest(Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
