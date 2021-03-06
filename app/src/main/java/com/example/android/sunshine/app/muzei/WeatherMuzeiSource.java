package com.example.android.sunshine.app.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

/**
 * Created by Gerbage on 11/23/2016.
 */

public class WeatherMuzeiSource extends MuzeiArtSource {

    private static final String [] FORECAST_COLUMNS = new String []{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };

    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_WEATHER_DESC = 1;


    public WeatherMuzeiSource() {
        super("WeatherMuzeiSource");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        boolean dataUpdated = intent != null && SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());

        if (dataUpdated && isEnabled()){
            onUpdate(UPDATE_REASON_OTHER);
        }
    }

    @Override
    protected void onUpdate(int reason) {
        String location = Utility.getPreferredLocation(this);
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location
        , System.currentTimeMillis());

        Cursor cursor = getContentResolver().query(weatherForLocationUri, FORECAST_COLUMNS, null,
                null,WeatherContract.WeatherEntry.COLUMN_DATE + "ASC");

        if (cursor.moveToFirst()){
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            String Desc = cursor.getString(INDEX_WEATHER_DESC);

            String imageUrl = Utility.getImageUrlForWeatherCondition(weatherId);

            if (imageUrl != null){
                publishArtwork(new Artwork.Builder()
                .imageUri(Uri.parse(imageUrl))
                .title(Desc)
                .byline(location)
                .viewIntent(new Intent(this, MainActivity.class))
                .build());
            }
        }
        cursor.close();
    }
}
