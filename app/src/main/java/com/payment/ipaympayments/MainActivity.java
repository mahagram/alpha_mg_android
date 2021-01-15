package com.payment.ipaympayments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.fragment.HelpFragment;
import com.payment.ipaympayments.fragment.HomeFragment;
import com.payment.ipaympayments.fragment.ProfileFragment;
import com.payment.ipaympayments.fragment.ReportFragment;
import com.payment.ipaympayments.httpRequest.UpdateBillService;
import com.payment.ipaympayments.model.MicroATMModel;
import com.payment.ipaympayments.printer.Invoice;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.PrefLoginManager;

import java.util.ArrayList;
import java.util.List;

import static com.payment.ipaympayments.utill.AppManager.getImei;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS = 2000;
    private FrameLayout frameLayout;
    private BottomNavigationView navigation;
    private FloatingActionButton fabButton;
    private Context context;
    MicroATMModel model;

    private void init() {
        context = MainActivity.this;
        frameLayout = findViewById(R.id.frame_container);
        fabButton = findViewById(R.id.fabButton);
        Constents.MOBILE_ID = getImei(MainActivity.this);
    }

    int fragPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else {
            viewInit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        int accessCourseLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int writeExternalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPhoneState = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        List<String> permissions = new ArrayList<>();
        if (accessCourseLocation != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(readPhoneState!=PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), MY_PERMISSIONS);
        } else {
            viewInit();
        }
    }

    boolean permissionState;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean isAllPermissionGranted = false;
        switch (requestCode) {
            case MY_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (!permissionState)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission();
                                boolean showRationale = shouldShowRequestPermissionRationale(permissions[i]);
                                if (!showRationale) {
                                    permissionState = true;
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                            MainActivity.this);
                                    alertDialogBuilder.setTitle("Permission Deny");
                                    alertDialogBuilder.setMessage("Application unable to run while " + permissions[i] + " permission deny! Open app setting and enable permission.");
                                    alertDialogBuilder
                                            .setCancelable(false)
                                            .setPositiveButton("Setting",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                            dialog.cancel();
                                                            Intent intent = new Intent();
                                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                            intent.setData(uri);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void viewInit() {
        init();
        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());
        fragPosition = 0;

        fabButton.setOnClickListener(v -> {
            if (fragPosition == 3) {
                AppManager.getInstance().logoutFromServer(MainActivity.this);
            } else {
                new UpdateBillService(this);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.home:
                fragPosition = 0;
                fragment = new HomeFragment();
                loadFragment(fragment);
                return true;
            case R.id.shop:
                fragPosition = 1;
                fragment = new ReportFragment();
                loadFragment(fragment);
                return true;
            case R.id.help:
                fragPosition = 2;
                fragment = new HelpFragment();
                loadFragment(fragment);
                return true;
            case R.id.profile:
                fragPosition = 3;
                fragment = new ProfileFragment();
                loadFragment(fragment);
                return true;
        }
        return false;
    };

    private void loadFragment(Fragment fragment) {
        changeFabIcon();
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (fragPosition == 0) {
            confirmPopup();
        } else {
            Fragment fragment = new HomeFragment();
            loadFragment(fragment);
            navigation.setSelectedItemId(R.id.home);
        }
    }

    AlertDialog alert;

    private void confirmPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.dismiss();
                    }
                });

        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                Toast.makeText(this, data.getStringExtra("StatusCode") + "\n" + data.getStringExtra("Message"), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                String respCode = data.getStringExtra("respcode");
                if (respCode.equals("999")) {//Response code from bank(999 for pending transactions 00 for success)
                    String requesttxn = data.getStringExtra("requesttxn ");//Type of transaction
                    String refstan = data.getStringExtra("refstan");// Mahagram Stan Numbe
                    String txnamount = data.getStringExtra("txnamount");//Transaction amount (0 in case of balanceinquiry and transaction amount in cash withdrawal)
                    String mid = data.getStringExtra("mid");//Mid
                    String tid = data.getStringExtra("tid");//Tid
                    String clientrefid = data.getStringExtra("clientrefid");//Your reference Id
                    String vendorid = data.getStringExtra("vendorid");//Your define value
                    String udf1 = data.getStringExtra("udf1");//Your define value
                    String udf2 = data.getStringExtra("udf2");//Your define value
                    String udf3 = data.getStringExtra("udf3");//Your define value
                    String udf4 = data.getStringExtra("udf4");//Your define value
                    String date = data.getStringExtra("date");//Date of transaction
                    Toast.makeText(this, "txnamount " + txnamount, Toast.LENGTH_LONG).show();
                    // public MicroATMModel(String requesttxn, String refstan, String txnamount, String mid, String tid, String clientrefid, String vendorid, String udf1, String udf2, String udf3, String udf4, String date, String bankremarks, String cardno, String amount, String invoicenumber, String rrn, String statuscode, String message) {
                    model = new MicroATMModel(requesttxn, refstan, txnamount, mid, tid, clientrefid, vendorid, udf1, udf2, udf3, udf4, date, "", "", "", "", "", respCode, "");
                    uplaodResponse(model);
                } else {
                    String requesttxn = data.getStringExtra("requesttxn ");//Type of transaction
                    String bankremarks = data.getStringExtra("msg");//Bank message
                    String refstan = data.getStringExtra("refstan");// Mahagram Stan Number
                    String cardno = data.getStringExtra("cardno");//Atm card number
                    String date = data.getStringExtra("date");//Date of transaction
                    String amount = data.getStringExtra("amount");//Account Balance
                    String invoicenumber = data.getStringExtra("invoicenumber");//Invoice Number
                    String mid = data.getStringExtra("mid");//Mid
                    String tid = data.getStringExtra("tid");//Tid
                    String clientrefid = data.getStringExtra("clientrefid");//Your reference Id
                    String vendorid = data.getStringExtra("vendorid");//Your define value
                    String udf1 = data.getStringExtra("udf1");//Your define value
                    String udf2 = data.getStringExtra("udf2");//Your define value
                    String udf3 = data.getStringExtra("udf3");//Your define value
                    String udf4 = data.getStringExtra("udf4");//Your define value
                    String txnamount = data.getStringExtra("txnamount");//Transaction amount (0 in case of balanceinquiry and transaction amount in cash withdrawal)
                    String rrn = data.getStringExtra("rrn");//Bank RRN number
                    Toast.makeText(this, "txnamount " + txnamount, Toast.LENGTH_LONG).show();
                    model = new MicroATMModel(requesttxn, refstan, txnamount, mid, tid, clientrefid, vendorid, udf1, udf2, udf3, udf4, date, bankremarks, cardno, amount, invoicenumber, rrn, respCode, "");
                    uplaodResponse(model);
                }
            } else {
                try {
                    data.getStringExtra("statuscode"); //to get status code
                    data.getStringExtra("message"); //to get response message
                    if (data.getStringExtra("statuscode").equals("111")) {
                        sdk();
                    } else {
                        Toast.makeText(this, "Status " + data.getStringExtra("statuscode") + "\nMessage " + data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, data.getStringExtra("StatusCode") + "\n" + data.getStringExtra("Message"), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uplaodResponse(MicroATMModel model) {
        Intent intent = new Intent(context, Invoice.class);
        intent.putExtra("data", model);
        startActivity(intent);
    }

    private void changeFabIcon() {
        switch (fragPosition) {
            case 0:
            case 1:
            case 2:
                setDrawable(R.drawable.refresh_icon);
                break;
            case 3:
                setDrawable(R.drawable.logout_icon);
        }
    }

    private void setDrawable(int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabButton.setImageDrawable(getResources().getDrawable(drawable, this.getTheme()));
        } else {
            fabButton.setImageDrawable(getResources().getDrawable(drawable));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefLoginManager pref = new PrefLoginManager(this);
        String status = pref.getFarmerLoginRes();
        if (status.length() == 0) {
            AppManager.getInstance().logoutApp(MainActivity.this);
        }
    }

    private void sdk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage("MAtm sdk version is outdated do you want to update ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.dismiss();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=org.egram.microatm")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert.dismiss();
                    }
                });

        alert = builder.create();
        alert.show();
    }
}
