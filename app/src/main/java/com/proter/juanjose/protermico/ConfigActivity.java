package com.proter.juanjose.protermico;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Observable;
import java.util.Observer;

public class ConfigActivity extends AppCompatActivity implements Observer {
    private ToggleButton homeBtn, statisticBtn, configBtn;
    private EditText unitsInEt, hoursInEt;
    private Button logoutBtn, saveBtn;
    private ConnectionBt conBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        logoutBtn = findViewById(R.id.logout_btn);

        homeBtn = findViewById(R.id.home_btn);
        configBtn = findViewById(R.id.config_btn);
        configBtn.setChecked(true);
        statisticBtn = findViewById(R.id.statistics_btn);
        saveBtn = findViewById(R.id.save_btn);
        unitsInEt = findViewById(R.id.input_units_et);
        hoursInEt = findViewById(R.id.input_hours_et);

        //Sale de la aplicación y regresa a la pantalla de inicio.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                conBt.closeConnection();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("WARNING", true);
                intent.putExtra("ALERT", "Se desconectó del dispositivo");
                startActivity(intent);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inUnit = unitsInEt.getText().toString();
                String inHour = hoursInEt.getText().toString();
                if (inUnit.matches("") && inHour.matches("")) {
                    msg("No hay ningún cambio.");
                } else {
                    msg("Se guardaron los cambios.");
                }

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

        conBt = ConnectionBt.getInstance();
        conBt.addObserver(this);
        if (!conBt.getIsConnect()) {
            conBt.getBtSocket();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectionBt.getInstance().addObserver(this);
        statisticBtn = findViewById(R.id.statistics_btn);
        statisticBtn.setChecked(false);
        homeBtn = findViewById(R.id.home_btn);
        homeBtn.setChecked(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionBt.getInstance().deleteObserver(this);
    }

    // fast way to call Toast
    private void msg(final String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void update(Observable o, Object arg) {
        String readMessage = ((ConnectionBt) o).getData();
        int bytes = ((ConnectionBt) o).getByte();
        if (readMessage != null) {
            Log.i("Notifico esto", readMessage);
//            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
//            Log.i("Notifico esto", readMessage);
        }

    }

}

