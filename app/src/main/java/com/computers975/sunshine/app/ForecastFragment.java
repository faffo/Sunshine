package com.computers975.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForecastFragment extends Fragment {


    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 64/53",
                "Fri - HELP GLADOS IS HUNTING ME - 70/46",
                "Sat - Sunny - 76/68",
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter=
                new ArrayAdapter<String>(
                        //The current context
                        getActivity(),
                        //ID of list item layout
                        R.layout.list_item_forecast,
                        //ID of the textview to populate
                        R.id.list_item_forecast_textview,
                        //Forecast data
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get a reference to the ListView and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

   public class FetchWeatherTask extends AsyncTask<Void, Void, Void>{

       private final String LOG_TAG= FetchWeatherTask.class.getSimpleName();

       @Override
       protected Void doInBackground(Void... params) {
           //---------------LESSON 2-----------

           // These two need to be declared outside the try/catch
           // so that they can be closed in the finally block.
           HttpURLConnection urlConnection = null;
           BufferedReader reader = null;

           // Will contain the raw JSON response as a string.
           String forecastJsonStr = null;

           try {
               // Construct the URL for the OpenWeatherMap query
               // Possible parameters are avaiable at OWM's forecast API page, at
               // http://openweathermap.org/API#forecast
               URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=41122,italy&mode=json&units=metric&cnt=7");

               // Create the request to OpenWeatherMap, and open the connection
               urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               urlConnection.connect();

               // Read the input stream into a String
               InputStream inputStream = urlConnection.getInputStream();
               StringBuffer buffer = new StringBuffer();
               if (inputStream == null) {
                   // Nothing to do.
                   return null;
               }
               reader = new BufferedReader(new InputStreamReader(inputStream));

               String line;
               while ((line = reader.readLine()) != null) {
                   // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                   // But it does make debugging a *lot* easier if you print out the completed
                   // buffer for debugging.
                   buffer.append(line + "\n");
               }

               if (buffer.length() == 0) {
                   // Stream was empty.  No point in parsing.
                   return null;
               }
               forecastJsonStr = buffer.toString();
           } catch (IOException e) {
               Log.e("ForecastFragment", "Error ", e);
               // If the code didn't successfully get the weather data, there's no point in attemping
               // to parse it.
               return null;
           } finally{
               if (urlConnection != null) {
                   urlConnection.disconnect();
               }
               if (reader != null) {
                   try {
                       reader.close();
                   } catch (final IOException e) {
                       Log.e("ForecastFragment", "Error closing stream", e);
                   }
               }
           }
       return null;
       }
   }
}