package com.payment.ipaympayments.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.payment.ipaympayments.R;
import com.payment.ipaympayments.interfaces.UpdateBeneList;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SenderDetailActivity extends AppCompatActivity implements UpdateBeneList {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String sender_number;
    private String name, status, available_limit, limit_spend, json;
    private TextView textView_sendername, textView_senderstatus, textView_senderavailablelitmit, textView_sendertotalspend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_detail);

        textView_sendername = findViewById(R.id.textView_sendername);
        textView_senderstatus = findViewById(R.id.textView_senderstatus);
        textView_senderavailablelitmit = findViewById(R.id.textView_senderavailablelimit);
        textView_sendertotalspend = findViewById(R.id.textView_sendertotalspend);


        Bundle bundle = getIntent().getExtras();
        sender_number = bundle.getString("sender_number");
        name = bundle.getString("name");
        status = bundle.getString("status");
        available_limit = bundle.getString("available_limit");
        limit_spend = bundle.getString("total_spend");
        json = bundle.getString("json");

        textView_sendername.setText("Dear " + name + " (" + sender_number + ")");
        textView_senderstatus.setText("Status : " + status);
        textView_senderstatus.setVisibility(View.GONE);
        textView_senderavailablelitmit.setText("Total Limit : " + available_limit);
        textView_sendertotalspend.setText("Used Limit : " + limit_spend);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //tabLayout.getTabAt(0).setIcon(R.drawable.bell_icon);
        //tabLayout.getTabAt(1).setIcon(R.drawable.bulb_icon);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
       // adapter.addFragment(new SavedBenActivity().newInstance(sender_number, name, json), "Saved");
       // adapter.addFragment(new AddBenActivity().newInstance(sender_number, name), "Add Beneficiary");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

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

    public void Update() {
        Bundle bundle = getIntent().getExtras();
        sender_number = bundle.getString("sender_number");
        name = bundle.getString("name");
        status = bundle.getString("status");
        available_limit = bundle.getString("available_limit");
        limit_spend = bundle.getString("total_spend");
        json = bundle.getString("json");

        textView_sendername.setText("Dear " + name + " (" + sender_number + ")");
        textView_senderstatus.setText("Status : " + status);
        textView_senderstatus.setVisibility(View.GONE);
        textView_senderavailablelitmit.setText("Total Limit : " + available_limit);
        textView_sendertotalspend.setText("Used Limit : " + limit_spend);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //tabLayout.getTabAt(0).setIcon(R.drawable.bell_icon);
        //tabLayout.getTabAt(1).setIcon(R.drawable.bulb_icon);
    }
}