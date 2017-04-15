package app.movemate.Wizard;

import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.movemate.MainActivity;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class EmailCheckActivity extends AppCompatActivity {
    String checkUrl = "https://movemate-api.azurewebsites.net/api/students/putstudentverification";
    ProgressDialog progDialog;
    EditText code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_check);
        getSupportActionBar().setTitle(R.string.email);
        code = (EditText)findViewById(R.id.code);
        TextView back = (TextView)findViewById(R.id.wrong);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                if (!code.getText().equals("")){
                    checkCode();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void checkCode(){
        progDialog = new ProgressDialog(this);
        progDialog.show();
        progDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        progDialog.setContentView( R.layout.progress );

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = checkUrl+"?facebookId="+ AccessToken.getCurrentAccessToken().getUserId()+"&code="+code.getText();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progDialog.dismiss();
                        Intent i = new Intent(EmailCheckActivity.this, MainActivity.class);
                        i.putExtra("user",response);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progDialog.dismiss();
                if (error.networkResponse.statusCode==412){
                    Toasty.error(EmailCheckActivity.this, getResources().getString(R.string.error_code), Toast.LENGTH_SHORT, true).show();
                }
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


}
