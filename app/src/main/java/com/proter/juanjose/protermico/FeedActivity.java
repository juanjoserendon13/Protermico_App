package com.proter.juanjose.protermico;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class FeedActivity extends AppCompatActivity implements Observer {

    Button logoutBtn;
    ToggleButton homeBtn, statisticBtn, configBtn;
    TextView stateFixedTv, stateChangeTv, tempFixedTv, tempChangeTv, remainderWaterTv, unitsTitleTv, unitsTotalTv, recollectedTitleTv, expectedTitleTv;
    TextView recollectedRealTv, expectedRealTv, realTimeTv, realTimeDosTv;
    Typeface fontHelRoman, fontHelLight, fontHelBold;

    ImageView arrowUnitIv;

    private String celsius;
    private float colorMap;
    private String temp;

    private int ex, re;

    private static final String RECOLL = "1";
    private static final String EXPECT = "1";


    private Random rand = new Random();
    private Handler handler = new Handler();

    //Manejo de mensajes con interfaz grafica y bluetooth
    ConnectionBt conBt;
    static Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        conBt = ConnectionBt.getInstance();
        conBt.addObserver(this);
        conBt.write("c");

        celsius = "°C";
        temp = "32";
        logoutBtn = findViewById(R.id.logout_btn);

        stateFixedTv = findViewById(R.id.state_fixed);
        stateChangeTv = findViewById(R.id.state_change);
        tempFixedTv = findViewById(R.id.temp_title);
        tempChangeTv = findViewById(R.id.temp_change);
        remainderWaterTv = findViewById(R.id.remainder_water);
        unitsTitleTv = findViewById(R.id.units_title_tv);
        unitsTotalTv = findViewById(R.id.units_total);
        recollectedTitleTv = findViewById(R.id.recollected_title_tv);
        expectedTitleTv = findViewById(R.id.expected_title_tv);
        recollectedRealTv = findViewById(R.id.recollected_real_tv);
        expectedRealTv = findViewById(R.id.expected_real_tv);
        realTimeTv = findViewById(R.id.real_time1_tv);
        realTimeDosTv = findViewById(R.id.real_time2_tv);
        arrowUnitIv = findViewById(R.id.arrow_units_iv);

        homeBtn = findViewById(R.id.home_btn);
        homeBtn.setChecked(true);
        statisticBtn = findViewById(R.id.statistics_btn);
        statisticBtn.setChecked(false);
        configBtn = findViewById(R.id.config_btn);
        configBtn.setChecked(false);

        fontHelRoman = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-Roman.otf");
        fontHelLight = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-Light.otf");
        fontHelBold = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-Bold.otf");

        //Seteo de fuentes tipograficas
        stateFixedTv.setTypeface(fontHelRoman);
        stateChangeTv.setTypeface(fontHelBold);
        tempFixedTv.setTypeface(fontHelRoman);
        tempChangeTv.setTypeface(fontHelRoman);
        remainderWaterTv.setTypeface(fontHelRoman);
        unitsTitleTv.setTypeface(fontHelRoman);
        unitsTotalTv.setTypeface(fontHelLight);
        recollectedTitleTv.setTypeface(fontHelRoman);
        expectedTitleTv.setTypeface(fontHelRoman);
        recollectedRealTv.setTypeface(fontHelBold);
        expectedRealTv.setTypeface(fontHelBold);
        realTimeTv.setTypeface(fontHelLight);
        realTimeDosTv.setTypeface(fontHelLight);

        //recollectedRealTv.setText(String.valueOf(re));
        //expectedRealTv.setText(String.valueOf(ex));


        realTimeTv.setText("10:00" + "am");
        realTimeDosTv.setText("10:00" + "am");

        //Intento de persistencia de DATOS NO FUNKA con boton de interfaz
        if (savedInstanceState != null) {
            String recollect = savedInstanceState.getString("saved_re");
            recollectedRealTv.setText(recollect);

            String expect = savedInstanceState.getString("saved_ex");
            expectedRealTv.setText(expect);

            Log.d("In savedInstance", recollect);
            Log.d("In savedInstance", expect);
        } else {
            Log.d("not in oncreate saved", "ggg");
        }
        // remainderWaterTv.setText("Recuerde hidratarse cada 20 minutos.");

        //Metodos y cambios de variables.
        if (extras != null) {
            String unitsChanged = extras.getString("DATO");
            unitsTotalTv.setText("Total: " + unitsChanged);
        }
        compareUnits(Integer.parseInt(recollectedRealTv.getText().toString()), Integer.parseInt(expectedRealTv.getText().toString()));
        systemState(1);
        systemTemp(temp);
        //metodos click listener para los botones de la pantalla feed
        //Sale de la aplicación y regresa a la pantalla de inicio.
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBtn.setChecked(true);
            }
        });

        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                startActivity(intent);
            }
        });

        configBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(intent);
            }
        });

        //Loop every 2 seconds simulating recollection
        Runnable r = new Runnable() {
            public void run() {
                re = rand.nextInt(1001);
                ex = rand.nextInt(1001);
                compareUnits(re, ex);
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(r, 2000);

        //Loop every 3 seconds simulating temperature
        Runnable ru = new Runnable() {
            public void run() {
                int tem = rand.nextInt(40);

                systemTemp(String.valueOf(tem));
                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(ru, 3000);

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
                            msg("Patron singleton, hilos y bluetooth OK");

                        }
                        recDataString.delete(0, recDataString.length());      //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };


    }

    //persistencia de datos NO FUNKA con boton de interfaz
    @Override
    public void onSaveInstanceState(Bundle outSate) {
        super.onSaveInstanceState(outSate);
        String toSaveRe = recollectedRealTv.getText().toString();
        String toSaveEx = expectedRealTv.getText().toString();
        outSate.putString("saved_re", toSaveRe);
        outSate.putString("saved_ex", toSaveEx);
        //outSate.putString(RECOLL, recollectedRealTv.getText().toString());
        //outSate.putString(EXPECT, expectedRealTv.getText().toString());

        Log.d("In onSaveRe", toSaveRe);
        Log.d("In onSaveEx", toSaveEx);

    }

    //persistencia de datos NO FUNKA con boton de interfaz
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d("In onRestore", "bien");
        String savedRe = savedInstanceState.getString("saved_re");
        String savedEx = savedInstanceState.getString("saved_ex");

        recollectedRealTv.setText(savedRe);
        expectedRealTv.setText(savedEx);
        //TextView recollected = findViewById(R.id.recollected_real_tv);
        //TextView expected = findViewById(R.id.expected_real_tv);

        //recollected.setText(savedInstanceState.getString(RECOLL));
        //expected.setText(savedInstanceState.getString(EXPECT));
        super.onRestoreInstanceState(savedInstanceState);
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


    //Gestor de estado del sistema con su configuración visual
    private void systemState(int state) {
        if (state == 1) {
            stateChangeTv.setText("Trabajando");
            stateChangeTv.setTextColor(getResources().getColor(R.color.colorYellowPalette));
        } else if (state == 0) {
            stateChangeTv.setText("Descansando");
            stateChangeTv.setTextColor(getResources().getColor(R.color.colorGreenPalette));
        }
    }

    //Gestor de temperatura del sistema con su configuración visual.
    private void systemTemp(final String temperature) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tempChangeTv.setText(temperature + celsius);
                colorMap = map(Float.parseFloat(temperature), 0, 40, 200, 0);
                //Hue, saturation, brightness
                tempChangeTv.setTextColor(Color.HSVToColor(new float[]{colorMap, 0.8f, 0.9f}));
            }
        });
    }

    //Metodo que simula el Map de processing
    private float map(float value, float start1, float stop1, float start2, float stop2) {
        return ((value - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
        //float color = ((value - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
        //return color;
    }

    //Metodo que compara las unidades recolectadas y esperadas, y actualiza la flecha central.
    private void compareUnits(final int real, final int expected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recollectedRealTv.setText(String.valueOf(real));
                expectedRealTv.setText(String.valueOf(expected));
                if (real < expected) {
                    arrowUnitIv.setImageResource(R.drawable.arrow_negative);
                } else if (real >= expected) {
                    arrowUnitIv.setImageResource(R.drawable.arrow_positive);
                }
            }
        });
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
