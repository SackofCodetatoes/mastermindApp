Mastermind was implemented through the use of Android Studio, (Android Studio Bumblebee | 2021.1.1 Patch 3 for Windows 64-bit) on Windows10.
My testing of the project utilizes Android Studio's built in Android Device Emulator.

How to run the project:
- Install Android Studio Bumblebee | 2021.1.1 Patch 3, following install prompts and make sure to install the AVD (Android Virtual Device) when the checkbox option is available, Android SDK.
- Import the project either by downloading from the repo and selecting the "Open" option on the Android Studio welcome screen or choose the "Get from VCS" and pasting the repo link in the URL with the Version control option set to Git and hit "clone".
- Once Android Studio boots up, select to trust the project (I have not tried safe mode yet, I assume it does not build and run scripts immediately) and allow time for Android studio to build the project. Progress bar should be on the bottom right of the IDE. This may take some time on the first launch of the project in Android Studio.
- Once the project has completed it's build, create an Android Virtual Device. The device specifications I used when building and testing the project were:

Device: Nexus_S
Resolution: 480x800 hdpi
System Image: Marshmallow API 23
Startup Orientation: Portrait

- Next, under the "Run" Tab, go to "Select Device" and select the newly created AVD to target which virtual device to run the app.
- After the Android Virtual Device has been set up, selected, and project built, you can go back to the "Run" tab and click Run 'app'.

- After clicking "Run App", an emulator tab should appear to launch the emulator on its home screen and begin to install the app. Progress can be seen on the bottom right of the IDE. Once the app has been installed, it will launch automatically and the game will be playable.


== Thought Process ==
I began my implementation with by programming the base game logic in a separate repo (i.e. initialize game variables, generate the random code via http request, game loop, a means to take user input, and provide feedback.)
Once I had the basic logic completed, I extended the project by using Android Studio to create an Android application. 





- Explain how interviewer could run your code
- document your thought process and or code structure
describe any creative extensions attempted or implemented
(BE CLEAR AND UNAMBIGUOUS IN LISTING ALL THE STEPS IN BUILDING, RUNNING, AND PLAYING THE GAME YOU BUILT, make no assumptions about what software the intervieer has and err on side of beign explicit)