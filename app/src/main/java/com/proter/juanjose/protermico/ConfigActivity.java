package com.proter.juanjose.protermico;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class ConfigActivity extends AppCompatActivity {
    ToggleButton homeBtn, statisticBtn, configBtn;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        logoutBtn = findViewById(R.id.logout_btn);

        homeBtn = findViewById(R.id.home_btn);
        configBtn = findViewById(R.id.config_btn);
        configBtn.setChecked(true);
        statisticBtn = findViewById(R.id.statistics_btn);

        //Sale de la aplicación y regresa a la pantalla de inicio.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configBtn.setChecked(true);
            }
        });

        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
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
        statisticBtn = findViewById(R.id.statistics_btn);
        statisticBtn.setChecked(false);
        homeBtn = findViewById(R.id.home_btn);
        homeBtn.setChecked(false);
    }

}

