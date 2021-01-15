package com.payment.ipaympayments.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.payment.ipaympayments.R;


public class UpdatePin extends AppCompatActivity {

    private Button btnUpdate;
    private ImageView imgKyc;

    private void init() {
        btnUpdate = findViewById(R.id.btnUpdate);
        //imgKyc = findViewById(R.id.kycImage);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Update Pin");

        btnUpdate.setText("Save Pin");


    }
}
