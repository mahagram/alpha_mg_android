package com.payment.ipaympayments.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.ipaympayments.R;


public class GasBill extends AppCompatActivity {

    private Button loginButton;
    private ImageView imgKyc;

    private void init() {
        //loginButton = findViewById(R.id.loginButton);
        //imgKyc = findViewById(R.id.kycImage);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_bill);

        init();

    }
}
