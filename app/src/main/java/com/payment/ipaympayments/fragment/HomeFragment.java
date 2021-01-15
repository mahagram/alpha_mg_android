package com.payment.ipaympayments.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.payment.ipaympayments.MoneyTransfer.SenderActivity;
import com.payment.ipaympayments.R;
import com.payment.ipaympayments.activity.AepsWalletRequest;
import com.payment.ipaympayments.activity.DTHRecharge;
import com.payment.ipaympayments.activity.MicoAtmWalletRequest;
import com.payment.ipaympayments.activity.MobileRecharge;
import com.payment.ipaympayments.activity.OurServices;
import com.payment.ipaympayments.activity.ReportsList;
import com.payment.ipaympayments.activity.Statement;
import com.payment.ipaympayments.activity.TransactionReports;
import com.payment.ipaympayments.activity.WalletRequest;
import com.payment.ipaympayments.adapter.HomeCenterGridAdapter;
import com.payment.ipaympayments.adapter.HomeFragOfferSliderAdapter;
import com.payment.ipaympayments.adapter.Offer_Pager_Adapter;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.bppsServices.OperatorListNew;
import com.payment.ipaympayments.httpRequest.UpdateBillService;
import com.payment.ipaympayments.httpRequest.VolleyGetNetworkCall;
import com.payment.ipaympayments.model.CenterGridModel;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.GridSpacingItemDecoration;
import com.payment.ipaympayments.utill.SharedPrefs;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.egram.aepslib.DashboardActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class HomeFragment extends Fragment implements HomeCenterGridAdapter.OnItemClick, VolleyGetNetworkCall.RequestResponseLis {

    private RecyclerView centerOptionsGrid;
    private List<Integer> offerDataList;
    TextView tvMicroAtmBalance;
    private TextView tvBalance, tvName, tvNumber, tvAeps, tvAccount, tvBank, tvIFSC;
    private LinearLayout mainCon, secondCon;
    private HomeFragOfferSliderAdapter offerSliderAdapter;
    private LinearLayout menuRechargeReport, menuBillReport, menuAepsReport, menuAllReport;
    private ImageView imgSync;
    private String user_id;
    private int REQUEST_TYPE = 0;
    // private RelativeLayout walletContainer;

    private List<String> banners;
    private List<Integer> drawables;
    private int bannerAvail = 0;
    private Offer_Pager_Adapter offer_pager_adapter;
    private int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;

    ArrayList<String> bannerImages;
    CarouselView carouselView;

    LinearLayout mainCon1;

    private void init(View view) {
        mainCon1 = view.findViewById(R.id.mainCon1);
        centerOptionsGrid = view.findViewById(R.id.centerOptionsGrid);
        //tvWallet = view.findViewById(R.id.tvWallet);
        menuRechargeReport = view.findViewById(R.id.menuRechargeReport);
        menuBillReport = view.findViewById(R.id.menuBillReport);
        menuAepsReport = view.findViewById(R.id.menuAepsReport);
        menuAllReport = view.findViewById(R.id.menuAllReport);
        tvMicroAtmBalance = view.findViewById(R.id.tvMicroAtmBalance);
        imgSync = view.findViewById(R.id.imgSync);
        mainCon = view.findViewById(R.id.mainCon);
        secondCon = view.findViewById(R.id.secondCon);
        //walletContainer = view.findViewById(R.id.walletContainer);
        tvAeps = view.findViewById(R.id.tvAeps);
        tvBalance = view.findViewById(R.id.tvBalance);
    }

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        init(view);
        prepareOfferData();
        initializeList();
        mAtmControlller();
        slider(view);

        menuRechargeReport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Statement.class);
            intent.putExtra("activity", "recharge");
            startActivity(intent);
        });

        menuBillReport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Statement.class);
            intent.putExtra("activity", "bill");
            startActivity(intent);
        });

        menuAepsReport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TransactionReports.class);
            intent.putExtra("type", "aepsstatement");
            startActivity(intent);
        });

        menuAllReport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReportsList.class);
            startActivity(intent);
        });

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


        imgSync.setOnClickListener(v -> new UpdateBillService(getActivity(), tvBalance, tvAeps, tvMicroAtmBalance));

        return view;
    }

    private void prepareOfferData() {
        drawables = new ArrayList<>();
        banners = new ArrayList<>();

        String is_avail_array = SharedPrefs.getValue(getActivity(), SharedPrefs.BANNERARRAY);
        try {
            if (is_avail_array != null && is_avail_array.length() > 0) {
                bannerAvail = 1;
                try {
                    JSONArray bannersJson = new JSONArray(is_avail_array);
                    for (int i = 0; i < bannersJson.length(); i++) {
                        JSONObject obj = bannersJson.getJSONObject(i);
                        String url = Constents.URL.baseUrl + "/public/" + obj.getString("value");
                        banners.add(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                bannerAvail = 0;
                drawables.add(R.drawable.image1);
                drawables.add(R.drawable.image2);
                drawables.add(R.drawable.image3);
                drawables.add(R.drawable.image4);
                drawables.add(R.drawable.image5);
            }
        } catch (Exception e) {
            bannerAvail = 0;
            drawables.add(R.drawable.image1);
            drawables.add(R.drawable.image2);
            drawables.add(R.drawable.image3);
            drawables.add(R.drawable.image4);
            drawables.add(R.drawable.image5);
        }
    }

    private void slider(View view) {
        bannerImages = new ArrayList<String>();

        offerDataList = new ArrayList<>();
        offerDataList.add(R.drawable.image1);
        offerDataList.add(R.drawable.image2);
        offerDataList.add(R.drawable.image3);
        offerDataList.add(R.drawable.image4);
        offerDataList.add(R.drawable.image5);

        String is_avail_array = SharedPrefs.getValue(getActivity(), SharedPrefs.BANNERARRAY);
        try {
            if (is_avail_array != null && is_avail_array.length() > 0) {
                try {
                    JSONArray bannersJson = new JSONArray(is_avail_array);
                    for (int i = 0; i < bannersJson.length(); i++) {
                        JSONObject obj = bannersJson.getJSONObject(i);
                        String url = Constents.URL.baseUrl + "/public/" + obj.getString("value");
                        bannerImages.add(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        bannerImages.add("https://besthqwallpapers.com/Uploads/24-3-2018/45534/thumb2-online-shopping-4k-modern-technology-payment-online-e-commerce.jpg");
//        bannerImages.add("https://miro.medium.com/max/1200/1*BuFLVvsj1c4wHkrzHv8wNg.jpeg");
//        bannerImages.add("https://cdn.dribbble.com/users/1810601/screenshots/9344342/save-money_4x.png");
//        bannerImages.add("https://www.teahub.io/photos/full/279-2797644_online-financial-services.jpg");

        carouselView = view.findViewById(R.id.carouselView);
        if (bannerImages.size() > 0) {
            carouselView.setPageCount(bannerImages.size());
        } else {
            carouselView.setPageCount(offerDataList.size());
        }
        carouselView.setImageListener(imageListener);
    }

    private ImageListener imageListener = (position, imageView) -> {
        if (bannerImages.size() > 0) {
            //carouselView.setPageCount(bannerImages.size());
            String url = bannerImages.get(position);
            AppManager.loadImage(getActivity(), imageView, url);
        } else {
            imageView.setImageResource(offerDataList.get(position));
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitial();
    }

    @Override
    public void onResume() {
        super.onResume();
        new UpdateBillService(getActivity(), tvBalance, tvAeps, tvMicroAtmBalance);
        mAtmControlller();
    }

    private void setInitial() {
        String balance = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.MAIN_WALLET);
        String aeps = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.APES_BALANCE);
        String MaMOUNT = getString(R.string.rupee) + " " + SharedPrefs.getValue(getActivity(), SharedPrefs.MICRO_ATM_BALANCE);
        tvMicroAtmBalance.setText(MaMOUNT);
        tvBalance.setText(balance);
        tvAeps.setText(aeps);
    }

    private void mAtmControlller() {
        if (mainCon1 != null) {
            String mAtm = SharedPrefs.getValue(getActivity(), SharedPrefs.MICRO_ATM_BALANCE);
            if (mAtm != null && mAtm.equalsIgnoreCase("no")) {
                mainCon1.setVisibility(View.GONE);
            } else {
                mainCon1.setVisibility(View.VISIBLE);
                mainCon1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MicoAtmWalletRequest.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void initializeList() {
        ArrayList<CenterGridModel> list = new ArrayList<>();
        list.add(new CenterGridModel("available", R.drawable.ic_iphone, "Mobile"));
        list.add(new CenterGridModel("available", R.drawable.dth_icon_new, "DTH"));
        list.add(new CenterGridModel("available", R.drawable.ic_electricity_icon_new, "Electricity"));
        list.add(new CenterGridModel("available", R.drawable.dmt_icon_new, "DMT"));
        list.add(new CenterGridModel("available", R.drawable.ic_finger, "AEPS"));
        list.add(new CenterGridModel("available", R.drawable.ic_matm, "mATM"));
        //list.add(new CenterGridModel("available", R.drawable.pancard_icon_new, "Utipan"));
        list.add(new CenterGridModel("available", R.drawable.ic_fastag_icon_new, "FASTag"));
        list.add(new CenterGridModel("available", R.drawable.ic_gas_cylinder_icon_new, "LPG GAS"));
        list.add(new CenterGridModel("available", R.drawable.ic_landline_icon_new, "Landline")); // => Holidays
        list.add(new CenterGridModel("available", R.drawable.broad_bank_icon_new, "Broadband")); // => Cash back
        list.add(new CenterGridModel("available", R.drawable.ic_water_icon_new, "Water"));
        list.add(new CenterGridModel("available", R.drawable.menu_ic_80, "More"));

        HomeCenterGridAdapter adapter = new HomeCenterGridAdapter(getActivity(), list, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 4);
        centerOptionsGrid.setLayoutManager(mLayoutManager);
        centerOptionsGrid.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(1), true));
        centerOptionsGrid.setItemAnimator(new DefaultItemAnimator());
        centerOptionsGrid.setAdapter(adapter);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    @Override
    public void onItemClick(int position) {
        Intent i;
        switch (position) {

            case 0:
                startActivity(new Intent(getActivity(), MobileRecharge.class));
                break;
            case 1:
                startActivity(new Intent(getActivity(), DTHRecharge.class));
                break;
            case 2:
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "Electric");
                i.putExtra("type", "electricity");
                i.putExtra("hint", "Bill Number");
                i.putExtra("descInput", "Please enter valid Bill Number");
                startActivity(i);
                break;
            case 3:
                //startActivity(new Intent(getActivity(), DMT.class));
                startActivity(new Intent(getActivity(), SenderActivity.class));
                break;
            case 4:
                user_id = SharedPrefs.getValue(getActivity(), SharedPrefs.USER_ID);
                REQUEST_TYPE = 1;
                volleyNetworkCall(Constents.URL.baseUrl + "api/android/aeps/initiate?apptoken=" +
                        SharedPrefs.getValue(getActivity(), SharedPrefs.APP_TOKEN) + "&user_id=" + user_id);
                break;
            case 5:
                user_id = SharedPrefs.getValue(getActivity(), SharedPrefs.USER_ID);
                REQUEST_TYPE = 2;
                volleyNetworkCall(Constents.URL.baseUrl + "api/android/secure/microatm/initiate?apptoken=" +
                        SharedPrefs.getValue(getActivity(), SharedPrefs.APP_TOKEN) + "&user_id=" + user_id);
                break;
            case 6:
                //startActivity(new Intent(getActivity(), PanCard.class));
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "FASTag");
                i.putExtra("type", "fasttag");
                i.putExtra("hint", "Vehicle Number");
                i.putExtra("descInput", "Please enter valid Vehicle Number");
                startActivity(i);
                break;
            case 7:
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "LPG GAS");
                i.putExtra("type", "lpggas");
                i.putExtra("hint", "Customer ID");
                i.putExtra("descInput", "Please enter your 10 digit numeric Customer ID");
                startActivity(i);
                break;
            case 8:
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "Landline");
                i.putExtra("type", "landline");
                i.putExtra("hint", "Telephone Number");
                i.putExtra("descInput", "Please enter your Telephone Number");
                startActivity(i);
                break;
            case 9:
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "Broadband");
                i.putExtra("type", "broadband");
                i.putExtra("hint", "Account Number");
                i.putExtra("descInput", "Please enter valid Account Number");
                startActivity(i);
                break;
            case 10:
                i = new Intent(getActivity(), OperatorListNew.class);
                i.putExtra("title", "Water");
                i.putExtra("type", "water");
                i.putExtra("hint", "Customer ID");
                i.putExtra("descInput", "Please enter valid Customer ID");
                startActivity(i);
                break;
            case 11:
                startActivity(new Intent(getActivity(), OurServices.class));
                break;
            default:
                Toast.makeText(getActivity(), "Available Soon", Toast.LENGTH_SHORT).show();
        }
    }

    private void volleyNetworkCall(String url) {
        if (AppManager.isOnline(getActivity())) {
            showLoader(getString(R.string.loading_text));
            new VolleyGetNetworkCall(this, getActivity(), url).netWorkCall();
        } else {
            Toast.makeText(getActivity(), "Network connection error", Toast.LENGTH_LONG).show();
        }
    }


    private void callAeps(final String saltKey, final String secretKey, final String BcId, final String userId, final String emailId, final String phone, final String cpid) {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        intent.putExtra("saltKey", saltKey);
        intent.putExtra("secretKey", secretKey);
        intent.putExtra("BcId", BcId);
        intent.putExtra("UserId", userId);
        intent.putExtra("bcEmailId", emailId);
        intent.putExtra("Phone1", phone);
        intent.putExtra("cpid", cpid);//(If any)
        startActivityForResult(intent, 1);
    }

    private AlertDialog loaderDialog;

    private void showLoader(String loaderMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    public static boolean isPackageInstalled(String packagename, PackageManager
            packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void mCallMicroATMIntent(final String saltKey, final String secretKey, final String BcId,
                                     final String userId, final String emailId, final String phone, final String cpid, String rId) {

        PackageManager packageManager = getActivity().getPackageManager();
        if (isPackageInstalled("org.egram.microatm", packageManager)) {
            Intent intent = new Intent();
            intent.setComponent(new
                    ComponentName("org.egram.microatm", "org.egram.microatm.BluetoothMacSearchActivity"));
            intent.putExtra("saltkey", saltKey);
            intent.putExtra("secretkey", secretKey);
            intent.putExtra("bcid", BcId);
            intent.putExtra("userid", userId);
            intent.putExtra("bcemailid", emailId);
            intent.putExtra("phone1", phone);
            intent.putExtra("clientrefid", rId);
            intent.putExtra("vendorid", "");
            intent.putExtra("udf1", "");
            intent.putExtra("udf2", "");
            intent.putExtra("udf3", "");
            intent.putExtra("udf4", "");
            startActivityForResult(intent, 123);
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle("Get Service");
            alertDialog.setMessage("MicroATM Service not installed. Click OK to download .");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String appPackageName = "org.egram.microatm";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();


        }
    }

    @Override
    public void onSuccessRequest(String result) {
        closeLoader();
        if (!result.equals("")) {
            try {
                String status = "";
                String saltKey = "";
                String secretKey = "";
                String BcId = "";
                String UserId = "";
                String bcEmailId = "";
                String Phone1 = "";

                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("txn")) {
                    if (REQUEST_TYPE == 3) {
                        JSONObject userObject = new JSONObject(jsonObject.getString("userdata"));
                        if (userObject.has("mainwallet"))
                            SharedPrefs.setValue(getActivity(), SharedPrefs.MAIN_WALLET, userObject.getString("mainwallet"));
                        else
                            SharedPrefs.setValue(getActivity(), SharedPrefs.MAIN_WALLET, userObject.getString("balance"));

                        if (userObject.has("nsdlwallet")) {
                            SharedPrefs.setValue(getActivity(), SharedPrefs.NSDL_WALLET, userObject.getString("nsdlwallet"));
                        } else {
                            SharedPrefs.setValue(getActivity(), SharedPrefs.NSDL_WALLET, userObject.getString("nsdlbalance"));
                        }
                        setInitial();
                    } else {
                        JSONObject data = jsonObject.getJSONObject("data");
                        saltKey = data.getString("saltKey");
                        secretKey = data.getString("secretKey");
                        BcId = data.getString("BcId");
                        UserId = data.getString("UserId");
                        bcEmailId = data.getString("bcEmailId");
                        Phone1 = data.getString("Phone1");


                        if (REQUEST_TYPE == 1) {
                            callAeps(saltKey, secretKey, BcId, UserId, bcEmailId, Phone1, user_id);
                        } else {
                            String clientrefid = data.getString("clientrefid");
                            mCallMicroATMIntent(saltKey, secretKey, BcId, UserId, bcEmailId, Phone1, user_id, clientrefid);
                        }
                    }
                } else {
                    if (jsonObject.has("message")) {
                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Something went wrong unable to initiate Payment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailRequest(String msg) {
        closeLoader();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}