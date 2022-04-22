Mastermind was implemented through the use of Android Studio, (Android Studio Bumblebee | 2021.1.1 Patch 3 for Windows 64-bit) on Windows10.
My testing of the project utilizes Android Studio's built in Android Device Emulator.

== How to run the project ==
- Install Android Studio Bumblebee | 2021.1.1 Patch 3, following install prompts and make sure to install the AVD (Android Virtual Device) when the checkbox option is available, Android SDK.
- Install OpenJDK, which should be included on the installation of Android Studio
- Import the project either by downloading from the repo and selecting the "Open" option on the Android Studio welcome screen or choose the "Get from VCS" and pasting the repo link in the URL with the Version control option set to Git and hit "clone".
- Once Android Studio boots up, choose to "Trust Project" or "Run in safe Mode" (I believe the difference is "Trust Project" will immediately run the build scripts while Safe Mode wont, allowing the user to look through the code before building the project) and allow time for Android studio to build the project. Progress bar should be on the bottom right of the IDE. This may take some time on the first launch of the project in Android Studio.
- Once the project has completed it's build, create an Android Virtual Device. The device specifications I used when building and testing the project were:

Device: Nexus_S
Resolution: 480x800 hdpi
System Image: Marshmallow API 23
Startup Orientation: Portrait

- Next, under the "Run" Tab, go to "Select Device" and select the newly created AVD to target which virtual device to run the app.
- After the Android Virtual Device has been set up, selected, and project built, you can go back to the "Run" tab and click Run 'app'.

- After clicking "Run App", an emulator tab should appear to launch the emulator on its home screen and begin to install the app. Progress can be seen on the bottom right of the IDE. Once the app has been installed, it will launch automatically and the game will be playable.


== How to play the game ==
- App starts on the menu screen, user selects a difficulty by clicking the difficulty and clicking start to begin the game (normal provides helpful hints, hard provides basic hints based off the base requirements of the project.)
- After clicking start, the user will have a screen of number dials and a keypad. The user can create a 4 digit combination though either use of the number dials or keypad. Using the number pad will highlight the next dial to change. Using the number wheel after using the keypad will clear the highlight. User can swap between input method as they choose. After using the number dial, the keypad will target the next number dial respectively (i.e. if user clicks and drags the dial wheel on the second position, any keypad press will target dial wheel in the third position).
-After the user is happy with their guess, the user can click Try code at the bottom of the screen to see if their code was correct. On normal difficulty, the number wheels will be highlighted depending on the result of their code. number wheels highlighted in green indicate the guess in that position was correct. Orange highlight indicates the value is in the secret code, but incorrect position. No highlight means the value is not in the code. A log of user guesses will be recorded above the number wheels. On normal the code will have emoji symbols to indicate which values were correct, incorrect, or not part of the code.
- Orange diamond means the value is in the code but incorrect position
- small box indicates number is not part of the code
- Large box indicates the number is correct
- Additionally, the keypad numbers will change color according to the guess result as well with the same schema.
On hard difficulty, the hint feedback is limited to informing the player of 3 responses.
1. Player guessed one or more correct numbers in correct positions
2. Player guessed one or more correct numbers but incorrect position
3. Player guessed no correct numbers.

- Once the player is out of attempts or guessed the correct code, a popup will appear informing the user of the total number of games played, their win rate, and the result of the recent game played.
- To dismiss the window, user can click anywhere outside of the popup window and then can click restart game to be brought back to the starting menu.

As a hidden testing feature, the user can click on the Mastermind text 25 times on the difficulty select screen to reset their play stats (total games played and total games won).


== Coding the game / Thought Process ==
- I began my implementation with by programming the base game logic as a Java program (i.e. initialize game variables, generate the random code via http request, game loop, a means to take user input, and provide feedback.)
- Once I had the base logic completed, I extended the project by using Android Studio to create an Android application. I started developing the project in Android Studio using a blank activity template and proceeded to learn and implement elements to create a UI and means of user input. 
- My initial plan was to theme the project around cracking a code to a number lock with the guess feedback in the form of how strong the lock shakes when trying a number combination. I shifted the direction of the project as I felt the lock implementation would require a very well designed, intuitive feedback system which would require a lot of time and testing to get the feel just right.
- Based off testing feedback, I decided to implement features similar to those from Wordle, as Wordle immediately came to mind when working on this project. Given the base requirements gave the user 10 attempts, I utilized a scrollView to log the user guesses and decided to keep the number wheels as a form of input.
- While testing and playing around with the app, I noticed the default softkeyboard didnt provide customization and I opted to manually create my own keypad so I can handle how which numberwheels would be focused and provide a feedback system similar to Wordle's keyboard.
- I ran into issues when attempting to add additional input fields dynamically as I am not familiar with all the quirks with programming for Android, so I decided to stay with a 4 digit input with the implementations setup to handle different code lengths and number ranges.
- In terms of how the code is structured, the Main Activity in the app handles the views while a separate MasterMind class is created to handle to game logic.
- One of the main feedbacks I received when working on the project was the base requirement feedback felt like random guesses and was suggested to create wordle like hints.
