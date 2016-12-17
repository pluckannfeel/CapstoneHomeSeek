package homeseek.app.android.capstonehomeseek.HttpRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import homeseek.app.android.capstonehomeseek.BaseUrl;

/**
 * Created by pluckannfeel on 6/12/2016.
 */
public class RegisterRequest extends StringRequest {

    static BaseUrl baseUrl;
    private static final String REGISTER_REQUEST_URL = baseUrl.getBaseUrl() + "Register.php";
    private Map<String, String> params;

    public RegisterRequest(String f_name, String l_name, String username, int age, String contactNo, String email, String address, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("firstname", f_name);
        params.put("lastname", l_name);
        params.put("age", age + "");
        params.put("contactNo", contactNo);
        params.put("email", email);
        params.put("address", address);
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
