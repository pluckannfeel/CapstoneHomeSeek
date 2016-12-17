package homeseek.app.android.capstonehomeseek;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.RegisterRequest;

public class RegisterActivity extends AppCompatActivity {
    EditText etfname, etlname, etusername, etpassword, etemail, etaddress, etage, etcontactnum;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register an Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f06c00")));

        etfname = (EditText) findViewById(R.id.et_firstname);
        etlname = (EditText) findViewById(R.id.et_lastname);
        etusername = (EditText) findViewById(R.id.et_username);
        etpassword = (EditText) findViewById(R.id.et_password);
        etemail = (EditText) findViewById(R.id.et_email);
        etaddress = (EditText) findViewById(R.id.et_address);
        etage = (EditText) findViewById(R.id.et_age);
        etcontactnum = (EditText) findViewById(R.id.et_contactNo);
        Button btnreg = (Button) findViewById(R.id.btn_register);

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate edit texts
                if (etfname.getText().toString().trim().equals("") || etlname.getText().toString().trim().equals("")
                        || etusername.getText().toString().trim().equals("") || etpassword.getText().toString().trim().equals("")
                        || etemail.getText().toString().trim().equals("") || etaddress.getText().toString().trim().equals("") || etage.getText().toString().trim().equals("")
                        || etcontactnum.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "All input fields are required.", Toast.LENGTH_LONG).show();
                } else {

                    progressDialog = ProgressDialog.show(v.getContext(),
                            "Please wait ..", "Loading..", true);
                    progressDialog.setCancelable(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                registerUser();
                            }catch (Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                            }

                        }
                    }).start();


                }
            }
        });

        }


    public void registerUser(){
        final String first_name = etfname.getText().toString();
        final String last_name = etlname.getText().toString();
        final String username = etusername.getText().toString();
        final String password = etpassword.getText().toString();
        final String email = etemail.getText().toString();
        final String address = etaddress.getText().toString();
        final int age = Integer.parseInt(etage.getText().toString());
        final String contactNo = etcontactnum.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    String myResponse = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                    JSONObject jsonResponse = new JSONObject(myResponse);
                    boolean success = jsonResponse.getBoolean("success");

                    //disables the whole activity
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if (success) {
                        progressDialog.dismiss();
                        // enables the whole activity
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("new_user_added", "Registered Successfully.");
                        RegisterActivity.this.startActivity(intent);

                    } else {
                        progressDialog.dismiss();
                        showAlertDialog();
                        Toast.makeText(getApplicationContext(), "Username input is already taken.", Toast.LENGTH_SHORT).show();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Log.d("My Response", response);
                    Toast.makeText(getApplicationContext(), "Can't connect to server.", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        };

        RegisterRequest registerRequest = new RegisterRequest(first_name, last_name, username, age, contactNo, email, address, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);
    }

    public void showAlertDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Username input is already taken.").setNegativeButton("Retry", dialogClickListener).show();

    }

}
