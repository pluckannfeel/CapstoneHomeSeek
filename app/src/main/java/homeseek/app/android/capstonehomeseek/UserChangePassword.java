package homeseek.app.android.capstonehomeseek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.UserChangePasswordRequest;

public class UserChangePassword extends AppCompatActivity {
    EditText et_npass, et_rnpass, et_opass;
    Button btn_changePass;
    String new_pass, retype_pass, old_pass;
    int user_id;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_change_password);
        getSupportActionBar().setTitle("Change your password");

        et_npass = (EditText) findViewById(R.id.et_npass);
        et_rnpass = (EditText) findViewById(R.id.et_rnpass);
        et_opass = (EditText) findViewById(R.id.et_opass);
        btn_changePass = (Button) findViewById(R.id.btn_changePass);

        Intent intent = getIntent();
        user_id = Integer.parseInt(intent.getStringExtra("user_id"));
        //changes password
        ChangePassword(btn_changePass);
    }

    private void ChangePassword(Button btn_change){
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInputs()){
                    if(NewPasswordMatch()){
                        progressDialog = ProgressDialog.show(v.getContext(), "Please wait ..", "Updating", true);
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FinalizeProcess();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                } finally {
                                    progressDialog.dismiss();
                                }

                            }
                        }).start();
                    }else{
                        Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Insufficient information provided", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FinalizeProcess(){
        new_pass = et_npass.getText().toString();
        retype_pass = et_rnpass.getText().toString();
        old_pass = et_opass.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    boolean invalidpassword = jsonResponse.getBoolean("invalidpass");

                    if(invalidpassword){
                        showAlertDialog();
                    }else{
                        if(success){
                            Toast.makeText(getApplicationContext(), "Successfully Changed.", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to change password.", Toast.LENGTH_LONG).show();
                        }
                    }

                }catch (JSONException e) {
                    Log.e("Response", response);
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_LONG).show();
                }finally {
                    progressDialog.dismiss();
                }

            }
        };

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(user_id, old_pass, new_pass, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(userChangePasswordRequest);
    }

    public void showAlertDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Invalid Old Password").setNegativeButton("Retry", dialogClickListener).show();

    }

    private boolean NewPasswordMatch(){
        if(et_npass.getText().toString().trim().equals(et_rnpass.getText().toString().trim()))
            return true;
        else
            return false;
    }


    private boolean CheckInputs(){
        if(et_npass.getText().toString().trim().equals("") || et_rnpass.getText().toString().trim().equals("") ||
                et_opass.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }
}
