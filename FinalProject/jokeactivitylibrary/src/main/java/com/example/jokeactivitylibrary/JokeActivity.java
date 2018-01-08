package com.example.jokeactivitylibrary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {
    public static final String JOKE = "JOKE";
    private TextView mTextViewJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        mTextViewJoke = findViewById(R.id.joke_TextView);

        if (getIntent().hasExtra(JOKE)) {
            mTextViewJoke.setText(getIntent().getStringExtra(JOKE));
        }

    }

}
