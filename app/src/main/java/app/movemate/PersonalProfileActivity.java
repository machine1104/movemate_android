package app.movemate;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PersonalProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);


        try {
            final JSONObject json = new JSONObject(getIntent().getStringExtra("info"));
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://movemate-api.azurewebsites.net/api/students/getphoto?id="+json.getString("StudentId");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String imageBytes = response;
                            byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
                            Glide.with(PersonalProfileActivity.this).load(imageByteArray)
                                    .fitCenter()
                                    .bitmapTransform(new CropCircleTransformation(PersonalProfileActivity.this))
                                    .into((ImageView) findViewById(R.id.pic));



                        }
                    }
                    , new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(stringRequest);

            TextView name = (TextView)findViewById(R.id.name);
            name.setText(json.getString("Name"));
            TextView rate = (TextView)findViewById(R.id.feedback);
            Double r = json.getDouble("TotalFeedback");
            String rs ;
            if(r>5){
                rs = "N.A.";
            }else{
                rs = new DecimalFormat("##.#").format(r);
            }
            rate.setText(rs+"/5");

        } catch (JSONException e) {
            e.printStackTrace();
        }

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