package com.example.android.sunshine;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


// Within ForecastAdapter.java /////////////////////////////////////////////////////////////////
// TODO completed (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>
// Within ForecastAdapter.java /////////////////////////////////////////////////////////////////
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // TODO (47) Create the default constructor (we will pass in parameters in a later lesson)
    public ForecastAdapter() {
    }

    // TODO completed (23) Create a private string array called mWeatherData
    private String[] mWeatherData;

    // TODO completed (24) Override onCreateViewHolder
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // TODO completed (25) Within onCreateViewHolder, inflate the list item xml into a view
        // TODO completed (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter
        // sort of condensed version
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_list_item,
                        parent,
                        false);
        return new ForecastAdapterViewHolder(view);

        // separated version
/*
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int itemLayout = R.layout.forecast_list_item;
        boolean initiallyAttach = false;
        View view = inflater.inflate(itemLayout, parent, initiallyAttach);
        return new ForecastAdapterViewHolder(view);
*/

        // really condensed version
/*
        return new ForecastAdapterViewHolder(
                // view
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.forecast_list_item,
                                parent,
                                false)
        );
*/

    }

    // TODO completed (27) Override onBindViewHolder
    // TODO completed (28) Set the text of the TextView to the weather for this list item's position
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        holder.mWeatherTextView.setText(mWeatherData[position]);
    }

    // TODO completed (29) Override getItemCount
    // TODO completed (30) Return 0 if mWeatherData is null, or the size of mWeatherData if it is not null
    @Override
    public int getItemCount() {
        if (mWeatherData == null) return 0;
        else return mWeatherData.length;
    }

    // TODO completed (31) Create a setWeatherData method that saves the weatherData to mWeatherData
    // TODO completed (32) After you save mWeatherData, call notifyDataSetChanged
    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged(); // built-in method in RecyclerView
    }

    // TODO completed (15) Add a class file called ForecastAdapter
    // TODO completed (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // TODO completed (17) Extend RecyclerView.ViewHolder
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {

        // Within ForecastAdapterViewHolder ///////////////////////////////////////////////////////////
        // TODO completed (18) Create a public final TextView variable called mWeatherTextView
        public final TextView mWeatherTextView;

        // TODO completed (19) Create a constructor for this class that accepts a View as a parameter
        // TODO completed (20) Call super(view) within the constructor for ForecastAdapterViewHolder
        // TODO completed (21) Using view.findViewById, get a reference to this layout's TextView and save it to mWeatherTextView
        // Within ForecastAdapterViewHolder ///////////////////////////////////////////////////////////
        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
        }
    }
}

