package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


class ProjectorWindow extends JFrame {
    private RotatableLabel title;
    private List<String> lines;
    private int currentIndex;
    private int fontSize;
    private Coords coords;
    private static final int TRANSLATION_INCREMENT = 5;
    private static final double ROTATION_INCREMENT = 0.5;
    private static final int FONT_SIZE_INCREMENT = 2;

    public static enum Direction {
        TRANSLATE_UP,
        TRANSLATE_DOWN,
        TRANSLATE_LEFT,
        TRANSLATE_RIGHT,
        ROTATE_LEFT,
        ROTATE_RIGHT
    }

    public ProjectorWindow(String filePath) {
        lines = new ArrayList<>();
        currentIndex = 0;
        fontSize = 28;
        coords = new Coords();
    
        // Set up the JFrame
        setTitle("SuperTitles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Remove window decorations
        getContentPane().setBackground(Color.BLACK);
        setLayout(null); // Use null layout for absolute positioning
    
        // Create and configure the RotatableLabel
        title = new RotatableLabel("", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        add(title);
    
        // Load lines from the text file
        loadLines(filePath);
    
        // Add key listener for navigation and font changes
        keylistener();
    
        // Set the location of the window on the second display if available
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        if (gs.length > 1) {
            // More than one screen, set the location to the second screen
            GraphicsDevice secondScreen = gs[1];
            secondScreen.setFullScreenWindow(this);
        } else {
            // Only one screen, set to fullscreen on the primary screen
            GraphicsDevice primaryScreen = ge.getDefaultScreenDevice();
            primaryScreen.setFullScreenWindow(this);
        }
    
        updateTitle();
        
        setVisible(true);
    }

    public void loadLines(String filePath) {
        lines.clear();
        if (filePath != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
                lines.add("");
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    lines.add("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentIndex = 0;
        }
        updateTitle();
    }

    private void updateTitle() {
        if (lines.isEmpty()) {
            title.setText("Choose a file to display");
        } else {
            String currentLine = lines.get(currentIndex);
            title.setText("<html><div style='text-align: center; line-height: 1.5;'>" + currentLine + "</div></html>");
        }

        title.setFont(new Font("Serif", Font.PLAIN, fontSize));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(JLabel.CENTER); // Center align the text
        title.setVerticalAlignment(JLabel.CENTER);

        // Set the size of the JLabel to be larger to accommodate rotation
        int width = getWidth();
        int height = 400; // Increase height to accommodate rotation
        title.setSize(width, height);

        // Adjust the location based on the coords
        title.setLocation(coords.x, coords.y);

        // Set the rotation
        title.setRotation(coords.rotation);

        title.repaint();
    }

    public void nextLine() {
        if (currentIndex < lines.size() - 1) {
            currentIndex++;
            updateTitle();
        }
    }

    public void previousLine() {
        if (currentIndex > 0) {
            currentIndex--;
            updateTitle();
        }
    }

    public void move_textbox(Direction direction) {
        switch (direction) {
            case TRANSLATE_UP:
                coords.y -= TRANSLATION_INCREMENT;
                break;
            case TRANSLATE_DOWN:
                coords.y += TRANSLATION_INCREMENT;
                break;
            case TRANSLATE_LEFT:
                coords.x -= TRANSLATION_INCREMENT;
                break;
            case TRANSLATE_RIGHT:
                coords.x += TRANSLATION_INCREMENT;
                break;
            case ROTATE_LEFT:
                coords.rotation -= ROTATION_INCREMENT;
                break;
            case ROTATE_RIGHT:
                coords.rotation += ROTATION_INCREMENT;
                break;
            default:
                break;
        }
        updateTitle();
    }

    public void increaseFontSize() {
        fontSize += FONT_SIZE_INCREMENT;
        updateTitle();
    }

    public void decreaseFontSize() {
        fontSize -= FONT_SIZE_INCREMENT;
        updateTitle();
    }

    public void keylistener() {
        // Add key listener for navigation and font changes
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.isShiftDown()) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                move_textbox(Direction.ROTATE_LEFT);
                                break;
                            case KeyEvent.VK_RIGHT:
                                move_textbox(Direction.ROTATE_RIGHT);
                                break;
                            default:
                                break;
                        }
                    } else {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_UP:
                                move_textbox(Direction.TRANSLATE_UP);
                                break;
                            case KeyEvent.VK_DOWN:
                                move_textbox(Direction.TRANSLATE_DOWN);
                                break;
                            case KeyEvent.VK_LEFT:
                                move_textbox(Direction.TRANSLATE_LEFT);
                                break;
                            case KeyEvent.VK_RIGHT:
                                move_textbox(Direction.TRANSLATE_RIGHT);
                                break;
                            case KeyEvent.VK_PLUS:
                                increaseFontSize();
                                break;
                            case KeyEvent.VK_MINUS:
                                decreaseFontSize();
                                break;
                            default:
                                break;
                        }
                    }
                    updateTitle();
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            previousLine();
                            break;
                        case KeyEvent.VK_DOWN:
                            nextLine();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public List<String> getLines() {
        return lines;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFont(Font font) {
        title.setFont(font);
    }
}