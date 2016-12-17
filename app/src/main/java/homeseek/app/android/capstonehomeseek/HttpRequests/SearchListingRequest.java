package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 7/3/2016.
 */
public class SearchListingRequest extends StringRequest {

    static BaseUrl baseUrl;
    private final static String SEARCH_REQUEST_URL = baseUrl.getBaseUrl() + "searchListing.php";
    private Map<String, String> params;

    public SearchListingRequest(String city, String term, String p_type,
                                int price_min, int price_max, int bedrooms, int bathrooms, Response.Listener<String> listener){
        super(Method.POST, SEARCH_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("city", city);
        params.put("term", term);
        params.put("property_type", p_type);
        params.put("price_min", price_min + "");
        params.put("price_max", price_max + "");
        params.put("bedrooms", bedrooms + "");
        params.put("bathrooms", bathrooms + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
