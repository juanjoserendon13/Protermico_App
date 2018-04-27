package com.proter.juanjose.protermico;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class KeyBoardActivity extends AppCompatActivity {

    TextView instructionTv;
    Typeface fontInstruction;

    Button logoutBtn;
    Button confirmBtn;
    EditText inputUnitEt;

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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });


    }
}
