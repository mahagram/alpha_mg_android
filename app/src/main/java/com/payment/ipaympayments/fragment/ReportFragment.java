package com.payment.ipaympayments.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.WalletREquestReport.WalletRequestsReports;
import com.payment.ipaympayments.activity.AEPSRequests;
import com.payment.ipaympayments.activity.DMTStatement;
import com.payment.ipaympayments.activity.McroAtmSettleReport;
import com.payment.ipaympayments.activity.Statement;
import com.payment.ipaympayments.activity.TransactionReports;
import com.payment.ipaympayments.adapter.ReportsListAdapter;
import com.payment.ipaympayments.pancards.PanCardStatement;
import com.payment.ipaympayments.utill.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {


    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activtiy_all_reports, container, false);

        listView = view.findViewById(R.id.listView);

        initListView();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                Intent intent = new Intent(getActivity(), TransactionReports.class);
                intent.putExtra("type", "aepsstatement");
                startActivity(intent);
            } else if (position == 1) {
                Intent intent = new Intent(getActivity(), TransactionReports.class);
                intent.putExtra("type", "awalletstatement");
                startActivity(intent);
            } else if (position == 2) {
                Intent intent = new Intent(getActivity(), AEPSRequests.class);
                intent.putExtra("type", "awalletstatement");
                startActivity(intent);
            } else if (position == 3) {
                Intent intent = new Intent(getActivity(), Statement.class);
                intent.putExtra("activity", "recharge");
                startActivity(intent);
            } else if (position == 4) {
                Intent intent = new Intent(getActivity(), Statement.class);
                intent.putExtra("activity", "bill");
                startActivity(intent);
            } else if (position == 5) {
                Intent intent = new Intent(getActivity(), DMTStatement.class);
                startActivity(intent);
            } else if (position == 6) {
                Intent intent = new Intent(getActivity(), PanCardStatement.class);
                startActivity(intent);
            } else if (position == 7) {
                Intent intent = new Intent(getActivity(), WalletRequestsReports.class);
                startActivity(intent);
            } else if (position == 8) {
                Intent intent = new Intent(getActivity(), TransactionReports.class);
                intent.putExtra("type", "matmstatement");
                startActivity(intent);
            } else if (position == 9) {
                Intent intent = new Intent(getActivity(), McroAtmSettleReport.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initListView() {
        List<String> dataList = new ArrayList<>();
        dataList.add("Aeps Transactions");
        dataList.add("Aeps Statements");
        dataList.add("Aeps Settlement Reports");
        dataList.add("Recharge Statement");
        dataList.add("Bill Pay Statement");
        dataList.add("DMT Statement");
        dataList.add("Pancard Statement");
        dataList.add("Wallet Request Report");
        String MatmFlag = SharedPrefs.getValue(getActivity(), SharedPrefs.MICRO_ATM_BALANCE);
        if (MatmFlag != null && !MatmFlag.equalsIgnoreCase("no")) {
            dataList.add("MATM Transaction Report");
            dataList.add("MATM Settlement Report");
        }
        ReportsListAdapter adapter = new ReportsListAdapter(getActivity(), dataList);
        listView.setAdapter(adapter);
    }
}