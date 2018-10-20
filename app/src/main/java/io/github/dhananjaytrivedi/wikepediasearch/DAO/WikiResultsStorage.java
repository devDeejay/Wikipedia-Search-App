package io.github.dhananjaytrivedi.wikepediasearch.DAO;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import io.github.dhananjaytrivedi.wikepediasearch.Model.Result;

public class WikiResultsStorage {
    private static ArrayList<Result> visitedPagesForOneSession = new ArrayList<>();
    final static String TAG = "STORAGE";

    public static void writeVisitedPagesToStorage(Context context) {

        // First Get The Stored Array list
        ArrayList<Result> storedResults = readPastVisitedPagesFromStorage(context);

        if (storedResults != null) { // If there is an existing data we need to append new data to that
            // Add Your New List Items to that
            storedResults.addAll(visitedPagesForOneSession);
        }
        else {
            // For First Time We are adding something
            storedResults = visitedPagesForOneSession;
        }

        // Parse whole list into JSON (I know it's not the best way forward)
        String jsonFromJavaArrayList = getJsonStringFromArraylist(storedResults);

        String filename = "storage_file";

        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonFromJavaArrayList.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Result> readPastVisitedPagesFromStorage(Context context) {
        String storedArrayListAsJSONString = readStringFromStorage(context);
        ArrayList<Result> storedPages = getArrayListFromJSONString(storedArrayListAsJSONString);
        return storedPages;
    }

    @Nullable
    private static String readStringFromStorage(Context context) {

        String storedArrayList = "";
        try {
            FileInputStream fileInputStream = context.openFileInput("storage_file");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            while ((storedArrayList = bufferedReader.readLine()) != null) {
                stringBuffer.append(storedArrayList);
            }

            storedArrayList = stringBuffer.toString();

        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return storedArrayList;
    }

    private static ArrayList<Result> getArrayListFromJSONString(String storedArrayList) {
        JsonParser jsonParser = new JsonParser();
        Gson googleJson = new Gson();
        if (!storedArrayList.equals("")){
            JsonArray arrayFromString = jsonParser.parse(storedArrayList.trim()).getAsJsonArray();
            ArrayList<Result> list = googleJson.fromJson(arrayFromString, ArrayList.class);
            return list;
        }
        return null;
    }

    private static String getJsonStringFromArraylist(ArrayList<Result> list) {
        Gson gsonBuilder = new GsonBuilder().create();
        String jsonFromJavaArrayList = gsonBuilder.toJson(list);
        System.out.println(jsonFromJavaArrayList);
        return jsonFromJavaArrayList;
    }

    public static void addNewResultObjectToStore(Result result, Context context) {
        visitedPagesForOneSession.add(result);
    }
}
