package homeseek.app.android.capstonehomeseek;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.HasRatingRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.PropertyDetailsRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.SendRatingRequest;

public class PropertyDetails extends AppCompatActivity {
    TextView p_name, p_type, term, city, address, lot_area, floor_area, rate, bedrooms, bathrooms, host_name,host_cno,
    host_details, date_listed, status;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    Context context = this;
    CardView cardView;
    static BaseUrl baseUrl;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);
        view = this.findViewById(android.R.id.content);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Property Details");
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));

        //card view
        cardView = (CardView) findViewById(R.id.card_view_details);

        //all fields of property details - initialized here
        p_type = (TextView) findViewById(R.id.property_type);
        p_name = (TextView) findViewById(R.id.property_name);
        term = (TextView) findViewById(R.id.term);
        city = (TextView) findViewById(R.id.city);
        address = (TextView) findViewById(R.id.address);
        rate = (TextView) findViewById(R.id.price);
        lot_area = (TextView) findViewById(R.id.lot_area);
        floor_area = (TextView) findViewById(R.id.floor_area);
        bedrooms = (TextView) findViewById(R.id.bedrooms);
        bathrooms = (TextView) findViewById(R.id.bathrooms);
        date_listed = (TextView) findViewById(R.id.date_listed);
        host_name = (TextView) findViewById(R.id.host_name);
        host_cno = (TextView) findViewById(R.id.host_contact_no);
        host_details = (TextView) findViewById(R.id.host_details);
        status = (TextView) findViewById(R.id.property_status);


        // method for checking the network connections (wi-fi or mobile data)
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        findNetwork(viewGroup);

        Intent intent = getIntent();
        retrieveData(intent);

    }

    private void retrieveData(Intent intent){
        String selected_id = intent.getStringExtra("property_id");
        int property_id = Integer.parseInt(selected_id);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getJSON(response);
            }
        };

        PropertyDetailsRequest propertyDetailsRequest = new PropertyDetailsRequest(property_id, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(propertyDetailsRequest);
    }

    private void getJSON(String response){
        boolean hasImage, hasData;
        String image_path;

        //show all data of selected property
        allPropertyFields(response);

        //with image
        try{
            String myResponse = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            JSONObject jsonResponse = new JSONObject(myResponse);
            hasImage = jsonResponse.getBoolean("hasImage");
            hasData = jsonResponse.getBoolean("hasData");
            image_path = jsonResponse.getString("image_path");

            if(hasData){


                if(hasImage){
                    final ImageView toolbarImage;
                    String url = baseUrl.getBaseUrl() + image_path;
                    toolbarImage = (ImageView) findViewById(R.id.property_pimage);

                    ImageRequest request = new ImageRequest(url,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    toolbarImage.setImageBitmap(bitmap);
                                }
                            }, 0, 0, null,
                            new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
// Access the RequestQueue through your singleton class.
                    MySingleton.getInstance(this).addToRequestQueue(request);

                }else {
                    Toast.makeText(getApplicationContext(), "has data, no image.", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "No Data Retrieved.", Toast.LENGTH_LONG).show();
            }


        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private String getAddress(){
        String address;

        address = data_address;

        return  address;
    }

    String property_id, property_name, property_type, data_term, data_city, data_address, data_lot_area, data_floor_area,
            data_rate, data_bedrooms, data_bathrooms, data_host_name,
            data_host_contact_no, data_host_details, data_date_listed, data_status;

    private void allPropertyFields(final String response){
        try {
            JSONObject jsonResponse = new JSONObject(response);
            Boolean hasData;

            property_id = jsonResponse.getString("property_id");
            property_name = jsonResponse.getString("property_name");
            property_type = jsonResponse.getString("property_type");
            data_term = jsonResponse.getString("term");
            data_city = jsonResponse.getString("city");
            data_address = jsonResponse.getString("address");
            data_lot_area = jsonResponse.getString("lot_area");
            data_floor_area = jsonResponse.getString("floor_area");
            data_rate = String.valueOf(jsonResponse.getInt("rate"));
            data_bedrooms = String.valueOf(jsonResponse.getInt("bedrooms"));
            data_bathrooms = String.valueOf(jsonResponse.getInt("bathrooms"));
            data_host_name = jsonResponse.getString("host_name");
            data_host_contact_no = jsonResponse.getString("host_contact_no");
            data_host_details = jsonResponse.getString("host_details");
            data_date_listed = String.valueOf(jsonResponse.get("date_listed"));
            data_status = jsonResponse.getString("status");
            hasData = jsonResponse.getBoolean("hasData");

            if(hasData){
                p_name.setText(property_name);
                status.setText(data_status);
                p_type.setText(property_type);
                term.setText(data_term);
                city.setText(data_city);
                address.setText(data_address);
                rate.setText(data_rate);
                lot_area.setText(data_lot_area);
                floor_area.setText(data_floor_area);
                bedrooms.setText(data_bedrooms);
                bathrooms.setText(data_bathrooms);
                date_listed.setText(data_date_listed);
                host_name.setText(data_host_name);
                host_cno.setText(data_host_contact_no);
                host_details.setText(data_host_details);
            }else{
                Toast.makeText(getApplicationContext(), "No data retrieved.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.property_details_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private String getPropertyName(){
        return property_name;
    }

    Dialog rateDialog;
    RatingBar ratingBar;
    TextView rateTitle;
    Button rateButton;
    DeviceName deviceName;
    private void rateListing(){
        deviceName = new DeviceName();
        rateDialog = new Dialog(PropertyDetails.this, R.style.FullHeightDialog);
        rateDialog.setContentView(R.layout.rate_dialog);
        rateDialog.setCancelable(true);

        ratingBar = (RatingBar)rateDialog.findViewById(R.id.dialog_ratingbar);
        ratingBar.setRating(0);

        rateTitle = (TextView) rateDialog.findViewById(R.id.rate_dialog_title);
        rateTitle.setText(getPropertyName());

        rateButton = (Button) rateDialog.findViewById(R.id.rate_dialog_button);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                rateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int value = Math.round(rating);
                        if(HasRating()){
                            showAlertDialog();
                            rateDialog.dismiss();
                        }else{
                            SendRating(value);
                            rateDialog.dismiss();
                        }
                    }
                });
            }
        });
        rateDialog.show();
    }

    private void SendRating(final int rating){
        String device_name;
        device_name = deviceName.getDeviceName();


        Response.Listener<String> responseListener  = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        Toast.makeText(getApplicationContext(), "You rated " + rating + " star(s)", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Unsuccess inserting rate, error in sendRating.php",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SendRatingRequest sendRatingRequest = new SendRatingRequest(Integer.parseInt(property_id), rating, device_name, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(sendRatingRequest);
    }

    boolean hasRating;
    private boolean HasRating(){
        String device_name;
        device_name = deviceName.getDeviceName();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean hasData = jsonResponse.getBoolean("hasData");

                    if(hasData){
                        hasRating = true;
                    }else{
                        hasRating = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        HasRatingRequest hasRatingRequest = new HasRatingRequest(Integer.parseInt(property_id), device_name, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(hasRatingRequest);

        return hasRating;
    }

    @Override
    protected void onPause() {
        super.onPause();
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.rate_property:
                if(HasRating()){
                    showAlertDialog();
                }else{
                    rateListing();
                }
                break;
            case R.id.map_location:
                Intent intent = new Intent(getApplicationContext(), PropertyLocationMap.class);
                intent.putExtra("address", getAddress());
                startActivity(intent);
                break;
            case R.id.terms_conditions:
                Toast.makeText(getApplicationContext(), "Terms and Conditions", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pin_listing:
                Toast.makeText(getApplicationContext(), "Pin Listing", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAlertDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //nothing to do
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // for validation
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(PropertyDetails.this);
        builder.setMessage("You rated this listing on this device already.")
                .setPositiveButton("Ok", dialogClickListener).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void findNetwork(ViewGroup viewGroup){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
        } else{
            Snackbar snackbar = Snackbar.make(viewGroup, "No Internet Connection Detected.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
