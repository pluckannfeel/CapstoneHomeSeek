package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 11/8/2016.
 */
public class EditUserInfoRequest extends StringRequest {
    static BaseUrl baseUrl;
    private static final String EDITUSERINFO_REQUEST_URL = baseUrl.getBaseUrl() + "editUserInfo.php";
    private Map<String, String> params;

     public EditUserInfoRequest(int user_id, String firstname, String lastname, String contact_no, String email,
                                String address, Response.Listener<String> listener){
         super(Method.POST, EDITUSERINFO_REQUEST_URL, listener, null);
         params = new HashMap<>();
         params.put("user_id", user_id + "");
         params.put("firstname", firstname);
         params.put("lastname", lastname);
         params.put("contact_no", contact_no);
         params.put("email", email);
         params.put("address", address);
     }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
