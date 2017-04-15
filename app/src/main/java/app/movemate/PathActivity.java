package app.movemate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.movemate.Adapters.PassAdapter;
import app.movemate.Wizard.PhoneActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class PathActivity extends AppCompatActivity implements OnMapReadyCallback {
    String id;
    String url = "http://movemate-api.azurewebsites.net/api/paths/getpath?PathId=";
    TextView pn, p, fa, ta, d, s, h, v, m, desc;
    ImageView imv, m_pic;
    Button join_btn, del_btn, disjoin_btn,close_btn,feed_btn,call_btn,sms_btn;
    String user_id = MainActivity.user_id;
    RelativeLayout rl;
    LinearLayout ll;
    MapView map;
    LatLng origin = null;
    LatLng destination = null;
    JSONObject info = null;
    GoogleMap gMap;
    NestedScrollView scroller;
    int maker_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.path_info);


        try {
            id = new JSONObject(getIntent().getStringExtra("path")).getString("PathId");



            pn = (TextView) findViewById(R.id.pn);
            p = (TextView) findViewById(R.id.p);
            fa = (TextView) findViewById(R.id.fa);
            ta = (TextView) findViewById(R.id.ta);
            d = (TextView) findViewById(R.id.d);
            s = (TextView) findViewById(R.id.s);
            h = (TextView) findViewById(R.id.h);
            m = (TextView) findViewById(R.id.m_name);
            desc = (TextView) findViewById(R.id.desc);
            map = (MapView) findViewById(R.id.map);
            map.onCreate(savedInstanceState);
            scroller = (NestedScrollView) findViewById(R.id.scroller);
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
            m_pic = (ImageView) findViewById(R.id.m_pic);
            imv = (ImageView) findViewById(R.id.i);
            join_btn = (Button) findViewById(R.id.join_btn);
            del_btn = (Button) findViewById(R.id.del_btn);
            disjoin_btn = (Button) findViewById(R.id.disjoin_btn);
            close_btn = (Button) findViewById(R.id.close_btn);
            feed_btn = (Button) findViewById(R.id.feed_btn);
            join_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(PathActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                    new SweetAlertDialog(PathActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                    new SweetAlertDialog(PathActivity.this, SweetAlertDialog.WARNING_TYPE)
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
            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(PathActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.close_confirm))
                            .setContentText(getResources().getString(R.string.close_text))
                            .setConfirmText(getResources().getString(R.string.confirm))
                            .setCancelText(getResources().getString(R.string.cancel))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    close();
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
            feed_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feed();
                }
            });
            ImageView m_pic = (ImageView)findViewById(R.id.m_pic);
            m_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getUserInfo();
                }
            });
            call_btn = (Button)findViewById(R.id.call_btn);
            sms_btn = (Button)findViewById(R.id.sms_btn);


            getPathInfo();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getPathInfo() {

        int i;
        try {

            info = new JSONObject(getIntent().getStringExtra("path"));
            maker_id = info.getJSONObject("Maker").getInt("StudentId");
            d.setText(info.getString("Date"));
            fa.setText(info.getString("StartAddress"));
            ta.setText(info.getString("DestinationAddress"));
            pn.setText(info.getString("PathName"));
            String sDesc = info.getString("Description");
            if (sDesc.length()>0){
                LinearLayout l = (LinearLayout)findViewById(R.id.descL);
                l.setVisibility(View.VISIBLE);
                desc.setText(sDesc);
            }

            call_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    try {
                        String n = info.getJSONObject("Maker").getString("PhoneNumber");
                        intent.setData(Uri.parse("tel:"+n));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            sms_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String n = info.getJSONObject("Maker").getString("PhoneNumber");
                        Intent intentsms = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + n ) );
                        intentsms.putExtra( "sms_body", "" );
                        startActivity( intentsms );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://movemate-api.azurewebsites.net/api/students/getphoto?id="+info.getJSONObject("Maker").getString("StudentId");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String imageBytes = response;
                            byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
                            Glide.with(PathActivity.this).load(imageByteArray)
                                    .bitmapTransform(new CropCircleTransformation(PathActivity.this))
                                    .into((ImageView) findViewById(R.id.m_pic));



                        }
                    }
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                    return map;
                }
            };

            queue.add(stringRequest);

            if (info.getBoolean("ToFrom")) {
                String addressFrom = info.getString("StartAddress");
                String addressTo = info.getString("DepartmentAddress");
                origin2LatLng(addressFrom, addressTo);
            } else {
                String addressTo = info.getString("DestinationAddress");
                String addressFrom = info.getString("DepartmentAddress");
                origin2LatLng(addressFrom, addressTo);
            }

            i = info.getInt("Vehicle");


            String uid = info.getJSONObject("Maker").getString("StudentId");
            JSONArray ja = info.getJSONArray("Participants");
            if (user_id.equals(uid)) {
                del_btn.setVisibility(View.VISIBLE);
                if (info.getBoolean("Open")) {
                    close_btn.setVisibility(View.VISIBLE);
                }

            }else {
                if(info.getInt("Seats")>ja.length()){
                    join_btn.setVisibility(View.VISIBLE);
                }
                int count = 0;
                while (count < ja.length()) {
                    JSONObject JO = ja.getJSONObject(count);
                    if (JO.getString("StudentId").equals(user_id)) {
                        if (info.getBoolean("Open")) {
                            join_btn.setVisibility(View.GONE);
                            disjoin_btn.setVisibility(View.VISIBLE);
                        } else {
                            join_btn.setVisibility(View.GONE);
                            feed_btn.setVisibility(View.VISIBLE);
                        }
                    }
                    count++;
                }


            }


            Drawable drawable;
            if (i == 1) {
                rl = (RelativeLayout) findViewById(R.id.moto_ly);
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
                ll = (LinearLayout) findViewById(R.id.bus_ly);
                ll.setVisibility(View.VISIBLE);
                drawable = ContextCompat.getDrawable(imv.getContext(), R.drawable.ic_bus);
                imv.setBackground(drawable);
                p.setText("FREE");

                if (info.getBoolean("Train")) {
                    v = (TextView) findViewById(R.id.v_t);
                    v.setVisibility(View.VISIBLE);
                }
                if (info.getBoolean("Tram")) {
                    v = (TextView) findViewById(R.id.v_tr);
                    v.setVisibility(View.VISIBLE);
                }
                if (info.getBoolean("Bus")) {
                    v = (TextView) findViewById(R.id.v_b);
                    v.setVisibility(View.VISIBLE);
                }
                if (info.getBoolean("Metro")) {
                    v = (TextView) findViewById(R.id.v_m);
                    v.setVisibility(View.VISIBLE);
                }
                p.setTextColor(Color.parseColor("#27ae60"));

            } else {
                rl = (RelativeLayout) findViewById(R.id.car_ly);
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
                    ll = (LinearLayout) findViewById(R.id.partecipants);
                    ll.setVisibility(View.VISIBLE);
                    RecyclerView rec = (RecyclerView) findViewById(R.id.rec);
                    PassAdapter passAdapter = new PassAdapter(PathActivity.this, ja);
                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(PathActivity.this, LinearLayoutManager.HORIZONTAL, false);
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

    private void join() {
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);

        String url = "https://movemate-api.azurewebsites.net/api/paths/putjoinpath?StudentId=" + user_id + "&PathId=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(PathActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user",MainActivity.user_id);
                        startActivity(intent);
                        finish();
                    }

                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PathActivity.this, getResources().getString(R.string.error_join), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);
    }
    private void close() {
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);

        String url = "https://movemate-api.azurewebsites.net/api/paths/putclosepath?StudentId=" + user_id + "&PathId=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(PathActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user",MainActivity.user_id);
                        startActivity(intent);
                        finish();
                    }

                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PathActivity.this, getResources().getString(R.string.error_close), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);
    }
    private void feed() {
        final Dialog dialog = new Dialog(PathActivity.this);
        dialog.setContentView(R.layout.dialog_user_feed);
        final SeekBar rate = (SeekBar)dialog.findViewById(R.id.feed_bar);
        final TextView feed = (TextView)dialog.findViewById(R.id.feedback);
        feed.setText(rate.getProgress()+"/5");
        rate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                feed.setText(i+"/5");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                feed.setText(progress+"/5");


            }
        });
        Button send = (Button)dialog.findViewById(R.id.send_feed);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
                String url = null;
                try {
                    url = "https://movemate-api.azurewebsites.net/api/students/postfeedback?StudentId="+maker_id+"&Rate="+rate.getProgress()
                            +"&LeaverId="+user_id+"&PathId="+info.getInt("PathId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toasty.success(PathActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
                                dialog.dismiss();
                                Intent intent = new Intent(PathActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user",MainActivity.user_id);
                                startActivity(intent);
                                finish();

                            }

                        }
                        , new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                        Toasty.error(PathActivity.this, getResources().getString(R.string.error_feed), Toast.LENGTH_SHORT, true).show();

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                        return map;
                    }
                };

                queue.add(stringRequest);
            }
        });
        dialog.show();

    }

    private void delete() {
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
        String url = "https://movemate-api.azurewebsites.net/api/paths/deletepath?StudentId=" + user_id + "&PathId=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(PathActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user",MainActivity.user_id);
                        startActivity(intent);
                        finish();
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(PathActivity.this, getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT, true).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);
    }

    private void disjoin() {
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
        String url = "https://movemate-api.azurewebsites.net/api/paths/putdisjoinpath?StudentId=" + user_id + "&PathId=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(PathActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("user",MainActivity.user_id);
                        startActivity(intent);
                        finish();
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(PathActivity.this, getResources().getString(R.string.error_disjoin), Toast.LENGTH_SHORT, true).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);
    }

    private void origin2LatLng(String from, final String to) {
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
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
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
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
                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(PathActivity.this, pointList, 5, getResources().getColor(R.color.colorPrimary));
                    gMap.addPolyline(polylineOptions);
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

    private void getUserInfo(){
        RequestQueue queue = Volley.newRequestQueue(PathActivity.this);
        String url = "https://movemate-api.azurewebsites.net/api/students/getstudentinfo?StudentId="+maker_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            Intent intent = new Intent(PathActivity.this, ProfileActivity.class);
                            intent.putExtra("info",json.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                getFragmentManager().popBackStack();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);



    }

    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
