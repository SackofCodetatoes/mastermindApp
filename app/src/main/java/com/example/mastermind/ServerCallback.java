package com.example.mastermind;

public interface ServerCallback {
    void onSuccess(String response);
    void onFailure(String response);
}
