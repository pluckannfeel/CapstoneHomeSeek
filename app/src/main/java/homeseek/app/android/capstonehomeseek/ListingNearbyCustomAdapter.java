package homeseek.app.android.capstonehomeseek;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by pluckannfeel on 11/29/2016.
 */
public class ListingNearbyCustomAdapter extends ArrayAdapter<String>{
    private Activity context;
    private String[] property_id;
    private String[] property_names;
    private String[] property_types;
    private String[] property_rate;
    private String[] property_address;

    public ListingNearbyCustomAdapter(Activity context, String[] property_id, String[] property_name, String[] type,
                                      String[] rate, String[] address){
        super(context, R.layout.listing_nearby_row_layout, property_id);
        this.context = context;
        this.property_id = property_id;
        this.property_names = property_name;
        this.property_types = type;
        this.property_rate = rate;
        this.property_address = address;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View list_row = layoutInflater.inflate(R.layout.listing_nearby_row_layout, null, true);
        TextView property_name, type, price, distance;
        property_name = (TextView) list_row.findViewById(R.id.ln_property_name);
        type = (TextView) list_row.findViewById(R.id.ln_type);
        price = (TextView) list_row.findViewById(R.id.ln_price);
        distance = (TextView) list_row.findViewById(R.id.ln_distance);

        property_name.setText("Property Name:  " + property_names[position]);
        type.setText("Type:  " + property_types[position]);
        price.setText("Rate:  P  " + property_rate[position]);
        distance.setText("Address:  " + String.valueOf(property_address[position]));

        return list_row;
    }
}
