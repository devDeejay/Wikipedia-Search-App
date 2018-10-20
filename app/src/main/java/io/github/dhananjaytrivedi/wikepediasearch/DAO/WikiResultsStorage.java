package io.github.dhananjaytrivedi.wikepediasearch.DAO;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import io.github.dhananjaytrivedi.wikepediasearch.Model.Result;

public class WikiResultsStorage {

    static SharedPreferences sharedpreferences;

    private static ArrayList<Result> newVisitedPagesArrayList = new ArrayList<>();

    final static String TAG = "STORAGE";

    public static void saveVisitedPagesInSharedPreferences(Context context) {

        if (newVisitedPagesArrayList.isEmpty()) {
            // If there is nothing new added, no need to procced
            return;
        }

        // 1.1 First Get The Stored Array list
        ArrayList<Result> storedResults = getStoredPagesArrayList(context);

        // 1.2 If there is an existing data we need to append new data to that
        if (storedResults != null) {
            // Add Your New List Items to that

            for (Result result : newVisitedPagesArrayList) {
                if (storedResults.contains(result)) {
                    storedResults.remove(result);
                }
                storedResults.add(result);
            }
        } else {
            // For First Time We are adding something
            storedResults = newVisitedPagesArrayList;
        }

        // 1.4 Parse whole list into JSON (I know it's not the best way forward)
        String serializedJSONString = serializeObject(storedResults);

        // 1.5 Write to Shared Preferences
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("jsonData", serializedJSONString);
        editor.apply();

    }

    public static ArrayList<Result> getStoredPagesArrayList(Context context) {

        // First Get the String from the Storage File
        String storedArrayListAsJSONString = readFromSharedPreferences(context);

        // Get the arraylist from that string
        return getResultsObjectArrayList(storedArrayListAsJSONString);

    }

    private static String readFromSharedPreferences(Context context) {

        sharedpreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        return sharedpreferences.getString("jsonData", "");
    }

    private static ArrayList<Result> getResultsObjectArrayList(String json) {

        ArrayList<Result> resultsList = new ArrayList<>();

        ArrayList list = deserializeObject(json);

        // If there is no data, just return null
        if (list == null) {
            return null;
        }

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Result result = new Result();
            LinkedTreeMap<String, String> object = (LinkedTreeMap<String, String>) iterator.next();
            result.setDescription(object.get("description"));
            result.setPageID(object.get("pageID"));
            result.setImageURL(object.get("imageURL"));
            result.setTitle(object.get("title"));
            resultsList.add(result);
        }

        return resultsList;

    }

    private static ArrayList deserializeObject(String jsonString) {
        ArrayList list = new Gson().fromJson(jsonString, ArrayList.class);

        if (list != null) {
            return list;
        }

        return null;
    }

    private static String serializeObject(ArrayList<Result> list) {

        return new Gson().toJson(list);

    }

    public static void addNewResultObjectToStore(Result result) {
        if (!newVisitedPagesArrayList.contains(result)) {
            newVisitedPagesArrayList.add(result);
        }
    }
}
