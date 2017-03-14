package app.movemate.Filter;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import app.movemate.FilterActivity;
import app.movemate.R;


public class FilterDirectionFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_direction, container, false);

        Button btn_to = (Button)view.findViewById(R.id.to);
        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBoolean("ToFrom",true);
                FilterTransportFragment frag = new FilterTransportFragment();
                frag.setArguments(b);
                ((FilterActivity)getActivity()).nextFrag(frag);
            }
        });

        Button btn_from = (Button)view.findViewById(R.id.from);
        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putBoolean("ToFrom",false);
                FilterTransportFragment frag = new FilterTransportFragment();
                frag.setArguments(b);
                ((FilterActivity)getActivity()).nextFrag(frag);
            }
        });

        return view;
    }


}
