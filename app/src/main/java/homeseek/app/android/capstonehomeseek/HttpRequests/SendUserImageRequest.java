package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 9/30/2016.
 */
public class SendUserImageRequest extends StringRequest{

    static BaseUrl baseUrl;
    private static final String USERIMAGE_REQUEST_URL = baseUrl.getBaseUrl() + "saveUserImage.php";
    private Map<String, String> params;

    public SendUserImageRequest(String encoded_string, String image_name, int user_id, Response.Listener<String> listener){
        super(Method.POST, USERIMAGE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("encoded_string", encoded_string);
        params.put("image_name", image_name);
        params.put("user_id", user_id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
