package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/9/2016.
 */
public class UserChangePasswordRequest extends StringRequest{
    static BaseUrl baseUrl;
    private static final String SEARCH_REQUEST_URL = baseUrl.getBaseUrl() + "changePassword.php";
    private Map<String, String> params;


    public UserChangePasswordRequest(int user_id, String old_password, String new_password, Response.Listener<String> listener){
        super(Method.POST, SEARCH_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id + "");
        params.put("old_password", old_password);
        params.put("new_password", new_password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
