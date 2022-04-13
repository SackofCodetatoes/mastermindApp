package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private Button tryButton, easyButton, mediumButton, hardButton;
    private TextView textDisplay, attemptsDisplay, textRecord;
    private RequestQueue queue;
    private int[] secretCode = {1, 2, 3, 4}, userInput = {0, 0 ,0 ,0}, secretNums = {0, 0, 0, 0, 0, 0, 0, 0}, correctInputs = {0, 0, 0, 0};
    private String responseString, result;
    StringBuilder recordedAttempts = new StringBuilder(500);
    int attempts = 10, correctDigits = 0, correctPositions = 0, difficulty = 0;
    private boolean gameOver = false;
//    private boolean[] secretNums = new boolean[8];
    NumberPicker[] numberPickers = new NumberPicker[4];

    String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        interfaceInit();
        initButtons();
//        initializeGame();



    }
    void toggleButtons(int onOff){
        if(onOff == 0){
            easyButton.setVisibility(View.INVISIBLE);
            mediumButton.setVisibility(View.INVISIBLE);
            hardButton.setVisibility(View.INVISIBLE);
            easyButton.setEnabled(false);
            mediumButton.setEnabled(false);
            hardButton.setEnabled(false);
            tryButton.setVisibility(View.VISIBLE);
        }
        else if(onOff == 1){
            easyButton.setVisibility(View.VISIBLE);
            mediumButton.setVisibility(View.VISIBLE);
            hardButton.setVisibility(View.VISIBLE);
            easyButton.setEnabled(true);
            mediumButton.setEnabled(true);
            hardButton.setEnabled(true);
            tryButton.setVisibility(View.INVISIBLE);
        }
    }
    void initButtons(){
        tryButton = findViewById(R.id.button_main_clicker);
        easyButton = findViewById(R.id.button_main_easy);
        mediumButton = findViewById(R.id.button_main_medium);
        hardButton = findViewById(R.id.button_main_hard);

        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                checkCode();
            }
        });
        easyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //set difficulty + hide buttons
                difficulty = 0;
                toggleButtons(0);
                initializeGame();
            }
        });
        mediumButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //set difficulty + hide buttons
                difficulty = 1;
                toggleButtons(0);
                initializeGame();
            }
        });
        hardButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //set difficulty + hide buttons
                difficulty = 2;
                toggleButtons(0);
                initializeGame();
            }
        });


    }
    void initializeGame(){
        //change buttons to difficulty setting
        //on click and setting of difficulty, the function will hide difficulty buttons and show the try code button
        tryButton.setEnabled(false);
        textDisplay.setText("Generating secret code...");
        gameOver = false;
        attempts = 10;
        for(int i = 0; i < secretNums.length; i++) {
            secretNums[i] = 0;
        }
        generateSecretCode();

        //reset pickers
        for(int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setValue(0);
            numberPickers[i].setBackgroundColor(Color.TRANSPARENT);
        }
        recordedAttempts.setLength(0);
        textRecord.setText("");
        attemptsDisplay.setText("Attempts Remaining: "+ attempts);
        tryButton.setText("Try Code");

    }

    void colorNumberPickers(int[] correctInputs){
        // color state replaced with difficulty, w
        //clear all color before coloring
        int [] correctMultiples = secretNums.clone();
        for(int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setBackgroundColor(Color.TRANSPARENT);
        }


        //color number pickers based on difficulty
        switch(difficulty){
            case 0:
                //if easy mode, color all userinput responses accordingly
                //color wheel based on userResponse with multis
                for(int i = 0; i < correctInputs.length; i++) {
                    switch(correctInputs[i]){
                        case 1:
                            numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
                            break;
                        case 2:
                            numberPickers[i].setBackgroundColor(Color.rgb(45, 198, 207));
                            break;
                        case 3:
                            numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
                            break;
                    }
                }
                break;
            case 1:
                //medium - color correct nums
                //color wheel based on userInputResponse;
                for(int i = 0; i < correctInputs.length; i++) {
                    switch(correctInputs[i]){
                        case 1:
                            numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
                            break;
                        case 2: case 3:
                            if(correctMultiples[userInput[i]] > 0){
                                numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
                                correctMultiples[userInput[i]] -= 1;
                            }
                            break;
                    }

                }
                break;
            case 2:
                //color whole combination to indicate response
                if(correctPositions > 0){
                    //color whole wheel in green
                    for(int i = 0; i < numberPickers.length; i++) {
                        numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
                    }
                }
                else if(correctDigits > 0){
                    //color whole wheel in orange209, 180, 48
                    for(int i = 0; i < numberPickers.length; i++) {
                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
                    }
                }
                break;
        }
    }


    void checkCode(){
        if(gameOver){
            //Reset game variables and UI
            toggleButtons(1);
        }
        else {
            //can place pickers in an array to build on extension
            correctPositions = 0;  //reset counter values
            correctDigits = 0;
            for(int i = 0; i < userInput.length; i++){
                correctInputs[i] = 0;
            }

            //setup array as userinput from number pickers
            for(int i = 0; i < userInput.length; i++){
                userInput[i] = numberPickers[i].getValue();
            }

            //check code
            for(int i = 0; i < 4; i++){
                if(userInput[i] == secretCode[i]){
                    correctPositions+=1;
                    correctInputs[i] = 1;
                }
                //9 = correct pos, > 0 will reduce
                else if(secretNums[userInput[i]] > 0){
                    correctDigits+=1;
                    if(secretNums[userInput[i]] > 1){
                        correctInputs[i] = 2;
                    }
                    else {
                        correctInputs[i] = 3;
                    }
                }
            }
            //color nums,
            colorNumberPickers(correctInputs);
            //result from checking code, can modify values to variables for extension
            if(correctPositions == 4) {
                gameOver = true;
                textDisplay.setText("You won! pls program reset");
                //do stuff on win and set game to gameover state
                //run congradulations effect
                //play sound?
            } else if(correctPositions > 0){
                textDisplay.setText("Player guessed a correct number and position. Try again");
                result = " correct num & pos";
                //color numberpickers based on correctness
            } else if(correctDigits > 0) {
                textDisplay.setText("Player guessed a correct number. Try again");
                result = " correct num";
            } else {
                textDisplay.setText("No correct guesses. Try again");
                result = " no correct nums";

            }

            attempts-=1;
            attemptsDisplay.setText("Attempts Remaining: " + attempts);
            //print simpler method of hints?
            recordedAttempts.insert(0,"" + userInput[0] + userInput[1] + userInput[2] + userInput[3] + " "+ result +"\n");
            textRecord.setText(recordedAttempts);

            if(attempts <= 0){
                textDisplay.setText("Gameover. Secret code was " + secretCode[0]+ secretCode[1]+ secretCode[2]+ secretCode[3]+".");
                gameOver = true;
                tryButton.setText("Restart Game");
            };
        }
        //debug
        Log.d("userInput:", "" + userInput[0] + userInput[1] + userInput[2] + userInput[3]);
        Log.d("secret code", "" + secretCode[0] + secretCode[1] + secretCode[2] + secretCode[3]);
        Log.d("secret nums: ", ""+ secretNums[0] + " " + secretNums[1] + " "+ secretNums[2] + " "+ secretNums[3] + " "+ secretNums[4] + " "+ secretNums[5] + " "+ secretNums[6] + " "+ secretNums[7] + " ");
    }


    void interfaceInit(){
        //assigns interface values
        pickerVals = new String[] {"0", "1", "2", "3", "4", "5", "6", "7"};
        picker1 = findViewById(R.id.numberpicker_main_picker);
        picker2 = findViewById(R.id.numberpicker_main_picker2);
        picker3 = findViewById(R.id.numberpicker_main_picker3);
        picker4 = findViewById(R.id.numberpicker_main_picker4);
        numberPickers[0] = picker1;
        numberPickers[1] = picker2;
        numberPickers[2] = picker3;
        numberPickers[3] = picker4;

        for(int i = 0; i < numberPickers.length; i++){
            numberPickers[i].setMaxValue(7);
            numberPickers[i].setMinValue(0);
            numberPickers[i].setDisplayedValues(pickerVals);
        }

        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Guess a 4 digit combination"));
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);

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
                        for(int i = 0; i < 4; i++){
                            secretNums[secretCode[i]] += 1;
                        }
                        tryButton.setEnabled(true);
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                textDisplay.setText("Something went wrong with code generation :(");
            }
        });
        queue.add(stringRequest);
    }
}