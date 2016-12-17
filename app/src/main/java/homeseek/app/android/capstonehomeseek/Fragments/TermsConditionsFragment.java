package homeseek.app.android.capstonehomeseek.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import homeseek.app.android.capstonehomeseek.R;

/**
 * Created by pluckannfeel on 12/5/2016.
 */

public class TermsConditionsFragment extends android.support.v4.app.Fragment {

    View view;

    public TermsConditionsFragment(){
        // required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_terms_conditions, container, false);
        getActivity().setTitle("Terms and Conditions");

        return view;
    }
}
