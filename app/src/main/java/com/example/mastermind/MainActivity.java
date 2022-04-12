package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private NumberPicker picker1, picker2, picker3, picker4;
    private String[] pickerVals;
    private Button tryButton;
    private TextView textDisplay, attemptsDisplay, textRecord;
    private RequestQueue queue;
    private int[] secretCode = {1, 2, 3, 4};
    private String responseString, result;
    StringBuilder recordedAttempts = new StringBuilder(500);
    int attempts = 10, correctDigits = 0, correctPositions = 0;
    private boolean[] secretNums = new boolean[8];
    LinearLayout record;
    ScrollView scrollingRecord;

    String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        initializeGame();

        interfaceInit();
    }

    void initializeGame(){
        attempts = 10;
        for(int i = 0; i < secretNums.length; i++) {
            secretNums[i] = false;
        }
        generateSecretCode();
        for(int i = 0; i < 4; i++){
            secretNums[secretCode[i]] = true;
        }
        recordedAttempts.setLength(0);


    }

    private void generateSecretCode(){
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseString = response.substring(0, 8);
                        responseString = responseString.replaceAll("[^0-7]", "");
                        for(int i = 0; i < responseString.length(); i++){
                            secretCode[i] = Character.getNumericValue((responseString.charAt(i)));
                        }
                        textDisplay.setText("Game is ready to go!");
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                textDisplay.setText("Something went wrong with code generation :(");
            }
        });
        queue.add(stringRequest);
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
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);
        attemptsDisplay.setText("Attempts Remaining: " + attempts);
//        record = findViewById(R.id.layout_main_record);


        tryButton = findViewById(R.id.button_main_clicker);
        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int userInput[] = {picker1.getValue(), picker2.getValue(), picker3.getValue(), picker4.getValue()};

                correctDigits = 0;
                correctPositions = 0;
                //check code
                if(attempts > 0){
                    for(int i = 0; i < 4; i++){
                        if(userInput[i] == secretCode[i]){
                            correctPositions+=1;
                        }
                        if(secretNums[userInput[i]]){
                            correctDigits+=1;
                        }
                    }
                    if(correctPositions == 4) {
                        textDisplay.setText("You won! pls program reset");
                    } else if(correctPositions > 0){
                        textDisplay.setText("Player guessed a correct number and position. Try again");
                        result = "one or more correct nums & pos";
                    } else if(correctDigits > 0) {
                        textDisplay.setText("Player guessed a correct number. Try again");
                        result = "one or more correct nums";
                    } else {
                        textDisplay.setText("No correct guesses. Try again");
                        result = "no correct nums";

                    }
                    attempts-=1;
                    attemptsDisplay.setText("Attempts Remaining: " + attempts);
                    recordedAttempts.insert(0,"" + userInput[0] + userInput[1] + userInput[2] + userInput[3] + " "+ result +"\n");
                    textRecord.setText(recordedAttempts);
                }

                if(attempts == 0){
                    textDisplay.setText("Gameover, secret code was " + secretCode[0]+ secretCode[1]+ secretCode[2]+ secretCode[3]+". pls reset game");
                    initializeGame();
                };

            }});

    }
}