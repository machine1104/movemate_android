package app.movemate.Filter;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import app.movemate.FilterActivity;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class FilterTransportFragment extends Fragment implements android.widget.CompoundButton.OnCheckedChangeListener {
    View view;
    CheckBox carCheck, motoCheck, busCheck;
    RelativeLayout priceLayout;
    SeekBar price_bar;
    TextView price;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter_transport, container, false);

        carCheck = (CheckBox) view.findViewById(R.id.car);
        motoCheck = (CheckBox) view.findViewById(R.id.moto);
        busCheck = (CheckBox) view.findViewById(R.id.public_transport);
        carCheck.setOnCheckedChangeListener(this);
        motoCheck.setOnCheckedChangeListener(this);
        busCheck.setOnCheckedChangeListener(this);
        priceLayout = (RelativeLayout)view.findViewById(R.id.price_layout);

        price_bar = (SeekBar) view.findViewById(R.id.price_seekBar);
        price = (TextView) view.findViewById(R.id.price);
        price.setText(R.string.free);
        price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 10;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0) {
                    price.setText("FREE");
                } else {
                    price.setText(progress + "€");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress == 0) {
                    price.setText("FREE");
                } else {
                    price.setText(progress + "€");
                }

            }
        });


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
        car = carCheck.isChecked();
        moto = motoCheck.isChecked();
        transport = busCheck.isChecked();

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
            url += "&Price="+price_bar.getProgress();
            FilterDepartmentFragment frag = new FilterDepartmentFragment();
            Bundle b = new Bundle();
            b.putBoolean("ToFrom",getArguments().getBoolean("ToFrom"));
            b.putString("url", url);
            frag.setArguments(b);
            ((FilterActivity)getActivity()).nextFrag(frag);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (carCheck.isChecked() || motoCheck.isChecked()){
            priceLayout.setVisibility(View.VISIBLE);
        }else{
            priceLayout.setVisibility(View.GONE);
        }
    }
}
