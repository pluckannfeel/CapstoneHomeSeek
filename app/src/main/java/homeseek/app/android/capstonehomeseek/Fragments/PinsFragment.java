package homeseek.app.android.capstonehomeseek.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import homeseek.app.android.capstonehomeseek.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PinsFragment extends Fragment {
    View view;

    public PinsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pins, container, false);
        getActivity().setTitle("Your pins");

        return view;
    }

}
