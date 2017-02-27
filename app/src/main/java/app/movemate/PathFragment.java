package app.movemate;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import app.movemate.ListAdapter.PassAdapter;

public class PathFragment extends Fragment implements OnMapReadyCallback {
    View view;
    String id;
    String url =  "http://movemate-api.azurewebsites.net/api/paths/getpath?PathId=";
    TextView pn,p,fa,ta,d,s,h,v,m,desc;
    ImageView imv,m_pic;
    Button join_btn, del_btn, disjoin_btn;
    String user_id = ((MainActivity)getActivity()).user_id;
    RelativeLayout rl;
    LinearLayout ll;
    MapView map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_path, container, false);
        try {
            id = new JSONObject(getArguments().getString("path")).getString("PathId");
            pn =(TextView)view.findViewById(R.id.pn);
            p =(TextView)view.findViewById(R.id.p);
            fa =(TextView)view.findViewById(R.id.fa);
            ta =(TextView)view.findViewById(R.id.ta);
            d =(TextView)view.findViewById(R.id.d);
            s =(TextView)view.findViewById(R.id.s);
            h =(TextView)view.findViewById(R.id.h);
            m = (TextView)view.findViewById(R.id.m_name);
            desc = (TextView)view.findViewById(R.id.desc);
            map = (MapView)view.findViewById(R.id.map);
            map.onCreate(savedInstanceState);
            map.getMapAsync(this);
            m_pic = (ImageView)view.findViewById(R.id.m_pic);
            imv =(ImageView)view.findViewById(R.id.i);
            join_btn = (Button)view.findViewById(R.id.join_btn);
            del_btn = (Button)view.findViewById(R.id.del_btn);
            disjoin_btn = (Button)view.findViewById(R.id.disjoin_btn);
            join_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    join();
                }
            });
            del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
            disjoin_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disjoin();
                }
            });



            getPathInfo();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    public void getPathInfo(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        url += id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        int i;
                        try {
                            jsonObject = new JSONObject(response);
                            d.setText(jsonObject.getString("Date"));
                            fa.setText(jsonObject.getString("StartAddress"));
                            ta.setText(jsonObject.getString("DestinationAddress"));
                            pn.setText(jsonObject.getString("PathName"));
                            //desc.setText(jsonObject.getString("Desc"));
                            i = jsonObject.getInt("Vehicle");

                            String uid = jsonObject.getJSONObject("Maker").getString("StudentId");

                            if (user_id.equals(uid)){
                                //NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                //View dView =  navigationView.getHeaderView(0);
                                //ImageView pic = (ImageView) dView.findViewById(R.id.photo);
                                //m_pic.setBackground(((MainActivity)getActivity()).pic);
                                del_btn.setVisibility(View.VISIBLE);

                            }else{
                                join_btn.setVisibility(View.VISIBLE);
                            }
                            JSONArray ja = jsonObject.getJSONArray("Participants");
                            int count = 0;
                            while(count<ja.length()){
                                JSONObject JO = ja.getJSONObject(count);
                                if (JO.getString("StudentId").equals(user_id)){
                                    join_btn.setVisibility(View.GONE);
                                    disjoin_btn.setVisibility(View.VISIBLE);
                                }
                                count++;
                            }
                            Drawable drawable;
                            if (i == 1){
                                rl = (RelativeLayout) view.findViewById(R.id.moto_ly);
                                rl.setVisibility(View.VISIBLE);

                                if (jsonObject.getBoolean("Head")){
                                    h.setText(R.string.yes);
                                    h.setTextColor(ContextCompat.getColor(p.getContext(),R.color.LightGreenA700));
                                }else{
                                    h.setText(R.string.no);
                                    h.setTextColor(ContextCompat.getColor(p.getContext(),R.color.RedA700));
                                }
                                drawable = ContextCompat.getDrawable(imv.getContext(),R.drawable.ic_motorcycle);
                                imv.setBackground(drawable);
                                int price = 0;
                                try {
                                    price = Integer.parseInt(jsonObject.getString("Price"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(price == 0){
                                    p.setText("FREE");
                                }else{
                                    p.setText(price+"€");
                                }
                                if (price < 3){
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.LightGreenA700));
                                }
                                else if (price < 7 && price >= 4){
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.Amber900));
                                }else{
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.RedA700));
                                }

                            }
                            else if (i == 2) {
                                ll = (LinearLayout) view.findViewById(R.id.bus_ly);
                                ll.setVisibility(View.VISIBLE);
                                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_bus);
                                imv.setBackground(drawable);
                                p.setText("FREE");

                                if (jsonObject.getBoolean("Train")){
                                    v =(TextView)view.findViewById(R.id.v_t);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (jsonObject.getBoolean("Tram")){
                                    v =(TextView)view.findViewById(R.id.v_tr);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (jsonObject.getBoolean("Bus")){
                                    v =(TextView)view.findViewById(R.id.v_b);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (jsonObject.getBoolean("Metro")){
                                    v =(TextView)view.findViewById(R.id.v_m);
                                    v.setVisibility(View.VISIBLE);
                                }
                                p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.LightGreenA700));

                            }
                            else{
                                rl = (RelativeLayout) view.findViewById(R.id.car_ly);
                                rl.setVisibility(View.VISIBLE);
                                s.setText(jsonObject.getInt("Seats")-ja.length()+"");
                                drawable = ContextCompat.getDrawable(imv.getContext(),R.drawable.ic_car);
                                imv.setBackground(drawable);
                                int price = 0;
                                try {
                                    price = Integer.parseInt(jsonObject.getString("Price"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(price == 0){
                                    p.setText("FREE");
                                }else{
                                    p.setText(price+"€");
                                }
                                if (price < 4){
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.LightGreenA700));
                                }
                                else if (price < 7 && price >= 4){
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.Amber900));
                                }else{
                                    p.setTextColor(ContextCompat.getColor(p.getContext(),R.color.RedA700));
                                }
                            }
                            if (i!=1){
                                if (ja.length()>0) {
                                    ll = (LinearLayout) view.findViewById(R.id.partecipants);
                                    ll.setVisibility(View.VISIBLE);
                                    RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
                                    PassAdapter passAdapter = new PassAdapter(getActivity(),ja);
                                    LinearLayoutManager layoutManager
                                            = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                    rec.setLayoutManager(layoutManager);
                                    rec.setItemAnimator(new DefaultItemAnimator());
                                    rec.setAdapter(passAdapter);
                                }
                            }
                            m.setText(jsonObject.getJSONObject("Maker").getString("Name"));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void join(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "http://movemate-api.azurewebsites.net/api/paths/putjoinpath?StudentId="+user_id+"&PathId="+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        View t = getActivity().findViewById(R.id.myMates);
                        t.performClick();
                    }

                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Errore recupero percorsi",Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public void delete(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://movemate-api.azurewebsites.net/api/paths/deletepath?StudentId="+user_id+"&PathId="+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        View t = getActivity().findViewById(R.id.myMates);
                        t.performClick();
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void disjoin(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://movemate-api.azurewebsites.net/api/paths/putdisjoinpath?StudentId="+user_id+"&PathId="+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        View t = getActivity().findViewById(R.id.myMates);
                        t.performClick();
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJs9dd_-5hLxMRphsGkxcQs5o&key="
                +getResources().getString(R.string.API);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j = new JSONObject(response);
                            Double lat = j.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            Double lng = j.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            LatLng place = new LatLng(lat, lng);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 17));

                            googleMap.addMarker(new MarkerOptions()
                                    .position(place));

                            map.onResume();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

    }
}
