package com.misterfocusth.covid19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

// Made By Focus Sila Pakdeewong (MisterFocusTH)
// Thanks API & Data From : Open Government Data of Thailand and Ministry of Public Health (Thailand)

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView textUpdatedDate , textNewCases , textNewRecovered , textNewDeath , textTodayUpdate, textTotalCases, textTotalRecovered; // TextViews UI Components
    private Button btnReportIssues , btnOpenBrowser; // Button UI Components
    private SwipeRefreshLayout mSwipeRefreshLayout; // SwipeRefreshLayout UI Component
    private View view; // View UI Component
    private ProgressDialog progressDialog; // ProgressDialog

    private static final String API_URL = "https://covid19.th-stat.com/api/open/today";
    private static final String OFFICIAL_URL = "https://covid19.ddc.moph.go.th/";

    public static String[] replacedData = new String[7]; // Received Data + TextViews Text
    public static String[] textViewData = new String[7]; // Original TextViews Text
    public static String[] receivedData = new String[7]; // Received Data From API

    private String TAG = "MainActivity : ";

    private String versionName, versionCode, newVersionName, newVersionCode, downloadUrl;

    // Firebase Realtime Database
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Components - View
        view = findViewById(R.id.constrainLayout);

        // UI Components - TextViews
        textUpdatedDate = findViewById(R.id.textView_updatedDate);
        textNewCases = findViewById(R.id.textView_newcases);
        textNewRecovered = findViewById(R.id.textView_recovered);
        textNewDeath = findViewById(R.id.textView_death);
        textTodayUpdate = findViewById(R.id.textView_todayUpdate);
        textTotalCases = findViewById(R.id.textViewTotalCases);
        textTotalRecovered = findViewById(R.id.textViewTotalRecovered);

        // UI Components - Buttons
        btnReportIssues = findViewById(R.id.btn_reportIssue);
        btnReportIssues.setOnClickListener(MainActivity.this);

        btnOpenBrowser = findViewById(R.id.btn_viewOnBrowser);
        btnOpenBrowser.setOnClickListener(MainActivity.this);

        // UI Components - SwipeRefresLayout
        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(MainActivity.this);

        // Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        checkInternetConnection(MainActivity.this);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(getResources().getString(R.string.dialog_loading_title));
        progressDialog.setMessage(getResources().getString(R.string.dialog_loading_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Request Data From API_URL
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            receivedData[0] = jsonObject.getString("UpdateDate"); // GET : Date
                            receivedData[1] = jsonObject.getString("NewConfirmed"); // GET : New Cases
                            receivedData[2] = jsonObject.getString("NewRecovered"); // GET : New Recovered
                            receivedData[3] = jsonObject.getString("NewDeaths"); // GET : New Death
                            receivedData[4] = jsonObject.getString("Hospitalized"); // Get Total Hospitalized
                            receivedData[5] = jsonObject.getString("Confirmed"); // GET : Total Confirmed
                            receivedData[6] = jsonObject.getString("Recovered"); // GET : Total Recovered

                            replaceData(); // Replace Received Data To TextViews
                            updateTextView(); // Update Text On TextViews
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "เกิดข้อผิดพลาดขณะเรียกข้อมูล...", Toast.LENGTH_LONG);
                    }
                });
        requestQueue.add(jsonObjectRequest);

        Snackbar.make(view, getResources().getString(R.string.snackbar_text), Snackbar.LENGTH_LONG);

        try {
            checkNewVersionUpdate(MainActivity.this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkNewVersionUpdate(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
        versionName = packageInfo.versionName; // Current App Version Name
        int intVersionCode = packageInfo.versionCode; // Current App Version Code
        versionCode = String.valueOf(intVersionCode);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getNewApplicationVersionUpdate(dataSnapshot, context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNewApplicationVersionUpdate(DataSnapshot dataSnapshot, Context context) {
        final String DATASNAPSHOT_CHILD = "versionInfo";
        String[] newVersionInfo = new String[3];
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            VersionData versionData = new VersionData();
            versionData.setVersionCode(ds.child(DATASNAPSHOT_CHILD).getValue(VersionData.class).getVersionCode());
            versionData.setVersionName(ds.child(DATASNAPSHOT_CHILD).getValue(VersionData.class).getVersionName());
            versionData.setDownloadUrl(ds.child(DATASNAPSHOT_CHILD).getValue(VersionData.class).getDownloadUrl());

            newVersionInfo[0] = versionData.getVersionCode();
            newVersionInfo[1] = versionData.getVersionName();
            newVersionInfo[2] = versionData.getDownloadUrl();
        }
        if (!versionName.equals(newVersionInfo[1]) || !versionCode.equals(newVersionInfo[0])) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getResources().getString(R.string.dialog_new_version_update_title))
                    .setMessage(getResources().getString(R.string.dialog_new_version_update_message))
                    .setPositiveButton(getResources().getString(R.string.dialog_new_version_update_button_update),
                            (dialog, which) -> {
                                dialog.dismiss();
                                CustomTabsIntent.Builder chromeTabsBuilder = new CustomTabsIntent.Builder();
                                CustomTabsIntent customTabsIntent = chromeTabsBuilder.build();
                                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(newVersionInfo[2]));
                            })
                    .setNegativeButton(getResources().getString(R.string.dialog_new_version_update_button_close),
                            (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reportIssue) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, "Silapakdeewong2546.3@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report_Issues - COVID-19 TH Situation Tracker (รายงานข้อผิดพลาด)");
            intent.putExtra(Intent.EXTRA_TEXT, "Explain Your Problem Here ! - อธิบายปัญหาของคุณมาเลย");
            startActivity(Intent.createChooser(intent, "กรุณาเลือกเเอพพลิเคชั่นเพื่อดำเนินการต่อ"));
        } else if (v.getId() == R.id.btn_viewOnBrowser) { // Open Browser Link WIth ChromeCustomTabs
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(OFFICIAL_URL));
        }
    }

    @Override
    public void onRefresh() {
        recreate();
    }

    private void replaceData() {

        textViewData[0] = getResources().getString(R.string.box_updatedDate);
        textViewData[1] = getResources().getString(R.string.box_newcases);
        textViewData[2] = getResources().getString(R.string.box_recovered);
        textViewData[3] = getResources().getString(R.string.box_death);
        textViewData[4] = getResources().getString(R.string.box_today_update);
        textViewData[5] = getResources().getString(R.string.box_total_cases);
        textViewData[6] = getResources().getString(R.string.box_total_recovered);

        replacedData[0] = textViewData[0].replaceAll("date", receivedData[0]); // Updated Date
        replacedData[1] = textViewData[1].replaceAll("_", receivedData[1]); // Update NewCases
        replacedData[2] = textViewData[2].replaceAll("_", receivedData[2]); // Update Recovered
        replacedData[3] = textViewData[3].replaceAll("_", receivedData[3]); // Update NewDeath
        // Replaced Data 4 : Tooday Update Box Update Below Loop !
        replacedData[5] = textViewData[5].replaceAll("totalCases", receivedData[5]); // Update Total Cases
        replacedData[5] = replacedData[5].replaceAll("_", receivedData[1]); // Update New Case In Total Cases Box
        replacedData[6] = textViewData[6].replaceAll("totalRecovered", receivedData[6]); // Update Total Recovered
        replacedData[6] = replacedData[6].replaceAll("_", receivedData[2]); // Update New Recovered In Total Recovered Box

        String replacedString = "";

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                replacedString = textViewData[4].replaceAll("newCase", receivedData[1]);
                i++;
            }
            if (i == 1) {
                replacedString = replacedString.replaceAll("newRecovered", receivedData[2]);
                i++;
            }
            if (i == 2) {
                replacedString = replacedString.replaceAll("Hospitalized", receivedData[4]); //New Hospitalized
                i++;
            }
            if (i == 3) {
                replacedString = replacedString.replaceAll("newDeath", receivedData[3]);
                i++;
            }
            if (i == 4) {
                replacedString = replacedString.replaceAll("date", receivedData[0]);
                i++;
            }
        }

        replacedData[4] = replacedString; // Text : Today Update Box
    }

    private void updateTextView() {
        textUpdatedDate.setText(replacedData[0]); // Update : Updated Date TextViews
        textNewCases.setText(replacedData[1]); // Update : New Cases TextViews
        textNewRecovered.setText(replacedData[2]); // Update : New Recovered TextViews
        textNewDeath.setText(replacedData[3]); // Update : New Death TextViews
        textTodayUpdate.setText(replacedData[4]); // Update : Today TextViews
        textTotalCases.setText(replacedData[5]); // Update : Total Cases TextViews
        textTotalRecovered.setText(replacedData[6]); // Update : Total Recovered TextViews

        progressDialog.dismiss();
    }

    private void checkInternetConnection(Context context) {
        if (!isInternetAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(getResources().getString(R.string.dialog_no_internet_title))
                    .setMessage(getResources().getString(R.string.dialog_no_internet_message))
                    .setPositiveButton(getResources().getString(R.string.dialog_no_internet_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.show();
        }
    }

    private Boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return  false;
        }
        return false;
    }
}
