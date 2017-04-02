package app.movemate.Phone;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import app.movemate.R;
import es.dmoral.toasty.Toasty;

public class PhoneActivity extends AppCompatActivity {
    EditText mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        mobile = (EditText)findViewById(R.id.mobile);
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mNumber = tMgr.getLine1Number();
        if (mNumber != null){
            mobile.setText(mNumber);
        }



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
            if (!s.equals("") && s.length()>7){
                Intent intent = new Intent(this, PhoneCheckActivity.class);
                intent.putExtra("mobile", s);
                intent.putExtra("user",getIntent().getStringExtra("user"));
                startActivity(intent);
            }else{
                Toasty.error(this, getResources().getString(R.string.error_mobile), Toast.LENGTH_SHORT, true).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
