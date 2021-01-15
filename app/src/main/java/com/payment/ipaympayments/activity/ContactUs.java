package com.payment.ipaympayments.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.utill.SharedPrefs;

public class ContactUs extends AppCompatActivity {

    private Button loginButton;
    private ImageView imgKyc;

    private void init() {
        //loginButton = findViewById(R.id.loginButton);
        //imgKyc = findViewById(R.id.kycImage);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        init();

        TextView tvContact = findViewById(R.id.tvContact);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvAddress = findViewById(R.id.tvAddress);
        String email = SharedPrefs.getValue(this, SharedPrefs.SUPPORT_EMAIL);
        String contact = SharedPrefs.getValue(this, SharedPrefs.SUPPORT_NUMBER);
        String address = SharedPrefs.getValue(this, SharedPrefs.SUPPORT_ADDRESS);

        if (email!=null && email.length()>0)
            tvEmail.setText(email);
        if (contact!=null && contact.length()>0)
            tvContact.setText(contact);
        if (address!=null && address.length()>0)
            tvAddress.setText(address);
    }
}
