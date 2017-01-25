package app.movemate;


import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNav;
    private int mSelectedItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.find){
                    nextFrag(new FindMateFragment());
                    setTitle("Find");
                }
                if (item.getItemId() == R.id.map){
                    nextFrag(new MapFragment());
                    setTitle("Map");
                }
                if (item.getItemId() == R.id.myMates){
                    nextFrag(new MyMatesFragment());
                    setTitle("My Mates");
                }
                return true;
            }
        });
        View view = mBottomNav.findViewById(R.id.find);
        view.performClick();

    }


    //SOLO PER MENU
    //IMPLEMENTARE ONBACKPRESSED SE NECESSARIO IN SEGUITO
    public void nextFrag(Fragment frag){
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();
    }



}