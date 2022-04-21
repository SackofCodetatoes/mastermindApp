package com.example.mastermind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button tryButton, normalButton, hardButton, restartButton, keyBackButton;
    private Button[] keypadButtons = new Button[8];

    HorizontalScrollView scrollField;
    ScrollView attemptsRecord;
    private Button menuNormalButton, menuHardButton, menuStartButton;
    private TextView textDisplay, attemptsDisplay, textRecord, menuText;
    NumberPicker[] numberPickers = new NumberPicker[4];
    private Group difficultyButtonsGroup, numberPickersGroup;
    int[] userInput, inputResult;
    int selectedDifficulty, numOfNums = 4, currentIndex = 0, totalPlays = 0;
    MasterMind gameInstance;
    View numberPickerBorder;
    SharedPreferences saveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        contentViewSwitcher(0);
        saveData = getSharedPreferences("completedGames", Context.MODE_PRIVATE);
        totalPlays = saveData.getInt("completedGames", 0);

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
                currentIndex = 0;

                break;
            case 2:
                setContentView(R.layout.dynamic_testing);
                //init text and buttons
                break;
        }
    }
    void updateView(){
        colorUI(inputResult);

        if(gameInstance.gameOver){
            totalPlays+=1;
            Log.d("total plays: ", String.valueOf(totalPlays));;
            SharedPreferences.Editor editor = saveData.edit();
            editor.putInt("completedGames", totalPlays);
            editor.apply();

            gameoverPopup(findViewById(R.id.text_main_attempts));
            if(gameInstance.correctPositions == 4){
                textDisplay.setText("You won! Play again?");
            }
            else {
                textDisplay.setText("Gameover. Secret code was " + gameInstance.revealCode());
            }
            tryButton.setVisibility(View.INVISIBLE);
            restartButton.setVisibility(View.VISIBLE);
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

    void colorKeypad(HashMap<Integer, String> results){
        Integer keyNum;
        for(Map.Entry m : results.entrySet()){
            keyNum = (int) m.getKey();
            if (m.getValue() == "wrong") {
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(23, 23, 23));
            } else if(m.getValue() == "found"){
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(209, 180, 48));
            } else if(m.getValue() == "correct"){
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(71, 201, 132));
            }
        }
    }

    void colorUI(int[] inputResult){
        for(int i = 0; i < numberPickers.length; i++) {
            numberPickers[i].setBackgroundColor(Color.TRANSPARENT);
        }
        numberPickersGroup.setBackgroundResource(0);

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
                colorKeypad(gameInstance.guessResults(userInput));
                break;

            case 1:
                if(gameInstance.correctPositions > 0){
                    numberPickerBorder.setBackgroundResource(R.drawable.correct_position);
                }
                else if(gameInstance.correctDigits > 0){
                    numberPickerBorder.setBackgroundResource(R.drawable.correct_digit);
                }
                break;
        }
    }

    void gameoverPopup(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_constraint, null);
        String manualResultCheck = "Game Over";
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; //dismiss popup on clicking outside according to notes
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        TextView textEndResult = (TextView) popupWindow.getContentView().findViewById(R.id.text_popup_result);
        TextView textStats = (TextView) popupWindow.getContentView().findViewById(R.id.text_popup_stats);
        if(gameInstance.correctPositions == 4){
            textEndResult.setText("Good Job!");
        }
        else {
            textEndResult.setText("Better Luck Next Time");
        }
        textStats.setText(String.valueOf(totalPlays));
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
                contentViewSwitcher(1);
            }
        });
    }

    void initGameLayoutButtons(){
        int[] keypadIDS = {R.id.button_main_0, R.id.button_main_1, R.id.button_main_2,R.id.button_main_3, R.id.button_main_4, R.id.button_main_5,
                R.id.button_main_6, R.id.button_main_7};
        tryButton = findViewById(R.id.button_main_clicker);
        restartButton = findViewById(R.id.button_main_restart);
        normalButton = findViewById(R.id.button_main_easy);
        hardButton = findViewById(R.id.button_main_hard);

        difficultyButtonsGroup = findViewById(R.id.difficulty_main_group);

        tryButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View v){
                userInput = new int[numOfNums];
                for(int i = 0; i < userInput.length; i++){
                    userInput[i] = numberPickers[i].getValue();
                }
                inputResult = gameInstance.checkCode(userInput);
                currentIndex = 0;
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
        for(int i = 0; i < keypadIDS.length; i++){
            keypadButtons[i] = findViewById(keypadIDS[i]);
            setKeypadFunction(keypadButtons[i], i);
        }
        keyBackButton = findViewById(R.id.button_main_backspace);
        keyBackButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                numberPickers[currentIndex].setValue(0);
                if(currentIndex > 0){
                    currentIndex -= 1;
                }
                changeFocus(currentIndex+1, currentIndex);
            }
        });
    }
    void changeFocus(int prev, int current){

        numberPickers[prev].setBackgroundResource(0);
        numberPickers[current].setBackgroundResource(R.drawable.focus_border);
    }
    void removeFocus(){
        for(int i = 0; i < numberPickers.length; i++){
            numberPickers[i].setBackgroundResource(0);
        }
    }
    //create keyboard function to apply to each keypad button
    void setKeypadFunction(Button setButton, int value){
        setButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(currentIndex < numOfNums){
                    numberPickers[currentIndex].setValue(value);
                    currentIndex+=1;
                }
                if(currentIndex >= numOfNums){
                    currentIndex = numOfNums - 1;
                }
                changeFocus(currentIndex - 1, currentIndex);
            }
        });
    }

    void initGameLayoutInterface(){
        String[] pickerVals = {"0", "1", "2", "3", "4", "5", "6", "7"};
        int[] pickerIDs = {R.id.numberpicker_main_picker0, R.id.numberpicker_main_picker1, R.id.numberpicker_main_picker2, R.id.numberpicker_main_picker3};
        numberPickersGroup = findViewById(R.id.numberpickers_main_group);

        for(int i = 0; i < pickerIDs.length; i++){
            numberPickers[i] = findViewById(pickerIDs[i]);
            numberPickers[i].setMaxValue(7);
            numberPickers[i].setMinValue(0);
            numberPickers[i].setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            final int setIndex = i;
            if(i < pickerIDs.length){
                numberPickers[i].setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
                    @Override
                    public  void onValueChange(NumberPicker np, int before, int after){
                        numberPickers[setIndex].setBackgroundColor(Color.TRANSPARENT);
                        currentIndex = setIndex + 1;
                        if(currentIndex >= numberPickers.length){
                            currentIndex = numberPickers.length - 1;
                        }
//                        changeFocus();
                        removeFocus();
                    }
                });
                numberPickers[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        numberPickers[setIndex].setBackgroundColor(Color.TRANSPARENT);
                        currentIndex = setIndex;
//                        changeFocus();
                        removeFocus();
                        return false;
                    }
                });
            }
        }

        attemptsRecord = findViewById(R.id.scrollview_main_record);
        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Guess a 4 digit combination"));
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);
        numberPickerBorder = findViewById(R.id.numberpicker_view_border);
    }
}