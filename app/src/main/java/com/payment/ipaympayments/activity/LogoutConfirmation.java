package com.payment.ipaympayments.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.PrefLoginManager;

public class LogoutConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_confirmation);
        new PrefLoginManager(this).clearFarmerLoginRes();
        AppManager.getInstance().logoutApp(LogoutConfirmation.this);
    }
}
