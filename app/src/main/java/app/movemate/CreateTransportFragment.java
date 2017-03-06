package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class CreateTransportFragment extends Fragment {
    View view;
    private SeekBar avaible_bar, car_price_bar, moto_price_bar;
    private TextView avaible, car_price, moto_price;
    private RadioGroup vehicles;
    private int c_price;
    private int m_price;
    private int c_seats = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_transport, container, false);
        //-------------------------Barra posti disponibili Car
        avaible_bar = (SeekBar) view.findViewById(R.id.car_avaible_seekBar);
        avaible = (TextView) view.findViewById(R.id.car_avaible);
        avaible.setText(avaible_bar.getProgress() + 1 + "");
        avaible_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 1;



            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                avaible.setText(progress + 1 + "");
                c_seats = progress;



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                avaible.setText(progress + 1 + "");
                c_seats = progress;

            }
        });

        //-------------------------Barra prezzo Car
        car_price_bar = (SeekBar) view.findViewById(R.id.car_price_seekBar);
        car_price = (TextView) view.findViewById(R.id.car_price);
        car_price.setText(R.string.free);
        car_price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0) {
                    car_price.setText(R.string.free);
                } else {
                    car_price.setText(progress + "€");
                }
                c_price = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress == 0) {
                    car_price.setText(R.string.free);
                } else {
                    car_price.setText(progress + "€");
                }
                c_price = progress;
            }
        });

        //-------------------------Barra prezzo Moto
        moto_price_bar = (SeekBar) view.findViewById(R.id.moto_price_seekBar);
        moto_price = (TextView) view.findViewById(R.id.moto_price);
        moto_price.setText(R.string.free);
        moto_price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0) {
                    moto_price.setText(R.string.free);
                } else {
                    moto_price.setText(progress + "€");
                }
                m_price = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress == 0) {
                    moto_price.setText("FREE");
                } else {
                    moto_price.setText(progress + "€");
                }
                m_price = progress;
            }
        });


        //-------------------------Visualizzazione prezzo/disponibilità
        final RelativeLayout car_price = (RelativeLayout) view.findViewById(R.id.car_price_layout);
        final RelativeLayout car_avaible = (RelativeLayout) view.findViewById(R.id.car_avaible_layout);
        final RelativeLayout moto_price = (RelativeLayout) view.findViewById(R.id.moto_price_layout);
        final LinearLayout public_layout = (LinearLayout) view.findViewById(R.id.public_transport_layout);
        moto_price.setVisibility(View.GONE);
        public_layout.setVisibility(View.GONE);

        vehicles = (RadioGroup) view.findViewById(R.id.vehicles);
        vehicles.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.car) {
                    car_price.setVisibility(View.VISIBLE);
                    car_avaible.setVisibility(View.VISIBLE);
                    moto_price.setVisibility(View.GONE);
                    public_layout.setVisibility(View.GONE);
                }
                if (i == R.id.moto) {
                    car_price.setVisibility(View.GONE);
                    car_avaible.setVisibility(View.GONE);
                    public_layout.setVisibility(View.GONE);
                    moto_price.setVisibility(View.VISIBLE);
                }
                if (i == R.id.public_transport) {
                    car_price.setVisibility(View.GONE);
                    car_avaible.setVisibility(View.GONE);
                    moto_price.setVisibility(View.GONE);
                    public_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        Button next = (Button)view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    next();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    private void next() throws JSONException {
        JSONObject route = new JSONObject(this.getArguments().getString("route"));
        int vId = vehicles.getCheckedRadioButtonId();
        if (vId == R.id.car) {
            route.put("vId",0);
            route.put("Price", c_price);
            route.put("Seats", c_seats + 1);

        }
        if (vId == R.id.moto) {
            route.put("vId",1);
            route.put("Price", m_price);
            route.put("Head", ((CheckBox) view.findViewById(R.id.helmet)).isChecked());

        }
        if (vId == R.id.public_transport) {
            route.put("vId",2);
            Boolean tram = ((CheckBox) view.findViewById(R.id.tram)).isChecked();
            Boolean train = ((CheckBox) view.findViewById(R.id.train)).isChecked();
            Boolean metro = ((CheckBox) view.findViewById(R.id.metro)).isChecked();
            Boolean bus = ((CheckBox) view.findViewById(R.id.bus)).isChecked();

            if (!tram && !train && !metro && !bus) {
                Toasty.error(getActivity(), getString(R.string.error_missing_public_type), Toast.LENGTH_SHORT, true).show();
            } else {
                if (tram) {
                    route.put("Tram", true);
                }
                if (metro) {
                    route.put("Metro", true);
                }
                if (bus) {
                    route.put("Bus", true);
                }
                if (train) {
                    route.put("Train", true);
                }

            }


        }
        CreateDateFragment frag = new CreateDateFragment();
        Bundle b = new Bundle();
        b.putString("route", route.toString());
        frag.setArguments(b);
        ((MainActivity) getActivity()).nextFrag(frag);
    }
}