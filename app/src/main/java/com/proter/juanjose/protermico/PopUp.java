package com.proter.juanjose.protermico;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PopUp extends Activity {

    Button closeBtn;
    TextView alertTv;
    Typeface fontHelBold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        closeBtn = findViewById(R.id.closepop_btn);
        alertTv = findViewById(R.id.alert_tv);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height));

        fontHelBold = Typeface.createFromAsset(getAssets(), "fonts/HelveticaLTStd-Bold.otf");

        alertTv.setTypeface(fontHelBold);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
