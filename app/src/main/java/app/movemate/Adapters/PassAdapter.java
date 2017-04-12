package app.movemate.Adapters;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import app.movemate.MainActivity;
import app.movemate.PathActivity;
import app.movemate.ProfileActivity;
import app.movemate.R;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class PassAdapter extends RecyclerView.Adapter<PassAdapter.MyViewHolder> {

    private JSONArray passList;
    private PathActivity ctx;

    public PassAdapter(Context context,JSONArray passList) {
        this.passList = passList;
        this.ctx = (PathActivity)context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipant_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        try {
            final JSONObject partecipant =  passList.getJSONObject(position);
            holder.name.setText(partecipant.getString("Name"));
            RequestQueue queue = Volley.newRequestQueue(ctx);
            String url = "https://movemate-api.azurewebsites.net/api/students/getphoto?id="+partecipant.getInt("StudentId");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String imageBytes = response;
                            byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
                            Glide.with(ctx).load(imageByteArray)
                                    .bitmapTransform(new CropCircleTransformation(ctx))
                                    .into(holder.imv);



                        }
                    }
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(stringRequest);
            holder.imv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        getUserInfo(partecipant.getInt("StudentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return passList.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView imv;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            imv = (ImageView)view.findViewById(R.id.pic);

        }
    }
    public JSONObject getItem(int position) throws JSONException {
        return passList.getJSONObject(position);
    }
    private void getUserInfo(int id){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = "http://movemate-api.azurewebsites.net/api/students/getstudentinfo?StudentId="+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            Intent intent = new Intent(ctx, ProfileActivity.class);
                            intent.putExtra("info",json.toString());
                            ctx.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ctx.getFragmentManager().popBackStack();
            }
        });

        queue.add(stringRequest);



    }
}


