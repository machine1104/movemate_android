package app.movemate;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class CreateFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private SeekBar avaible_bar, car_price_bar, moto_price_bar;
    private TextView avaible, car_price, moto_price;
    private RadioGroup vehicles;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(41.891527, 12.491170), new LatLng(41.891527, 12.491170));
    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_create, container, false);

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

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                avaible.setText(progress + 1 + "");

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
            }
        });


        //-------------------------Visualizzazione prezzo/disponibilità
        final RelativeLayout car_price = (RelativeLayout) v.findViewById(R.id.car_price_layout);
        final RelativeLayout car_avaible = (RelativeLayout) v.findViewById(R.id.car_avaible_layout);
        final RelativeLayout moto_price = (RelativeLayout) v.findViewById(R.id.moto_price_layout);
        final LinearLayout public_layout = (LinearLayout) v.findViewById(R.id.public_transport_layout);
        car_price.setVisibility(View.GONE);
        car_avaible.setVisibility(View.GONE);
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


        //-------------------------Autocomplete
        final AutoCompleteTextView from = (AutoCompleteTextView) v.findViewById(R.id.from);
        final AutoCompleteTextView to = (AutoCompleteTextView) v.findViewById(R.id.to);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((FragmentActivity) getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS, null);

        from.setThreshold(3);
        from.setOnItemClickListener(mAutocompleteClickListener);
        from.setAdapter(mPlaceArrayAdapter);


        to.setThreshold(3);
        to.setOnItemClickListener(mAutocompleteClickListener);
        to.setAdapter(mPlaceArrayAdapter);

        ImageButton from_delete = (ImageButton)v.findViewById(R.id.from_delete);
        from_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from.setText("");
            }
        });

        ImageButton to_delete = (ImageButton)v.findViewById(R.id.to_delete);
        to_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to.setText("");
            }
        });


        //------------------------------------

        //IMPLEMENTARE .SHOW() ONBACKPRESSED
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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
            Log.i(LOG_TAG, "Selected: " + item.description);
            // FUNZIONE GET PLACE BY ID - SALVARE SOLO ID NEL DATABASE
            /* Creare GoogleApiClient
             * Creare PlaceBuffer by ID
             * Prendere il primo elemento
             * Prendere le info tramite getName() ecc*/

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

