package io.github.dhananjaytrivedi.wikepediasearch.UI;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.github.dhananjaytrivedi.wikepediasearch.R;

public class


BrowserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        WebView webView = findViewById(R.id.webView);

        Intent intent = getIntent();
        String pageID = intent.getStringExtra("pageID");
        String title = intent.getStringExtra("title");
        String URL = "https://en.wikipedia.org/wiki?curid=" + pageID;

        webView.loadUrl(URL);

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Loading '" + title + "'", Snackbar.LENGTH_LONG);
        snackbar.show();

    }
}
