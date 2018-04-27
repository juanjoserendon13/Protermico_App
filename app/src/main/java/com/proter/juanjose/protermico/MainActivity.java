package com.proter.juanjose.protermico;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView remainderTv;
    Typeface fontRemainder;

    Button loginBtn;
    private BluetoothAdapter protermicoBt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remainderTv = findViewById(R.id.remainder_tv);
        fontRemainder = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-LightObl.otf");
        remainderTv.setTypeface(fontRemainder);

        loginBtn = findViewById(R.id.login_btn);

        //Evalua si el dispositivo posee bluetooth y si este se encuentra encendido
        protermicoBt = BluetoothAdapter.getDefaultAdapter();


        //Valida la conexión con el dispositivo protermico
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (protermicoBt == null) {
                    //Show a mensag. that thedevice has no bluetooth adapter
                    Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                    //finish apk
                    finish();
                } else {
                    if (protermicoBt.isEnabled()) {
                        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                        startActivity(intent);
                    } else {
                        //Ask to the user turn the bluetooth on
                        Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnBTon, 1);
                    }
                }
            }
        });

    }
}
