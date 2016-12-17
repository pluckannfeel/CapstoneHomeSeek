package homeseek.app.android.capstonehomeseek.Fragments;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.LoginRequest;
import homeseek.app.android.capstonehomeseek.R;
import homeseek.app.android.capstonehomeseek.RegisterActivity;
import homeseek.app.android.capstonehomeseek.UserAreaActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    Button btn_signIn, btn_signUp;
    EditText et_username, et_password;
    TextView title_homeseek;
    OnSignInCredentialsPassed onSignInCredentialsPassed;
    View view;
    boolean stop_rotation = false;
    ProgressDialog progressDialog;
    boolean noServerOnline = false;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ActionBar actionBar = getActivity().getActionBar();
        setHasOptionsMenu(true);
        getActivity().setTitle("Sign In");

        et_username = (EditText) view.findViewById(R.id.et_user_name);
        et_password = (EditText) view.findViewById(R.id.et_pass_word);
        btn_signIn = (Button) view.findViewById(R.id.btn_sign_in);

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(view.getContext(),
                        "Please wait ..", "Signing in ..", true);
                progressDialog.setCancelable(false);

                if(et_username.getText().toString().trim().equals("") || et_password.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity().getApplicationContext(), "All input fields are required.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }else{

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                loginUser();
                            }catch (Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(getActivity().getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                            }

                        }
                    }).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if(this!=null){
                                if(noServerOnline == true)
                                    TryAgainLaterDialog(view);

                                progressDialog.dismiss();
                            }
                        }
                    }, 5000);
                }
            }
        });

        //not used yet including callback methods from this fragment class to the main activity
        onSignInCredentialsPassed.signInCredentialsPass(et_username.getText().toString(), et_password.getText().toString());

        return view;
    }

    @Override
    public void onResume() {
        et_username.setText("");
        et_password.setText("");
        et_username.requestFocus();

        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            onSignInCredentialsPassed = (OnSignInCredentialsPassed) activity;
        }catch (Exception e){}

    }

    public interface OnSignInCredentialsPassed{
        void signInCredentialsPass(String uname, String pword);
    }

    public void loginUser(){
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            progressDialog.dismiss();
                            String userID = String.valueOf(jsonResponse.getInt("userID"));
                            String f_name = jsonResponse.getString("firstname");
                            String l_name = jsonResponse.getString("lastname");
                            int age = jsonResponse.getInt("age");
                            String contact_no = jsonResponse.getString("contactNo");
                            String email = jsonResponse.getString("email");
                            String address = jsonResponse.getString("address");

                            Intent intent = new Intent(getActivity(), UserAreaActivity.class);
                            intent.putExtra("userID", userID);
                            intent.putExtra("firstname", f_name);
                            intent.putExtra("lastname", l_name);
                            intent.putExtra("age", age);
                            intent.putExtra("contact_no", contact_no);
                            intent.putExtra("email", email);
                            intent.putExtra("address", address);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else {
                            noServerOnline = false;
                            showAlertDialog(view);
                            progressDialog.dismiss();
                        }

                }catch (JSONException e){
                    noServerOnline = true;
                    Log.e("Response:", response);
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot connect to server. please try again later", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        };

        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(loginRequest);


    }

    public void TryAgainLaterDialog(View view){
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

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Cannot connect to server, Please try again later.").setNegativeButton("Okay", dialogClickListener).show();

    }

    public void showAlertDialog(View view){
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

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Invalid Username or Password.").setNegativeButton("Retry", dialogClickListener).show();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sign_in_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_up:
                Intent signUpIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(signUpIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
