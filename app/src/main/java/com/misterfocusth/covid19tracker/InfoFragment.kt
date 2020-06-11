package com.misterfocusth.covid19tracker

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment

class InfoFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_info, container, false)

        // UI Component - Button
        val btnInfo: ImageView = rootView.findViewById(R.id.imageViewInfo)
        btnInfo.setOnClickListener(this)

        return rootView
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.imageViewInfo) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(v.context, Uri.parse("https://github.com/MisterFocusTH/Android-COVID-19-TH-Situation-Tracker"))
            }
        }
    }

}