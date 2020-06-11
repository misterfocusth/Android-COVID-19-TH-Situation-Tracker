package com.misterfocusth.covid19tracker

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment

class WarningAndPreventFragment : Fragment() {

    companion object {
        private lateinit var btnCheck: Button
        private const val CHECK_WEBSITE = "https://covid19.ddc.moph.go.th/th/self_screening"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View = inflater.inflate(R.layout.fragment_warning, container, false)

        btnCheck = rootView.findViewById(R.id.btnCheck)
        btnCheck.setOnClickListener { v ->
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(v.context, Uri.parse(CHECK_WEBSITE))
        }

        return rootView
    }

}