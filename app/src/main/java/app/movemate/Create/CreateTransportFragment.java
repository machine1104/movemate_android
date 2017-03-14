package app.movemate.Create;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import app.movemate.CreateActivity;
import app.movemate.CreateRouteFragment;
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
        //-------------------------Data e Ora
        ImageButton date_btn = (ImageButton)view.findViewById(R.id.date_btn);
        ImageButton time_btn = (ImageButton)view.findViewById(R.id.time_btn);

        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int yearToday = c.get(Calendar.YEAR);
                final int monthToday = c.get(Calendar.MONTH);
                final int dayToday = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog v, int year, int monthOfYear, int dayOfMonth) {
                        TextView date= (TextView) view.findViewById(R.id.group_date);
                        int month = monthOfYear+1;
                        if(year > yearToday){
                            date.setText(dayOfMonth+"/"+month+"/"+year);
                        }
                        else if(monthOfYear > monthToday && year >= yearToday){
                            date.setText(dayOfMonth+"/"+month+"/"+year);
                        }
                        else if(dayOfMonth >= dayToday && monthOfYear >= monthToday && year >= yearToday){
                            date.setText(dayOfMonth+"/"+month+"/"+year);
                        }
                    }
                },yearToday,monthToday,dayToday);
                datePicker.show(getFragmentManager(),"");
            }
        });

        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout v, int hourOfDay, int minute, int second) {
                        TextView time= (TextView) view.findViewById(R.id.group_time);
                        String minuti = minute+"";
                        if (minuti.length()==1){
                            minuti = "0"+minuti;
                        }
                        time.setText(hourOfDay+":"+minuti);
                    }
                },hour,minute,true);
                timePicker.show(getFragmentManager(),"");
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
        Boolean tram = ((CheckBox) view.findViewById(R.id.tram)).isChecked();
        Boolean train = ((CheckBox) view.findViewById(R.id.train)).isChecked();
        Boolean metro = ((CheckBox) view.findViewById(R.id.metro)).isChecked();
        Boolean bus = ((CheckBox) view.findViewById(R.id.bus)).isChecked();
        TextView trip_date = (TextView)view.findViewById(R.id.group_date);
        String date = trip_date.getText().toString();
        TextView trip_time = (TextView) view.findViewById(R.id.group_time);
        String time = trip_time.getText().toString();
        int vId = vehicles.getCheckedRadioButtonId();
        if (vId == R.id.car) {

            route.put("vId",0);
            route.put("Price", c_price);
            route.put("Seats", c_seats + 1);

        }
        else if (vId == R.id.moto) {
            route.put("vId",1);
            route.put("Price", m_price);
            route.put("Head", ((CheckBox) view.findViewById(R.id.helmet)).isChecked());

        }
        else if (vId == R.id.public_transport) {
            route.put("vId",2);
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
        if (vId == R.id.public_transport && !tram && !train && !metro && !bus){
            Toasty.error(getActivity(), getString(R.string.error_missing_public_type), Toast.LENGTH_SHORT, true).show();
        }
        else if (date.equals(getResources().getString(R.string.hint_route_date)) || time.equals(getResources().getString(R.string.hint_route_time))) {
            Toasty.error(getActivity(), getString(R.string.error_missing_date), Toast.LENGTH_SHORT, true).show();
        }else{
            CreateRouteFragment frag = new CreateRouteFragment();
            Bundle b = new Bundle();
            b.putString("route", route.toString());
            b.putString("date",date);
            b.putString("time",time);
            frag.setArguments(b);
            ((CreateActivity) getActivity()).nextFrag(frag);
        }
    }
}