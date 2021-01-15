package com.payment.ipaympayments.activity;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.adapter.BenCardAdapter;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.httpRequest.VolleyGetNetworkCall;
import com.payment.ipaympayments.model.BeneficiaryItems;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SavedBenActivity extends AppCompatActivity implements VolleyGetNetworkCall.RequestResponseLis,
        BenCardAdapter.BenCardClickEvents {

    private LinearLayout ll_contain_listview;
    private LinearLayout ll_contain_norecepient_found;
    private RecyclerView benListView;
    private RecyclerView.LayoutManager layoutManager;
    private List<BeneficiaryItems> beneficiaryitems;
    private BenCardAdapter benCardAdapter = null;
    private String message, status = "";
    private String username, password;
    private FloatingActionButton fabButton;
    private String senderNumber, name = "", json = "";
    private String url;
    private String type = "", sender_number, account_number, otp, benemobile;


    private TextView tvName, tvNumber, tvMainBalance, tvAepsBalance, tvAddNew;

    private void init() {
        fabButton = findViewById(R.id.addFebFab);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);
        tvMainBalance = findViewById(R.id.tvMainBalance);
        tvAepsBalance = findViewById(R.id.tvAepsBalance);
        tvAddNew = findViewById(R.id.tvAddNew);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_benificiary_fragment);

        init();

        //Bundle bundle = this.getArguments();
        senderNumber = getIntent().getStringExtra("senderNumber");
        name = getIntent().getStringExtra("name");
        json = getIntent().getStringExtra("json");

        SharedPreferences sharedPreferences = SavedBenActivity.this.getSharedPreferences("user", 0);
        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");
        beneficiaryitems = new ArrayList<>();
        benListView = findViewById(R.id.recyclerview_plan);
        benListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SavedBenActivity.this);
        benListView.setLayoutManager(layoutManager);
        benCardAdapter = new BenCardAdapter(SavedBenActivity.this, beneficiaryitems, this);
        benListView.setAdapter(benCardAdapter);

        ll_contain_listview = findViewById(R.id.ll_contain_listview);
        ll_contain_norecepient_found = findViewById(R.id.ll_contain_norecepient_found);

        type = "verification";
        url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                SharedPrefs.getValue(SavedBenActivity.this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                SharedPrefs.getValue(SavedBenActivity.this, SharedPrefs.USER_ID) + "&type=verification&mobile="
                + senderNumber;

        volleyHttpRequest(url);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedBenActivity.this, AddBenActivity.class);
                intent.putExtra("mobile", senderNumber);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        tvAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedBenActivity.this, AddBenActivity.class);
                intent.putExtra("mobile", senderNumber);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        String name = SharedPrefs.getValue(this, SharedPrefs.USER_NAME);
        String balance = getString(R.string.rupee) + " " + SharedPrefs.getValue(this, SharedPrefs.MAIN_WALLET);
        String aeps = getString(R.string.rupee) + " " + SharedPrefs.getValue(this, SharedPrefs.APES_BALANCE);
        String contact = SharedPrefs.getValue(this, SharedPrefs.USER_CONTACT);
        tvName.setText(name);
        tvMainBalance.setText(balance);
        tvNumber.setText(contact);
        tvAepsBalance.setText(aeps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constents.isBack.equals("2")) {
            Constents.isBack = "1";
            volleyHttpRequest(url);
        }
    }

    private void volleyHttpRequest(String url) {
        if (AppManager.isOnline(SavedBenActivity.this)) {
            showLoader(getString(R.string.loading_text));
            new VolleyGetNetworkCall(this, SavedBenActivity.this, url).netWorkCall();
        } else {
            Toast.makeText(SavedBenActivity.this, "Network connection error", Toast.LENGTH_LONG).show();
        }
    }

    private AlertDialog loaderDialog;

    private void showLoader(String loaderMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SavedBenActivity.this);
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
    public void onSuccessRequest(String JSonResponse) {
        closeLoader();
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

            //String status = jsonObject.getString("statuscode");
            if (status.equalsIgnoreCase("UA")) {
                AppManager.getInstance().logoutApp(SavedBenActivity.this);
            } else if (status.equalsIgnoreCase("txn")) {
                if (type.equals("otp")) {
                    showOtpPopUp();
                } else if (type.equals("beneverify")) {
                    successPopUp(message);
                } else {
                    JSONArray jsonArray = jsonObject.getJSONArray("beneficiary");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        BeneficiaryItems item = new BeneficiaryItems();
                        item.setBeneid(data.getString("beneid"));
                        item.setBenename(data.getString("benename"));
                        item.setBeneaccount(data.getString("beneaccount"));
                        item.setBenebank(data.getString("benebank"));
                        item.setBeneifsc(data.getString("beneifsc"));
                        item.setBenebankid(data.getString("benebankid"));
                        item.setBenestatus(data.getString("benestatus"));
                        item.setBenemobile(data.getString("benemobile"));
                        item.setSendernumber(senderNumber);
                        item.setSendername(name);
                        beneficiaryitems.add(item);
                        benCardAdapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
        Toast.makeText(SavedBenActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void verifyListener(BeneficiaryItems item) {
        sender_number = item.getSendernumber();
        account_number = item.getBeneaccount();
        benemobile = item.getBenemobile();
        type = "otp";
        url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                SharedPrefs.getValue(SavedBenActivity.this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                SharedPrefs.getValue(SavedBenActivity.this, SharedPrefs.USER_ID) + "&type=verification&mobile="
                + senderNumber;
        volleyHttpRequest(url);
    }

    @Override
    public void transferListener(BeneficiaryItems item) {
        Intent intent = new Intent(this, Transaction.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showOtpPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_otp_dialog, null);
        Button v = customView.findViewById(R.id.otp_submit);
        final EditText e = customView.findViewById(R.id.otp);

        builder.setView(customView);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        v.setOnClickListener(v1 -> {
            String otp = e.getText().toString();
            alertDialog.dismiss();
            type = "beneverify";
            String url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" +
                    SharedPrefs.getValue(this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                    SharedPrefs.getValue(this, SharedPrefs.USER_ID) + "&type=" + type + "&mobile="
                    + sender_number + "&benemobile=" + benemobile + "&beneaccount=" + account_number + "&otp=" + otp;
            volleyHttpRequest(url);
        });
    }

    private void successPopUp(String msg) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(msg);

        builder1.setCancelable(false)
                .setTitle("Success")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder1.create();
        alert.show();
    }
}