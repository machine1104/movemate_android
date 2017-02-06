package app.movemate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends Activity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    String checkUrl = "http://movemate-api.azurewebsites.net/api/students/getregisteredstudent?facebookId=";
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                new Load().execute();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Errore: " + exception, Toast.LENGTH_LONG).show();
            }
        });

        if (isLoggedIn()) {
            new Load().execute();
        }

        ImageButton btn = (ImageButton) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            if (!accessToken.isExpired()) {
                return true;
            }
            return false;
        }

        return false;
    }

    class Load extends AsyncTask<String, String, String> {
        ProgressDialog progDialog = new ProgressDialog(ctx);

        @Override
        protected void onPreExecute() {
            progDialog.setMessage("Loading...");
            progDialog.show();
        }

        @Override
        protected String doInBackground(String... aurl) {
            RequestQueue queue = Volley.newRequestQueue(ctx);
            String url = checkUrl + AccessToken.getCurrentAccessToken().getUserId();

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //codice 200
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //codice 404

                    if (error.networkResponse.statusCode == 404) {
                        Intent i = new Intent(LoginActivity.this, CheckActivity.class);
                        startActivity(i);
                    }
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            progDialog.dismiss();
            LoginActivity.this.finish();
        }
    }

}
