package com.proter.juanjose.protermico;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class FeedActivity extends AppCompatActivity implements Observer {

    private Button logoutBtn;
    private ToggleButton homeBtn, statisticBtn, configBtn;
    private TextView stateFixedTv, stateChangeTv, tempFixedTv, tempChangeTv, remainderWaterTv, unitsTitleTv, unitsTotalTv, recollectedTitleTv, expectedTitleTv;
    private TextView recollectedRealTv, expectedRealTv, realTimeTv, realTimeDosTv, stateHarvestTv, counterRestTv, unitRestTv;
    private Typeface fontHelRoman, fontHelLight, fontHelBold;
    private LinearLayout layoutTimer;
    private CardView cardRemainderCv;
    private Context cont;
    private ImageView arrowUnitIv, clockIv;

    private String celsius;
    private float colorMap;
    private String temp;

    private int ex, re;

    private Random rand = new Random();
    private Handler handler = new Handler();

    //Manejo de mensajes con interfaz grafica y bluetooth
    private ConnectionBt conBt;
    private Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    //Variables para el contador regresivo
    private long START_TIME_IN_MILLIS = 0;
    private CountDownTimer countDown;
    private boolean timerRunning;
    private long timeLeftInMillis;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("--------------", "onCREATE");
        setContentView(R.layout.activity_feed);
        Intent intent = getIntent();
        extras = intent.getExtras();
        cont = getApplicationContext();


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
        stateHarvestTv = findViewById(R.id.state_harvest);
        layoutTimer = findViewById(R.id.layout_timer_rest);
        clockIv = findViewById(R.id.clock_iv);
        counterRestTv = findViewById(R.id.counter_rest_tv);
        unitRestTv = findViewById(R.id.unit_rest_tv);
        cardRemainderCv = findViewById(R.id.card_remainder_rest);

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
        stateHarvestTv.setTypeface(fontHelBold);
        counterRestTv.setTypeface(fontHelBold);
        unitRestTv.setTypeface(fontHelBold);

        //recollectedRealTv.setText(String.valueOf(re));
        //expectedRealTv.setText(String.valueOf(ex));


        realTimeTv.setText("10:00" + "am");
        realTimeDosTv.setText("10:00" + "am");

        // remainderWaterTv.setText("Recuerde hidratarse cada 20 minutos.");

        //Metodos y cambios de variables.
        if (extras != null) {
            String unitsChanged = extras.getString("DATO");
            unitsTotalTv.setText("Total: " + unitsChanged);
        }
        compareUnits(Integer.parseInt(recollectedRealTv.getText().toString()), Integer.parseInt(expectedRealTv.getText().toString()));

        systemTemp(temp);
        //metodos click listener para los botones de la pantalla feed
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


        //Hanlder que conecta los datos entrantes y los despliega en interfaz

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {          //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
//                    Log.i("----------", "llega al handler feed " + recDataString);//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();       //get length of data received

                        if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                        {
                            conBt.write("start");
//                            msg("Patron singleton, hilos y bluetooth OK");

                        }
                        if (recDataString.charAt(0) == '/') {
                            String temp = recDataString.substring(1, endOfLineIndex);
//                            Log.i("----------", "llega al / " + temp);
                            systemTemp(temp);
                        }

                        //Condicionales que controlan el tiempo de descanso proveniente del dispositivo.
                        if (recDataString.charAt(0) == '*') {
                            msg("Patron singleton, hilos y bluetooth OK");
                            String type = recDataString.substring(1, 5);
                            String millis = recDataString.substring(6, endOfLineIndex);

                            if (type.equals("rest")) {
                                Log.i("----------", "Millis para descansar " + millis);
                                startActivity(new Intent(FeedActivity.this, PopUp.class));
                                systemState(0);
                                START_TIME_IN_MILLIS = Long.parseLong(millis, 10);
                                timeLeftInMillis = START_TIME_IN_MILLIS;
                                startTimer();
                            }
                        }

                        recDataString.delete(0, recDataString.length());      //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                        readMessage = " ";
                    }
                }
            }
        };

        conBt = ConnectionBt.getInstance();
        conBt.addObserver(this);
        if (extras != null) {
            if (extras.getBoolean("CONNECT")) {
                conBt.write("start+" + extras.getString("DATO"));
//                conBt.write("w");
            }
        }
        updateCountDownText();

    }

    // Sobre escribo el metodo para cambiar el intent entrante.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        intent = getIntent();
        extras = intent.getExtras();
        Log.i("---------", "llega al new intent");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        conBt.getBtSocket();
        if (extras != null) {
            Intent intent = getIntent();
            Bundle extraTwo = intent.getExtras();

            String unitsChanged = extras.getString("DATO");
            Log.i("-----------", "setea las unidades " + extras.getString("DATO"));
            if (unitsChanged != null) {
                unitsTotalTv.setText("Total: " + unitsChanged);
                Log.i("-----------", "setea las unidades");
            }


        }
    }

    protected void onResume() {
        super.onResume();
        Log.i("--------------", "onRESUME");

        ConnectionBt.getInstance().addObserver(this);
        homeBtn.setChecked(true);
        statisticBtn.setChecked(false);
        configBtn.setChecked(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("--------------", "onPAUSE");
        ConnectionBt.getInstance().deleteObserver(this);

    }


    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        counterRestTv.setText(timeLeftFormat);
    }

    private void startTimer() {
        countDown = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                Log.i("--------------", "termina la cuenta regresiva");
                systemState(1);
                resetTimer();
                timerRunning = false;
            }
        }.start();

        timerRunning = true;
    }

    private void resetTimer() {
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }


    //Gestor de estado del sistema con su configuración visual
    private void systemState(int state) {
        if (state == 1) {
            stateChangeTv.setText("Trabajando");
            stateChangeTv.setTextColor(getResources().getColor(R.color.colorYellowPalette));
            layoutTimer.setBackgroundResource(R.drawable.card_back);
            remainderWaterTv.setText(R.string.remainder_water);
            remainderWaterTv.setTextSize(30);
            clockIv.setVisibility(View.INVISIBLE);
            counterRestTv.setVisibility(View.INVISIBLE);
            unitRestTv.setVisibility(View.INVISIBLE);
            cardRemainderCv.setMaxCardElevation(getPixelsFromDPs(0));
            cardRemainderCv.setCardElevation(getPixelsFromDPs(0));

        } else if (state == 0) {
            stateChangeTv.setText("Descansando");
            stateChangeTv.setTextColor(getResources().getColor(R.color.colorGreenPalette));
            layoutTimer.setBackgroundResource(R.drawable.card_back_rest);
            remainderWaterTv.setText(R.string.remainder_rest);
            remainderWaterTv.setTextSize(20);
            clockIv.setVisibility(View.VISIBLE);
            counterRestTv.setVisibility(View.VISIBLE);
            unitRestTv.setVisibility(View.VISIBLE);
            cardRemainderCv.setMaxCardElevation(getPixelsFromDPs(15));
            cardRemainderCv.setCardElevation(getPixelsFromDPs(10));
        }
    }

    protected int getPixelsFromDPs(int dps) {
        Resources r = cont.getResources();
        int px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    //Gestor de temperatura del sistema con su configuración visual.
    private void systemTemp(final String temperature) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    tempChangeTv.setText(temperature + celsius);
                    colorMap = map(Float.parseFloat(temperature), 0, 40, 200, 0);
                    //Hue, saturation, brightness
                    tempChangeTv.setTextColor(Color.HSVToColor(new float[]{colorMap, 0.8f, 0.9f}));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("----------", "formato de temperatura incorrecto");
                }

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
                    arrowUnitIv.setImageResource(R.drawable.ic_negative);
                    stateHarvestTv.setText(R.string.recollection_state_1);
                    stateHarvestTv.setTextColor(getResources().getColor(R.color.colorRedPalette));
                } else if (real >= expected) {
                    arrowUnitIv.setImageResource(R.drawable.ic_positive);
                    stateHarvestTv.setText(R.string.recollection_state_2);
                    stateHarvestTv.setTextColor(getResources().getColor(R.color.colorGreenPalette));
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
        int bytes = ((ConnectionBt) o).getByte();
        if (readMessage != null) {
            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            Log.i("--Notifico en el update", readMessage);
        }
    }
}
