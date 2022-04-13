Start of readme
Wednesday Goals
- complete hint system
    - provide difficulty options at each restart and provide highlighted hints accordingly
        - easy: highlights correct digits with green, multiple correct nums with blue and single correct nums with orange
        - medium: hl correct digits with green and correct nums with orange
        - hard: highlight all pickers with the responses from directions. green if num is right pos, orange if correct num but not spot, blank for no correct

        main will have difficult setting. cick button -> set the difficulty rating
        create an array of numbers to indicate the reading output when checking code
        - [0,0,0,0]
        - 0 indicates incorrect
        - 1 indicates value is in the code
        - 2 indicates value is in the correct pos
        colorNumberPicker will respond to request accordingly depending on difficulty
        - if diff 1, color individual pickrs based on result
        - if 2, color whole wheel as a response
        - if 0, color individual wheels with additional color of multiples

- shift UI around to be more user friendly

- add start screen to provide game instructions and difficulty setting
- factor in horizontal mode (test on different device sizes)
- factor in customization (code length, number of attempts)
- add some effects (audio + animation on win, etc)

Thursday
- remove debugging messages