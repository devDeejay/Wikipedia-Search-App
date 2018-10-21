package io.github.dhananjaytrivedi.wikepediasearch.DAO;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Iterator;

import io.github.dhananjaytrivedi.wikepediasearch.Model.WikiResult;

public class Storage {

    // Will be reading and writing to Shared Preferences
    static SharedPreferences sharedpreferences;

    // ArrayList of newly added pages
    private static ArrayList<WikiResult> newVisitedPagesArrayList = new ArrayList<>();

    final static String TAG = "DAO";

    /*
    * This method gets the new ArrayList, and the one stored in SharedPreferences after we deserialize it
    * Creates a final ArrayList by combining two ArrayLists in a way that duplicates are removed
    * Serializes the final ArrayList
    * Writes it to Shared Preference as a String
    * */
    public static void saveVisitedPagesInSharedPreferences(Context context) {

        if (newVisitedPagesArrayList.isEmpty()) {
            // If there is nothing new added, no need to procced
            Log.d(TAG, "Nothing to add");
            return;
        }

        // 1.1 First Get The Stored Array list
        ArrayList<WikiResult> storedResults = getStoredPagesArrayList(context);

        // 1.2 If there is an existing data we need to append new data to that
        if (storedResults != null) {

            // Add Your New List Items to that

            // If the item is already stored, delete that existing item, and add this fresh item
            for (WikiResult result : newVisitedPagesArrayList) {
                Iterator<WikiResult> iterator = storedResults.iterator();
                while (iterator.hasNext()){
                    if (result.getPageID().equals(iterator.next().getPageID())) {
                        iterator.remove();
                    }
                }

                // Add a new result which will be displayed on Top now
                storedResults.add(result);
            }
        } else {

            Log.d(TAG, "Stored Results Is Empty");

            // For First Time We are adding something
            storedResults = newVisitedPagesArrayList;
        }

        // 1.4 Parse new list into JSON String
        String serializedJSONString = serializeObject(storedResults);

        // 1.5 Write to Shared Preferences
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("jsonData", serializedJSONString);
        editor.apply();

        // Empty the newVisitedArrayList as its items are already added
        newVisitedPagesArrayList.clear();

    }

    public static ArrayList<WikiResult> getStoredPagesArrayList(Context context) {

        // First Get the String from the Storage File
        String storedArrayListAsJSONString = readFromSharedPreferences(context);

        // Get the arraylist from that string
        return getResultsObjectArrayList(storedArrayListAsJSONString);

    }

    private static String readFromSharedPreferences(Context context) {

        sharedpreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        return sharedpreferences.getString("jsonData", "");
    }

    private static ArrayList<WikiResult> getResultsObjectArrayList(String json) {

        ArrayList<WikiResult> resultsList = new ArrayList<>();

        ArrayList list = deserializeObject(json);

        // If there is no data, just return null
        if (list == null) {
            return null;
        }

        // Using an iterator instead of foreach as we have to remove the elements while we are iterating them
        // Foreach loop throws an exception

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            WikiResult result = new WikiResult();
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
        return list;
    }

    private static String serializeObject(ArrayList<WikiResult> list) {

        return new Gson().toJson(list);

    }

    public static void addNewResultObjectToStore(WikiResult result) {
        if (!newVisitedPagesArrayList.contains(result)) {
            newVisitedPagesArrayList.add(result);
        }
    }
}
