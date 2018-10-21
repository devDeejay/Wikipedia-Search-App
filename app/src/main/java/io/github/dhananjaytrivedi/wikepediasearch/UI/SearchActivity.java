package io.github.dhananjaytrivedi.wikepediasearch.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.dhananjaytrivedi.wikepediasearch.Adapter.ResultAdapter;
import io.github.dhananjaytrivedi.wikepediasearch.DAO.Storage;
import io.github.dhananjaytrivedi.wikepediasearch.Model.WikiResult;
import io.github.dhananjaytrivedi.wikepediasearch.R;

public class SearchActivity extends AppCompatActivity {

    final String TAG = "DJ";
    ArrayList<WikiResult> resultsList = new ArrayList<>();
    ArrayList<WikiResult> visitedPages = new ArrayList<>();
    @BindView(R.id.searchResultSubmitButton)
    ImageView searchSubmitButton;
    @BindView(R.id.githubButton)
    ImageView githubButton;
    @BindView(R.id.inputQueryET)
    EditText inputQueryEditText;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.errorMessageLayout)
    RelativeLayout errorMessage;

    @BindView(R.id.headingTV)
    TextView headingTV;
    @BindView(R.id.subHeadingTV)
    TextView subHeadingTV;

    private ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ButterKnife.bind(this);
        errorMessage.setVisibility(View.INVISIBLE);

        stopImageViewAnimation();
        showPastVisitedPages();

        Typeface headingFont = Typeface.createFromAsset(this.getAssets(), "fonts/heading.ttf");
        Typeface subHeadingFont = Typeface.createFromAsset(this.getAssets(), "fonts/sub_heading.ttf");
        headingTV.setTypeface(headingFont);
        subHeadingTV.setTypeface(subHeadingFont);

        inputQueryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setVisibility(View.INVISIBLE);
                startSearching();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = "https://github.com/DhananjayTrivedi/Wikipedia-Search-App";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(URL));
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inputQueryEditText.getText().toString().isEmpty()) {
            showPastVisitedPages();
        }
    }

    private void startSearching() {
        if (isNetworkAvailable()) {
            if (inputQueryEditText.getText().toString().equals("")) {
                showPastVisitedPages();
            } else {
                startImageViewAnimation();
                getResultsFromAPI();
            }
        } else {
            showSnackbarMessage("We need Internet Connectivity To Complete This Task");
        }
    }

    private void showPastVisitedPages() {
        visitedPages = Storage.getStoredPagesArrayList(SearchActivity.this);
        if (visitedPages != null) {
            Collections.reverse(visitedPages);
            updateDisplayWithResultsData(visitedPages);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Storage.saveVisitedPagesInSharedPreferences(SearchActivity.this);

    }

    private void showSnackbarMessage(String inputMessageString) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), inputMessageString, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void getResultsFromAPI() {

        String inputQueryString = inputQueryEditText.getText().toString();
        inputQueryString = inputQueryString.replaceAll("\\s", "%");
        String API_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&generator=prefixsearch&prop=pageprops|pageimages|description&redirects=&ppprop=displaytitle&piprop=thumbnail&pithumbsize=300&pilimit=40&gpssearch=" + inputQueryString + "&gpsnamespace=0&gpslimit=6";
        Log.d(TAG, API_URL);

        StringRequest request = new StringRequest(API_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response Received From Server.");
                Log.d(TAG, response);

                try {
                    resultsList = parseJSONData(response);
                    if (resultsList == null) {
                        stopImageViewAnimation();
                    } else {
                        updateDisplayWithResultsData(resultsList);
                        stopImageViewAnimation();
                    }
                } catch (JSONException e) {
                    stopImageViewAnimation();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "ErrorListener : Error");

                updateDisplayForNoData();
                stopImageViewAnimation();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void updateDisplayWithResultsData(ArrayList<WikiResult> results) {
        recyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);

        Log.d(TAG, "*************** UPDATING LAYOUT ***************");

        adapter = new ResultAdapter(results, SearchActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

    }

    private void updateDisplayForNoData() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private ArrayList<WikiResult> parseJSONData(String response) throws JSONException {

        ArrayList<WikiResult> results = new ArrayList<>();
        JSONObject parentObject = new JSONObject(response);
        if (parentObject.has("query")) {
            JSONObject queryObject = parentObject.getJSONObject("query");
            JSONObject pagesObject = queryObject.getJSONObject("pages");

            Iterator<String> iterator = pagesObject.keys();
            while (iterator.hasNext()) {
                String pageKey = iterator.next();
                JSONObject page = pagesObject.getJSONObject(pageKey);

                String pageID = page.getString("pageid");
                String title = page.getString("title");
                String description = page.getString("description");
                String imageURL = "default";

                if (page.has("thumbnail")) {
                    JSONObject thumbnailObject = page.getJSONObject("thumbnail");
                    imageURL = thumbnailObject.getString("source");
                }

                WikiResult result = new WikiResult();
                result.setPageID(pageID);
                result.setTitle(title);
                result.setDescription(description);
                result.setImageURL(imageURL);

                results.add(result);

            }

            // Added all the results objects to the WikiResult Array

            return results;
        } else {
            return null;
        }
    }

    /*
     * This method check whether the device is connected to Internet or not
     **/

    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void startImageViewAnimation() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                searchSubmitButton.animate().rotation(360).withEndAction(this).setDuration(1000).setInterpolator(new LinearInterpolator()).start();
            }
        };

        searchSubmitButton.animate().rotation(360).withEndAction(runnable).setDuration(1000).setInterpolator(new LinearInterpolator()).start();
    }

    public void stopImageViewAnimation() {
        searchSubmitButton.animate().cancel();
        searchSubmitButton.setRotation(0f);
    }
}
