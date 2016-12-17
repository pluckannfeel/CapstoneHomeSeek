package homeseek.app.android.capstonehomeseek;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.HttpRequests.SearchListingRequest;

public class ShowResults extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView test;
    ListView data_listing;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);
        getSupportActionBar().setTitle("Search Property");
        //unfocus on edittexts when starting
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // method for checking the network connections (wi-fi or mobile data)
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        findNetwork(viewGroup);

        //initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLists);
        swipeRefreshLayout.setOnRefreshListener(this);

        //initialize listview
        data_listing = (ListView) findViewById(R.id.lv_data_listing);

        progressDialog = ProgressDialog.show(viewGroup.getContext(),
                "Please wait ..", "Searching ..", true);
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //gets data from home fragment
                    Intent intent = getIntent();
                    retrieveData(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                }

            }
        }).start();


    }

    public void retrieveData(Intent intent){
        final String inputCity = intent.getStringExtra("city");
        final String inputTerm = intent.getStringExtra("term");
        final String inputType = intent.getStringExtra("type");
        final int inputPMin = Integer.parseInt(intent.getStringExtra("price_min"));
        final int inputPMax = Integer.parseInt(intent.getStringExtra("price_max"));
        final int inputBedrooms = Integer.parseInt(intent.getStringExtra("bedrooms"));
        final int inputBathrooms = Integer.parseInt(intent.getStringExtra("bathrooms"));

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ShowJSON(response);
            }
        };

        SearchListingRequest searchListingRequest =
                new SearchListingRequest(inputCity,inputTerm,inputType,inputPMin,inputPMax,inputBedrooms,inputBathrooms,responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(searchListingRequest);
    }

    private void ShowJSON(String response){
        Boolean hasData;

        final String JSONArray_name = "listing";
        final String KEY_pName = "property_name";
        final String KEY_pType = "property_type";
        final String KEY_pTerm = "term";
        final String KEY_pRate = "rate";
        final String KEY_hasData = "hasData";
        final String KEY_pId = "property_id";

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray arrayResponse = jsonResponse.getJSONArray(JSONArray_name);
            hasData = jsonResponse.getBoolean(KEY_hasData);
            //get the property id here
//            property_image_url = jsonResponse.getString(KEY_pId);
//            if(property_image_url != null){
//
//            }

            String[] property_id = new String[arrayResponse.length()];
            String[] property_names = new String[arrayResponse.length()];
            String[] property_types = new String[arrayResponse.length()];
            String[] property_terms = new String[arrayResponse.length()];
            String[] property_price = new String[arrayResponse.length()];

            if(hasData){
                Toast.makeText(getApplicationContext(), arrayResponse.length() + " properties found.", Toast.LENGTH_LONG).show();
                for(int x = 0; x < arrayResponse.length(); x++){
                    JSONObject jsonObject = arrayResponse.getJSONObject(x);
                    property_id[x] = jsonObject.getString(KEY_pId);
                    property_names[x] = jsonObject.getString(KEY_pName);
                    property_types[x] = jsonObject.getString(KEY_pType);
                    property_terms[x] = jsonObject.getString(KEY_pTerm);
                    property_price[x] = jsonObject.getString(KEY_pRate);
                }

                CustomListAdapter adapter = new CustomListAdapter(this,property_id, property_names, property_types, property_terms, property_price);
                data_listing.setAdapter(adapter);
                data_listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " is selected", Toast.LENGTH_SHORT).show();
                        view.setSelected(true);
                        String property_id = parent.getItemAtPosition(position).toString();
                        Intent intent = new Intent(getApplicationContext(), PropertyDetails.class);
                        intent.putExtra("property_id", property_id);
                        startActivity(intent);
                    }
                });


            }else{
                Toast.makeText(getApplicationContext(), "No data retrieved", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Error : ", response.toString());
        }finally {
            progressDialog.dismiss();
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    public void findNetwork(ViewGroup viewGroup){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
//            Snackbar snackbar = Snackbar.make(viewGroup, "Internet Connection Detected: " + networkInfo.getTypeName(), Snackbar.LENGTH_LONG);
//            snackbar.show();
        } else{
            Snackbar snackbar = Snackbar.make(viewGroup, "No Internet Connection Detected.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getApplicationContext(), "Listings reloaded.", Toast.LENGTH_LONG).show();
        //gets data from home fragment
        Intent intent = getIntent();
        retrieveData(intent);
    }
}
