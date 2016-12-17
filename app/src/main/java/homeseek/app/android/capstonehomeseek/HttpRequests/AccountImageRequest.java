package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 8/31/2016.
 */
public class AccountImageRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String ACCOUNTIMAGE_REQUEST_URL = baseUrl.getBaseUrl() + "getAccountImage.php";
    private Map<String, String> params;

    public AccountImageRequest(String user_id, Response.Listener<String> listener){
        super(Method.POST, ACCOUNTIMAGE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
