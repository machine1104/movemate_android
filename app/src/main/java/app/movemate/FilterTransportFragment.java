package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class FilterTransportFragment extends Fragment {
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter_transport, container, false);

        Button next = (Button)view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        return view;
    }

    private void next() {
        String url = "http://movemate-api.azurewebsites.net/api/paths/getfilteredpaths?";
        Boolean car, moto, transport;
        car = ((CheckBox) view.findViewById(R.id.car)).isChecked();
        moto = ((CheckBox) view.findViewById(R.id.moto)).isChecked();
        transport = ((CheckBox) view.findViewById(R.id.public_transport)).isChecked();

        if (!car && !moto && !transport) {
            Toasty.error(getActivity(), getString(R.string.missing_vehicle), Toast.LENGTH_SHORT, true).show();
        } else {

            url += "ToFrom=" + getArguments().getBoolean("ToFrom");

            if (car) {
                url += "&Vehicle=0";
            }
            if (moto) {
                url += "&Vehicle=1";
            }
            if (transport) {
                url += "&Vehicle=2";
            }
            FilterPriceFragment frag = new FilterPriceFragment();
            Bundle b = new Bundle();
            b.putBoolean("ToFrom",getArguments().getBoolean("ToFrom"));
            b.putString("url", url);
            frag.setArguments(b);
            ((MainActivity) getActivity()).nextFrag(frag);
        }
    }
}
