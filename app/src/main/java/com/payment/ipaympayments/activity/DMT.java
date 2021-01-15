package com.payment.ipaympayments.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.httpRequest.UpdateBillService;
import com.payment.ipaympayments.httpRequest.VolleyGetNetworkCall;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.SharedPrefs;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;


public class DMT extends AppCompatActivity implements VolleyGetNetworkCall.RequestResponseLis {

    private BottomSheetBehavior sheetBehavior;
    private EditText etMobileNumber;
    String mobile = "", type = "";
    LinearLayout ll_contain;
    private int REQUEST_TYPE = 1;
    private EditText etOtp, etName;
    private Button btnRegister;

    private void init() {
        etMobileNumber = findViewById(R.id.etMobileNumber);
        ll_contain = findViewById(R.id.ll_contain_fname_lname);
        etName = findViewById(R.id.etName);
        etOtp = findViewById(R.id.etOtp);
        btnRegister = findViewById(R.id.btnRegister);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmt);

        init();

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 9) {
                    type = "verification";
                    mobile = s.toString();
                    String url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                            SharedPrefs.getValue(DMT.this, SharedPrefs.APP_TOKEN) + "&user_id="
                            + SharedPrefs.getValue(DMT.this, SharedPrefs.USER_ID) + "&type=" + type + "&mobile=" + mobile+ "&device_id=" + Constents.MOBILE_ID;
                    REQUEST_TYPE = 1;
                    volleyHttpRequest(url);
                } else {
                    ll_contain.setVisibility(View.GONE);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    String first_name = etName.getText().toString();
                    String otp = etOtp.getText().toString();
                    type = "registration";
                    String url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                            SharedPrefs.getValue(DMT.this, SharedPrefs.APP_TOKEN) + "&user_id="
                            + SharedPrefs.getValue(DMT.this, SharedPrefs.USER_ID) + "&type=" + type +
                            "&mobile=" + mobile + "&fname=" + first_name + "&otp=" + otp+ "&device_id=" + Constents.MOBILE_ID;
                    REQUEST_TYPE = 2;
                    volleyHttpRequest(url);
                }
            }
        });

        TextView tvWallet = findViewById(R.id.tvBalance);
        TextView tvAeps = findViewById(R.id.tvAeps);
        ImageView imgSync = findViewById(R.id.imgSync);
        String balance = getString(R.string.rupee) + " " + SharedPrefs.getValue(this, SharedPrefs.MAIN_WALLET);
        String aeps = getString(R.string.rupee) + " " + SharedPrefs.getValue(this, SharedPrefs.APES_BALANCE);
        tvWallet.setText(balance);
        tvAeps.setText(aeps);
        imgSync.setOnClickListener(v -> new UpdateBillService(this, tvWallet, tvAeps));

    }

    private boolean isValid() {
        if (etMobileNumber.getText().toString().equals("")) {
            Toast.makeText(DMT.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etName.getText().toString().equals("")) {
            Toast.makeText(DMT.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etOtp.getText().toString().equals("")) {
            Toast.makeText(DMT.this, "Please enter otp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void volleyHttpRequest(String url) {
        if (AppManager.isOnline(this)) {
            showLoader(getString(R.string.loading_text));
            new VolleyGetNetworkCall(this, this, url).netWorkCall();
        } else {
            Toast.makeText(this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private android.app.AlertDialog loaderDialog;

    private void showLoader(String loaderMsg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.android_loader, null);
        builder.setView(view);
        builder.create();
        loaderDialog = builder.show();
        loaderDialog.setCancelable(false);
    }

    private void closeLoader() {
        if (loaderDialog != null && loaderDialog.isShowing()) {
            loaderDialog.dismiss();
        }
    }

    @Override
    public void onSuccessRequest(String json) {
        closeLoader();
        if (type.equalsIgnoreCase("verification")) {
            mShowSenderCheckStatus(json);
        } else if (type.equalsIgnoreCase("registration")) {
            mShowRegistrationStatus(json);
        }
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
        Toast.makeText(DMT.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void mShowSenderCheckStatus(final String json) {
        String status = "";
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("statuscode")) {
                status = jsonObject.getString("statuscode");
            } else {
                status = "ERR";
            }

            if (jsonObject.has("message")) {
                message = jsonObject.getString("message");
            } else {
                message = "Technical Error";
            }

            //String status = jsonObject.getString("statuscode");
            if (status.equalsIgnoreCase("UA")) {
                AppManager.getInstance().logoutApp(DMT.this);
            } else if (status.equalsIgnoreCase("TXN")) {
                String name = jsonObject.getString("name");
                String totallimit = jsonObject.getString("totallimit");
                String usedlimit = jsonObject.getString("usedlimit");

                Intent intent = new Intent(DMT.this, SavedBenActivity.class);
                intent.putExtra("senderNumber", mobile);
                intent.putExtra("name", name);
                intent.putExtra("status", status);
                intent.putExtra("available_limit", totallimit);
                intent.putExtra("total_spend", usedlimit);
                intent.putExtra("json", json);
                intent.putExtra("tab", 1);
                startActivity(intent);
            } else if (status.equalsIgnoreCase("RNF")) {
                ll_contain.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(DMT.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void mShowRegistrationStatus(final String json) {
        String status = "";
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("statuscode")) {
                status = jsonObject.getString("statuscode");
            }
            if (jsonObject.has("message")) {
                message = jsonObject.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String status = jsonObject.getString("statuscode");
        if (status.equalsIgnoreCase("UA")) {
            AppManager.getInstance().logoutApp(DMT.this);
        } else if (status.equalsIgnoreCase("txn")) {
            Toast.makeText(DMT.this, message, Toast.LENGTH_SHORT).show();
            type = "verification";
            String url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                    SharedPrefs.getValue(DMT.this, SharedPrefs.APP_TOKEN) + "&user_id="
                    + SharedPrefs.getValue(DMT.this, SharedPrefs.USER_ID) + "&type=" + type + "&mobile=" + mobile+ "&device_id=" + Constents.MOBILE_ID;
            volleyHttpRequest(url);
        } else {
            Toast.makeText(DMT.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
