package com.proter.juanjose.protermico;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class KeyBoardActivity extends AppCompatActivity implements Observer {

    private TextView instructionTv;
    private Typeface fontInstruction;

    private Button logoutBtn;
    private Button confirmBtn;
    private EditText inputUnitEt;

    private ConnectionBt conBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_board);

        instructionTv = findViewById(R.id.instruction_tv);
        fontInstruction = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-Roman.otf");
        instructionTv.setTypeface(fontInstruction);

        logoutBtn = findViewById(R.id.logout_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        inputUnitEt = findViewById(R.id.units_input);

        //Confirma las unidades ingresadas al sistema, y valida el número
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputUnitEt.getText().toString() != null && !inputUnitEt.getText().toString().isEmpty()) {
                    //Log.i("Units Print", inputUnitEt.getText().toString());
                    String unidades = inputUnitEt.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    intent.putExtra("CONNECT", true);
                    intent.putExtra("DATO", unidades);
                    startActivity(intent);
                } else {
                    //Log.d("In to message", "Prueba");
                    inputUnitEt.setError("Por favor ingrese un Valor");
                }
            }
        });

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
        conBt = ConnectionBt.getInstance();
        conBt.addObserver(this);
    }

    protected void onResume() {
        super.onResume();
        ConnectionBt.getInstance().addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectionBt.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        String readMessage = ((ConnectionBt) o).getData();
        int bytes = ((ConnectionBt) o).getByte();
        if (readMessage != null) {
//            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            Log.i("--Notifico en el update", readMessage);
        }
    }
}
