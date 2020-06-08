package com.misterfocusth.covid19tracker

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var textUpdatedDate: TextView
    private lateinit var textNewCases: TextView
    private lateinit var textNewRecovered: TextView
    private lateinit var textNewDeath: TextView
    private lateinit var textTodayUpdate: TextView
    private lateinit var textTotalCases: TextView
    private lateinit var textTotalRecovered : TextView

    private lateinit var btnReportIssues: Button
    private lateinit var btnOpenBrowser : Button

    lateinit var progressDialog : ProgressDialog

    private var TAG = "MainActivity : "

    companion object {
        private const val API_URL = "https://covid19.th-stat.com/api/open/today"
        private const val OFFICIAL_URL = "https://covid19.ddc.moph.go.th/"
        var replacedData = arrayOfNulls<String>(7) // Received Data + TextViews Text
        var textViewData = arrayOfNulls<String>(7) // Original TextViews Text
        var receivedData = arrayOfNulls<String>(7) // Received Data From API
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)

        // UI Components - TextViews
        textUpdatedDate = rootView.findViewById(R.id.textView_updatedDate)
        textNewCases = rootView.findViewById(R.id.textView_newcases)
        textNewRecovered = rootView.findViewById(R.id.textView_recovered)
        textNewDeath = rootView.findViewById(R.id.textView_death)
        textTodayUpdate = rootView.findViewById(R.id.textView_todayUpdate)
        textTotalCases = rootView.findViewById(R.id.textViewTotalCases)
        textTotalRecovered = rootView.findViewById(R.id.textViewTotalRecovered)

        // UI Components - Buttons
//        btnReportIssues = rootView.findViewById(R.id.btn_reportIssue)
//        btnReportIssues.setOnClickListener(this)
//        btnOpenBrowser = rootView.findViewById(R.id.btn_viewOnBrowser)
//        btnOpenBrowser.setOnClickListener(this)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle(resources.getString(R.string.dialog_loading_title))
        progressDialog.setMessage(resources.getString(R.string.dialog_loading_message))
        progressDialog.setCancelable(false)
        progressDialog.show()

        //Request Data From API_URL
        val requestQueue = Volley.newRequestQueue(context)
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
                        Log.i(TAG, "onCreateView: " + receivedData.toString())
                        replaceData() // Replace Received Data To TextViews
                        updateTextView() // Update Text On TextViews
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    if (container != null) {
                        Toast.makeText(container.context, "เกิดข้อผิดพลาดขณะเรียกข้อมูล...", Toast.LENGTH_LONG).show()
                    }
                })
        requestQueue.add(jsonObjectRequest)

        return rootView
    }

    override fun onClick(v: View?) {
//        if (v != null) {
//            if (v.id == R.id.btn_reportIssue) {
//                val intent = Intent(Intent.ACTION_SENDTO)
//                intent.type = "text/plain"
//                intent.putExtra(Intent.EXTRA_EMAIL, "Silapakdeewong2546.3@gmail.com")
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Report_Issues - COVID-19 TH Situation Tracker (รายงานข้อผิดพลาด)")
//                intent.putExtra(Intent.EXTRA_TEXT, "Explain Your Problem Here ! - อธิบายปัญหาของคุณมาเลย")
//                startActivity(Intent.createChooser(intent, "กรุณาเลือกเเอพพลิเคชั่นเพื่อดำเนินการต่อ"))
//            } else if (v.id == R.id.btn_viewOnBrowser) {// Open Browser Link WIth ChromeCustomTabs
//                    val builder = CustomTabsIntent.Builder()
//                    val customTabsIntent = builder.build()
//                    customTabsIntent.launchUrl(v.context, Uri.parse(OFFICIAL_URL))
//                }
//            }
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
        replacedData[5] = textViewData[5]!!.replace("TC".toRegex(), receivedData[5]!!) // Update Total Cases
        replacedData[5] = replacedData[5]!!.replace("_".toRegex(), receivedData[1]!!) // Update New Case In Total Cases Box
        replacedData[6] = textViewData[6]!!.replace("TR".toRegex(), receivedData[6]!!) // Update Total Recovered
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
        textUpdatedDate.text = replacedData[0] // Update : Updated Date TextViews
        textNewCases.text = replacedData[1] // Update : New Cases TextViews
        textNewRecovered.text = replacedData[2] // Update : New Recovered TextViews
        textNewDeath.text = replacedData[3] // Update : New Death TextViews
        textTodayUpdate.text = replacedData[4] // Update : Today TextViews
        textTotalCases.text = replacedData[5] // Update : Total Cases TextViews
        textTotalRecovered.text = replacedData[6] // Update : Total Recovered TextViews
        progressDialog.dismiss()
    }

}