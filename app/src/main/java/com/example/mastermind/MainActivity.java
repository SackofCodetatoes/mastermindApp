package com.example.mastermind;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button[] keypadButtons = new Button[8];
    private Button menuNormalButton, menuHardButton, menuStartButton, tryButton, restartButton, keyBackButton, menuResetStats;
    ScrollView attemptsRecord;
    private TextView textDisplay, attemptsDisplay, textRecord, menuText;
    NumberPicker[] numberPickers = new NumberPicker[4];
    private Group difficultyButtonsGroup, numberPickersGroup;
    int[] userInput, inputResult;
    int selectedDifficulty, numOfNums = 4, floor = 0, ceiling = 7, attemptsGoal = 10, currentIndex = 0, totalPlays = 0, wins = 0, titleClickCounter = 0;
    MasterMind gameInstance;
    View numberPickerBorder;
    SharedPreferences totalGamesPlayed, totalGamesWon;
    CharSequence toastText;
    Context context;
    Toast toast;

    /**
     * on create method for main activity. clears starting action bar, disables keyboard input, and initializes game elements
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        loadStats();
        gameInstance = new MasterMind(this, createCallbackMethods());
        changeContentView(0);
    }


    /**
     * Declares callback methods for Mastermind's http request result.
     * onSuccess: enables tryButton
     * onFailure: planned feature to enable a retry button to retry the http request
     * @return gameReadyCallback callback methods to be sent to Mastermind on instance declaration
     */
    ServerCallback createCallbackMethods(){
        ServerCallback gameReadyCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                textDisplay.setText("Game is Ready! Guess a 4 digit combination");
                tryButton.setEnabled(true);
            }
            @Override
            public void onFailure(String response) {
                //todo: implement method to enable a retry init button
                textDisplay.setText("Code could not be generated.");
            }
        };
        return gameReadyCallback;
    }

    /**
     * Changes current screen content between starting menu and game activity
     * @param viewTarget integer indicating which view to change to.
     *                   0 = Starting Menu, 1 = Game activity
     */
    void changeContentView(int viewTarget){
        switch(viewTarget){
            case 0:
                setContentView(R.layout.activity_menu);
                initMenuLayoutButtons();
                break;
            case 1:
                setContentView(R.layout.activity_main);
                initGameLayoutInterface();
                initGameLayoutButtons();
//                toggleButtons(0);
                tryButton.setVisibility(View.VISIBLE);
                gameInstance.initialize(numOfNums, floor, ceiling, attemptsGoal, selectedDifficulty);
                currentIndex = 0;
                break;
        }
    }

    /**
     * loadStats and saveStats called to load saved game values from sharedPreferences.
     * Currently records number of completed games and wins
     */
    void loadStats(){
        totalGamesPlayed = getSharedPreferences("completedGames", Context.MODE_PRIVATE);
        totalGamesWon = getSharedPreferences("wins", Context.MODE_PRIVATE);
        totalPlays = totalGamesPlayed.getInt("completedGames", 0);
        wins = totalGamesWon.getInt("wins", 0);
    }
    void saveStats(){
        SharedPreferences.Editor editor = totalGamesPlayed.edit();
        editor.putInt("completedGames", totalPlays);
        editor.putInt("wins", wins);
        editor.apply();
    }

    /**
     * Step function triggered by tryButton. Checks gameInstance state updates view elements accordingly.
     * Colors UI, checks if game is over, or tells player the result of their guess and updates UI elements
     */
    void updateView(){
        colorUI(inputResult);
        if(gameInstance.gameOver){
            totalPlays += 1;
            if(gameInstance.correctPositions == numOfNums){
                textDisplay.setText("You won! Play again?");
                wins+=1;
                Log.d("wins / winrate", " "+ wins + " / " + ((float)wins/(float)totalPlays) * 100);
            }
            else {
                textDisplay.setText("Secret code was " + gameInstance.revealCode());
            }
            tryButton.setVisibility(View.INVISIBLE);
            restartButton.setVisibility(View.VISIBLE);
            saveStats();
            gameOverPopup(findViewById(R.id.text_main_attempts));
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


    /**
     * colorKeypad takes in results and colors the keypad depending on the outcome. Called from colorUI
     * @param results HashMap object containing Integer key and string value detailing the guess result
     */
    void colorKeypad(HashMap<Integer, String> results){
        Integer keyNum;
        for(Map.Entry m : results.entrySet()){
            keyNum = (int) m.getKey();
            keypadButtons[keyNum].setTextColor(Color.WHITE);
            if (m.getValue() == "wrong") {
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(23, 23, 23));
            } else if(m.getValue() == "found"){
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(209, 180, 48));
            } else if(m.getValue() == "correct"){
                keypadButtons[keyNum].setBackgroundColor(Color.rgb(71, 201, 132));
            }
        }
    }

    /**
     * clears the UI of previous indicators and updates the response hints according set difficulty
     * selected difficulty 0: normal and 1: hard. Normal highlights correct numbers and numbers in the code, hard reveals less hint info
     * @param inputResult integer array with values between 0 - 2 indicating the how the numberpickers should be colored based off user guess and correct code
     *                    0: do nothing, 1: found, but incorrect position, 2: correct guess and position
     */
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
                else{
                    numberPickerBorder.setBackgroundResource(0);
                }
                break;
        }
    }

    /**
     * creates popup window using popupwindow constraints layout showing overall results and end game result
     * @param view  reference point on where the show the popup. since popup is centered on screen, view just needs to be an element from the starting activity
     */
    void gameOverPopup(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_constraint, null);
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
        int winRate = (int)(((float)wins / (float)totalPlays) * 100);
        textStats.setText("Games Played: " + totalPlays + " | Win Rate: " + winRate + "%");
    }

    /**
     * initialize declared buttons from the menu layout and sets click functionality
     */
    void initMenuLayoutButtons(){
        menuStartButton = findViewById(R.id.button_menu_start);
        menuNormalButton = findViewById(R.id.button_menu_normal);
        menuHardButton = findViewById(R.id.button_menu_hard);
        menuResetStats = findViewById(R.id.button_menu_reset);

        menuNormalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 0;
                menuNormalButton.setBackgroundColor(Color.parseColor("#47c984"));
                menuHardButton.setBackgroundColor(Color.GRAY);
                menuStartButton.setEnabled(true);
            }
        });
        menuHardButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                selectedDifficulty = 1;
                menuHardButton.setBackgroundColor(Color.parseColor("#EA5E45"));
                menuNormalButton.setBackgroundColor(Color.GRAY);
                menuStartButton.setEnabled(true);
            }
        });
        menuStartButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeContentView(1);
            }
        });
        menuResetStats.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                wins = 0;
                totalPlays = 0;
            }
        });
        menuText = findViewById(R.id.text_menu_title);
        menuText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(titleClickCounter == 25){
                    Log.d("Reset Stats", String.valueOf(titleClickCounter));
                    totalPlays = 0;
                    wins = 0;
                    saveStats();
                    titleClickCounter = 0;
                    context = getApplicationContext();
                    toastText = "Stats have been reset.";
                    toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(titleClickCounter == 15){
                    context = getApplicationContext();
                    toastText = "10 more clicks to reset stats!";
                    toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
                    toast.show();

                }
                titleClickCounter+=1;
            }
        });
        menuText.setSoundEffectsEnabled(false);
    }

    /**
     * initialize game layout buttons and sets click functionality
     * tryButton: sets userInput attribute based off values on numberpickers, checks with gameInstance's for feedback, and updatesView
     * restartButton: changes view to Start Menu
     * keyPadButtons: goes through keypadButtons Array, initializes each button and sets the function using setKeypad Method
     * keyBackButton: sets the backspace button functionality by changing currentIndex and changing the focus window
     */
    void initGameLayoutButtons(){
        int[] keypadIDS = {R.id.button_main_0, R.id.button_main_1, R.id.button_main_2,R.id.button_main_3, R.id.button_main_4, R.id.button_main_5,
                R.id.button_main_6, R.id.button_main_7};
        tryButton = findViewById(R.id.button_main_clicker);
        restartButton = findViewById(R.id.button_main_restart);

        difficultyButtonsGroup = findViewById(R.id.difficulty_main_group);

        tryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                userInput = new int[numberPickers.length];
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
                changeContentView(0);
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

    /**
     * updates current focused numberpicker by removing the highlight from previous and highlights the current
     * @param prev index indicating previously highlighted item
     * @param current index of what to highlight
     */
    void changeFocus(int prev, int current){
        numberPickers[prev].setBackgroundResource(0);
        numberPickers[current].setBackgroundResource(R.drawable.focus_border);
    }


    /**
     * clear all focused numberpickers to simulate change of user input
     */
    void clearFocus(){
        for(int i = 0; i < numberPickers.length; i++){
            numberPickers[i].setBackgroundResource(0);
        }
    }

    /**
     * called to set a keypad button functionality. onclick, button should set the current focused item to the button's value and move current focused item to the right
     * @param setButton button to set
     * @param value value to assign
     */
    void setKeypadFunction(Button setButton, int value){
        setButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(currentIndex < numberPickers.length){
                    numberPickers[currentIndex].setValue(value);
                    currentIndex+=1;
                }
                if(currentIndex >= numberPickers.length){
                    currentIndex = numberPickers.length - 1;
                }
                changeFocus(currentIndex - 1, currentIndex);
                numberPickerBorder.setBackgroundResource(0);

            }
        });
    }

    /**
     * initialize game layout elements, textView elements and numberpickers
     * pickerIds preset to indicate available pickers. (future feature is to dynamically add more for custom code length)
     * numberPickers[i].setOnValueChangeListener: on value change, change current index to next numberpicker for keypad press
     * numberPicker[i].setOnTouchListener: on touch, change current index to touched element to allow selective keypad edits
     */
    void initGameLayoutInterface(){
        String[] pickerVals = {"0", "1", "2", "3", "4", "5", "6", "7"};
        int[] pickerIDs = {R.id.numberpicker_main_picker0, R.id.numberpicker_main_picker1, R.id.numberpicker_main_picker2, R.id.numberpicker_main_picker3};
        numberPickersGroup = findViewById(R.id.numberpickers_main_group);

        for(int i = 0; i < pickerIDs.length; i++){
            numberPickers[i] = findViewById(pickerIDs[i]);
            numberPickers[i].setMaxValue(ceiling);
            numberPickers[i].setMinValue(floor);
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
                        clearFocus();
                        numberPickerBorder.setBackgroundResource(0);

                    }
                });
                numberPickers[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        numberPickers[setIndex].setBackgroundColor(Color.TRANSPARENT);
                        currentIndex = setIndex;
//                        changeFocus();
                        clearFocus();
                        numberPickerBorder.setBackgroundResource(0);

                        return false;
                    }
                });
            }
        }
        attemptsRecord = findViewById(R.id.scrollview_main_record);
        textDisplay = findViewById(R.id.text_main_display);
        textDisplay.setText(("Code is generating..."));
        textRecord = findViewById(R.id.text_main_record);
        attemptsDisplay = findViewById(R.id.text_main_attempts);
        attemptsDisplay.setText("Attempts Remaining: " + gameInstance.attemptsRemaining);
        numberPickerBorder = findViewById(R.id.numberpicker_view_border);

    }
}