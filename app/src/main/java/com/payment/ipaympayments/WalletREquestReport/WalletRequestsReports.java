package com.payment.ipaympayments.WalletREquestReport;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.payment.ipaympayments.R;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WalletRequestsReports extends AppCompatActivity {


    SwipeRefreshLayout swiprefresh_aeps_report;
    RecyclerView recyclerview_eaps_transaction_reports;
    TextView textview_message;
    List<WalletRequestReportItems> AEPSRequestItems;
    WalletRequestCardAdapter AEPSRequestCardAdapter;

    ProgressDialog dialog;

    String type = "fundrequest";
    private int reloadCount = 0;

    public static boolean last_array_empty = false;
    public static int start_page = 0;
    boolean swiped_refresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_reports);

        start_page = 1;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        swiprefresh_aeps_report = findViewById(R.id.swiprefresh_aeps_report);
        recyclerview_eaps_transaction_reports = findViewById(R.id.recyclerview_eaps_transaction_reports);
        recyclerview_eaps_transaction_reports.setLayoutManager(new LinearLayoutManager(WalletRequestsReports.this));
        AEPSRequestItems = new ArrayList<>();
        AEPSRequestCardAdapter = new WalletRequestCardAdapter(WalletRequestsReports.this, AEPSRequestItems);
        recyclerview_eaps_transaction_reports.setAdapter(AEPSRequestCardAdapter);
        textview_message = findViewById(R.id.textview_message);


        swiprefresh_aeps_report.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swiprefresh_aeps_report.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppManager.isOnline(WalletRequestsReports.this)) {
                    start_page = 1;
                    swiped_refresh = true;
                    AEPSRequestItems.clear();
                    textview_message.setVisibility(View.GONE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.VISIBLE);
                    mGetTransactionReports(SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.APP_TOKEN),
                            SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.USER_ID), type);

                } else {
                    swiprefresh_aeps_report.setRefreshing(false);
                    textview_message.setText("No internet connection");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
                }
            }
        });


        if (AppManager.isOnline(WalletRequestsReports.this)) {
            textview_message.setVisibility(View.GONE);
            recyclerview_eaps_transaction_reports.setVisibility(View.VISIBLE);

            mGetTransactionReports(SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.APP_TOKEN),
                    SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.USER_ID), type);
        } else {
            textview_message.setVisibility(View.VISIBLE);
            textview_message.setText("No internet connection");
            recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
        }
    }


    private void mGetTransactionReports(final String apptoken, final String user_id, final String type) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(WalletRequestsReports.this);
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
                swiprefresh_aeps_report.setRefreshing(true);
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(Constents.URL.baseUrl + "api/android/transaction?apptoken=" + apptoken + "&user_id=" + user_id + "&type=" + type + "&start=" + start_page+ "&device_id=" + Constents.MOBILE_ID);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }


                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {

                //Do something with the JSON string
                swiprefresh_aeps_report.setRefreshing(false);
                if (dialog != null && dialog.isShowing())
                dialog.dismiss();
                //Log.e("login response", result);

                if (!result.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("statuscode")) {
                            if (jsonObject.getString("statuscode").equalsIgnoreCase("txn")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);
                                    WalletRequestReportItems items = new WalletRequestReportItems();
                                    items.setId(data.getString("id"));
                                    items.setAmount(data.getString("amount"));
                                    items.setStatus(data.getString("status"));
                                    items.setType(data.getString("type"));
                                    items.setCreated_at(data.getString("paydate") + "\nRequest Date : " + data.getString("created_at"));
                                    items.setRemark(data.getString("remark") + "\nReference no : " + data.getString("ref_no"));
                                    AEPSRequestItems.add(items);

                                }

                                if (jsonArray.length() > 0)
                                    AEPSRequestCardAdapter.notifyDataSetChanged();
                                last_array_empty = jsonArray.length() == 0;

                                if (start_page > 1) {
                                    recyclerview_eaps_transaction_reports.scrollToPosition(reloadCount);
                                }
                            }
                            if (AEPSRequestItems.size()==0){
                                Toast.makeText(WalletRequestsReports.this, "Data not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    textview_message.setText("Something went wrong");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_eaps_transaction_reports.setVisibility(View.GONE);
                }

            }
        }

        getJSONData getJSONData = new getJSONData();
        getJSONData.execute();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void mCallNextList() {
        start_page += 1;

        if (AppManager.isOnline(WalletRequestsReports.this)) {
            mGetTransactionReports(SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.APP_TOKEN),
                    SharedPrefs.getValue(WalletRequestsReports.this, SharedPrefs.USER_ID), type);

        }
    }


}
