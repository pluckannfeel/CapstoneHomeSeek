package homeseek.app.android.capstonehomeseek.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import homeseek.app.android.capstonehomeseek.R;
import homeseek.app.android.capstonehomeseek.ShowResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    Spinner property_term, property_type, city;
    EditText price_min, price_max, bedrooms, bathrooms;
    String[] cities, terms, types;
    Button searchbtn;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Where do you want to live?");

        city = (Spinner) view.findViewById(R.id.s_city);
//        term = (AutoCompleteTextView) view.findViewById(R.id.et_term);
        property_term = (Spinner) view.findViewById(R.id.s_term);
        property_type = (Spinner) view.findViewById(R.id.s_type);
        price_min = (EditText) view.findViewById(R.id.et_min);
        price_max = (EditText) view.findViewById(R.id.et_max);
        bedrooms = (EditText) view.findViewById(R.id.et_bedroom);
        bathrooms = (EditText) view.findViewById(R.id.et_bathroom);

        //button
        searchbtn = (Button) view.findViewById(R.id.btn_search);

        //arrays on string res
        cities = getResources().getStringArray(R.array.cities);
        terms = getResources().getStringArray(R.array.property_term);
        types = getResources().getStringArray(R.array.property_type);

        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String itemSelected = parent.getItemAtPosition(position).toString();

                property_term.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //spinner property_term
        final ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, terms);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        property_term.setAdapter(termAdapter);

        property_term.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();

                property_term.setPrompt(itemSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner property type
        final ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        property_type.setAdapter(typesAdapter);

        property_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();

                property_type.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //method for starting search query
        SearchProperty(searchbtn);

        return view;
    }

    public void SearchProperty(Button search){
        //search button paramater
        // city - autocompletetextview
        // term and type - spinner
        // min, max, bedrooms, bathrooms - edittext (numbers)
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(city.getSelectedItem().toString().trim().equals("") || property_term.getSelectedItem().toString().trim().equals("")
                        || property_type.getSelectedItem().toString().trim().equals("") || price_min.getText().toString().trim().equals("")
                        || price_max.getText().toString().trim().equals("") || bedrooms.getText().toString().trim().equals("")
                        || bathrooms.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "All input fields are required.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), ShowResults.class);
                    intent.putExtra("city", city.getSelectedItem().toString());
                    intent.putExtra("term", property_term.getSelectedItem().toString());
                    intent.putExtra("type", property_type.getSelectedItem().toString());
                    intent.putExtra("price_min", price_min.getText().toString());
                    intent.putExtra("price_max", price_max.getText().toString());
                    intent.putExtra("bedrooms", bedrooms.getText().toString());
                    intent.putExtra("bathrooms", bathrooms.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

}
