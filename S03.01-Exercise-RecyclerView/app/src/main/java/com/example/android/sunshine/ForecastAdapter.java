package com.example.android.sunshine;

// Within ForecastAdapter.java /////////////////////////////////////////////////////////////////

// TODO (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>
// TODO (23) Create a private string array called mWeatherData
// TODO (47) Create the default constructor (we will pass in parameters in a later lesson)


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {


    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // TODO completed (15) Add a class file called ForecastAdapter
    // TODO completed (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // TODO completed (17) Extend RecyclerView.ViewHolder
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
