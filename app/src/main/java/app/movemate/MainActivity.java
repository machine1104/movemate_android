package app.movemate;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity {
    private BottomNavigationView mBottomNav;
    private ImageView imageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------BOTTOM NAVIGATION BAR

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.find){
                    FragmentManager fm = getFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
                        fm.popBackStack();
                    }
                    changeTab(new FindMateFragment());
                    setTitle("Find");



                }
                if (item.getItemId() == R.id.map){
                    FragmentManager fm = getFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
                        fm.popBackStack();
                    }
                    changeTab(new MapFragment());
                    setTitle("Map");



                }
                if (item.getItemId() == R.id.myMates){
                    FragmentManager fm = getFragmentManager();
                    for(int i = 0; i < fm.getBackStackEntryCount(); i++) {
                        fm.popBackStack();
                    }
                    changeTab(new MyMatesFragment());
                    setTitle("My Mates");


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
                                    View dView =  navigationView.getHeaderView(0);
                                    imageView = (ImageView) dView.findViewById(R.id.photo);
                                    new bitMapTask().execute(profilePicUrl);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();





        View view = mBottomNav.findViewById(R.id.find);
        view.performClick();

    }


    //SOLO PER MENU
    //IMPLEMENTARE ONBACKPRESSED SE NECESSARIO IN SEGUITO
    public void changeTab(Fragment frag){
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();
    }

    public void nextFrag(Fragment frag){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag).addToBackStack(null);
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        fragmentTransaction.commit();
    }

    class bitMapTask extends AsyncTask<String,ImageView, Bitmap> {

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