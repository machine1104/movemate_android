package app.movemate.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.movemate.MainActivity;
import app.movemate.PathActivity;
import app.movemate.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PathsAdapter extends ArrayAdapter {
    List list = new ArrayList();
    String url = "https://movemate-api.azurewebsites.net/api/paths/getpath?PathId=";

    public PathsAdapter(Context context, int resource) {
        super(context, resource);
    }
    public void add(Path object) {
        super.add(object);
        list.add(object);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        PathHolder pathHolder = new PathHolder();

        if (row==null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.path_list_layout,parent,false);


            pathHolder.tx_pathname= (TextView) row.findViewById(R.id.pathname);
            pathHolder.tx_fa= (TextView) row.findViewById(R.id.fa);
            pathHolder.tx_ta= (TextView) row.findViewById(R.id.ta);
            pathHolder.imv= (ImageView) row.findViewById(R.id.icon);
            pathHolder.tx_d= (TextView) row.findViewById(R.id.d);
            pathHolder.tx_p= (TextView) row.findViewById(R.id.p);

            row.setTag(pathHolder);

        }else{
            pathHolder = (PathHolder)row.getTag();
        }
        final Path path = (Path) this.getItem(position);
        try {

            pathHolder.tx_pathname.setText(path.path.getString("PathName"));
            pathHolder.tx_fa.setText(path.path.getString("StartAddress"));
            pathHolder.tx_ta.setText(path.path.getString("DestinationAddress"));
            pathHolder.tx_d.setText(path.path.getString("Date"));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        getPathInfo(path.path.getInt("PathId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            int i = path.path.getInt("Vehicle");

            Drawable drawable;
            if (i == 1){
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_motorcycle);
                pathHolder.imv.setImageDrawable(drawable);
                int price = Integer.parseInt(path.path.getString("Price"));
                if(price == 0){
                    pathHolder.tx_p.setText("FREE");
                }else{
                    pathHolder.tx_p.setText(price+"€");
                }
                if (price < 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
                }
                else if (price < 7 && price >= 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_orange));
                }else{
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_red));
                }
                pathHolder.tx_p.setText(price+"€");
            }
            else if (i == 2){
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_bus);
                pathHolder.imv.setImageDrawable(drawable);
                pathHolder.tx_p.setText("FREE");
                pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
            }else{
                drawable = ContextCompat.getDrawable(pathHolder.imv.getContext(),R.drawable.ic_car);
                pathHolder.imv.setImageDrawable(drawable);
                int price = Integer.parseInt(path.path.getString("Price"));
                if(price == 0){
                    pathHolder.tx_p.setText("FREE");
                }else{
                    pathHolder.tx_p.setText(price+"€");
                }
                if (price < 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_green));
                }
                else if (price < 7 && price >= 4){
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_orange));
                }else{
                    pathHolder.tx_p.setBackground(ContextCompat.getDrawable(pathHolder.tx_p.getContext(),R.drawable.rounded_red));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    private void getPathInfo(int id) {
        final SweetAlertDialog dialog = new SweetAlertDialog(this.getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(this.getContext().getResources().getString(R.string.loading_info));
        dialog.getProgressHelper().setBarColor(this.getContext().getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        JSONObject info = null;

                        try {
                            info = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getContext(), PathActivity.class);
                        intent.putExtra("path",info.toString());
                        getContext().startActivity(intent);
                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.error_info), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                return map;
            }
        };

        queue.add(stringRequest);
    }

    static class PathHolder{

        TextView tx_pathname;
        TextView tx_fa;
        TextView tx_ta;
        TextView tx_p;
        TextView tx_d;
        ImageView imv;

    }
}
