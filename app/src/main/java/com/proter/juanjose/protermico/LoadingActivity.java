package com.proter.juanjose.protermico;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;


public class LoadingActivity extends AppCompatActivity implements Observer {
    TextView feedbackTv;
    Typeface fontTvFeedback;
    ProgressBar loadingProgress;

    private String addressO = null;

    //Manejo de mensajes con interfaz grafica y bluetooth
    ConnectionBt conBt;
    static Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        conBt = ConnectionBt.getInstance();
        conBt.addObserver(this);
        conBt.setAppContext(getApplicationContext());
        addressO = "20:16:12:05:88:65";

        feedbackTv = findViewById(R.id.feedback_tv);
        fontTvFeedback = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-LightObl.otf");
        feedbackTv.setTypeface(fontTvFeedback);

        loadingProgress = findViewById(R.id.loading_progressBar);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {          //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);              //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();       //get length of data received

                        if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                        {
                            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                            startActivity(intent);

                        }
                        recDataString.delete(0, recDataString.length());      //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    protected void onResume() {
        ConnectionBt.getInstance().addObserver(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        ConnectionBt.getInstance().deleteObserver(this);
        super.onPause();
    }


    // fast way to call Toast
    private void msg(final String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void update(Observable o, Object arg) {

        String readMessage = ((ConnectionBt) o).getData();
        bluetoothIn.obtainMessage(handlerState, readMessage).sendToTarget();
        Log.i("Notifico esto", readMessage);

    }
}
