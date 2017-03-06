package app.movemate;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class CreateDateFragment extends Fragment {
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_date, container, false);

        //-------------------------Set Data e Ora

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
        TextView trip_date = (TextView)view.findViewById(R.id.group_date);
        String date = trip_date.getText().toString();
        TextView trip_time = (TextView) view.findViewById(R.id.group_time);
        String time = trip_time.getText().toString();
        if (date.equals(getResources().getString(R.string.hint_group_date)) || time.equals(getResources().getString(R.string.hint_group_time))){
            Toasty.error(getActivity(), getString(R.string.error_missing_date), Toast.LENGTH_SHORT, true).show();
        }else{
            CreateRouteFragment frag = new CreateRouteFragment();
            Bundle b = new Bundle();
            b.putString("route", route.toString());
            b.putString("date",date);
            b.putString("time",time);
            frag.setArguments(b);
            ((MainActivity) getActivity()).nextFrag(frag);
        }


    }

}
