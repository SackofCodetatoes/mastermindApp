package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private NumberPicker picker1, picker2, picker3, picker4;
    private String[] pickerVals;
    private Button tryButton;
    private TextView textDisplay;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interfaceInit();

    }


    void interfaceInit(){
        pickerVals = new String[] {"0", "1", "2", "3", "4", "5", "6", "7"};
        picker1 = findViewById(R.id.numberpicker_main_picker);
        picker1.setMaxValue(7);
        picker1.setMinValue(0);
        picker2 = findViewById(R.id.numberpicker_main_picker2);
        picker2.setMaxValue(7);
        picker2.setMinValue(0);
        picker3 = findViewById(R.id.numberpicker_main_picker3);
        picker3.setMaxValue(7);
        picker3.setMinValue(0);
        picker4 = findViewById(R.id.numberpicker_main_picker4);
        picker4.setMaxValue(7);
        picker4.setMinValue(0);
        picker1.setDisplayedValues(pickerVals);
        picker2.setDisplayedValues(pickerVals);
        picker3.setDisplayedValues(pickerVals);
        picker4.setDisplayedValues(pickerVals);

        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Hello World!"));


        tryButton = findViewById(R.id.button_main_clicker);
        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int valuePicker1 = picker1.getValue(), valuePicker2 = picker2.getValue(), valuePicker3 = picker3.getValue(), valuePicker4 = picker4.getValue();
                String pickerCode = "" + valuePicker1 + valuePicker2 + valuePicker3 + valuePicker4;
                Log.d("code is :", pickerCode);
                textDisplay.setText((pickerCode));

            }});

    }
}