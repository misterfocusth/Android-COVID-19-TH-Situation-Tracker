package com.misterfocusth.covid19tracker

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.misterfocusth.covid19tracker.MainActivity
import org.json.JSONException
import org.json.JSONObject

// Made By Focus Sila Pakdeewong (MisterFocusTH)
// Thanks API & Data From : Open Government Data of Thailand and Ministry of Public Health (Thailand)

class MainActivity : AppCompatActivity(), View.OnClickListener, OnRefreshListener {
    lateinit var textUpdatedDate: TextView
    lateinit var textNewCases: TextView
    lateinit var textNewRecovered: TextView
    lateinit var textNewDeath: TextView
    lateinit var textTodayUpdate: TextView
    lateinit var textTotalCases: TextView
    lateinit var textTotalRecovered : TextView
    lateinit var btnReportIssues: Button
    lateinit var btnOpenBrowser : Button
    lateinit var mSwipeRefreshLayout : SwipeRefreshLayout
    lateinit var view : View
    lateinit var progressDialog : ProgressDialog
    private var TAG = "MainActivity : "
    lateinit var versionName: String
    lateinit var versionCode: String
    lateinit var newVersionName: String
    lateinit var newVersionCode: String
    lateinit var downloadUrl: String

    // Firebase Realtime Database
    var database: FirebaseDatabase? = null
    var myRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI Components - View
        view = findViewById(R.id.constrainLayout)

        // UI Components - TextViews
        textUpdatedDate = findViewById(R.id.textView_updatedDate)
        textNewCases = findViewById(R.id.textView_newcases)
        textNewRecovered = findViewById(R.id.textView_recovered)
        textNewDeath = findViewById(R.id.textView_death)
        textTodayUpdate = findViewById(R.id.textView_todayUpdate)
        textTotalCases = findViewById(R.id.textViewTotalCases)
        textTotalRecovered = findViewById(R.id.textViewTotalRecovered)

        // UI Components - Buttons
        btnReportIssues = findViewById(R.id.btn_reportIssue)
        btnReportIssues.setOnClickListener(this)
        btnOpenBrowser = findViewById(R.id.btn_viewOnBrowser)
        btnOpenBrowser.setOnClickListener(this@MainActivity)

        // UI Components - SwipeRefresLayout
        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh)
        mSwipeRefreshLayout.setOnRefreshListener(this@MainActivity)

        // Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        myRef = database!!.reference
        checkInternetConnection(this@MainActivity)
        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setTitle(resources.getString(R.string.dialog_loading_title))
        progressDialog.setMessage(resources.getString(R.string.dialog_loading_message))
        progressDialog.setCancelable(false)
        progressDialog.show()

        //Request Data From API_URL
        val requestQueue = Volley.newRequestQueue(this@MainActivity)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                API_URL,
                null,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response.toString())
                        receivedData[0] = jsonObject.getString("UpdateDate") // GET : Date
                        receivedData[1] = jsonObject.getString("NewConfirmed") // GET : New Cases
                        receivedData[2] = jsonObject.getString("NewRecovered") // GET : New Recovered
                        receivedData[3] = jsonObject.getString("NewDeaths") // GET : New Death
                        receivedData[4] = jsonObject.getString("Hospitalized") // Get Total Hospitalized
                        receivedData[5] = jsonObject.getString("Confirmed") // GET : Total Confirmed
                        receivedData[6] = jsonObject.getString("Recovered") // GET : Total Recovered
                        replaceData() // Replace Received Data To TextViews
                        updateTextView() // Update Text On TextViews
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { Toast.makeText(this@MainActivity, "เกิดข้อผิดพลาดขณะเรียกข้อมูล...", Toast.LENGTH_LONG) })
        requestQueue.add(jsonObjectRequest)
        Snackbar.make(view, resources.getString(R.string.snackbar_text), Snackbar.LENGTH_LONG)
        try {
            checkNewVersionUpdate(this@MainActivity)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun checkNewVersionUpdate(context: Context) {
        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        versionName = packageInfo.versionName // Current App Version Name
        val intVersionCode = packageInfo.versionCode // Current App Version Code
        versionCode = intVersionCode.toString()
        myRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                getNewApplicationVersionUpdate(dataSnapshot, context)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getNewApplicationVersionUpdate(dataSnapshot: DataSnapshot, context: Context) {
        val DATASNAPSHOT_CHILD = "versionInfo"
        val newVersionInfo = arrayOfNulls<String>(3)
        for (ds in dataSnapshot.children) {
            val versionData = VersionData()
            versionData.versionCode = ds.child(DATASNAPSHOT_CHILD).getValue(VersionData::class.java)!!.versionCode
            versionData.versionName = ds.child(DATASNAPSHOT_CHILD).getValue(VersionData::class.java)!!.versionName
            versionData.downloadUrl = ds.child(DATASNAPSHOT_CHILD).getValue(VersionData::class.java)!!.downloadUrl
            newVersionInfo[0] = versionData.versionCode
            newVersionInfo[1] = versionData.versionName
            newVersionInfo[2] = versionData.downloadUrl
        }
        if (versionName != newVersionInfo[1] || versionCode != newVersionInfo[0]) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.dialog_new_version_update_title))
                    .setMessage(resources.getString(R.string.dialog_new_version_update_message))
                    .setPositiveButton(resources.getString(R.string.dialog_new_version_update_button_update)
                    ) { dialog: DialogInterface, which: Int ->
                        dialog.dismiss()
                        val chromeTabsBuilder = CustomTabsIntent.Builder()
                        val customTabsIntent = chromeTabsBuilder.build()
                        customTabsIntent.launchUrl(this@MainActivity, Uri.parse(newVersionInfo[2]))
                    }
                    .setNegativeButton(resources.getString(R.string.dialog_new_version_update_button_close)
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            builder.show()
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_reportIssue) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, "Silapakdeewong2546.3@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report_Issues - COVID-19 TH Situation Tracker (รายงานข้อผิดพลาด)")
            intent.putExtra(Intent.EXTRA_TEXT, "Explain Your Problem Here ! - อธิบายปัญหาของคุณมาเลย")
            startActivity(Intent.createChooser(intent, "กรุณาเลือกเเอพพลิเคชั่นเพื่อดำเนินการต่อ"))
        } else if (v.id == R.id.btn_viewOnBrowser) { // Open Browser Link WIth ChromeCustomTabs
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this@MainActivity, Uri.parse(OFFICIAL_URL))
        }
    }

    override fun onRefresh() {
        recreate()
    }

    private fun replaceData() {
        textViewData[0] = resources.getString(R.string.box_updatedDate)
        textViewData[1] = resources.getString(R.string.box_newcases)
        textViewData[2] = resources.getString(R.string.box_recovered)
        textViewData[3] = resources.getString(R.string.box_death)
        textViewData[4] = resources.getString(R.string.box_today_update)
        textViewData[5] = resources.getString(R.string.box_total_cases)
        textViewData[6] = resources.getString(R.string.box_total_recovered)
        replacedData[0] = textViewData[0]!!.replace("date".toRegex(), receivedData[0]!!) // Updated Date
        replacedData[1] = textViewData[1]!!.replace("_".toRegex(), receivedData[1]!!) // Update NewCases
        replacedData[2] = textViewData[2]!!.replace("_".toRegex(), receivedData[2]!!) // Update Recovered
        replacedData[3] = textViewData[3]!!.replace("_".toRegex(), receivedData[3]!!) // Update NewDeath
        // Replaced Data 4 : Tooday Update Box Update Below Loop !
        replacedData[5] = textViewData[5]!!.replace("totalCases".toRegex(), receivedData[5]!!) // Update Total Cases
        replacedData[5] = replacedData[5]!!.replace("_".toRegex(), receivedData[1]!!) // Update New Case In Total Cases Box
        replacedData[6] = textViewData[6]!!.replace("totalRecovered".toRegex(), receivedData[6]!!) // Update Total Recovered
        replacedData[6] = replacedData[6]!!.replace("_".toRegex(), receivedData[2]!!) // Update New Recovered In Total Recovered Box
        var replacedString = ""
        var i = 0
        while (i < 4) {
            if (i == 0) {
                replacedString = textViewData[4]!!.replace("newCase".toRegex(), receivedData[1]!!)
                i++
            }
            if (i == 1) {
                replacedString = replacedString.replace("newRecovered".toRegex(), receivedData[2]!!)
                i++
            }
            if (i == 2) {
                replacedString = replacedString.replace("Hospitalized".toRegex(), receivedData[4]!!) //New Hospitalized
                i++
            }
            if (i == 3) {
                replacedString = replacedString.replace("newDeath".toRegex(), receivedData[3]!!)
                i++
            }
            if (i == 4) {
                replacedString = replacedString.replace("date".toRegex(), receivedData[0]!!)
                i++
            }
            i++
        }
        replacedData[4] = replacedString // Text : Today Update Box
    }

    private fun updateTextView() {
        textUpdatedDate!!.text = replacedData[0] // Update : Updated Date TextViews
        textNewCases!!.text = replacedData[1] // Update : New Cases TextViews
        textNewRecovered!!.text = replacedData[2] // Update : New Recovered TextViews
        textNewDeath!!.text = replacedData[3] // Update : New Death TextViews
        textTodayUpdate!!.text = replacedData[4] // Update : Today TextViews
        textTotalCases!!.text = replacedData[5] // Update : Total Cases TextViews
        textTotalRecovered!!.text = replacedData[6] // Update : Total Recovered TextViews
        progressDialog!!.dismiss()
    }

    private fun checkInternetConnection(context: Context) {
        if (!isInternetAvailable) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.dialog_no_internet_title))
                    .setMessage(resources.getString(R.string.dialog_no_internet_message))
                    .setPositiveButton(resources.getString(R.string.dialog_no_internet_button)) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }
            builder.show()
        }
    }

    private val isInternetAvailable: Boolean
        private get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null) {
                    return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                        true
                    } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                        true
                    } else {
                        false
                    }
                }
            } else {
                return false
            }
            return false
        }

    companion object {
        private const val API_URL = "https://covid19.th-stat.com/api/open/today"
        private const val OFFICIAL_URL = "https://covid19.ddc.moph.go.th/"
        var replacedData = arrayOfNulls<String>(7) // Received Data + TextViews Text
        var textViewData = arrayOfNulls<String>(7) // Original TextViews Text
        var receivedData = arrayOfNulls<String>(7) // Received Data From API
    }
}