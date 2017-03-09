package app.movemate;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity {
    public static String user_id;
    public BottomNavigationView mBottomNav;
    private ImageView imageView;
    private ActionBarDrawerToggle mDrawerToggle;
    private int tab_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------USER ID
        user_id = getIntent().getStringExtra("user");
        //----------------------BOTTOM NAVIGATION BAR
        mBottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tab_id = item.getItemId();
                if (item.getItemId() == R.id.find) {
                    changeTab(new FilterDirectionFragment());
                    setTitle(getResources().getString(R.string.find));
                }
                if (item.getItemId() == R.id.map) {
                    changeTab(new MapFragment());
                    setTitle(getResources().getString(R.string.map));
                }
                if (item.getItemId() == R.id.myMates) {
                    changeTab(new MyPathsFragment());
                    setTitle(getResources().getString(R.string.myRoutes));
                }
                return true;
            }
        });

        //----------------------DRAWER PROFILE PIC
        Bundle params = new Bundle();
        params.putString("fields", "id,email,gender,cover,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                    View dView = navigationView.getHeaderView(0);
                                    imageView = (ImageView) dView.findViewById(R.id.photo);
                                    new bitMapTask().execute(profilePicUrl);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();

        //----------------------DRAWER TOGGLE
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        View view = mBottomNav.findViewById(R.id.myMates);
        view.performClick();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeTab(Fragment frag) {

        FragmentManager fragmentManager = getFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount()+1; i++) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag).addToBackStack("init");
        fragmentTransaction.commit();
    }

    public void nextFrag(Fragment frag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag).addToBackStack(null);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        super.onBackPressed();
        if (fm.getBackStackEntryCount() == 1) {
            if (tab_id == R.id.find) {
                setTitle(getResources().getString(R.string.find));
            }/*
            if (tab_id == R.id.map) {
                setTitle(getResources().getString(R.string.map));
            }*/
            if (tab_id == R.id.myMates) {
                setTitle(getResources().getString(R.string.myRoutes));
            }

        }
        if (fm.getBackStackEntryCount() == 0){
            super.onBackPressed();
        }

    }

    class bitMapTask extends AsyncTask<String, ImageView, Bitmap> {

        private Exception exception;


        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;

            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(Bitmap bit) {
            imageView.setImageBitmap(bit);

        }

    }
}