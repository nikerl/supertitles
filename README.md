# SuperTitles
A better supertitling / surtitling program

### Compile and run
Compile the JAR file using this command:
```bash
javac src/* && 
jar cvfm SuperTitles.jar MANIFEST.MF src/*.class && 
rm src/*.class && 
chmod +x SuperTitles.jar
```

Either run it by double clicking the executable or in the terminal using:
```bash
java -jar SuperTitles.jar
```

### Usage
The program takes UTF-8 text files. If multiple lines should be shown on one frame, such as if two people are singing over each other, these should be on the same line in the file but separated by a `<br>` tag. For exmaple: `Hello there!<br>General kenobi!`

To move the text on the projector screen and cue the next line you can use the following keyboard shortcuts:

|Shortcut|Description|
|---|---|
|Up arrow|Cue the next line|
|Down arrow|Cue the previous line|
|CTRL + Arrow keys|Move the text around on the screen|
|CTRL + SHIFT + Left/Right arrow keys|Rotate the text on the screen|