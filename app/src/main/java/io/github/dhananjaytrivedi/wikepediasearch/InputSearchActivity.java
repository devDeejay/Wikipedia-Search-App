package io.github.dhananjaytrivedi.wikepediasearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;

public class InputSearchActivity extends AppCompatActivity {

    final String TAG = "DJ";

    @BindView(R.id.searchResultSubmitButton)
    ImageView searchSubmitButton;


    @BindView(R.id.inputQueryET)
    EditText inputQueryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_search);

        searchSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    getResultsFromAPI();
                }
                else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Welcome To Main Activity", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


    }

    private void getResultsFromAPI() {

        String inputQueryString = inputQueryEditText.getText().toString();
        inputQueryString = inputQueryString.replaceAll("\\s", "%");

        String API_URL = "https://en.wikipedia.org//w/api.php?action=query&format=json&prop=pageimages%7Cpageterms&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50&pilimit=10&wbptterms=description&gpssearch=" + inputQueryString + "+T&gpslimit=10";

        Log.d(TAG, API_URL);

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
}
