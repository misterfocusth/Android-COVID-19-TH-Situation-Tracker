package com.misterfocusth.covid19tracker.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.misterfocusth.covid19tracker.R;
import com.misterfocusth.covid19tracker.model.HistoryDataModel;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder> {

    private List<HistoryDataModel> dataList;
    private Context context;

    public MyRecyclerViewAdapter(List<HistoryDataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView textDay, textDate, textNewConfirmed, textNewRecovered, textNewDeaths,
                textConfirmed, textRecovered, textDeaths;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            textDay = mView.findViewById(R.id.textviewHistoryDay);
            textDate = mView.findViewById(R.id.textViewHistoryDate);
            textNewConfirmed = mView.findViewById(R.id.textViewHistoryNewConfirmed);
            textNewRecovered = mView.findViewById(R.id.textViewHistoryNewRecovered);
            textNewDeaths = mView.findViewById(R.id.textViewHistoryNewDeaths);
            textConfirmed = mView.findViewById(R.id.textViewHistoryConfirmed);
            textRecovered = mView.findViewById(R.id.textViewHistoryRecovered);
            textDeaths = mView.findViewById(R.id.textViewHistoryDeaths);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.history_data_list, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Resources res = holder.itemView.getContext().getResources();
        HistoryDataModel dataModel = dataList.get(position);
        String  textDay, textDate, textNewConfirmed, textNewRecovered, textNewDeaths, textConfirmed, textRecovered, textDeaths;

        textDay =  res.getString(R.string.explore_day) + " " + dataModel.getDay();
        textDate = res.getString(R.string.explore_date) + " " + dataModel.getDate();
        textNewConfirmed = res.getString(R.string.explore_new_confirmed) + " " + dataModel.getNewConfirmed();
        textNewRecovered = res.getString(R.string.explore_new_recovered) + " " + dataModel.getNewRecovered();
        textNewDeaths = res.getString(R.string.explore_new_deaths) + " " + dataModel.getNewDeaths();
        textConfirmed = res.getString(R.string.explore_confirmed) + " " + dataModel.getConfirmed();
        textRecovered = res.getString(R.string.explore_recovered) + " " + dataModel.getRecovered();
        textDeaths = res.getString(R.string.explore_deaths) + " " + dataModel.getDeaths();

        holder.textDay.setText(textDay);
        holder.textDate.setText(textDate);
        holder.textNewConfirmed.setText(textNewConfirmed);
        holder.textNewRecovered.setText(textNewRecovered);
        holder.textNewDeaths.setText(textNewDeaths);
        holder.textConfirmed.setText(textConfirmed);
        holder.textRecovered.setText(textRecovered);
        holder.textDeaths.setText(textDeaths);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
