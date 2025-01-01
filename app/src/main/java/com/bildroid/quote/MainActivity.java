package com.bildroid.quote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Placeholder;
import com.bildroid.quote.BuildConfig;


import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView;
    private TextView authorTextView;
    private ProgressBar progressBar;
    private QuoteApi quoteApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView shareAnim  = findViewById(R.id.share_anim);
        LottieAnimationView nextAnim = findViewById(R.id.next_anim);
        quoteTextView = findViewById(R.id.quote_txt);
        authorTextView = findViewById(R.id.quote_aut);
        progressBar = findViewById(R.id.progress);


        nextAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchQuote();
            }
        });

        shareAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareQuote();
            }
        });



        // Create a Retrofit instance
        quoteApi = RetrofitInstance.getRetrofitInstance().create(QuoteApi.class);

        // Initial quote fetch
        fetchQuote();


    }

    private void fetchQuote() {
        // Show progress bar while fetching
        progressBar.setVisibility(View.VISIBLE);

        // Make the API call
        Call<List<Quote>> call = quoteApi.getRandomQuote();
        call.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                // Hide progress bar after fetching
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Update TextViews with the first quote in the list
                    Quote firstQuote = response.body().get(0);
                    quoteTextView.setText(firstQuote.getQuoteText());
                    authorTextView.setText(firstQuote.getAuthor());
                    Log.d("MainActivity", "Quote: " + firstQuote.getQuoteText());
                    Log.d("MainActivity", "Author: " + firstQuote.getAuthor());
                } else {
                    Log.e("MainActivity", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                // Hide progress bar on failure
                progressBar.setVisibility(View.GONE);
                Log.e("MainActivity", "Error fetching quote: " + t.getMessage(), t);
            }
        });
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder extDialog = new AlertDialog.Builder(this);

        extDialog.setIcon(R.drawable.baseline_exit_to_app_24);
        extDialog.setTitle("Exit");
        extDialog.setMessage("are you sure you want to exit?");
        extDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
            }
        });
        extDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //null
            }
        });

        extDialog.show();
    }

    private void shareQuote() {
        // Get the quote and author from TextViews
        String quote = quoteTextView.getText().toString();
        String author = authorTextView.getText().toString();

        // Create a text to share
        String shareText = "Check out this quote:\n\n" + quote + "\n\n- " + author;

        // Create an Intent to share text
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quote");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Start the Intent
        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

}