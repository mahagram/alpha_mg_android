package com.payment.ipaympayments.pancards;

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

public class PanCardStatement extends AppCompatActivity {


    SwipeRefreshLayout swiprefresh_statement;
    RecyclerView recyclerview_statement;
    TextView textview_message;

    List<PanCardStatementItems> panCardStatementItems;
    PanCardAdapter panCardAdapter;

    String type = "utipancardstatement";


    public static boolean last_array_empty = false;
    public static int start_page = 0;
    boolean swiped_refresh = false;

    private int reloadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card_statement);

        start_page = 1;

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swiprefresh_statement = findViewById(R.id.swiprefresh_statement);

        recyclerview_statement = findViewById(R.id.recyclerview_statement);
        recyclerview_statement.setLayoutManager(new LinearLayoutManager(PanCardStatement.this));
        panCardStatementItems = new ArrayList<>();
        panCardAdapter = new PanCardAdapter(PanCardStatement.this, panCardStatementItems);
        recyclerview_statement.setAdapter(panCardAdapter);

        textview_message = findViewById(R.id.textview_message);

        if (AppManager.isOnline(PanCardStatement.this)) {
            mGetStatementData(type);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        swiprefresh_statement.setColorSchemeResources(R.color.colorPrimary);
        swiprefresh_statement.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (AppManager.isOnline(PanCardStatement.this)) {
                    start_page = 1;
                    panCardStatementItems.clear();
                    mGetStatementData(type);
                    textview_message.setVisibility(View.GONE);
                    recyclerview_statement.setVisibility(View.VISIBLE);
                } else {
                    textview_message.setText("No internet connection");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_statement.setVisibility(View.GONE);
                    swiprefresh_statement.setRefreshing(false);
                }
            }
        });
    }


    private void mGetStatementData(final String type) {
        class getJSONData extends AsyncTask<String, String, String> {


            HttpURLConnection urlConnection;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (swiprefresh_statement.isRefreshing()) {

                } else {
                    swiprefresh_statement.setRefreshing(true);
                }

            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(Constents.URL.baseUrl + "api/android/transaction?apptoken=" +
                            SharedPrefs.getValue(PanCardStatement.this, SharedPrefs.APP_TOKEN) + "&user_id=" +
                            SharedPrefs.getValue(PanCardStatement.this, SharedPrefs.USER_ID) + "&type=" + type + "&start=" + start_page + "&device_id=" + Constents.MOBILE_ID);
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
                if (swiprefresh_statement.isRefreshing()) {
                    swiprefresh_statement.setRefreshing(false);
                }
                //System.out.print("login response : "+result);

                if (!result.equals("")) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("statuscode")) {
                            String status = jsonObject.getString("statuscode");
                            if (status.equalsIgnoreCase("UA")) {
                                AppManager.getInstance().logoutApp(PanCardStatement.this);
                            } else if (status.equalsIgnoreCase("txn")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    PanCardStatementItems item = new PanCardStatementItems();
                                    item.setId(data.getString("id"));
                                    item.setAmount(data.getString("amount"));
                                    item.setBalance(data.getString("balance"));
                                    item.setCharge(data.getString("charge"));
                                    item.setCreated_at(data.getString("created_at"));
                                    item.setNumber(data.getString("number"));
                                    item.setDescription(data.getString("description"));
                                    item.setProfit(data.getString("profit"));
                                    item.setCharge(data.getString("charge"));
                                    item.setTxnid(data.getString("txnid"));
                                    item.setStatus(data.getString("status"));
                                    item.setVia(data.getString("via"));
                                    item.setMobile(data.getString("mobile"));
                                    item.setGst(data.getString("gst"));
                                    item.setTds(data.getString("tds"));
                                    item.setTrans_type(data.getString("trans_type"));


                                    panCardStatementItems.add(item);
                                }


                                if (jsonArray.length() > 0)
                                    panCardAdapter.notifyDataSetChanged();
                                last_array_empty = jsonArray.length() == 0;

                                if (start_page > 1) {
                                    recyclerview_statement.scrollToPosition(reloadCount);
                                }
                            } else {
                                recyclerview_statement.setVisibility(View.GONE);
                                textview_message.setText("Something went wrong");
                                textview_message.setVisibility(View.VISIBLE);
                            }
                        }

                        if (panCardStatementItems.size() == 0) {
                            textview_message.setText("No records found");
                            recyclerview_statement.setVisibility(View.GONE);
                            textview_message.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    textview_message.setText("Something went wrong");
                    recyclerview_statement.setVisibility(View.GONE);
                    textview_message.setVisibility(View.VISIBLE);
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

        if (AppManager.isOnline(PanCardStatement.this)) {
            mGetStatementData(type);
        }
    }

}
