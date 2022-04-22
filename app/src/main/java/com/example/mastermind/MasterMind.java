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
import java.util.HashMap;


public class MasterMind {
    private int[] secretCode, secretNums = {0, 0, 0, 0, 0, 0, 0, 0};
    private String responseString;
    StringBuilder previousGuesses;
    int attemptsRemaining, correctDigits = 0, correctPositions = 0;
    boolean gameOver = false;
    Context mainContext;
    ServerCallback signalOnResponse;
    HashMap<Integer, String> checkedNums;




    public MasterMind(Context fromWhere, ServerCallback callOnSuccess){
        mainContext = fromWhere;
        signalOnResponse = callOnSuccess;
    }

    /**
     * initialize game attributes with given params and generates a secret code
     * @param numOfDigits integer determining length of secret code to generate
     * @param floor smallest number for number generator
     * @param ceiling largest number for number generator
     * @param attemptsGoal how many attempts player has to complete the game
     */
    void initialize(int numOfDigits, int floor, int ceiling, int attemptsGoal){
        gameOver = false;
        secretCode = new int[numOfDigits];
        attemptsRemaining = attemptsGoal;
        Arrays.fill(secretNums, 0);
        previousGuesses= new StringBuilder(500);
        generateSecretCode(numOfDigits, floor, ceiling);
        checkedNums = new HashMap<Integer, String>();
    }

    /**
     * given user input, update the hashmap of tested values on the result and return said result
     * @param userInput input values to check (will be called from main activity) (method can be merged with checkcode  method in future)
     * @return returns hashmap of guessed numbers and the resulting values,
     *         found: number is in the code but incorrect position. correct: correct num and pos, wrong: number is not in the code
     */
    HashMap<Integer, String> guessResults(int[] userInput){
        for(int i = 0; i < userInput.length; i++){
            if(secretNums[userInput[i]] > 0 && !checkedNums.containsKey(userInput[i])) {
                checkedNums.put(userInput[i], "found");
            }
            if(userInput[i] == secretCode[i]){
                checkedNums.put(userInput[i], "correct");
            }

            if(secretNums[userInput[i]]==0){
                checkedNums.put(userInput[i], "wrong");
            }
        }
        return checkedNums;
    }

    /**
     * compares provided userInput with secretcode, sets attribute data accordingly, and returns int[] on how it should be colored for hints
     * first forloop checks and sets inputResults for correct positions and subtracts from total number quantity to address multiples of a digit in code
     *  (e.g. secretCode = [0077], userInput = [7777], only two 7's in code and should mark the corrected values and ignore remaining duplicates
     * second loop covers remaining digits that are in the code but not in the correct positions (e.g. secretCode = [0123], userInput = [3210] should mark all as in the code
     * both loops logs the results in form of a string as well
     * @param userInput
     * @return
     */
    int[] checkCode(int[] userInput){
        int[] inputResult = new int[userInput.length];
        int[] numQuantities = secretNums.clone();
        String result = " no correct #(s)";
        correctPositions = 0;
        correctDigits = 0;

        for(int i = 0; i < userInput.length; i++){
            if(userInput[i] == secretCode[i]) {
                inputResult[i] = 2;
                correctPositions += 1;
                numQuantities[userInput[i]] -= 1;
                if(result == " no correct #(s)"){
                    result = " correct #(s) & position";
                }
            }
        }
        for(int i = 0; i < userInput.length; i++){
            if (numQuantities[userInput[i]] > 0 && inputResult[i] == 0) {
                inputResult[i] = 1;
                correctDigits += 1;
                numQuantities[userInput[i]] -= 1;
                if(result == " no correct #(s)"){
                    result = " correct #(s)";
                }
            }
        }

        if(correctPositions == 4) {
            gameOver = true;
        }
        attemptsRemaining -=1;
        if(attemptsRemaining <= 0){
            gameOver = true;
        }
        //todo: simplify recorded guesses
        previousGuesses.append("" + userInput[0] + userInput[1] + userInput[2] + userInput[3] + " "+ result +"\n");
//        Log.INFO();
        Log.d("secretNums is : ", Arrays.toString(secretNums));
        Log.d("secret code", "" + secretCode[0] + secretCode[1] + secretCode[2] + secretCode[3]);
        return inputResult;
    }

    /**
     * show the secret code only when game is not in progress and returns as print friendly string (future plan to return only array and parse on the display class)
     * @return string result of either game isnt over or string itself
     */
    String revealCode(){
        String cleanStringOfCode = "";
        if(gameOver){
            for(int i = 0; i < secretCode.length; i++){
                cleanStringOfCode+= secretCode[i];
                if(i < secretCode.length - 1){
                    cleanStringOfCode+= ", ";
                }
            }
           return cleanStringOfCode;
        }
        return "Game is not over";
    }


    /**
     * creates http request to random number generator using params using Volley and runs callback method provided on instance creation to signal game is ready
     * on success, parses response and sets attributes
     * @param numberOfDigits int on how long secretCode will be
     * @param floor int on smallest value to generate
     * @param ceiling int on largest value to generate
     */
    private void generateSecretCode(int numberOfDigits, int floor, int ceiling){
        String url = "https://www.random.org/integers/?num=" + numberOfDigits + "&min=" + floor + "&max=" + ceiling + "&col=1&base=10&format=plain&rnd=new";
        RequestQueue queue = Volley.newRequestQueue(mainContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseString = response.replaceAll("[^0-7]", "");//clean up parsing method
                        for (int i = 0; i < responseString.length(); i++) {
                            secretCode[i] = Character.getNumericValue((responseString.charAt(i)));
                            secretNums[secretCode[i]] += 1;
                        }

                        signalOnResponse.onSuccess(response);
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Http Request: ","Something went wrong with code generation :(");
                signalOnResponse.onFailure("Request could not be completed.");
            }
        });
        queue.add(stringRequest);
    }
}