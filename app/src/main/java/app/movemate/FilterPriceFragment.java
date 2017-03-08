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


public class FilterPriceFragment extends Fragment {
    View view;
    SeekBar price_bar;
    TextView price;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_filter_price, container, false);

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

        Button confirm = (Button)view.findViewById(R.id.next);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        return view;
    }

    private void next() {
        String url = getArguments().getString("url");
        url += "&Price="+price_bar.getProgress();

        FilterDepartmentFragment frag = new FilterDepartmentFragment();
        Bundle b = new Bundle();
        b.putBoolean("ToFrom",getArguments().getBoolean("ToFrom"));
        b.putString("url", url);
        frag.setArguments(b);
        ((MainActivity) getActivity()).nextFrag(frag);

    }

}
