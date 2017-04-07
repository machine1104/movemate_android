package app.movemate.Phone;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import java.util.Random;

import app.movemate.Email.EmailActivity;
import app.movemate.Email.EmailCheckActivity;
import app.movemate.MainActivity;
import app.movemate.Manifest;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class PhoneActivity extends AppCompatActivity {
    EditText mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        getSupportActionBar().setTitle(R.string.phone_number);
        mobile = (EditText)findViewById(R.id.mobile);





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
            String s = mobile.getText().toString();
            if (s.length()>=7){
                Intent intent = new Intent(this, EmailActivity.class);
                String n = "+39"+mobile.getText().toString();
                Log.d("telefono",n);
                intent.putExtra("mobile", n);
                startActivity(intent);
            }else{
                Toasty.error(PhoneActivity.this, getResources().getString(R.string.error_mobile), Toast.LENGTH_SHORT, true).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
