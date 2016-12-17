package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 10/18/2016.
 */
public class UserListingsRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String USERLISTING_REQUEST_URL = baseUrl.getBaseUrl() + "userLists.php";
    private Map<String, String> params;

    public UserListingsRequest(int userID, Response.Listener<String> listener) {
        super(Method.POST, USERLISTING_REQUEST_URL, listener,null);
        params = new HashMap<>();
        params.put("user_id", userID + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
