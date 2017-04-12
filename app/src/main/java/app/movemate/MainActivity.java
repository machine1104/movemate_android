package app.movemate;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

import app.movemate.Adapters.Path;
import app.movemate.Adapters.PathsAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends ActionBarActivity {
    public static String user_id;
    private ImageView imageView;
    PathsAdapter pathsAdapter;
    ListView rv;
    String url =  "https://movemate-api.azurewebsites.net/api/paths/getmypaths?StudentId=";
    private ActionBarDrawerToggle mDrawerToggle;
    NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getResources().getString(R.string.myRoutes));

        //----------------------USER ID
        user_id = getIntent().getStringExtra("user");



        //----------------------DRAWER


        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        navigation = (NavigationView) findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.profile:
                        getUserInfo();
                        break;
                }
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rv = (ListView) findViewById(R.id.matesList);

        pathsAdapter = new PathsAdapter(this,R.layout.path_list_layout);
        rv.setAdapter(pathsAdapter);
        populateList();

        FloatingActionButton add = (FloatingActionButton)findViewById(R.id.add_btn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://movemate-api.azurewebsites.net/api/students/getphoto?id="+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals(null) || response.equals("null") ){
                            Log.d("null","null");
                        }
                        View dView = navigation.getHeaderView(0);
                        String imageBytes = response;
                        byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
                        imageView = (ImageView) dView.findViewById(R.id.photo);
                        Glide.with(MainActivity.this).load(imageByteArray)
                                .bitmapTransform(new CropCircleTransformation(MainActivity.this))
                                .into(imageView);



                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);




    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.find) {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    public void populateList(){
        final SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResources().getString(R.string.loading_path));
        dialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        dialog.setCancelable(false);
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int count=jsonArray.length()-1;
                            while(count>=0){
                                JSONObject JO = jsonArray.getJSONObject(count);
                                Path path = new Path(JO);
                                count--;
                                pathsAdapter.add(path);
                            }
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toasty.error(MainActivity.this, getString(R.string.error_getpath), Toast.LENGTH_SHORT, true).show();
            }
        })
                ;

        queue.add(stringRequest);
    }

    private void getUserInfo(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://movemate-api.azurewebsites.net/api/students/getstudentinfo?StudentId="+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            Intent intent = new Intent(MainActivity.this, PersonalProfileActivity.class);
                            intent.putExtra("info",json.toString());
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                getFragmentManager().popBackStack();
            }
        });

        queue.add(stringRequest);



    }
}
