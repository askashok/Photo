package com.allmenu;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import photocontest.bliss.com.photocontest.R;

/**
 * Created by Jenifa Mary.C on 5/11/2015.
 */
public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    private static final String LOG_TAG = "PlacesAutoCompleteAdapter";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/";
    private static final String TYPE_AUTOCOMPLETE = "autocomplete";
    private static final String OUT_JSON = "/json";


    public static int check_value = 0;
    Context context;

    public PlacesAutoCompleteAdapter(Context con, int listItem) {
        // TODO Auto-generated constructor stub
        super(con, listItem);
        context = con;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    check_value = results.count;

                    notifyDataSetChanged();
                } else {
                    check_value = results.count;
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    protected ArrayList<String> autocomplete(String input) {
        // TODO Auto-generated method stub
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + context.getResources().getString(R.string.google_APi_AutoCompletetxt_key));
            // sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            //System.out.println("" + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
           // MemberVariables.showLog(LOG_TAG, "Error processing Places API URL" + e);
            return resultList;
        } catch (IOException e) {
           // MemberVariables.showLog(LOG_TAG, "Error connecting to Places API" + e);
           e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
          // MemberVariables.showLog(LOG_TAG, "Cannot process JSON results" + e);
        }

        return resultList;

    }
}
