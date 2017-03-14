package app.movemate.Filter;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.movemate.FilterActivity;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class FilterDepartmentFragment extends Fragment {
    View view;
    private int dId;
    private AutoCompleteTextView venue;
    private Spinner spinner_uni;
    private String[] venue_list;
    JSONObject departmentId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Boolean toFrom = getArguments().getBoolean("ToFrom");
        if (toFrom){
            view = inflater.inflate(R.layout.fragment_filter_department_to, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_filter_department_from, container, false);
        }
        venue = (AutoCompleteTextView) view.findViewById(R.id.venue);

        ImageButton venue_delete = (ImageButton)view.findViewById(R.id.venue_delete);
        venue_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                venue.setText("");
            }
        });

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

        Button confirm = (Button)view.findViewById(R.id.next);
        confirm.setOnClickListener(new View.OnClickListener() {
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

    private void next() throws JSONException {

        if (!departmentId.has(venue.getText().toString())) {
            Toasty.error(getActivity(), getString(R.string.error_venue), Toast.LENGTH_SHORT, true).show();
        }else{

        String url = getArguments().getString("url")+"&DepId="+ departmentId.get(venue.getText().toString());

        FindPathFragment frag = new FindPathFragment();
        Bundle b = new Bundle();
        b.putBoolean("ToFrom",getArguments().getBoolean("ToFrom"));
        b.putString("url", url);
        frag.setArguments(b);
        ((FilterActivity) getActivity()).nextFrag(frag);
        }

    }

}
