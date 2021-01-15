package com.payment.ipaympayments.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.payment.ipaympayments.R;
import com.payment.ipaympayments.adapter.BankListAdapter;
import com.payment.ipaympayments.adapter.OperatorListAdapter;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.httpRequest.VolleyGetNetworkCall;
import com.payment.ipaympayments.model.BankListItems;
import com.payment.ipaympayments.model.Operators_Items;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorList extends AppCompatActivity implements VolleyGetNetworkCall.RequestResponseLis {
    private String url, type;
    private String title,hint,descInput;
    private ListView operatorListView;
    private List<Operators_Items> operatorDataList;
    private List<BankListItems> bankDataList;
    private OperatorListAdapter operatorListAdapter;
    private BankListAdapter bankListAdapter;
    private Context context;
    private EditText etSearch;
    private Toolbar toolbar;

    private void init() {
        context = OperatorList.this;
        operatorListView = findViewById(R.id.operatorList);
        etSearch = findViewById(R.id.etSearch);
        operatorDataList = new ArrayList<>();
        bankDataList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_list);
        title= getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        hint = getIntent().getStringExtra("hint");
        descInput = getIntent().getStringExtra("descInput");
        setSupportActionBar(toolbar);

        init();

        if (type != null && type.equals("bank")) {
            if (getSupportActionBar() != null) {
                toolbar.setTitle(R.string.select_bank_title);
            }
            url = Constents.URL.baseUrl + "api/android/dmt/transaction?apptoken=" + SharedPrefs.getValue
                    (OperatorList.this, SharedPrefs.APP_TOKEN) + "&user_id=" + SharedPrefs.getValue(
                    OperatorList.this, SharedPrefs.USER_ID) + "&type=getbank";
        } else {
            if (getSupportActionBar() != null) {
                toolbar.setTitle(R.string.select_provider_title);
            }
            url = Constents.URL.baseUrl + "api/android/recharge/providers?type=" + type;
        }

        if (type != null && type.equals("bank")) {
            bankListAdapter = new BankListAdapter(this, bankDataList);
            operatorListView.setAdapter(bankListAdapter);
        } else {
            operatorListAdapter = new OperatorListAdapter(this, operatorDataList,type,title,hint,descInput);
            operatorListView.setAdapter(operatorListAdapter);
        }

        if (type.equalsIgnoreCase("electricity")) {
            etSearch.setVisibility(View.VISIBLE);
        }

        if (type.equalsIgnoreCase("bank")) {
            etSearch.setVisibility(View.VISIBLE);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (type.equals("bank")) {
                    if (bankDataList != null) {
                        List<BankListItems> temp = new ArrayList<>();
                        for (BankListItems d : bankDataList) {
                            if (d.getBank().toLowerCase().contains(s.toString().toLowerCase())) {
                                temp.add(d);
                            }
                        }
                        bankListAdapter.UpdateList(temp);
                    }
                } else {
                    if (operatorDataList != null) {
                        List<Operators_Items> temp = new ArrayList<>();
                        for (Operators_Items d : operatorDataList) {
                            if (d.getOperator_name().toLowerCase().contains(s.toString().toLowerCase())) {
                                temp.add(d);
                            }
                        }
                        operatorListAdapter.UpdateList(temp);
                    }
                }
            }
        });

        networkCallUsingVolleyApi(url);
    }

    private void networkCallUsingVolleyApi(String url) {
        if (AppManager.isOnline(this)) {
            showLoader(getString(R.string.loading_text));
            new VolleyGetNetworkCall(this, this, url).netWorkCall();
        } else {
            Toast.makeText(this,R.string.no_internet , Toast.LENGTH_LONG).show();
        }
    }

    private androidx.appcompat.app.AlertDialog loaderDialog;

    private void showLoader(String loaderMsg) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
    public void onSuccessRequest(String result) {

        closeLoader();
        if (!result.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("statuscode")) {
                    if (jsonObject.getString("statuscode").equalsIgnoreCase("TXN")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);

                            if (!type.equals("bank")) {
                                Operators_Items operators_items = new Operators_Items();
                                operators_items.setOperator_id(data.getString("id"));
                                operators_items.setOperator_name(data.getString("name"));
                                if (data.has("logo"))
                                    operators_items.setOperator_image(data.getString("logo"));
                                operatorDataList.add(operators_items);
                                operatorListAdapter.notifyDataSetChanged();
                            } else {
                                BankListItems bankitem = new BankListItems();
                                bankitem.setId(data.getString("id"));
                                bankitem.setBank(data.getString("bankname"));
                                bankitem.setIfsc(data.getString("masterifsc"));
                                bankitem.setBank_id(data.getString("bankid"));
                                bankitem.setBank_icon(data.getString("url"));
                                bankDataList.add(bankitem);
                                bankListAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        if (jsonObject.has("message")) {
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                        getFragmentManager().popBackStack();
                    }
                } else {
                    Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
