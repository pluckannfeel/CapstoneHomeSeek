package homeseek.app.android.capstonehomeseek;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by pluckannfeel on 7/7/2016.
 */
public class CustomListAdapter extends ArrayAdapter<String>{
    private Activity context;
    private String[] property_names;
    private String[] property_types;
    private String[] property_terms;
    private String[] property_price;
    private String[] property_id;

    public CustomListAdapter(Activity context,String[] property_id, String[] property_names, String[] property_types, String[] property_terms,
                             String[] property_price){
        super(context, R.layout.row_layout, property_id);
        this.context = context;
        this.property_names = property_names;
        this.property_types = property_types;
        this.property_terms = property_terms;
        this.property_price = property_price;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View list_row = layoutInflater.inflate(R.layout.row_layout, null, true);
        TextView p_name = (TextView) list_row.findViewById(R.id.p_name);
        TextView p_type = (TextView) list_row.findViewById(R.id.p_type);
        TextView p_term = (TextView) list_row.findViewById(R.id.p_term);
        TextView p_address = (TextView) list_row.findViewById(R.id.p_price);

        p_name.setText("Property Name: " + property_names[position]);
        p_type.setText("Property Type: " + property_types[position]);
        p_term.setText("Length of stay: " + property_terms[position]);
        p_address.setText("Rate: P  " + property_price[position]);

        return list_row;
    }
}
