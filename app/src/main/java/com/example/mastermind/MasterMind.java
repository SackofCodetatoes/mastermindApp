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
    private int[] secretCode = {1, 2, 3, 4}, secretNums = {0, 0, 0, 0, 0, 0, 0, 0};
    private String responseString;
    StringBuilder previousGuesses;
    int attemptsRemaining = 10, correctDigits = 0, correctPositions = 0;
    boolean gameOver = false;
    Context mainContext;
    ServerCallback signalOnResponse;
    HashMap<Integer, String> checkedNums;


    public MasterMind(Context fromWhere, ServerCallback callOnSuccess){
        mainContext = fromWhere;
        signalOnResponse = callOnSuccess;
    }
    void initialize(int numberOfDigits){
        gameOver = false;
        attemptsRemaining = 10;
        Arrays.fill(secretNums, 0);
        previousGuesses= new StringBuilder(500);
        generateSecretCode(numberOfDigits);
        checkedNums = new HashMap<Integer, String>();
    }
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

    int[] checkCode(int[] userInput){
        int[] inputResult = {0,0,0,0};
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

        Log.d("secretNums is : ", Arrays.toString(secretNums));
        Log.d("secret code", "" + secretCode[0] + secretCode[1] + secretCode[2] + secretCode[3]);
        return inputResult;
    }


    String revealCode(){
        int[] nopeCode = {0,0,0,0};
        if(gameOver){
           return Arrays.toString(secretCode);
        }
        return "You do not have the right";
    }


    private void generateSecretCode(int numberOfDigits){ //take out as own class
        String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";
        RequestQueue queue = Volley.newRequestQueue(mainContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", "" + Arrays.toString(response.split("\n")));
//                        int[] secretCodeResponse = response.split("\n"); //implment this / clean up response parsing
                        responseString = response.substring(0, 8);
                        responseString = responseString.replaceAll("[^0-7]", "");//redundant, put
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