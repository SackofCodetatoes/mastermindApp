package com.example.mastermind;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button tryButton, normalButton, mediumButton, hardButton, restartButton;
    private Button menuNormalButton, menuHardButton, menuStartButton;
    private TextView textDisplay, attemptsDisplay, textRecord;
    NumberPicker[] numberPickers = new NumberPicker[4];
    StringBuilder recordedAttempts = new StringBuilder(500);
    private Group difficultyButtonsGroup, numberPickersGroup;
    int[] userInput, inputResult;
    int selectedDifficulty, numOfNums = 4;//refactor later
    MasterMind gameInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        contentViewSwitcher(0);
        ServerCallback enableButtonOnSuccess = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                textDisplay.setText("Game is Ready!");
                tryButton.setEnabled(true);
            }

            @Override
            public void onFailure(String response) {
                //do these to allow retry
                textDisplay.setText("Code could not be generated");
            }
        };

        gameInstance = new MasterMind(this, enableButtonOnSuccess);
    }

    void contentViewSwitcher(int viewTarget){
        switch(viewTarget){
            case 0:
                setContentView(R.layout.activity_menu);
                initMenuLayoutButtons();
                break;
            case 1:
                setContentView(R.layout.activity_main);
                initGameLayoutInterface();
                initGameLayoutButtons();
                toggleButtons(0);
                tryButton.setVisibility(View.VISIBLE);
                gameInstance.initialize(selectedDifficulty, numOfNums);

                break;
            case 2:
                setContentView(R.layout.activity_gameover);
                //init text and buttons
                break;
        }
    }
    void updateView(){
        String result = new String("the impending result is this");

        if(gameInstance.gameOver){
            //1 - gameover, do gameover popup and options
            if(gameInstance.correctPositions == 4){
                textDisplay.setText("You won! Play again?");
            }
            else {
                textDisplay.setText("Gameover. Secret code was " + gameInstance.revealCode());
            }
            tryButton.setVisibility(View.INVISIBLE);
            restartButton.setVisibility(View.VISIBLE);
            //change pop up modal with semi transparent white background with game state, stats, and option buttons
            //toggleButtons(1); should be handled on main activity
        }
        else{            //check result, need to print simpler method of hints?
            if(gameInstance.correctPositions > 0){
                textDisplay.setText("Player guessed a correct number and position. Try again");
                result = " correct #(s) in position";
            } else if(gameInstance.correctDigits > 0) {
                textDisplay.setText("Player guessed a correct number. Try again");
                result = " correct #(s)";
            } else {
                textDisplay.setText("No correct guesses. Try again");
                result = " no correct #(s)";
            }
            colorNumberPickers(inputResult);
        }
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);
        recordedAttempts.append("" + userInput[0] + userInput[1] + userInput[2] + userInput[3] + " "+ result +"\n");
        textRecord.setText((recordedAttempts));
    }

    void toggleButtons(int onOff){
        if(onOff == 0){
            difficultyButtonsGroup.setVisibility(View.INVISIBLE);
            difficultyButtonsGroup.setEnabled(false);
            tryButton.setVisibility(View.VISIBLE);
        }
        else if(onOff == 1){
            difficultyButtonsGroup.setVisibility(View.VISIBLE);
            difficultyButtonsGroup.setEnabled(true);
            tryButton.setVisibility(View.INVISIBLE);
        }
    }

    void colorNumberPickers(int[] inputResult){
        for(int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setBackgroundColor(Color.TRANSPARENT);
        }
        Log.d("Sanity check on inputResult ", Arrays.toString(inputResult));

        switch(selectedDifficulty){
            case 0:
                for(int i = 0; i < inputResult.length; i++){
                    switch(inputResult[i]){
                        case 0:
                            break;
                        case 1:
                            numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
                            break;
                        case 2:
                            numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
                            break;
                    }
                }
//                for(int i = 0; i < correctInputs.length; i++) {
//                    if (correctInputs[i] == 1) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
//                        //correctMultiples[userInput[i]] -= 1;
//                    }
//                }
//                for(int i = 0; i < correctInputs.length; i++){
//                    if(correctMultiples[userInput[i]] > 0 && correctInputs[i] != 1){
//                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
//                        correctMultiples[userInput[i]] -= 1;
//                    }
//                }
                break;
            case 1:
                //make a better ui to indicate difficult hints
                if(gameInstance.correctPositions > 0){
                    //color whole wheel in green
                    for(int i = 0; i < numberPickers.length; i++) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
                        numberPickers[i].setBackgroundColor(Color.rgb(45, 198, 207));
                    }
                }
                else if(gameInstance.correctDigits > 0){
                    //color whole wheel in orange209, 180, 48
                    for(int i = 0; i < numberPickers.length; i++) {
                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
                    }
                }
                break;
        }
    }

    void initMenuLayoutButtons(){
        menuStartButton = findViewById(R.id.button_menu_start);
        menuNormalButton = findViewById(R.id.button_menu_normal);
        menuHardButton = findViewById(R.id.button_menu_hard);

        menuNormalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 0;
                menuNormalButton.setBackgroundColor(Color.BLUE);
                menuHardButton.setBackgroundColor(Color.GRAY);
                menuStartButton.setEnabled(true);
            }
        });
        menuHardButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 1;
                menuHardButton.setBackgroundColor(Color.BLUE);
                menuNormalButton.setBackgroundColor(Color.GRAY);
                menuStartButton.setEnabled(true);
            }
        });
        menuStartButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //disable main group and enable the start screen with difficulty
                Group menuGroup = findViewById(R.id.group_menu_screen);
                contentViewSwitcher(1); //set view to main activity
            }
        });
    }

    void initGameLayoutButtons(){
        tryButton = findViewById(R.id.button_main_clicker);
        restartButton = findViewById(R.id.button_main_restart);

        normalButton = findViewById(R.id.button_main_easy);
        hardButton = findViewById(R.id.button_main_hard);
        difficultyButtonsGroup = findViewById(R.id.difficulty_main_group);

        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //check clickers for input and do game check and respond
                userInput = new int[numOfNums];
                for(int i = 0; i < userInput.length; i++){
                    userInput[i] = numberPickers[i].getValue();
                }
                inputResult = gameInstance.checkCode(userInput);
                updateView();
                //run method to update gamestate on screen, looks int gameInstance object to print and stuff
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                contentViewSwitcher(0);
            }
        });


        normalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 0;
                toggleButtons(0);
                gameInstance.initialize(selectedDifficulty, numOfNums);

            }
        });
        hardButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 1;
                toggleButtons(0);
                gameInstance.initialize(selectedDifficulty, numOfNums);
            }
        });
    }

    void initGameLayoutInterface(){
        String[] pickerVals = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
        int[] pickerIDs = {R.id.numberpicker_main_picker, R.id.numberpicker_main_picker2, R.id.numberpicker_main_picker3, R.id.numberpicker_main_picker4};
        numberPickersGroup = findViewById(R.id.numberpickers_main_group);

        for(int i = 0; i < pickerIDs.length; i++){
            numberPickers[i] = findViewById(pickerIDs[i]);
            numberPickers[i].setMaxValue(7);
            numberPickers[i].setMinValue(0);
            numberPickers[i].setDisplayedValues(pickerVals);
        }

        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Guess a 4 digit combination"));
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);
    }
}