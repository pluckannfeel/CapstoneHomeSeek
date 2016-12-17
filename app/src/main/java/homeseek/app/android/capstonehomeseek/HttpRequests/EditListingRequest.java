package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 10/30/2016.
 */
public class EditListingRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String EDITLISTING_REQUEST_URL = baseUrl.getBaseUrl() + "editListing.php";
    private Map<String, String> params;

    public EditListingRequest(int property_id, String p_name, String p_type, String term, String city, String address,
                             String lot_area, String floor_area, int rate, int bedrooms, int bathrooms,
                             String host_name, String host_cno, String host_details, String status,
                             int user_id, Response.Listener<String> listener){
        super(Method.POST, EDITLISTING_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("property_id", property_id + "");
        params.put("property_name", p_name);
        params.put("property_type", p_type);
        params.put("term", term);
        params.put("city", city);
        params.put("address", address);
        params.put("lot_area", lot_area);
        params.put("floor_area", floor_area);
        params.put("rate", rate + "");
        params.put("bedrooms", bedrooms + "");
        params.put("bathrooms", bathrooms + "");
        params.put("host_name", host_name);
        params.put("host_contact_no", host_cno);
        params.put("host_details", host_details);
        params.put("status", status);
        params.put("user_id", user_id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
