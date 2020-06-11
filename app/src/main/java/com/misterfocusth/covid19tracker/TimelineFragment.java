package com.misterfocusth.covid19tracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.misterfocusth.covid19tracker.adapter.MyRecyclerViewAdapter;
import com.misterfocusth.covid19tracker.model.HistoryDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimelineFragment extends Fragment {

    private List<HistoryDataModel> dataList;
    private RecyclerView.Adapter adapter;

    private String URL = "https://covid19.th-stat.com/api/open/timeline";

    private String TAG = "ExploreFragment : ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        dataList = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(dataList, getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        getData(getContext());

        return rootView;
    }

    private void getData(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.dialog_loading_title));
        progressDialog.setMessage(getResources().getString(R.string.dialog_loading_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
            try {
                Log.i(TAG, "onResponse: " + response.toString());
                JSONArray jsonArray = response.getJSONArray("Data");
                Log.i(TAG, "onResponse: " + jsonArray);
                for (int i = 0 ; i < jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HistoryDataModel dataModel = new HistoryDataModel();
                    dataModel.setDay(String.valueOf(i + 1));
                    dataModel.setDate(jsonObject.getString("Date"));
                    dataModel.setNewConfirmed(jsonObject.getString("NewConfirmed"));
                    dataModel.setNewRecovered(jsonObject.getString("NewRecovered"));
                    dataModel.setNewDeaths(jsonObject.getString("NewDeaths"));
                    dataModel.setConfirmed(jsonObject.getString("Confirmed"));
                    dataModel.setRecovered(jsonObject.getString("Recovered"));
                    dataModel.setDeaths(jsonObject.getString("Deaths"));
                    Log.i(TAG, "onResponse: " + dataModel.date);
                    dataList.add(dataModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }, error -> Log.i(TAG, "onResponse: Error !"));
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(context));
        requestQueue.add(jsonObjectRequest);
    }
}