# SuperTitles
### A better supertitling / surtitling program
This is a simple program which easily allows you to project song lyrics line by line. Perfect for subtitling a live performance.

### Features
- Easily manipulate the text around to fit a projector screen. Using keyboard shortcuts you are able to:
  - Move the text around
  - Rotate the text
  - Increase and decrease the font size
- A preview window that displays the full text file, while keeping the current line in the center of the screen
- Easy font selection
- Easily change between Plain / Bold / Italic
- Lock the projected text to prevent accidentally moving things around
- Save current text config to file (Only useful if the projector resolution stays the same)
- Dark mode

### Compile and run
Compile the JAR file using this command, for Linux:
```bash
javac src/*
jar cvfm SuperTitles.jar MANIFEST.MF src/*.class
rm src/*.class
chmod +x SuperTitles.jar
```

For Windows, run these commands in PowerShell:
```powershell
javac src/*
jar cvfm SuperTitles.jar MANIFEST.MF src/*.class
rm src/*.class
```

Run it by either double clicking the executable or in the terminal using this command:
```bash
java -jar SuperTitles.jar
```

### Usage
The program takes UTF-8 text files. If multiple lines should be shown on one frame, such as if two people are singing over each other, these should be on the same line in the file but separated by a `<br>` tag. For example: `Hello there!<br>General kenobi!`

To move the text on the projector screen and cue the next line you can use the following keyboard shortcuts:

|Shortcut|Description|
|---|---|
|Down arrow (S)|Cue the next line|
|Up arrow (W)|Cue the previous line|
|CTRL + Arrow keys (CTRL + WASD)|Move the text around on the screen|
|CTRL + SHIFT + Left/Right arrow keys (CTRL + SHIFT + A/D)|Rotate the text on the screen|

The save config button saves the current text configuration to the file `.supertitles_config.cfg`. This includes the text posision, rotation, font face, font style, font size, as well as if the config is locked or not. The load config button loads a previously saved config if available.

### Dependencies
Java 9, or later
