package com.payment.ipaympayments.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.WalletREquestReport.WalletRequestsReports;
import com.payment.ipaympayments.adapter.ReportsListAdapter;
import com.payment.ipaympayments.pancards.PanCardStatement;

import java.util.ArrayList;
import java.util.List;

public class ReportsList extends AppCompatActivity {
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_all_reports);
        listView = findViewById(R.id.listView);

        initListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(ReportsList.this, TransactionReports.class);
                    intent.putExtra("type", "aepsstatement");
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(ReportsList.this, TransactionReports.class);
                    intent.putExtra("type", "awalletstatement");
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(ReportsList.this, AEPSRequests.class);
                    intent.putExtra("type", "awalletstatement");
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(ReportsList.this, Statement.class);
                    intent.putExtra("activity", "recharge");
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(ReportsList.this, Statement.class);
                    intent.putExtra("activity", "bill");
                    startActivity(intent);
                } else if (position == 5) {
                    Intent intent = new Intent(ReportsList.this, DMTStatement.class);
                    startActivity(intent);
                } else if (position == 6) {
                    Intent intent = new Intent(ReportsList.this, PanCardStatement.class);
                    startActivity(intent);
                } else if (position == 7) {
                    Intent intent = new Intent(ReportsList.this, WalletRequestsReports.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initListView() {
        List<String> dataList = new ArrayList<>();
        dataList.add("Aeps Transactions");
        dataList.add("Aeps Statements");
        dataList.add("Aeps Request Reports");
        dataList.add("Recharge Statement");
        dataList.add("Bill Pay Statement");
        dataList.add("DMT Statement");
        dataList.add("Pancard Statement");
        dataList.add("Wallet Request Report");
        ReportsListAdapter adapter = new ReportsListAdapter(this, dataList);
        listView.setAdapter(adapter);
    }
}
