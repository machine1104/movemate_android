package app.movemate.Create;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.movemate.CreateActivity;
import app.movemate.MainActivity;
import app.movemate.R;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

import static app.movemate.R.id.address;
import static app.movemate.R.id.venue;


public class CreateInfoFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_info, container, false);
        Button create = (Button)view.findViewById(R.id.next);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    create();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void create() throws JSONException {
        final JSONObject route = new JSONObject(this.getArguments().getString("route"));
        String name = ((EditText)view.findViewById(R.id.route_name)).getText().toString();
        String desc = ((EditText)view.findViewById(R.id.desc)).getText().toString();
        if(name.equals("")) {
            Toasty.error(getActivity(), getString(R.string.missing_fields), Toast.LENGTH_SHORT, true).show();
        }else{
            route.put("PathName",name);
            route.put("Description",desc);

            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            dialog.setTitleText(getResources().getString(R.string.loading_creation));
            dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            dialog.setCancelable(false);
            dialog.show();
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url;
            int vId = route.getInt("vId");


            if (vId == 0){
                url = "https://movemate-api.azurewebsites.net/api/paths/postpathcar";
            }else if (vId == 1){
                url = "https://movemate-api.azurewebsites.net/api/paths/postpathcyc";
            }else{
                url = "https://movemate-api.azurewebsites.net/api/paths/postpathpub";
            }


            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            Toasty.success(getActivity(), "Success!", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("user",MainActivity.user_id);
                            startActivity(intent);
                            getActivity().finish();

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toasty.error(getActivity(), getString(R.string.error_create), Toast.LENGTH_SHORT, true).show();
                }
            })

            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Authorization", AccessToken.getCurrentAccessToken().getUserId());

                    return map;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    String s = route.toString();
                    String js = s.substring(0,s.indexOf("}"));
                    String date = getArguments().getString("date");
                    String time = getArguments().getString("time");

                    js = js+",\"Date\":\""+date+" "+time+"\"}";
                    Log.d("json",js);
                    return js.getBytes();
                }

            };

            queue.add(stringRequest);
        }


    }

}
