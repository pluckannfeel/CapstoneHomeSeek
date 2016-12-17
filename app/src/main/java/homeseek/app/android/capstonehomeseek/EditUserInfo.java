package homeseek.app.android.capstonehomeseek;

import android.app.ProgressDialog;
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

import homeseek.app.android.capstonehomeseek.HttpRequests.EditUserInfoRequest;

public class EditUserInfo extends AppCompatActivity {
    EditText et_fname, et_lname, et_cno, et_email, et_address;
    ProgressDialog progressDialog;
    Button btn_editInfo;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        getSupportActionBar().setTitle("Edit your information");

        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_cno = (EditText) findViewById(R.id.et_cno);
        et_email = (EditText) findViewById(R.id.et_email);
        et_address = (EditText) findViewById(R.id.et_address);
        btn_editInfo = (Button) findViewById(R.id.EditInfo);

        Intent intent = getIntent();
        user_id = Integer.parseInt(intent.getStringExtra("user_id"));

        //displays the recent information coming from my account fragment
        DisplayRecentInfo(et_fname, et_lname, et_cno, et_email, et_address);

        //update user information
        UpdateInfo(btn_editInfo);
    }

    private void UpdateInfo(Button btn_edit){
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(v.getContext(), "Please wait ..", "Updating", true);
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FinalizeData();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                        } finally {
                            progressDialog.dismiss();
                        }

                    }
                }).start();
            }
        });
    }

    private void FinalizeData(){
        final String firstname, lastname, cno, email, address;
        firstname = et_fname.getText().toString();
        lastname = et_lname.getText().toString();
        cno = et_cno.getText().toString();
        email = et_email.getText().toString();
        address = et_address.getText().toString();

        if(CheckInputs()){
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            Toast.makeText(getApplicationContext(), "Successfully Saved Changes.", Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Log.d("user_id", String.valueOf(user_id));
                            Log.d("firstname", firstname);
                            Log.d("lastname", lastname);
                            Log.d("contact number", cno);
                            Log.d("email", email);
                            Log.d("address", address);
                            Toast.makeText(getApplicationContext(), "Failed to update listing.", Toast.LENGTH_LONG).show();
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

            EditUserInfoRequest editUserInfoRequest = new EditUserInfoRequest(user_id, firstname, lastname, cno, email, address, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(editUserInfoRequest);
        }else
            Toast.makeText(getApplicationContext(), "Insufficient information provided", Toast.LENGTH_SHORT).show();

    }

    private boolean CheckInputs(){
        if(et_fname.getText().toString().trim().equals("") || et_lname.getText().toString().trim().equals("") ||
                et_cno.getText().toString().trim().equals("") || et_email.getText().toString().trim().equals("") ||
                et_address.getText().toString().trim().equals(""))
            return false;
        else
            return true;

    }

    private void DisplayRecentInfo(EditText fname, EditText lname, EditText cno, EditText email, EditText address){
        Intent intent = getIntent();

        fname.setText(intent.getStringExtra("firstname"));
        lname.setText(intent.getStringExtra("lastname"));
        cno.setText(intent.getStringExtra("cno"));
        email.setText(intent.getStringExtra("email"));
        address.setText(intent.getStringExtra("address"));
    }
}
