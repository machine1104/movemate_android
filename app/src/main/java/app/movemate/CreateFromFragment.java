package app.movemate;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class CreateFromFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    private SeekBar avaible_bar, car_price_bar, moto_price_bar;
    private TextView avaible, car_price, moto_price;
    private RadioGroup vehicles;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(41.891527, 12.491170), new LatLng(41.891527, 12.491170));
    private String final_placeId;
    private Spinner spinner_uni;
    private String[] venue_list;
    private AutoCompleteTextView venue;
    JSONObject departmentId;
    private int c_price;
    private int m_price;
    private int c_seats = 1;
    String date,time;
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_create_from, container, false);

        //-------------------------Barra posti disponibili Car
        avaible_bar = (SeekBar) v.findViewById(R.id.car_avaible_seekBar);
        avaible = (TextView) v.findViewById(R.id.car_avaible);
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
        car_price_bar = (SeekBar) v.findViewById(R.id.car_price_seekBar);
        car_price = (TextView) v.findViewById(R.id.car_price);
        car_price.setText("FREE");
        car_price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0) {
                    car_price.setText("FREE");
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
                    car_price.setText("FREE");
                } else {
                    car_price.setText(progress + "€");
                }
                c_price = progress;
            }
        });

        //-------------------------Barra prezzo Moto
        moto_price_bar = (SeekBar) v.findViewById(R.id.moto_price_seekBar);
        moto_price = (TextView) v.findViewById(R.id.moto_price);
        moto_price.setText("FREE");
        moto_price_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                if (progress == 0) {
                    moto_price.setText("FREE");
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
        final RelativeLayout car_price = (RelativeLayout) v.findViewById(R.id.car_price_layout);
        final RelativeLayout car_avaible = (RelativeLayout) v.findViewById(R.id.car_avaible_layout);
        final RelativeLayout moto_price = (RelativeLayout) v.findViewById(R.id.moto_price_layout);
        final LinearLayout public_layout = (LinearLayout) v.findViewById(R.id.public_transport_layout);

        moto_price.setVisibility(View.GONE);
        public_layout.setVisibility(View.GONE);

        vehicles = (RadioGroup) v.findViewById(R.id.vehicles);
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


        //-------------------------INDIRIZZO
        final AutoCompleteTextView address = (AutoCompleteTextView) v.findViewById(R.id.address);
        venue = (AutoCompleteTextView) v.findViewById(R.id.venue);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS, null);

        address.setThreshold(3);
        address.setOnItemClickListener(mAutocompleteClickListener);
        address.setAdapter(mPlaceArrayAdapter);

        ImageButton address_delete = (ImageButton)v.findViewById(R.id.address_delete);
        address_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setText("");
            }
        });

        ImageButton venue_delete = (ImageButton)v.findViewById(R.id.venue_delete);
        venue_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                venue.setText("");
            }
        });




        //-------------------------Set Università e Sede


        spinner_uni = (Spinner)v.findViewById(R.id.uni_spinner);
        getUni();

        spinner_uni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getVenues((int)id+1);
                venue.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });






        //-------------------------Set Data e Ora

        ImageButton date_btn = (ImageButton)v.findViewById(R.id.date_btn);
        ImageButton time_btn = (ImageButton)v.findViewById(R.id.time_btn);

        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int yearToday = c.get(Calendar.YEAR);
                final int monthToday = c.get(Calendar.MONTH);
                final int dayToday = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        EditText date= (EditText) v.findViewById(R.id.group_date);
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
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        EditText time= (EditText) v.findViewById(R.id.group_time);
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
        //-------------------------Crea Viaggio
        Button create_btn = (Button)v.findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject trip = new JSONObject();
                try {
                    trip.put("ToFrom",false);
                    trip.put("StudentId",((MainActivity)getActivity()).user_id);

                    EditText trip_name = (EditText)v.findViewById(R.id.trip_name);
                    String name = trip_name.getText().toString();
                    EditText trip_date = (EditText)v.findViewById(R.id.group_date);
                    date = trip_date.getText().toString();
                    EditText trip_time = (EditText)v.findViewById(R.id.group_time);
                    time = trip_time.getText().toString();
                    String adrs = address.getText().toString();
                    String ven = venue.getText().toString();
                    EditText trip_desc = (EditText)v.findViewById(R.id.desc);
                    String desc = trip_desc.getText().toString();

                    if(name.equals("") || date.equals("") || time.equals("") || adrs.equals("") || ven.equals("") || desc.equals("") ){
                        Toasty.error(getActivity(),getString(R.string.missing_fields),Toast.LENGTH_SHORT, true).show();
                    }else if (!departmentId.has(ven)){
                        Toasty.error(getActivity(),getString(R.string.dep_err),Toast.LENGTH_SHORT, true).show();
                    }

                    else{
                        trip.put("PathName",name);
                        trip.put("Address",adrs);
                        trip.put("DepId",departmentId.getString(ven));
                        trip.put("Description",desc);
                        int vId = vehicles.getCheckedRadioButtonId();
                        if (vId==R.id.car){
                            trip.put("Price",c_price);
                            trip.put("Seats",c_seats+1);
                            create(0,trip);
                        }
                        if (vId==R.id.moto){
                            trip.put("Price",m_price);
                            trip.put("Head",((CheckBox)v.findViewById(R.id.helmet)).isChecked());
                            create(1,trip);
                        }
                        if (vId==R.id.public_transport){

                            Boolean tram = ((CheckBox)v.findViewById(R.id.tram)).isChecked();
                            Boolean train = ((CheckBox)v.findViewById(R.id.train)).isChecked();
                            Boolean metro = ((CheckBox)v.findViewById(R.id.metro)).isChecked();
                            Boolean bus = ((CheckBox)v.findViewById(R.id.bus)).isChecked();

                            if (!tram && !train && !metro && !bus){
                                Toasty.error(getActivity(),getString(R.string.missing_vehicle),Toast.LENGTH_SHORT, true).show();
                            }else{
                                if (tram){
                                    trip.put("Tram",true);
                                }
                                if (metro){
                                    trip.put("Metro",true);
                                }
                                if (bus){
                                    trip.put("Bus",true);
                                }
                                if (train){
                                    trip.put("Train",true);
                                }
                                create(2,trip);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        return v;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(getActivity(),"Google Places API connection failed with error code:" +
                connectionResult.getErrorCode(),Toast.LENGTH_LONG).show();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            final_placeId = placeId;
            Log.i(LOG_TAG, final_placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            /*
            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }*/
        }
    };


    private void getUni() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://movemate-api.azurewebsites.net/api/universities/getuniversities";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //parsing
                        try {
                            JSONArray json = new JSONArray(response);
                            String[] uni_list = new String[json.length()];
                            for (int i = 0; i< uni_list.length;i++){
                                JSONObject obj = new JSONObject(json.getString(i));
                                uni_list[i] = obj.getString("UniversityName");


                            }
                            spinner_uni.setAdapter(new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_item, uni_list));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void getVenues(int s) {

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "http://movemate-api.azurewebsites.net/api/departments/getdepartments/"+s;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray json = new JSONArray(response);
                            venue_list = new String[json.length()];
                            departmentId = new JSONObject();
                            for (int i = 0;i<json.length();i++){
                                departmentId.put(new JSONObject(json.getString(i)).getString("DepartmentName")+", "+new JSONObject(json.getString(i)).getString("Address"),new JSONObject(json.getString(i)).getString("DepartmentId"));
                                venue_list[i] = new JSONObject(json.getString(i)).getString("DepartmentName")+", "+new JSONObject(json.getString(i)).getString("Address");
                            }
                            venue.setAdapter(new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_item, venue_list));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void create(int n, final JSONObject json) throws JSONException {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResources().getString(R.string.loading_creation));
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url;


        if (n == 0){
            url = "http://movemate-api.azurewebsites.net/api/paths/postpathcar";
        }else if (n == 1){
            url = "http://movemate-api.azurewebsites.net/api/paths/postpathcyc";
        }else{
            url = "http://movemate-api.azurewebsites.net/api/paths/postpathpub";
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Toasty.success(getActivity(), "Success!", Toast.LENGTH_SHORT, true).show();
                        View t = getActivity().findViewById(R.id.myMates);
                        t.performClick();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toasty.error(getActivity(), getString(R.string.error_create), Toast.LENGTH_SHORT, true).show();
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                String s = json.toString();
                String js = s.substring(0,s.indexOf("}"));
                js = js+",\"Date\":\""+date+" "+time+"\"}";

                return js.getBytes();
            }

        };

        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity)getActivity());
            mGoogleApiClient.disconnect();
        }
    }


    // FUNZIONE GET PLACE BY ID - SALVARE SOLO ID NEL DATABASE
            /* Creare GoogleApiClient
             * Creare PlaceBuffer by ID
             * Prendere il primo elemento
             * Prendere le info tramite getName() ecc*/
}

