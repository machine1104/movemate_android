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
import android.widget.ImageButton;
import android.widget.Spinner;
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

import app.movemate.Create.CreateInfoFragment;
import es.dmoral.toasty.Toasty;


public class CreateRouteFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    View view;
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
    private AutoCompleteTextView address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            Boolean toFrom = (new JSONObject(getArguments().getString("route"))).getBoolean("ToFrom");
            if (toFrom){
                view = inflater.inflate(R.layout.fragment_create_route_to, container, false);
            }else{
                view = inflater.inflate(R.layout.fragment_create_route_from, container, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //-------------------------Autocomplete
        address = (AutoCompleteTextView) view.findViewById(R.id.address);
        venue = (AutoCompleteTextView) view.findViewById(R.id.venue);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS, null);

        address.setThreshold(2);
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
        String adrs = address.getText().toString();
        String ven = venue.getText().toString();

        Log.d("json",route.toString());
        if(adrs.equals("") || ven.equals("")) {
            Toasty.error(getActivity(), getString(R.string.missing_fields), Toast.LENGTH_SHORT, true).show();
        }
        else if (!departmentId.has(ven)){
            Toasty.error(getActivity(), getString(R.string.error_venue), Toast.LENGTH_SHORT, true).show();
        }else{
            route.put("Address",adrs);
            route.put("DepId",departmentId.getString(ven));
            CreateInfoFragment frag = new CreateInfoFragment();
            Bundle b = new Bundle();
            b.putString("route", route.toString());
            b.putString("date",getArguments().getString("date"));
            b.putString("time",getArguments().getString("time"));
            frag.setArguments(b);
            ((CreateActivity) getActivity()).nextFrag(frag);
        }




    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity)getActivity());
            mGoogleApiClient.disconnect();
        }
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
                        //parsing

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


}
