package com.example.mastermind;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;


public class Keypad {
    myCallback myCallback = null;
    private Button keyBackButton;


    public Keypad(){
//        keyBackButton = findViewById(R.id.button_main_backspace);
        keyBackButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                keyBackButton.setText("nyo");
            }
        });
    }

}
