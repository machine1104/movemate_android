package app.movemate;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.request.DirectionRequest;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

import app.movemate.ListAdapter.PassAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PathFragment extends Fragment implements OnMapReadyCallback {
    View view;
    String id;
    String url = "http://movemate-api.azurewebsites.net/api/paths/getpath?PathId=";
    TextView pn, p, fa, ta, d, s, h, v, m, desc;
    ImageView imv, m_pic;
    Button join_btn, del_btn, disjoin_btn;
    String user_id = ((MainActivity) getActivity()).user_id;
    RelativeLayout rl;
    LinearLayout ll;
    MapView map;
    LatLng origin = null;
    LatLng destination = null;
    JSONObject info = null;
    GoogleMap gMap;
    NestedScrollView scroller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_path, container, false);

        try {
            id = new JSONObject(getArguments().getString("path")).getString("PathId");
            pn = (TextView) view.findViewById(R.id.pn);
            p = (TextView) view.findViewById(R.id.p);
            fa = (TextView) view.findViewById(R.id.fa);
            ta = (TextView) view.findViewById(R.id.ta);
            d = (TextView) view.findViewById(R.id.d);
            s = (TextView) view.findViewById(R.id.s);
            h = (TextView) view.findViewById(R.id.h);
            m = (TextView) view.findViewById(R.id.m_name);
            desc = (TextView) view.findViewById(R.id.desc);
            map = (MapView) view.findViewById(R.id.map);
            map.onCreate(savedInstanceState);
            scroller = (NestedScrollView) view.findViewById(R.id.scroller);
            map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            scroller.requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            scroller.requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return map.onTouchEvent(event);
                }


            });
            map.getMapAsync(this);
            m_pic = (ImageView) view.findViewById(R.id.m_pic);
            imv = (ImageView) view.findViewById(R.id.i);
            join_btn = (Button) view.findViewById(R.id.join_btn);
            del_btn = (Button) view.findViewById(R.id.del_btn);
            disjoin_btn = (Button) view.findViewById(R.id.disjoin_btn);
            join_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.join_confirm))
                            .setConfirmText(getResources().getString(R.string.confirm))
                            .setCancelText(getResources().getString(R.string.cancel))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    join();
                                }
                            })
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                }
            });
            del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.delete_confirm))
                            .setConfirmText(getResources().getString(R.string.confirm))
                            .setCancelText(getResources().getString(R.string.cancel))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    delete();
                                }
                            })
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                }
            });
            disjoin_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.disjoin_confirm))
                            .setConfirmText(getResources().getString(R.string.confirm))
                            .setCancelText(getResources().getString(R.string.cancel))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    disjoin();
                                }
                            })
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .show();
                }
            });


            getPathInfo();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    private void getPathInfo() {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResources().getString(R.string.loading_info));
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        url += id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();


                        int i;
                        try {
                            info = new JSONObject(response);
                            d.setText(info.getString("Date"));
                            fa.setText(info.getString("StartAddress"));
                            ta.setText(info.getString("DestinationAddress"));
                            pn.setText(info.getString("PathName"));
                            desc.setText(info.getString("Description"));

                            if (new JSONObject(getArguments().getString("path")).getBoolean("ToFrom")) {
                                String addressFrom = new JSONObject(getArguments().getString("path")).getString("StartAddress");
                                String addressTo = new JSONObject(getArguments().getString("path")).getString("DepartmentAddress");
                                origin2LatLng(addressFrom, addressTo);
                            } else {
                                String addressTo = new JSONObject(getArguments().getString("path")).getString("DestinationAddress");
                                String addressFrom = new JSONObject(getArguments().getString("path")).getString("DepartmentAddress");
                                origin2LatLng(addressFrom, addressTo);
                            }

                            i = info.getInt("Vehicle");


                            String uid = info.getJSONObject("Maker").getString("StudentId");

                            if (user_id.equals(uid)) {
                                del_btn.setVisibility(View.VISIBLE);

                            } else {
                                join_btn.setVisibility(View.VISIBLE);
                            }
                            JSONArray ja = info.getJSONArray("Participants");
                            int count = 0;
                            while (count < ja.length()) {
                                JSONObject JO = ja.getJSONObject(count);
                                if (JO.getString("StudentId").equals(user_id)) {
                                    join_btn.setVisibility(View.GONE);
                                    disjoin_btn.setVisibility(View.VISIBLE);
                                }
                                count++;
                            }
                            Drawable drawable;
                            if (i == 1) {
                                rl = (RelativeLayout) view.findViewById(R.id.moto_ly);
                                rl.setVisibility(View.VISIBLE);

                                if (info.getBoolean("Head")) {
                                    h.setText(R.string.yes);
                                    h.setTextColor(ContextCompat.getColor(p.getContext(), R.color.GreenA800));
                                } else {
                                    h.setText(R.string.no);
                                    h.setTextColor(ContextCompat.getColor(p.getContext(), R.color.RedA700));
                                }
                                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_motorcycle);
                                imv.setBackground(drawable);
                                int price = 0;
                                try {
                                    price = Integer.parseInt(info.getString("Price"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (price == 0) {
                                    p.setText("FREE");
                                } else {
                                    p.setText(price + "€");
                                }
                                if (price < 4) {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.GreenA800));
                                } else if (price < 7 && price >= 4) {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.Amber900));
                                } else {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.RedA700));
                                }

                            } else if (i == 2) {
                                ll = (LinearLayout) view.findViewById(R.id.bus_ly);
                                ll.setVisibility(View.VISIBLE);
                                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_bus);
                                imv.setBackground(drawable);
                                p.setText("FREE");

                                if (info.getBoolean("Train")) {
                                    v = (TextView) view.findViewById(R.id.v_t);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (info.getBoolean("Tram")) {
                                    v = (TextView) view.findViewById(R.id.v_tr);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (info.getBoolean("Bus")) {
                                    v = (TextView) view.findViewById(R.id.v_b);
                                    v.setVisibility(View.VISIBLE);
                                }
                                if (info.getBoolean("Metro")) {
                                    v = (TextView) view.findViewById(R.id.v_m);
                                    v.setVisibility(View.VISIBLE);
                                }
                                p.setTextColor(Color.parseColor("#27ae60"));

                            } else {
                                rl = (RelativeLayout) view.findViewById(R.id.car_ly);
                                rl.setVisibility(View.VISIBLE);
                                s.setText(info.getInt("Seats") - ja.length() + "");
                                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_car);
                                imv.setBackground(drawable);
                                int price = 0;
                                try {
                                    price = Integer.parseInt(info.getString("Price"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (price == 0) {
                                    p.setText("FREE");
                                } else {
                                    p.setText(price + "€");
                                }
                                if (price < 4) {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.GreenA800));
                                } else if (price < 7 && price >= 4) {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.Amber900));
                                } else {
                                    p.setTextColor(ContextCompat.getColor(p.getContext(), R.color.RedA700));
                                }
                            }
                            if (i != 1) {
                                if (ja.length() > 0) {
                                    ll = (LinearLayout) view.findViewById(R.id.partecipants);
                                    ll.setVisibility(View.VISIBLE);
                                    RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
                                    PassAdapter passAdapter = new PassAdapter(getActivity(), ja);
                                    LinearLayoutManager layoutManager
                                            = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                                    rec.setLayoutManager(layoutManager);
                                    rec.setItemAnimator(new DefaultItemAnimator());
                                    rec.setAdapter(passAdapter);
                                }
                            }
                            m.setText(info.getJSONObject("Maker").getString("Name"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                getFragmentManager().popBackStack();
            }
        });

        queue.add(stringRequest);
    }

    private void join() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String url = "http://movemate-api.azurewebsites.net/api/paths/putjoinpath?StudentId=" + user_id + "&PathId=" + id;
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
                Toast.makeText(getActivity(), "Errore recupero percorsi", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void delete() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://movemate-api.azurewebsites.net/api/paths/deletepath?StudentId=" + user_id + "&PathId=" + id;
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

    private void disjoin() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://movemate-api.azurewebsites.net/api/paths/putdisjoinpath?StudentId=" + user_id + "&PathId=" + id;
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

    private void origin2LatLng(String from, final String to) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        from = from.replace(" ", "");
        from = Normalizer.normalize(from, Normalizer.Form.NFD);
        from = from.replaceAll("[^\\p{ASCII}]", "");
        String url = "http://maps.google.com/maps/api/geocode/json?address=" + from;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            double lat = ((JSONArray) json.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            double lng = ((JSONArray) json.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            origin = new LatLng(lat, lng);
                            destination2LatLng(to);

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

    private void destination2LatLng(String to) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        to = to.replace(" ", "");
        to = Normalizer.normalize(to, Normalizer.Form.NFD);
        to = to.replaceAll("[^\\p{ASCII}]", "");
        String url = "http://maps.google.com/maps/api/geocode/json?address=" + to;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            double lat = ((JSONArray) json.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            double lng = ((JSONArray) json.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            destination = new LatLng(lat, lng);
                            createRoute();

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

    private void createRoute() throws JSONException {

        DirectionRequest direction = GoogleDirection.withServerKey(getResources().getString(R.string.API))
                .from(origin)
                .to(destination);
        if (info.getInt("Vehicle") != 2) {
            direction = direction.transportMode(TransportMode.DRIVING);
        } else {
            direction = direction.transportMode(TransportMode.TRANSIT);
            if (info.getBoolean("Train")) {
                direction = direction.transitMode(TransitMode.TRAIN);
            }
            if (info.getBoolean("Bus")) {
                direction = direction.transitMode(TransitMode.BUS);
            }
            if (info.getBoolean("Metro")) {
                direction = direction.transitMode(TransitMode.SUBWAY);
            }
            if (info.getBoolean("Tram")) {
                direction = direction.transitMode(TransitMode.TRAM);
            }
        }


        direction.execute(new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction, String rawBody) {
                if (direction.isOK()) {
                    map.onResume();
                    Route route = direction.getRouteList().get(0);
                    Leg leg = route.getLegList().get(0);
                    ArrayList<LatLng> pointList = leg.getDirectionPoint();
                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), pointList, 5, getResources().getColor(R.color.colorPrimary));
                    gMap.addPolyline(polylineOptions);
                    gMap.getUiSettings().setZoomControlsEnabled(true);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(origin);
                    builder.include(destination);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);

                    gMap.addMarker(new MarkerOptions().position(origin)).setIcon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    gMap.addMarker(new MarkerOptions().position(destination)).setIcon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    gMap.animateCamera(cu);

                }
            }

            @Override
            public void onDirectionFailure(Throwable t) {

            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }
}
