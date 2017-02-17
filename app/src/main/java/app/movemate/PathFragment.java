package app.movemate;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import app.movemate.ListAdapter.Path;

public class PathFragment extends Fragment {
    View view;
    String id;
    String url =  " http://movemate-api.azurewebsites.net/api/paths/getpath?PathId=";
    TextView pn,p,fa,ta,d;
    ImageView imv;
    Button join_btn, del_btn, disjoin_btn;
    String user_id = ((MainActivity)getActivity()).user_id;


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
                        int i = 0;
                        try {
                            jsonObject = new JSONObject(response);
                            d.setText(jsonObject.getString("Date"));
                            fa.setText(jsonObject.getString("StartAddress"));
                            ta.setText(jsonObject.getString("DestinationAddress"));
                            pn.setText(jsonObject.getString("PathName"));
                            i = jsonObject.getInt("Vehicle");
                            Drawable drawable;
                            if (i == 1){
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
                                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_bus);
                                imv.setBackground(drawable);
                                p.setText("FREE");
                                p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.LightGreenA700));

                            }
                            else{
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
                            String uid = jsonObject.getJSONObject("Maker").getString("StudentId");

                            if (user_id.equals(uid)){
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

}
