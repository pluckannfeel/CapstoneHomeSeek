package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/2/2016.
 */
public class ChangeListingImageRequest extends StringRequest{

    static BaseUrl baseUrl;
    private static final String LISTINGIMAGE_REQUEST_URL = baseUrl.getBaseUrl() + "changeListingImage.php";
    private Map<String, String> params;

    public ChangeListingImageRequest(String encoded_string, String image_name, int property_id, Response.Listener<String> listener){
        super(Method.POST, LISTINGIMAGE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("encoded_string", encoded_string);
        params.put("image_name", image_name);
        params.put("property_id", property_id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
