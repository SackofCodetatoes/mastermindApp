package com.example.mastermind;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button tryButton, normalButton, mediumButton, hardButton, restartButton, dynamicAddButton, menuDynamicButton;
    HorizontalScrollView scrollField;
    ScrollView attemptsRecord;
    private Button menuNormalButton, menuHardButton, menuStartButton;
    private TextView textDisplay, attemptsDisplay, textRecord;
    NumberPicker[] numberPickers = new NumberPicker[4];
//    StringBuilder previousGuesses = new StringBuilder(500);
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
                //todo: implement method to enable a retry init button
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
                gameInstance.initialize(numOfNums);

                break;
            case 2:
                setContentView(R.layout.dynamic_testing);
                initDynamicLayout();
                //init text and buttons
                break;
        }
    }
    void updateView(){
        colorNumberPickers(inputResult);

        if(gameInstance.gameOver){
            //todo: change end screen to pop up modal with semi transparent white background with game state, stats, and option buttons
            victoryPopup(findViewById(R.id.text_main_attempts));
            if(gameInstance.correctPositions == 4){
                textDisplay.setText("You won! Play again?");
            }
            else {
                textDisplay.setText("Gameover. Secret code was " + gameInstance.revealCode());
            }
            tryButton.setVisibility(View.INVISIBLE);
            restartButton.setVisibility(View.VISIBLE);
//            previousGuesses.delete(0, previousGuesses.length());
            //toggleButtons(1); should be handled on main activity
        }
        else{
            if(gameInstance.correctPositions > 0){
                textDisplay.setText("Player guessed a correct number and position. Try again");
            } else if(gameInstance.correctDigits > 0) {
                textDisplay.setText("Player guessed a correct number. Try again");
            } else {
                textDisplay.setText("No correct guesses. Try again");
            }
        }
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);

        textRecord.setText((gameInstance.previousGuesses));
        attemptsRecord.fullScroll(View.FOCUS_DOWN);
    }

    void toggleButtons(int onOff){
        //used in old implementation to show buttons clicker view, not currently used
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
                break;

//            case 1:
//                //make a better ui to indicate difficult hints
//                if(gameInstance.correctPositions > 0){
//                    //color aqua
//                    for(int i = 0; i < numberPickers.length; i++) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(45, 198, 207));
//                    }
//                }
//                else if(gameInstance.correctDigits > 0){
//                    //color whole wheel in orange209, 180, 48
//                    for(int i = 0; i < numberPickers.length; i++) {
//                        numberPickers[i].setBackgroundColor(Color.rgb(209, 180, 48));
//                    }
//                }
//                break;
        }
    }
    void dynamicAdd(int howMany) {
        //add a button to screen
//        NumberPicker picker = new NumberPicker(this);
//        String[] values = {"one", "two", "three"};
//        picker.setMaxValue(values.length);
//        picker.setMinValue(0);
//        picker.setDisplayedValues(values);

//        numberPickers[i] = findViewById(pickerIDs[i]);
//        numberPickers[i].setMaxValue(7);
//        numberPickers[i].setMinValue(0);
//        numberPickers[i].setDisplayedValues(pickerVals);
        Button btn = new Button(this);
        btn.setText("Submit");
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.dynamic_layout);
        LinearLayout.LayoutParams buttonlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(btn, buttonlayout);
    }

    void victoryPopup(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        String manualResultCheck = "Game Over";
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; //dismiss popout on clicking outside according to notes
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        //show popup window which view passed doesnt matter, just a token
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

//        TextView resultMessage = findViewById(R.id.text_popup_result);
        if(gameInstance.correctPositions == 4){
            manualResultCheck = "You Win!";
        }
//        resultMessage.setText(manualResultCheck);

        //dusnuss popup when tocuhed, might remove
//        popupView.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public  boolean onTouch(View v, MotionEvent event){
//                popupWindow.dismiss();
//                return true;
//            }
//        });


    }

    void initDynamicLayout(){
        scrollField = findViewById(R.id.scroll_field);
        dynamicAddButton = findViewById(R.id.button_menu_dynamic);
//        dynamicAdd(2);
        dynamicAddButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dynamicAdd(2);
            }
        });
    }
    void initMenuLayoutButtons(){
        menuStartButton = findViewById(R.id.button_menu_start);
        menuNormalButton = findViewById(R.id.button_menu_normal);
        menuHardButton = findViewById(R.id.button_menu_hard);
        menuDynamicButton = findViewById(R.id.button_menu_dynamic);

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
                contentViewSwitcher(1);
            }
        });
        menuDynamicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                contentViewSwitcher(2);
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
                userInput = new int[numOfNums];
                for(int i = 0; i < userInput.length; i++){
                    userInput[i] = numberPickers[i].getValue();
                }
                inputResult = gameInstance.checkCode(userInput);
                updateView();
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
                gameInstance.initialize(numOfNums);

            }
        });
        hardButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 1;
                toggleButtons(0);
                gameInstance.initialize(numOfNums);
            }
        });
    }

    void initGameLayoutInterface(){
        String[] pickerVals = {"0", "1", "2", "3", "4", "5", "6", "7"};
        int[] pickerIDs = {R.id.numberpicker_main_picker, R.id.numberpicker_main_picker2, R.id.numberpicker_main_picker3, R.id.numberpicker_main_picker4};
        numberPickersGroup = findViewById(R.id.numberpickers_main_group);

        for(int i = 0; i < pickerIDs.length; i++){
            numberPickers[i] = findViewById(pickerIDs[i]);
            numberPickers[i].setMaxValue(7);
            numberPickers[i].setMinValue(0);
//            numberPickers[i].setDisplayedValues(pickerVals);
        }


        attemptsRecord = findViewById(R.id.scrollview_main_record);
        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Guess a 4 digit combination"));
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);
    }
}