package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


public class FilterFragment extends Fragment {
    View view;
    TextView price;
    SeekBar price_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_filter, container, false);
        Button confirm = (Button)view.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply();
            }
        });

        price_bar = (SeekBar) view.findViewById(R.id.price_seekBar);
        price = (TextView) view.findViewById(R.id.price);
        price.setText("10€");
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

        return view;
    }

    private void apply(){
        String url = "http://movemate-api.azurewebsites.net/api/paths/getfilteredpaths?";
        Boolean car,moto,transport;
        car = ((CheckBox)view.findViewById(R.id.car)).isChecked();
        moto = ((CheckBox)view.findViewById(R.id.moto)).isChecked();
        transport = ((CheckBox)view.findViewById(R.id.public_transport)).isChecked();

        if(!car && !moto && !transport){
            Toasty.error(getActivity(),getString(R.string.missing_vehicle), Toast.LENGTH_SHORT, true).show();
        }else {

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

            FindPathFragment frag = new FindPathFragment();
            Bundle b = new Bundle();
            b.putString("url", url);
            frag.setArguments(b);
            ((MainActivity) getActivity()).nextFrag(frag);
        }


    }

}
