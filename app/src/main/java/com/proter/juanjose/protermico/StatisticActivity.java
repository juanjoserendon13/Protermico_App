package com.proter.juanjose.protermico;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class StatisticActivity extends AppCompatActivity {
    ToggleButton homeBtn, statisticBtn, configBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        homeBtn = findViewById(R.id.home_btn);
        configBtn = findViewById(R.id.config_btn);
        statisticBtn = findViewById(R.id.statistics_btn);
        statisticBtn.setChecked(true);

        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configBtn.setChecked(true);
            }
        });

        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        configBtn = findViewById(R.id.config_btn);
        configBtn.setChecked(false);
        homeBtn = findViewById(R.id.home_btn);
        homeBtn.setChecked(false);
    }
}
