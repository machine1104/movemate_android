package app.movemate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import app.movemate.Email.EmailActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends Activity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    String checkUrl = "http://movemate-api.azurewebsites.net/api/students/getregisteredstudent?facebookId=";
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                check();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "Errore: " + exception, Toast.LENGTH_LONG).show();
            }
        });

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        if (isLoggedIn()) {
            check();
        }else{
            btn.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.d("token",accessToken+"");
        if (accessToken != null) {
            if (!accessToken.isExpired()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void check() {
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResources().getString(R.string.loading_app));
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = checkUrl + AccessToken.getCurrentAccessToken().getUserId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //codice 200
                        dialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("user",response);
                        startActivity(i);
                        LoginActivity.this.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null){
                    //codice 404
                    if (error.networkResponse.statusCode == 404) {
                        Intent i = new Intent(LoginActivity.this, EmailActivity.class);
                        startActivity(i);
                        LoginActivity.this.finish();
                    }
                    else{
                        Toasty.error(LoginActivity.this,error.networkResponse.statusCode+"",Toast.LENGTH_LONG,true).show();
                    }
                    dialog.dismiss();
                }
                else{
                    dialog.dismiss();
                    check();
                }

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

}