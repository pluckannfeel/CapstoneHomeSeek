package homeseek.app.android.capstonehomeseek.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import homeseek.app.android.capstonehomeseek.AddListing;
import homeseek.app.android.capstonehomeseek.ChangeListingImage;
import homeseek.app.android.capstonehomeseek.CustomListAdapter;
import homeseek.app.android.capstonehomeseek.EditListing;
import homeseek.app.android.capstonehomeseek.HttpRequests.UserListingsRequest;
import homeseek.app.android.capstonehomeseek.PropertyDetails;
import homeseek.app.android.capstonehomeseek.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyListingsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    ImageButton btnAddList;
    View view;
    ListView lvUserList;
    String userID;
    boolean noDataYet;
    private SwipeRefreshLayout swipeRefreshLayout;
    public MyListingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_listings, container, false);

        //initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLists);
        swipeRefreshLayout.setOnRefreshListener(this);

        //list view
        lvUserList = (ListView) view.findViewById(R.id.lv_data_listing);

        retrieveData();

        addListing(btnAddList);

        return view;

    }

    private void retrieveData(){
        userID = getArguments().getString("userID");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processJSON(response);
            }
        };

        UserListingsRequest userListingsRequest = new UserListingsRequest(Integer.parseInt(userID), responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(userListingsRequest);
    }

    private void processJSON(String response){
        Boolean hasData;
        final String JSONArray_name = "listing";
        final String KEY_pName = "property_name";
        final String KEY_pType = "property_type";
        final String KEY_pTerm = "term";
        final String KEY_pPrice = "price";
        final String KEY_hasData = "hasData";
        final String KEY_pId = "property_id";

        try{
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray arrayResponse = jsonResponse.getJSONArray(JSONArray_name);
            hasData = jsonResponse.getBoolean(KEY_hasData);

            String[] property_id = new String[arrayResponse.length()];
            String[] property_names = new String[arrayResponse.length()];
            String[] property_types = new String[arrayResponse.length()];
            String[] property_terms = new String[arrayResponse.length()];
            String[] property_price = new String[arrayResponse.length()];

            if(hasData){
                Toast.makeText(view.getContext(), arrayResponse.length() + " properties found.", Toast.LENGTH_SHORT).show();
                for(int x = 0; x < arrayResponse.length(); x++){
                    JSONObject jsonObject = arrayResponse.getJSONObject(x);
                    property_id[x] = jsonObject.getString(KEY_pId);
                    property_names[x] = jsonObject.getString(KEY_pName);
                    property_types[x] = jsonObject.getString(KEY_pType);
                    property_terms[x] = jsonObject.getString(KEY_pTerm);
                    property_price[x] = jsonObject.getString(KEY_pPrice);
                }

                CustomListAdapter adapter = new CustomListAdapter(getActivity(),property_id, property_names, property_types, property_terms, property_price);
                lvUserList.setAdapter(adapter);
                lvUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                        String property_id = parent.getItemAtPosition(position).toString();
                        Intent intent = new Intent(view.getContext(), PropertyDetails.class);
                        intent.putExtra("property_id", property_id);
                        startActivity(intent);
                    }
                });


                //long click to choose what to do when the user long taps
                chooseDialog(lvUserList);

            }else{
                Toast.makeText(view.getContext(), "No data retrieved", Toast.LENGTH_LONG).show();
            }

        }catch (JSONException ex){
            ex.printStackTrace();
            Log.d("Error: ", response);
        }finally {
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }


    // this method links to the floating action button
    public void addListing(ImageButton addListing){
        addListing = (ImageButton) view.findViewById(R.id.addListing);

        addListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddListing.class);
                intent.putExtra("user_id", userID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        Toast.makeText(view.getContext(), "Listings reloaded.", Toast.LENGTH_SHORT).show();
        //gets data from home fragment
        Intent intent = getActivity().getIntent();
        retrieveData();
    }

    private void chooseDialog(ListView lv_data){
        lv_data.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                final String property_id = parent.getItemAtPosition(position).toString();

                final String[] items = new String[] {"Edit property", "Change Listing Image", "View property"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Choose Action:");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //edit
                            Intent intent = new Intent(getActivity().getApplicationContext(), EditListing.class);
                            intent.putExtra("user_id", userID);
                            intent.putExtra("property_id", property_id);
                            startActivity(intent);
                        }else if (which == 1){
                            //change image
                            Intent intent = new Intent(getActivity().getApplicationContext(), ChangeListingImage.class);
                            intent.putExtra("property_id", property_id);
                            startActivity(intent);
                        }
                        else if (which == 2) {
                            //view
                            Intent intent = new Intent(getActivity().getApplicationContext(), PropertyDetails.class);
                            intent.putExtra("property_id", property_id);
                            startActivity(intent);
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

}
