package app.movemate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CheckActivity extends Activity {
    Context ctx = this;
    String sendUrl = "http://movemate-api.azurewebsites.net/api/students/poststudent";
    String checkUrl = "http://movemate-api.azurewebsites.net/api/students/putstudentverification";
    String name,surname,confirmed_email,confirmed_code;
    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        Button btn_send_code = (Button) findViewById(R.id.btn_send_code);
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit_email = (EditText)findViewById(R.id.email);
                final String email = edit_email.getText().toString();

                if (email.contains("@studenti.uniroma1.it") || email.contains("@stud.uniroma3.it") || email.contains("students.uniroma2.eu")){


                    LayoutInflater inflater = CheckActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_alert, null);
                    TextView tv_email = (TextView) dialogView.findViewById(R.id.email);
                    tv_email.setText(email);

                    new AlertDialog.Builder(CheckActivity.this)
                            .setView(dialogView)
                            .setTitle(R.string.confirm)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
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
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else{
                    Toast.makeText(CheckActivity.this,"Email errata",Toast.LENGTH_LONG).show();
                }

            }
        });

        Button btn_check_code = (Button)findViewById(R.id.btn_check_code);
        btn_check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText code = (EditText)findViewById(R.id.code);
                confirmed_code = code.getText().toString();
                checkCode();
            }
        });
    }

    private void sendCode(){
        progDialog = new ProgressDialog(ctx);
        progDialog.setMessage("Loading...");
        progDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = sendUrl;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDialog.dismiss();

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

    private void checkCode(){
        progDialog = new ProgressDialog(ctx);
        progDialog.setMessage("Loading...");
        progDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = checkUrl+"?facebookId="+AccessToken.getCurrentAccessToken().getUserId()+"&code="+confirmed_code;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progDialog.dismiss();
                        Intent i = new Intent(CheckActivity.this, MainActivity.class);
                        i.putExtra("user",response);
                        startActivity(i);
                        CheckActivity.this.finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDialog.dismiss();
                if (error.networkResponse.statusCode==412){
                    Toast.makeText(CheckActivity.this,"Codice Errato",Toast.LENGTH_LONG);
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }




}
