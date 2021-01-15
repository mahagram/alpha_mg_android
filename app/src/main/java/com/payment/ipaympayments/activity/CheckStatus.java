package com.payment.ipaympayments.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.payment.ipaympayments.R;
import com.payment.ipaympayments.app.AppController;
import com.payment.ipaympayments.app.Constents;
import com.payment.ipaympayments.utill.AppManager;
import com.payment.ipaympayments.utill.Print;
import com.payment.ipaympayments.utill.SharedPrefs;

import org.json.JSONObject;

public class CheckStatus extends AppCompatActivity {
    Activity context;
    String typeValue, txnId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);
        context = CheckStatus.this;
        typeValue = getIntent().getStringExtra("typeValue");
        txnId = getIntent().getStringExtra("id");

        netVolleyWorkCall();
    }

    private void netVolleyWorkCall() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait...");
        dialog.show();
        dialog.setCancelable(false);

        // http://e-banker.in/api/android/transaction/status?apptoken=Jy55o0qKhwtbp53huR0OUVuTXf10JQpVfADPpOgp&user_id=489&id=1279707&type=recharge
        String token = SharedPrefs.getValue(context, SharedPrefs.APP_TOKEN);
        String id = SharedPrefs.getValue(context, SharedPrefs.USER_ID);
        String apiUrl = Constents.URL.baseUrl + "api/android/transaction/status?apptoken=" + token + "&user_id=" + id + "&id=" + txnId + "&type=" + typeValue;
        //Log.e("LOG", "apiUrl : " + apiUrl);
        StringRequest sendRequest = new StringRequest(
                Request.Method.GET,
                apiUrl,
                result -> {
                    if (!CheckStatus.this.isDestroyed() && dialog.isShowing())
                        dialog.dismiss();
                    //Log.e("LOG", "Volley Form Response : " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String status = "";
                        if (jsonObject.has("status")) {
                            status = jsonObject.getString("status");
                        } else {
                            status = jsonObject.getString("statuscode");
                        }

                        if (status.equalsIgnoreCase("UA")) {
                            AppManager.getInstance().logoutApp(context);
                        } else if (status.equalsIgnoreCase("txn")) {
                            //JSONObject userObject = new JSONObject(jsonObject.getString("data"));
                            String txnStatus = "Txn Status : " + jsonObject.getString("txn_status");
                            String refNo = "Ref No : " + jsonObject.getString("refno");
                            popUp(txnStatus, refNo);
                        } else {
                            String message;
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            } else {
                                message = "Something went wrong please contact to our service provider";
                            }
                            String s = "Status : " + status;
                            popUp(s, message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }, volleyError -> {

            if (!CheckStatus.this.isDestroyed() && dialog.isShowing())
                dialog.dismiss();
            String message = "Unexpected response please contact to our service provider";
            popUp("Error", message);
            volleyError.printStackTrace();
            Print.P("Not able to connect with server");
        });
        AppController.getInstance().addToRequestQueue(sendRequest);
    }

    AlertDialog alert;

    private void popUp(String title, String txn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(txn)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
    }
}
