package homeseek.app.android.capstonehomeseek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.photoutil.GalleryPhoto;

import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.EditListingRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.PropertyDetailsRequest;

public class EditListing extends AppCompatActivity {
    String property_id, user_id;
    Spinner s_type, s_term, s_city, s_bedrooms, s_bathrooms, s_status;
    EditText et_pname, et_address, et_larea, et_farea, et_price, et_hostname, et_cno, et_hdetails;

    Button btn_update;
    String[] cities, terms, types, bedrooms, bathrooms, status;
    BaseUrl baseUrl;
    com.android.volley.toolbox.ImageLoader displayImageLoader;
    String selectedPhoto;
    String imagePath, imageName;
    String final_encoded_string, final_image_name;
    GalleryPhoto galleryPhoto;
    protected static final int GALLERY_REQUEST = 1;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        getSupportActionBar().setTitle("Edit Property");

        //initialization of inputs
        et_pname = (EditText) findViewById(R.id.et_pname);
        s_type = (Spinner) findViewById(R.id.s_ptype);
        s_term = (Spinner) findViewById(R.id.s_term);
        s_city = (Spinner) findViewById(R.id.s_city);
        et_address = (EditText) findViewById(R.id.et_address);
        et_larea = (EditText) findViewById(R.id.et_larea);
        et_farea = (EditText) findViewById(R.id.et_farea);
        et_price = (EditText) findViewById(R.id.et_price);
        s_bedrooms = (Spinner) findViewById(R.id.s_bedrooms);
        s_bathrooms = (Spinner) findViewById(R.id.s_bathrooms);
        et_hostname = (EditText) findViewById(R.id.et_hostname);
        et_cno = (EditText) findViewById(R.id.et_cno);
        et_hdetails = (EditText) findViewById(R.id.et_hdetails);
        s_status = (Spinner) findViewById(R.id.s_status);
        btn_update = (Button) findViewById(R.id.AddProperty);
        UpdateListing(btn_update);

        //spinner items put in a method
        SpinnerItems();

        property_id = getPropertyID();
        user_id = getUserID();

        //display recent listing
        progressDialog = ProgressDialog.show(this, "Please wait ..", "Taking image ..", true);
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    retrieveData();
                    progressDialog.dismiss();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                }

            }
        }).start();

    }

    private void UpdateListing(Button btn_update){
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(v.getContext(), "Please wait ..", "Updating", true);
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalizeData();
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

    private void finalizeData(){
        try {
            if(checkInputs()){
                final String p_name = et_pname.getText().toString();
                final String p_type = s_type.getSelectedItem().toString();
                final String p_term = s_term.getSelectedItem().toString();
                final String p_city = s_city.getSelectedItem().toString();
                final String p_address = et_address.getText().toString();
                final String p_larea = et_larea.getText().toString();
                final String p_farea = et_farea.getText().toString();
                final int p_price = Integer.parseInt(et_price.getText().toString());
                final int p_bedrooms = Integer.parseInt(s_bedrooms.getSelectedItem().toString());
                final int p_bathrooms = Integer.parseInt(s_bathrooms.getSelectedItem().toString());
                final String p_hostname = et_hostname.getText().toString();
                final String p_cno = et_cno.getText().toString();
                final String p_hdetails = et_hdetails.getText().toString();
                final String p_status = s_status.getSelectedItem().toString();

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
                                Log.d("p_name", p_name);
                                Log.d("p_type", p_type);
                                Log.d("p_term", p_term);
                                Log.d("p_city", p_city);
                                Log.d("p_address", p_address);
                                Log.d("p_larea", p_larea);
                                Log.d("p_farea", p_farea);
                                Log.d("p_price", String.valueOf(p_price));
                                Log.d("p_bed", String.valueOf(p_bedrooms));
                                Log.d("p_bath", String.valueOf(p_bathrooms));
                                Log.d("p_hostname", p_hostname);
                                Log.d("p_cno", p_cno);
                                Log.d("p_hdetails", p_hdetails);
                                Log.d("p_status", p_status);
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

                EditListingRequest editListingRequest = new EditListingRequest(Integer.parseInt(getPropertyID()),
                        p_name, p_type, p_term, p_city, p_address, p_larea,p_farea,
                        p_price, p_bedrooms, p_bathrooms,
                        p_hostname, p_cno, p_hdetails, p_status,
                        Integer.parseInt(getUserID()), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(editListingRequest);
            }else{
                Toast.makeText(getApplicationContext(), "Insufficient information provided", Toast.LENGTH_SHORT).show();
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            Log.d("Error ", e.toString());
        }
    }

    private boolean checkInputs(){
        if(et_pname.getText().toString().trim().equals("") || s_type.getSelectedItem().toString().trim().equals("") ||
                s_term.getSelectedItem().toString().trim().equals("") || s_city.getSelectedItem().toString().trim().equals("") ||
                et_address.getText().toString().trim().equals("") ||  et_price.getText().toString().trim().equals("") ||
                s_bedrooms.getSelectedItem().toString().trim().equals("") || s_bathrooms.getSelectedItem().toString().trim().equals("") ||
                et_hostname.getText().toString().trim().equals("") || et_cno.getText().toString().trim().equals("") ||
                et_hdetails.getText().toString().trim().equals("") || s_status.getSelectedItem().toString().trim().equals(""))
            return false;
        else
            return true;
    }


    private void SpinnerItems(){
        //array datas on res/strings.xml
        cities = getResources().getStringArray(R.array.cities);
        terms = getResources().getStringArray(R.array.property_term);
        types = getResources().getStringArray(R.array.property_type);
        bedrooms = getResources().getStringArray(R.array.bedrooms);
        bathrooms = getResources().getStringArray(R.array.bathrooms);
        status = getResources().getStringArray(R.array.status);

        //spinner cities
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_city.setAdapter(cityAdapter);
        s_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_city.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner terms
        final ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, terms);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_term.setAdapter(termAdapter);
        s_term.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_term.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner types
        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_type.setAdapter(typeAdapter);
        s_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_type.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner bedrooms
        final ArrayAdapter<String> bedAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, bedrooms);
        bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_bedrooms.setAdapter(bedAdapter);
        s_bedrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_bedrooms.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner bathrooms
        final ArrayAdapter<String> bathAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, bathrooms);
        bathAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_bathrooms.setAdapter(bathAdapter);
        s_bathrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_bathrooms.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner status
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, status);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_status.setAdapter(statusAdapter);
        s_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_status.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getPropertyID(){
        Intent intent = getIntent();
        String property_id = intent.getStringExtra("property_id");

        return property_id;
    }

    private String getUserID(){
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");

        return user_id;
    }

    String property_name, property_type, data_term, data_city, data_address, data_lot_area, data_floor_area, data_price, data_bedrooms,
            data_bathrooms, data_host_name,
            data_host_contact_no, data_host_details, data_date_listed, data_status;

    private void retrieveData(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean hasData;

                    property_name = jsonResponse.getString("property_name");
                    property_type = jsonResponse.getString("property_type");
                    data_term = jsonResponse.getString("term");
                    data_city = jsonResponse.getString("city");
                    data_address = jsonResponse.getString("address");
                    data_lot_area = jsonResponse.getString("lot_area");
                    data_floor_area = jsonResponse.getString("floor_area");
                    data_price = String.valueOf(jsonResponse.getInt("rate"));
                    data_bedrooms = String.valueOf(jsonResponse.getInt("bedrooms"));
                    data_bathrooms = String.valueOf(jsonResponse.getInt("bathrooms"));
                    data_host_name = jsonResponse.getString("host_name");
                    data_host_contact_no = jsonResponse.getString("host_contact_no");
                    data_host_details = jsonResponse.getString("host_details");
                    data_date_listed = String.valueOf(jsonResponse.get("date_listed"));
                    data_status = jsonResponse.getString("status");
                    hasData = jsonResponse.getBoolean("hasData");

                    if(hasData){
                        et_pname.setText(property_name);
                        s_status.setSelection(getStatusIndex(data_status));
                        s_type.setSelection(getTypeIndex(property_type));
                        s_term.setSelection(getTermIndex(data_term));
                        s_city.setSelection(getCityIndex(data_city));

                        et_address.setText(data_address);
                        et_price.setText(data_price);
                        et_larea.setText(data_lot_area);
                        et_farea.setText(data_floor_area);

                        s_bedrooms.setSelection(getBedroomsIndex(data_bedrooms));
                        s_bathrooms.setSelection(getBathroomsIndex(data_bathrooms));

                        et_hostname.setText(data_host_name);
                        et_cno.setText(data_host_contact_no);
                        et_hdetails.setText(data_host_details);
                    }else{
                        Toast.makeText(getApplicationContext(), "No data retrieved.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        PropertyDetailsRequest propertyDetailsRequest = new PropertyDetailsRequest(Integer.parseInt(getPropertyID()), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(propertyDetailsRequest);
    }

    private int getStatusIndex(String status){
        int position = 0;
        switch(status){
            case "Available":
                position = 0;
                break;
            case "Unavailable":
                position = 1;
                break;
        }
        return position;
    }

    private int getTermIndex(String term){
        int position = 0;
        switch(term){
            case "Weekly":
                position = 0;
                break;
            case "Monthly":
                position = 1;
                break;
            case "Annually":
                position = 2;
                break;
        }
        return position;
    }

    private int getTypeIndex(String type){
        int position = 0;
        switch (type){
            case "Apartment":
                position = 0;
                break;
            case "House":
                position = 1;
                break;
            case "Townhouse":
                position = 2;
                break;
            case "Board House":
                position = 3;
                break;
            case "Bed Space":
                position = 4;
                break;
            case "Room":
                position = 5;
                break;
            case "Dormitory":
                position = 6;
                break;
            case "Condominium":
                position = 7;
                break;
        }
        return position;
    }

    private int getCityIndex(String city){
        int position = 0;
        switch(city){
            case "Davao":
                position = 0;
                break;
            case "Island Garden City of Samal":
                position = 1;
                break;
            case "Cebu":
                position = 2;
                break;
            case "Manila":
                position = 3;
                break;
            case "Iloilo":
                position = 4;
                break;
            case "Bohol":
                position = 5;
                break;
            case "Baguio":
                position = 6;
                break;
            case "Aklan":
                position = 7;
                break;
            case "Capiz":
                position = 8;
                break;
            case "Palawan":
                position = 9;
                break;
            case "Cagayan De Oro":
                position = 10;
                break;
            case "Taguig":
                position = 11;
                break;
            case "Ormoc":
                position = 11;
                break;
            case "Cavite":
                position = 11;
                break;
        }
        return position;
    }

    private int getBedroomsIndex(String bedrooms){
        int position = 0;
        switch(bedrooms){
            case "1":
                position = 0;
                break;
            case "2":
                position = 1;
                break;
            case "3":
                position = 2;
                break;
            case "4":
                position = 3;
                break;
            case "5":
                position = 4;
                break;
        }
        return position;
    }

    private int getBathroomsIndex(String bathrooms){
        int position = 0;
        switch(bathrooms){
            case "1":
                position = 0;
                break;
            case "2":
                position = 1;
                break;
            case "3":
                position = 2;
                break;
            case "4":
                position = 3;
                break;
            case "5":
                position = 4;
                break;
        }
        return position;
    }
}
