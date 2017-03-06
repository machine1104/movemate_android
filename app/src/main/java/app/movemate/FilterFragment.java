package app.movemate;


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
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import static app.movemate.R.id.venue;


public class FilterFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    private View view;
    private TextView price,distance;
    private SeekBar price_bar,distance_bar;
    private Spinner spinner_uni;
    private String[] venue_list;
    private AutoCompleteTextView venue;
    private String final_placeId;
    private static final String LOG_TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private JSONObject departmentId;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(41.891527, 12.491170), new LatLng(41.891527, 12.491170));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        distance_bar = (SeekBar) view.findViewById(R.id.distance_seekBar);
        distance = (TextView) view.findViewById(R.id.distance);
        distance.setText("500m");
        distance_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 500;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                distance.setText(progress + "m");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distance.setText(progress + "m");

            }
        });

        //-------------------------INDIRIZZO
        final AutoCompleteTextView address = (AutoCompleteTextView) view.findViewById(R.id.address);
        venue = (AutoCompleteTextView) view.findViewById(R.id.venue);

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

        ImageButton address_delete = (ImageButton)view.findViewById(R.id.address_delete);
        address_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setText("");
            }
        });

        ImageButton venue_delete = (ImageButton)view.findViewById(R.id.venue_delete);
        venue_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                venue.setText("");
            }
        });




        //-------------------------Set Università e Sede
        spinner_uni = (Spinner)view.findViewById(R.id.uni_spinner);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity)getActivity());
            mGoogleApiClient.disconnect();
        }
    }

}
