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

    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private List<HistoryDataModel> dataList;
    private RecyclerView.Adapter adapter;

    private String URL = "https://covid19.th-stat.com/api/open/timeline";

    private String TAG = "ExploreFragment : ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);

        dataList = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(dataList, getContext());

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());

//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(dividerItemDecoration);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onResponse: Error !");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(context));
        requestQueue.add(jsonObjectRequest);


//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener() {
//            @Override
//            public void onResponse(Object response) {
//                JSONArray jsonArray = new JSONArray();
//                Log.i(TAG, "onResponse: " + );
//            }

//            @Override
//            public void onResponse(JSONArray response) {
//                Log.i(TAG, "onResponse: " + response.getString(0));
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);

//                        HistoryDataModel dataModel = new HistoryDataModel();
//                        dataModel.setTitle(jsonObject.getString("title"));
//                        dataModel.setRating(jsonObject.getInt("rating"));
//                        dataModel.setYear(jsonObject.getInt("releaseYear"));
//
//                        movieList.add(movie);


                //        val URL = "https://covid19.th-stat.com/api/open/timeline"
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
//        URL,
//        null,
//        com.android.volley.Response.Listener { response ->
//        val list = ArrayList<HistoryDataModel>()
//        try {
//        val jsonArray: JSONArray = response.getJSONArray("Data")
//        for (i in 0 until jsonArray.length()) {
//        val jsonObject = jsonArray.getJSONObject(i)
//
//        val day = (i + 1).toString()
//        val date = jsonObject.getString("Date")
//        val newConfirmed = jsonObject.getString("NewConfirmed")
//        val newRecovered = jsonObject.getString("NewRecovered")
//        val newDeaths = jsonObject.getString("NewDeaths")
//        val confirmed = jsonObject.getString("Confirmed")
//        val recovered = jsonObject.getString("Recovered")
//        val deaths = jsonObject.getString("Deaths")
//    } catch (JSONException e) {
//                        e.printStackTrace();
//                        progressDialog.dismiss();
//                    }
//                }
//                adapter.notifyDataSetChanged();
//                progressDialog.dismiss();
//            }
//        } , new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Volley", error.toString());
//                progressDialog.dismiss();
//            }
//        });

    }
}



//class ExploreFragment : Fragment() {
//
//private var progressDialog: ProgressDialog? = null
//private var adapter: MyRecyclerViewAdapter? = null
//private var recyclerView: RecyclerView? = null
//private val TAG = "ExploreFragment : "
//
//        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView: View = inflater.inflate(R.layout.fragment_home, container, false)
//
//        progressDialog = ProgressDialog(context)
//        progressDialog!!.setMessage(resources.getString(R.string.dialog_loading_title))
//        progressDialog!!.setMessage(resources.getString(R.string.dialog_loading_message))
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.show()
//
//        recyclerView = rootView.findViewById(R.id.recyclerView)
//

//
//        val data = HistoryDataModel(day, date, newConfirmed, newRecovered, newDeaths, confirmed, recovered, deaths)
//        list.add(data)
//
//        Log.i(TAG, "RecyclerView: " + i)
//        }
//        Log.i(TAG, "RecyclerView: " + list)
//        adapter = MyRecyclerViewAdapter(list, context)
//        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
//        recyclerView?.layoutManager = layoutManager
//        recyclerView?.adapter = adapter
//        } catch (e: JSONException) {
//        e.printStackTrace()
//        }
//        },
//        com.android.volley.Response.ErrorListener {
//        Toast.makeText(context, "เกิดข้อผิดพลาดขณะเรียกข้อมูล...", Toast.LENGTH_LONG).show()
//        })
//        val queue = Volley.newRequestQueue(context)
//        queue.add(jsonObjectRequest)
//
//        return rootView
//        }
//
//        }
