package com.example.mastermind;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;


public class MasterMind {
    private int[] secretCode = {1, 2, 3, 4}, secretNums = {0, 0, 0, 0, 0, 0, 0, 0}, correctInputs = {0, 0, 0, 0};
    private String responseString, result;
    StringBuilder recordedAttempts = new StringBuilder(500);
    int attempts = 10, correctDigits = 0, correctPositions = 0, difficulty = 0;
    private boolean gameOver = false;
    Context mainContext;

    public MasterMind(Context fromWhere){
        mainContext = fromWhere;
    }
    void initialize(int selectedDifficulty){
        gameOver = false;
        attempts = 10;
        for(int i = 0; i < secretNums.length; i++) {
            secretNums[i] = 0;
        }
//        generateSecretCode();
    }


    void checkCode(int[] userInput){//take user input
        if(gameOver){
//            toggleButtons(1); should be handled on main activity
        }
        else {
            correctPositions = 0;
            correctDigits = 0;
            for(int i = 0; i < userInput.length; i++){
                correctInputs[i] = 0;
            }

            //setup array as userinput from number pickers
            for(int i = 0; i < userInput.length; i++){
//                userInput[i] = numberPickers[i].getValue();
            }

            //check code
            for(int i = 0; i < 4; i++){
                if(userInput[i] == secretCode[i]){
                    correctPositions+=1;
                    correctInputs[i] = 1;
                }
                //1 = correct pos, > 0 will reduce
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
//            colorNumberPickers(correctInputs);
            //result from checking code, can modify values to variables for extension
            if(correctPositions == 4) {
                gameOver = true;
//                textDisplay.setText("You won! Play again?");
//                tryButton.setText("Restart Game");
                //do stuff on win and set game to game over state
                //change pop up modal with semi transparent white background with game state, stats, and option buttons
            } else if(correctPositions > 0){
//                textDisplay.setText("Player guessed a correct number and position. Try again");
                result = " correct num & pos";
                //color number pickers based on correctness
            } else if(correctDigits > 0) {
//                textDisplay.setText("Player guessed a correct number. Try again");
                result = " correct num";
            } else {
//                textDisplay.setText("No correct guesses. Try again");
                result = " no correct nums";

            }

            attempts-=1;
//            attemptsDisplay.setText("Attempts Remaining: " + attempts);
            //print simpler method of hints?
            recordedAttempts.insert(0,"" + userInput[0] + userInput[1] + userInput[2] + userInput[3] + " "+ result +"\n");
//            textRecord.setText(recordedAttempts);

            if(attempts <= 0){
//                textDisplay.setText("Gameover. Secret code was " + secretCode[0]+ secretCode[1]+ secretCode[2]+ secretCode[3]+".");
                gameOver = true;
//                tryButton.setText("Restart Game");
            }
        }
        Log.d("difficulty set to", "" + difficulty);
        Log.d("secret code", "" + secretCode[0] + secretCode[1] + secretCode[2] + secretCode[3]);
    }


    private void generateSecretCode(){ //take out as own class
        String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";
        RequestQueue queue = Volley.newRequestQueue(mainContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "" + Arrays.toString(response.split("\n")));
//                        int[] secretCodeResponse = response.split("\n"); //implment this
                        responseString = response.substring(0, 8);
                        responseString = responseString.replaceAll("[^0-7]", "");//redundant, put
                        for(int i = 0; i < responseString.length(); i++){
                            secretCode[i] = Character.getNumericValue((responseString.charAt(i)));
                        }
                        for(int i = 0; i < 4; i++){
                            //{0,0,2,0,0,0,0}
                            secretNums[secretCode[i]] += 1;//bitmap overkill, can simplify to checking index , dictionary/ keyvalue par, keep index in mind and looking forwrd
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Http Request: ","Something went wrong with code generation :(");
            }
        });
        queue.add(stringRequest);
    }
}