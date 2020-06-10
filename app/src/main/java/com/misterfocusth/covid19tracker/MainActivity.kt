package com.misterfocusth.covid19tracker

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar

// Made By Focus Sila Pakdeewong (MisterFocusTH)
// Thanks API & Data From : Open Government Data of Thailand and Ministry of Public Health (Thailand)

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: ChipNavigationBar

    // Version Info
    private lateinit var versionName: String
    private lateinit var versionCode: String

    // Firebase Realtime Database
    private var database: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    companion object {
        private val DATASNAPSHOT_CHILD = "versionInfo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI Component - Navigation Bar
        bottomNavigation = findViewById(R.id.bottomNavigationBar)
        bottomNavigation.setItemSelected(R.id.bottomNav_home)
        bottomNavigation.setOnItemSelectedListener { id ->
            var selectedFragment: Fragment? = null
            if (id == R.id.bottomNav_home) {
                selectedFragment = HomeFragment()
            } else if (id == R.id.bottomNav_warning) {
                selectedFragment = WarningAndPreventFragment()
            } else if (id == R.id.bottomNav_explore) {
                selectedFragment = TimelineFragment()
            } else if (id == R.id.bottomNav_info) {
                selectedFragment = InfoFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }
        }

        // Load Default Fragment On Launch Application
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        }

        // Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        myRef = database!!.reference
        checkInternetConnection(this@MainActivity)

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
                    ) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        val chromeTabsBuilder = CustomTabsIntent.Builder()
                        val customTabsIntent = chromeTabsBuilder.build()
                        customTabsIntent.launchUrl(this@MainActivity, Uri.parse(newVersionInfo[2]))
                    }
                    .setNegativeButton(resources.getString(R.string.dialog_new_version_update_button_close)
                    ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            builder.show()
        }
    }

    private fun checkInternetConnection(context: Context) {
        if (!isInternetAvailable) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.dialog_no_internet_title))
                    .setMessage(resources.getString(R.string.dialog_no_internet_message))
                    .setPositiveButton(resources.getString(R.string.dialog_no_internet_button)) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
            builder.show()
        }
    }

    private val isInternetAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null) {
                return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    true
                } else activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
            }
            return false
        }
}
