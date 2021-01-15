package com.payment.ipaympayments.activity;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.payment.ipaympayments.R;
import com.payment.ipaympayments.adapter.DMTCardAdapter;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.model.StatementItems;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.Print;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DMTStatement extends AppCompatActivity {


    SwipeRefreshLayout swiprefresh_statement;
    RecyclerView recyclerview_statement;
    TextView textview_message;

    List<StatementItems> statementItems;
    DMTCardAdapter cardAdapter;

    String type = "dmtstatement";

    public static boolean last_array_empty = false;
    public static int start_page = 0;
    boolean swiped_refresh = false;

    private int reloadCount = 0;

    private TextView fromDate, toDate, tvSearch, tvFilterButton;
    private Spinner spnrStatus;
    private Button btnFilter;
    private ArrayAdapter stateAdapter;
    private int requestFlag;
    private List<String> stateNameList = new ArrayList<>();
    private String LEVEL1 = "-1";
    private boolean isFilterData = false;
    private String fromDateString = "", toDateString = "", searchText = "";
    private View filterContainer;

    private void init() {
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        btnFilter = findViewById(R.id.btnFetch);
        spnrStatus = findViewById(R.id.spnrStatus);
        btnFilter = findViewById(R.id.btnFetch);
        tvSearch = findViewById(R.id.tvSearch);
        tvFilterButton = findViewById(R.id.tvFilterButton);
        filterContainer = findViewById(R.id.filterContainer);
        tvSearch.setText("");

        tvFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterContainer.isShown()) {
                    filterContainer.setVisibility(View.GONE);
                    tvFilterButton.setText("Open Filter");
                    isFilterData = false;
                    stateAdapter.notifyDataSetChanged();
                    tvSearch.setText("");
                    searchText = "";
                    swiped_refresh = false;
                    statementItems = new ArrayList<>();
                    start_page = 1;
                    if (AppManager.isOnline(DMTStatement.this)) {
                        start_page = 1;
                        mGetStatementData(type);
                        textview_message.setVisibility(View.GONE);
                        recyclerview_statement.setVisibility(View.VISIBLE);
                        swiped_refresh = true;
                    } else {
                        textview_message.setText("No internet connection");
                        textview_message.setVisibility(View.VISIBLE);
                        recyclerview_statement.setVisibility(View.GONE);
                        swiprefresh_statement.setRefreshing(false);
                    }
                } else {
                    filterContainer.setVisibility(View.VISIBLE);
                    tvFilterButton.setText("Close Filter");
                }
            }
        });

        stateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateNameList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spnrStatus.setAdapter(stateAdapter);

        //accept, pending, success, reversed, refunded,failed
        stateNameList.add("Select Status");
        stateNameList.add("accept");
        stateNameList.add("pending");
        stateNameList.add("success");
        stateNameList.add("reversed");
        stateNameList.add("refunded");
        stateNameList.add("failed");
        stateAdapter.notifyDataSetChanged();

        spnrStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Log.e("STATE SELECTED", "onItemSelected: " + stateNameList.get(position + 1));
                if (position > 0) {
                    LEVEL1 = stateNameList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                System.out.println("nothing is selected");

            }
        });

        fromDate.setOnClickListener(arg0 -> {
            final AlertDialog.Builder adb = new AlertDialog.Builder(DMTStatement.this);
            final View view = LayoutInflater.from(DMTStatement.this).inflate(R.layout.date_picker, null);
            adb.setView(view);
            final Dialog dialog;
            adb.setPositiveButton("Add",
                    (dialog1, arg1) -> {
                        DatePicker etDatePicker = view.findViewById(R.id.datePicker1);
                        //etDatePicker.setMaxDate(System.currentTimeMillis() + 1000);
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(etDatePicker.getYear(), etDatePicker.getMonth(), etDatePicker.getDayOfMonth());
                        Date date = null;
                        date = cal.getTime();
                        String approxDateString = Constents.SHOWING_DATE_FORMAT.format(date);
                        fromDate.setText(approxDateString);
                        fromDateString = approxDateString;
                    });
            dialog = adb.create();
            dialog.show();
        });

        toDate.setOnClickListener(arg0 -> {
            final AlertDialog.Builder adb = new AlertDialog.Builder(DMTStatement.this);
            final View view = LayoutInflater.from(DMTStatement.this).inflate(R.layout.date_picker, null);
            adb.setView(view);
            final Dialog dialog;
            adb.setPositiveButton("Add",
                    (dialog1, arg1) -> {
                        DatePicker etDatePicker = view.findViewById(R.id.datePicker1);
                        //etDatePicker.setMaxDate(System.currentTimeMillis() + 1000);
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(etDatePicker.getYear(), etDatePicker.getMonth(), etDatePicker.getDayOfMonth());
                        Date date = null;
                        date = cal.getTime();
                        String approxDateString = Constents.SHOWING_DATE_FORMAT.format(date);
                        toDate.setText(approxDateString);
                        toDateString = approxDateString;
                    });
            dialog = adb.create();
            dialog.show();
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = tvSearch.getText().toString();
                statementItems = new ArrayList<>();
                swiped_refresh = false;
                start_page = 1;
                isFilterData = true;
                if (AppManager.isOnline(DMTStatement.this)) {
                    start_page = 1;
                    mGetStatementData(type);
                    textview_message.setVisibility(View.GONE);
                    recyclerview_statement.setVisibility(View.VISIBLE);
                    swiped_refresh = true;
                } else {
                    textview_message.setText("No internet connection");
                    textview_message.setVisibility(View.VISIBLE);
                    recyclerview_statement.setVisibility(View.GONE);
                    swiprefresh_statement.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card_statement);

        statementItems = new ArrayList<>();
        start_page = 1;
        init();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swiprefresh_statement = findViewById(R.id.swiprefresh_statement);

        recyclerview_statement = findViewById(R.id.recyclerview_statement);
        recyclerview_statement.setLayoutManager(new LinearLayoutManager(DMTStatement.this));

        cardAdapter = new DMTCardAdapter(DMTStatement.this, statementItems);
        recyclerview_statement.setAdapter(cardAdapter);

        textview_message = findViewById(R.id.textview_message);

        if (AppManager.isOnline(DMTStatement.this)) {
            mGetStatementData(type);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        swiprefresh_statement.setColorSchemeResources(R.color.colorPrimary);
        swiprefresh_statement.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (AppManager.isOnline(DMTStatement.this)) {
                    start_page = 1;
                    statementItems.clear();
                    mGetStatementData(type);
                    textview_message.setVisibility(View.GONE);
                    recyclerview_statement.setVisibility(View.VISIBLE);
                    swiped_refresh = true;
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
                    URL url;
                    if (isFilterData) {
                        //fromdate, todate, searchtext, status
                        String u = Constents.URL.baseUrl + "api/android/transaction?apptoken=" +
                                SharedPrefs.getValue(DMTStatement.this, SharedPrefs.APP_TOKEN) + "" +
                                "&fromdate=" + fromDateString + "" +
                                "&todate=" + toDateString + "" +
                                "&searchtext=" + searchText + "" +
                                "&status=" + LEVEL1 + "" +
                                "&user_id=" + SharedPrefs.getValue(DMTStatement.this, SharedPrefs.USER_ID)
                                + "" +
                                "&type=" + type + "&start=" + start_page+ "&device_id=" + Constents.MOBILE_ID;
                        url = new URL(u);
                        Print.P(u);
                    } else {
                        url = new URL(Constents.URL.baseUrl + "api/android/transaction?apptoken=" +
                                SharedPrefs.getValue(DMTStatement.this, SharedPrefs.APP_TOKEN) + "&user_id="
                                + SharedPrefs.getValue(DMTStatement.this, SharedPrefs.USER_ID) + "&type=" + type + "&start=" + start_page+ "&device_id=" + Constents.MOBILE_ID);

                    }


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
                //System.out.print(" response : " + result);
                //Log.e("data", result);

                if (!result.equals("")) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("statuscode")) {

                            String status = jsonObject.getString("statuscode");
                            if (status.equalsIgnoreCase("UA")) {
                                AppManager.getInstance().logoutApp(DMTStatement.this);
                            } else if (status.equalsIgnoreCase("txn")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    StatementItems item = new StatementItems();
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
                                    //item.setVia(data.getString("via"));
                                    item.setMobile(data.getString("mobile"));
                                    item.setGst(data.getString("gst"));
                                    item.setTds(data.getString("tds"));
                                    item.setVia(data.getString("updated_at"));
                                    item.setTrans_type(data.getString("trans_type"));


                                    statementItems.add(item);

                                }

                                if (jsonArray.length() > 0)
                                    cardAdapter.notifyDataSetChanged();
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

                        if (statementItems.size() == 0) {
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

        if (AppManager.isOnline(DMTStatement.this)) {
            mGetStatementData(type);
        }
    }
}
