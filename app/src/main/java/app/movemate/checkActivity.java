package app.movemate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckActivity extends Activity {
    Context ctx = this;
    String checkUrl = "http://movemate-api.azurewebsites.net/api/students/poststudent";
    String name,surname,confirmed_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Button btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edit_email = (EditText)findViewById(R.id.email);
                String email = edit_email.getText().toString();
                if (email.contains("@studenti.uniroma1.it") || email.contains("@stud.uniroma3.it")){
                    confirmed_email = email;
                    Bundle params = new Bundle();
                    params.putString("fields", "id,first_name,last_name");
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    if (response != null) {
                                        try {
                                            JSONObject data = response.getJSONObject();
                                            name = data.get("first_name").toString();
                                            surname = data.get("last_name").toString();
                                            sendCode();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).executeAsync();
                }else{
                    Toast.makeText(CheckActivity.this,"Email errata",Toast.LENGTH_LONG).show();
                }

            }
        });




    }

    private void sendCode(){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = checkUrl;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = null;
                try {
                    json = new JSONObject().put("facebookId", AccessToken.getCurrentAccessToken().getUserId()+"").
                            put("name", name).put("surname",surname).put("email",confirmed_email).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.getBytes();
            }

        };
        // Add the request to the RequestQueue.

        queue.add(stringRequest);


    }




}
