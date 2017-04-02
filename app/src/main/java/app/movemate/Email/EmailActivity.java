package app.movemate.Email;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONException;
import org.json.JSONObject;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class EmailActivity extends AppCompatActivity {
    Context ctx = this;
    String sendUrl = "http://movemate-api.azurewebsites.net/api/students/poststudent";
    String name,surname,confirmed_email;
    ProgressDialog progDialog;
    EditText mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        getSupportActionBar().setTitle(R.string.email);
        mail = (EditText)findViewById(R.id.email);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.confirm) {
            checkMail();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void checkMail(){
        final String email = mail.getText().toString();
        if (email.contains("@studenti.uniroma1.it") || email.contains("@stud.uniroma3.it") || email.contains("students.uniroma2.eu")){
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
                                    confirmed_email = email;
                                    sendCode();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).executeAsync();
        }else{
            Toasty.error(this, getResources().getString(R.string.error_email), Toast.LENGTH_SHORT, true).show();
        }

    }

    private void sendCode(){

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = sendUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(EmailActivity.this, EmailCheckActivity.class);
                        EmailActivity.this.startActivity(intent);


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

                    json = new JSONObject().put("facebookId", AccessToken.getCurrentAccessToken().getUserId()).
                            put("name", name).put("surname",surname).put("email",confirmed_email).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.getBytes();
            }

        };
        queue.add(stringRequest);


    }






}
