import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

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
        setSize(1200, 720);
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
    
        setVisible(true);
    }

    public void loadLines(String filePath) {
        lines.clear();
        if (filePath != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                lines.add("");
                String line;
                while ((line = br.readLine()) != null) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProjectorWindow projectorWindow = new ProjectorWindow(null);
            new ControlWindow(projectorWindow);
        });
    }
}

class ControlWindow extends JFrame {
    private ProjectorWindow projectorWindow;
    private String lastUsedPath;
    private JTextArea previewArea;

    public ControlWindow(ProjectorWindow projectorWindow) {
        this.projectorWindow = projectorWindow;

        // Set up the JFrame
        setTitle("Control Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        getContentPane().setBackground(new Color(15, 15, 15));
        setLayout(new BorderLayout());

        // Add file chooser button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(15, 15, 15));
        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(e -> {
            chooseFile();
            requestFocusInWindow();
        });
        topPanel.add(chooseFileButton);

        // Add control buttons
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(e -> {
            projectorWindow.nextLine();
            updatePreview();
            requestFocusInWindow();
        });
        topPanel.add(nextButton);

        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(e -> {
            projectorWindow.previousLine();
            updatePreview();
            requestFocusInWindow();
        });
        topPanel.add(previousButton);

        add(topPanel, BorderLayout.NORTH);

        // Add preview area
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFocusable(false);
        previewArea.setBackground(new Color(15, 15, 15));
        previewArea.setForeground(Color.WHITE);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        previewArea.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Add key listener to the control window
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                projectorWindow.dispatchEvent(e);
                updatePreview();
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        setVisible(true);
    }

    private void chooseFile() {
        FileDialog fileDialog = new FileDialog(this, "Choose a file", FileDialog.LOAD);
        fileDialog.setDirectory(lastUsedPath);
        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String file = fileDialog.getFile();
        if (directory != null && file != null) {
            String filePath = directory + file;
            projectorWindow.loadLines(filePath);

            // Save the last used path
            lastUsedPath = directory;
            Preferences prefs = Preferences.userNodeForPackage(ControlWindow.class);
            prefs.put("lastUsedPath", lastUsedPath);

            updatePreview();
        }
    }

    private void updatePreview() {
        List<String> lines = projectorWindow.getLines();
        int currentIndex = projectorWindow.getCurrentIndex();
        StringBuilder previewText = new StringBuilder();

        int start = Math.max(0, currentIndex - 10);
        int end = Math.min(lines.size(), currentIndex + 20);

        for (int i = start; i < end; i++) {
            String line = lines.get(i);
            if (line.contains("<br>")) {
                String[] splitLine = line.split("<br>");
                
                if (i == currentIndex) {
                    for (int j = 0; j < splitLine.length; j++) {
                        previewText.append(">> ").append(splitLine[j]).append("\n");
                    }
                } else {
                    for (int j = 0; j < splitLine.length; j++) {
                        previewText.append("   ").append(splitLine[j]).append("\n");
                    }
                }
            } else {
                if (i == currentIndex) {
                    previewText.append(">> ").append(line).append("\n");
                } else {
                    previewText.append("   ").append(line).append("\n");
                }
            }
        }
        previewArea.setText(previewText.toString());
    }
}

class RotatableLabel extends JLabel {
    private double rotation;

    public RotatableLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        this.rotation = 0;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Calculate the center of the label
        int x = getWidth() / 2;
        int y = getHeight() / 2;

        // Apply the rotation transformation
        g2d.rotate(Math.toRadians(rotation), x, y);

        // Draw the text
        super.paintComponent(g2d);

        // Dispose of the graphics context
        g2d.dispose();
    }
}

class Coords {
    int x;
    int y;
    double rotation;

    public Coords() {
        this.x = 0;
        this.y = 0;
        this.rotation = 0;
    }

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
        this.rotation = 0;
    }

    public Coords(int x, int y, double rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
}