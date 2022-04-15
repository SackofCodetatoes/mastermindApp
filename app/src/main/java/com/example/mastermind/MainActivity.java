package com.example.mastermind;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button tryButton, normalButton, mediumButton, hardButton;
    private Button menuNormalButton, menuHardButton, menuStartButton;
    private TextView textDisplay, attemptsDisplay, textRecord;
    NumberPicker[] numberPickers = new NumberPicker[4];
    private Group difficultyButtonsGroup, numberPickersGroup;
    int selectedDifficulty, numOfNums = 4;//refactor later
    MasterMind gameInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInstance = new MasterMind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        contentViewSwitcher(0);

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
                gameInstance.initialize(selectedDifficulty, numOfNums);
                break;
            case 2:
                setContentView(R.layout.activity_gameover);
                //init text and buttons
                break;
        }
    }
    void updateView(){
        //look at game instance object and update screen appropriately, 3 different results,
        //1 - gameover, do gameover popup and options
        //2 - game is ongoing, udpate gamelayout and do colorize
        //
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

    void colorNumberPickers(int[] correctInputs){
//        // color state replaced with difficulty, w
//        //clear all color before coloring
////        int [] correctMultiples = secretNums.clone();
//        for(int i = 0; i < numberPickers.length; i++) {
//            numberPickers[i].setBackgroundColor(Color.TRANSPARENT);
//        }
//        //color based on result from game isntance object
//        //color number pickers based on difficulty
//        switch(difficulty){
//            case 0:
//                //medium - color correct nums
//                //color wheel based on userInputResponse;
//                for(int i = 0; i < correctInputs.length; i++) {
//                    if (correctInputs[i] == 1) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
//                        correctMultiples[userInput[i]] -= 1;
//                    }
//                }
//                for(int i = 0; i < correctInputs.length; i++){
//                    if(correctMultiples[userInput[i]] > 0 && correctInputs[i] != 1){
//                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
//                        correctMultiples[userInput[i]] -= 1;
//                        //do doc strings to be more readability, there are built in functions Java docstring style copy that //comment only if confusing or esoteric code smell
//                    }
//                }
//                break;
//            case 1:
//                //hard - color whole combination to indicate response
//                if(correctPositions > 0){
//                    //color whole wheel in green
//                    for(int i = 0; i < numberPickers.length; i++) {
////                        numberPickers[i].setBackgroundColor(Color.rgb(71, 201, 132));
//                        numberPickers[i].setBackgroundColor(Color.rgb(45, 198, 207));
//                    }
//                }
//                else if(correctDigits > 0){
//                    //color whole wheel in orange209, 180, 48
//                    for(int i = 0; i < numberPickers.length; i++) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
//                    }
//                }
//                break;
//        }
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
        normalButton = findViewById(R.id.button_main_easy);
        hardButton = findViewById(R.id.button_main_hard);
        difficultyButtonsGroup = findViewById(R.id.difficulty_main_group);

        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //check clickers for input and do game check and respond
                int[] userInput = new int[numOfNums];
                for(int i = 0; i < userInput.length; i++){
                    userInput[i] = numberPickers[i].getValue();
                }
                gameInstance.checkCode(userInput);
                //run method to update gamestate on screen, looks int gameInstance object to print and stuff
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

    }
}