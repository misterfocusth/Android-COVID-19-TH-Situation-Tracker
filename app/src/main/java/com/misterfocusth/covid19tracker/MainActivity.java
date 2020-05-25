package com.misterfocusth.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

// Made By Focus Sila Pakdeewong (MisterFocusTH)
// Thanks API & Data From : Open Government Data of Thailand and Ministry of Public Health (Thailand)

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textUpdatedDate , textNewCases , textNewRecovered , textNewDeath , textTodayUpdate; // TextViews UI Components
    private Button btnReportIssues , btnOpenBrowser; // Button UI Components

    private static final String API_URL = "https://covid19.th-stat.com/api/open/today";
    private static final String OFFICIAL_URL = "https://covid19.ddc.moph.go.th/";

    public static String[] replacedData = new String[5]; // Received Data + TextViews Text
    public static String[] textViewData = new String[5]; // Original TextViews Text
    public static String[] receivedData = new String[5]; // Received Data From API

    private String TAG = "MainActivity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Components - TextViews
        textUpdatedDate = findViewById(R.id.textView_updatedDate);
        textNewCases = findViewById(R.id.textView_newcases);
        textNewRecovered = findViewById(R.id.textView_recovered);
        textNewDeath = findViewById(R.id.textView_death);
        textTodayUpdate = findViewById(R.id.textView_todayUpdate);

        // UI Components - Buttons
        btnReportIssues = findViewById(R.id.btn_reportIssue);
        btnReportIssues.setOnClickListener(MainActivity.this);

        btnOpenBrowser = findViewById(R.id.btn_viewOnBrowser);
        btnOpenBrowser.setOnClickListener(MainActivity.this);

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
                            receivedData[0] = jsonObject.getString("UpdateDate");
                            receivedData[1] = jsonObject.getString("NewConfirmed");
                            receivedData[2] = jsonObject.getString("NewRecovered");
                            receivedData[3] = jsonObject.getString("NewDeaths");
                            receivedData[4] = jsonObject.getString("NewHospitalized");

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

    private void replaceData() {

        textViewData[0] = getResources().getString(R.string.box_updatedDate);
        textViewData[1] = getResources().getString(R.string.box_newcases);
        textViewData[2] = getResources().getString(R.string.box_recovered);
        textViewData[3] = getResources().getString(R.string.box_death);
        textViewData[4] = getResources().getString(R.string.box_today_update);

        replacedData[0] = textViewData[0].replaceAll("replace", receivedData[0]); // Updated Date
        replacedData[1] = textViewData[1].replaceAll("_", receivedData[1]); // Update NewCases
        replacedData[2] = textViewData[2].replaceAll("_", receivedData[2]); // Update Recovered
        replacedData[3] = textViewData[3].replaceAll("_", receivedData[3]); // Update NewDeath

        String replacedString = "";

        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                replacedString = textViewData[4].replaceAll("newCase", receivedData[1]);
                i++;
            }
            if (i == 1) {
                replacedString = replacedString.replaceAll("newRecovered", receivedData[2]);
                i++;
            }
            if (i == 2) {
                replacedString = replacedString.replaceAll("newHospitalized", receivedData[4]); //New Hospitalized
                i++;
            }
            if (i == 3) {
                replacedString = replacedString.replaceAll("newDeath", receivedData[3]);
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
    }
}
