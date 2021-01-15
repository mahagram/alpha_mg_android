package com.payment.ipaympayments.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.payment.ipaympayments.R;
import com.payment.ipaympayments.activity.AepsWalletRequest;
import com.payment.ipaympayments.activity.MicoAtmWalletRequest;
import com.payment.ipaympayments.activity.UpdatePassword;
import com.payment.ipaympayments.activity.WalletRequest;
import com.payment.ipaympayments.adapter.MenuListAdapter;
import com.payment.ipaympayments.httpRequest.UpdateBillService;
import com.payment.ipaympayments.model.ProfileMenuModel;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.SharedPrefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    TextView tvMicroAtmBalance;
    private ListView listView;
    private TextView tvBalance, tvName, tvNumber, tvAeps, tvAccount, tvBank, tvIFSC,tvAgentID;
    private LinearLayout mainCon, secondCon;
    private ImageView imgSync;

    private void mAtmController(View view) {

        LinearLayout mainCon1 = view.findViewById(R.id.mainCon1);
        String mAtm = SharedPrefs.getValue(getActivity(), SharedPrefs.MICRO_ATM_BALANCE);
        if (mAtm != null && mAtm.equalsIgnoreCase("no")) {
            mainCon1.setVisibility(View.GONE);
        } else {
            mainCon1.setVisibility(View.VISIBLE);
            mainCon1.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MicoAtmWalletRequest.class);
                startActivity(intent);
            });
        }

    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void init(View v) {
        tvBalance = v.findViewById(R.id.tvBalance);
        tvName = v.findViewById(R.id.tvName);
        tvNumber = v.findViewById(R.id.tvNumber);
        tvAeps = v.findViewById(R.id.tvAeps);
        tvAccount = v.findViewById(R.id.tvAccount);
        tvBank = v.findViewById(R.id.tvBank);
        tvIFSC = v.findViewById(R.id.tvIFSC);
        mainCon = v.findViewById(R.id.mainCon);
        secondCon = v.findViewById(R.id.secondCon);
        imgSync = v.findViewById(R.id.imgSync);
        tvMicroAtmBalance = v.findViewById(R.id.tvMicroAtmBalance);
        tvAgentID = v.findViewById(R.id.tvAgentID);
        tvAgentID.setText(SharedPrefs.getValue(getActivity(),SharedPrefs.USER_ID));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        init(view);
        mAtmController(view);

        listView = view.findViewById(R.id.listView);

        initListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), UpdatePassword.class));
                        break;
                }
            }
        });

        String name = SharedPrefs.getValue(getActivity(), SharedPrefs.USER_NAME);
        String balance = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.MAIN_WALLET);
        String aeps = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.APES_BALANCE);
        String contact = SharedPrefs.getValue(getActivity(), SharedPrefs.USER_CONTACT);
        String account = SharedPrefs.getValue(getActivity(), SharedPrefs.ACCOUNT);
        String bank = SharedPrefs.getValue(getActivity(), SharedPrefs.BANK);
        String ifsc = SharedPrefs.getValue(getActivity(), SharedPrefs.IFSC);
        String MaMOUNT = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.MICRO_ATM_BALANCE);
        tvName.setText(name);
        tvBalance.setText(balance);
        tvNumber.setText(contact);
        tvAeps.setText(aeps);
        tvAccount.setText(account);
        tvBank.setText(bank);
        tvIFSC.setText(ifsc);
        tvMicroAtmBalance.setText(MaMOUNT);

        mainCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WalletRequest.class);
                startActivity(intent);
            }
        });

        secondCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AepsWalletRequest.class);
                startActivity(intent);
            }
        });


        TextView tv = view.findViewById(R.id.tvSettleMent);


        imgSync.setOnClickListener(v -> new UpdateBillService(getActivity(), tvBalance, tvAeps, tvMicroAtmBalance));

        FloatingActionButton sendBtn = view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //20 July 2019 Update
                    // Change Email id and add add addition log file to share
                    File logFile = new File(Environment.getExternalStorageDirectory(), getActivity().getString(R.string.app_name) + ".txt");
                    //String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                    //String FileName = "CCE_Log_" + timeStamp + ".txt";
                    File logFile2 = new File(getFiles());
                    System.out.println("logFile: " + logFile);
                    if (logFile.exists() || logFile2.exists()) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        emailIntent.setType("vnd.android.cursor.dir/email");
                        String to[] = {"sumitkumarx95@gmail.com"};
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                        ArrayList<Uri> outputFileUri = new ArrayList<Uri>();
                        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
                            if (logFile.exists()) {
                                outputFileUri.add(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".files", logFile));
                            }
                            if (logFile2.exists()) {
                                outputFileUri.add(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".files", logFile2));
                            }//  outputFileUri.add
                        } else {
                            if (logFile.exists()) {
                                outputFileUri.add(Uri.fromFile(logFile));
                            }
                            if (logFile2.exists()) {
                                outputFileUri.add(Uri.fromFile(logFile2));
                            }
                        }
                        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, outputFileUri);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Error log");
                        if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        } else {
                            Toast.makeText(getActivity(), "No email application is available to share error log file", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    AppManager.appendLog(e);
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    // 20 July Added
    // This code with return the newly created log file when user click on send error option.
    private String getFiles() {
        File sd = new File(Environment.getExternalStorageDirectory() + "/SECURE_PAYMENT/LOG/");
        File[] sdFileList = sd.listFiles();
        if (sdFileList != null && sdFileList.length > 1) {
            Arrays.sort(sdFileList, (object1, object2) -> (int) (Math.min(object2.lastModified(), object1.lastModified())));
            return sdFileList[sdFileList.length - 1].getAbsolutePath();
        } else if (sdFileList != null && sdFileList.length == 1) {
            return sdFileList[sdFileList.length - 1].getAbsolutePath();
        } else {
            return "";
        }
    }

    private void initListView() {
        List<ProfileMenuModel> dataList = new ArrayList<>();
        //dataList.add(new ProfileMenuModel(R.drawable.profile_icon, "Edit Profile"));
        dataList.add(new ProfileMenuModel(R.drawable.pass_ic, "Change Password"));
        //dataList.add(new ProfileMenuModel(R.drawable.pass_reset_ic, "Change Tpin"));
        //dataList.add(new ProfileMenuModel(R.drawable.payment_h_ic, "Payment History"));
        //dataList.add(new ProfileMenuModel(R.drawable.payment_h_ic, "Saved Payment"));
        MenuListAdapter adapter = new MenuListAdapter(getActivity(), dataList);
        listView.setAdapter(adapter);
    }
}