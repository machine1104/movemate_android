package app.movemate.Phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.movemate.R;

public class PhoneCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_check);
        String mobile = getIntent().getStringExtra("mobile");


    }
}
