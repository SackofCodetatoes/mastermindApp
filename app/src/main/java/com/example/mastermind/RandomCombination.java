package com.example.mastermind;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Arrays;

public class RandomCombination extends AppCompatActivity {
    private String responseString;
    private int[] secretCode, secretNums;

    void generate(int numbers, Context fromWhere){
        String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";
        RequestQueue queue = Volley.newRequestQueue(fromWhere);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "" + Arrays.toString(response.split("\n")));
//                        int[] secretCodeResponse = response.split("\n"); //implment this
//                        responseString = response.substring(0, 8);
                        responseString = responseString.replaceAll("[^0-7]", "");//redundant, put
                        for(int i = 0; i < responseString.length(); i++){
                            secretCode[i] = Character.getNumericValue((responseString.charAt(i)));
                        }
                        for(int i = 0; i < 4; i++){
                            secretNums[secretCode[i]] += 1;//bitmap overkill, can simplify to checking index , dictionary/ keyvalue par, keep index in mind and looking forwrd
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Http Request", "Something went wrong");
            }
        });
        queue.add(stringRequest);
    }
    int[] copyCombo(){
        return secretCode;
    }
    int[] copyNums(){
        return secretNums;
    }
}
