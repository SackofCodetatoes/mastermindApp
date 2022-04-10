package com.example.mastermind;

import java.util.Scanner;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class mastermind{
    public static void main(String []args){
        gameLoop();

    }

    public static void gameLoop(){
        boolean won = false;
        int attempts = 10, correctDigits = 0, correctPositions = 0, currentInt;
        int secretCode[] = generateSecretCode();
        boolean secretNums[] = new boolean[8];

        for(int i = 0; i < 4; i++){
            secretNums[secretCode[i]] = true;
        };

        Scanner reader = new Scanner(System.in);
        String userInput;

//        System.out.println("Game Start!");
//        System.out.println("===================");

        while(attempts > 0 && !won){
            System.out.println("Attempts Remaining: " + attempts);
            System.out.println("Enter a combination of numbers (0 - 7): ");
            userInput = reader.nextLine();
            correctDigits = 0;
            correctPositions = 0;
            //check if userinput is valid before looping or make this section more robust.

            userInput = userInput.replaceAll("[^0-7]", "");
            if(userInput.length() != 4){
                System.out.println("Invalid Response, try again.");
            }
            else {
                for(int i = 0; i < 4; i++){
                    currentInt = Character.getNumericValue(userInput.charAt(i));

                    if(currentInt == secretCode[i]){
                        correctPositions+=1;
                    }
                    if(secretNums[currentInt]){
                        correctDigits+=1;
                    }
                }

                if(correctPositions == 4){
                    won = true;
                }
                else if(correctPositions > 0){
                    System.out.println("Player guessed a correct number and position. Try again.");
                }
                else if(correctDigits > 0) {
                    System.out.println("Player guessed a correct number. Try again.");
                }
                else {
                    System.out.println("No correct guesses. Try again.");
                }

                attempts-=1;
            }
        }

        reader.close();
        if(won){
            System.out.println("You won, Congradulations!");
        }
        else{
            System.out.println("Game Over. The correct code was " + secretCode[0] + secretCode[1] + secretCode[2] + secretCode[3]+ ". Better luck next time");
        }

    }


    private static int[] generateSecretCode(){
        // call api to generate code, will have static code for time being
        int secretCode[] = {1, 2, 3, 4};
        var client = HttpClient.newHttpClient();

        var request = HttpRequest.newBuilder(
                URI.create("https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new"))
                .header("accept", "application/json")
                .build();

        // var response = client.send(request, new JsonBodyHandler<>(APOD.class));
        try{
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseString = response.body();
            //parse string
            responseString = responseString.replaceAll("[^0-7]", "");
            for(int i = 0; i < responseString.length(); i++){
                secretCode[i] = Character.getNumericValue(responseString.charAt(i));
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return secretCode;
    }


  /*
    functions needed
    a main loop the game logic
      - initialize the game parameters and tracking variables (attempts, guesses, the number as a question )
      run loop until attempts are out or player has won
        - check how many attempts are left
          if attempts remain then
            display previous guesses and response data
            take player input
            check if all player input string matches stored value
              if all 4 characters match, state player is a winner and show stats (replay or exit)
          else
            reveal actual code and tell player they have lost.
            ask if they wish to replay
    function to do the api call to get the number,
  */
}
