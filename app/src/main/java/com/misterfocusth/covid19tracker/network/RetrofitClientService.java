package com.misterfocusth.covid19tracker.network;

import com.misterfocusth.covid19tracker.model.HistoryDataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitClientService {

    @GET("/api/open/timeline")
    Call<List<HistoryDataModel>> getDataList();

}
