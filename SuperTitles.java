import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SuperTitles extends JFrame {
    private RotatableLabel title;
    private List<String> lines;
    private int currentIndex;
    private int fontSize;
    private Coords coords;
    private static final int TRANSLATION_INCREMENT = 5;
    private static final double ROTATION_INCREMENT = 0.5;
    private static final int FONT_SIZE_INCREMENT = 2;

    public SuperTitles() {
        lines = new ArrayList<>();
        currentIndex = 0;
        fontSize = 24;
        coords = new Coords();
    
        // Load lines from the text file
        loadLines("test_texts/text.txt");
    
        // Set up the JFrame
        setTitle("SuperTitles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null); // Use null layout for absolute positioning
    
        // Create and configure the RotatableLabel
        title = new RotatableLabel("", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        updateTitle();
    
        // Add the RotatableLabel to the frame
        add(title);
    
        // Add key listener for navigation and font changes
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            coords.y -= TRANSLATION_INCREMENT;
                            break;
                        case KeyEvent.VK_DOWN:
                            coords.y += TRANSLATION_INCREMENT;
                            break;
                        case KeyEvent.VK_LEFT:
                            coords.x -= TRANSLATION_INCREMENT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            coords.x += TRANSLATION_INCREMENT;
                            break;
                        case KeyEvent.VK_PLUS:
                            fontSize += FONT_SIZE_INCREMENT;
                            System.out.println("fontSize = " + fontSize);
                            System.out.println("y = " + coords.y);
                            break;
                        case KeyEvent.VK_MINUS:
                            fontSize -= FONT_SIZE_INCREMENT;
                            System.out.println("fontSize = " + fontSize);
                            System.out.println("y = " + coords.y);
                            break;
                        default:
                            break;
                    }
                    updateTitle();
                } else if (e.isShiftDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            coords.rotation -= ROTATION_INCREMENT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            coords.rotation += ROTATION_INCREMENT;
                            break;
                        default:
                            break;
                    }
                    updateTitle();
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (currentIndex > 0) {
                                currentIndex--;
                                updateTitle();
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if (currentIndex < lines.size() - 1) {
                                currentIndex++;
                                updateTitle();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    
        setVisible(true);
    }

    private void loadLines(String filePath) {
        lines.add("");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                lines.add("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTitle() {
        if (lines.isEmpty()) {
            title.setText("No lines to display");
        } else {
            title.setText(lines.get(currentIndex));
            title.setFont(new Font("Serif", Font.PLAIN, fontSize));
            title.setForeground(Color.WHITE);
            title.setHorizontalAlignment(JLabel.CENTER); // Center align the text
            title.setVerticalAlignment(JLabel.CENTER);
    
            // Set the size of the JLabel to be larger to accommodate rotation
            int width = getWidth();
            int height = title.getPreferredSize().height * 4; // Increase height to accommodate rotation
            title.setSize(width, height);
    
            // Adjust the location based on the coords
            title.setLocation(coords.x, coords.y);
    
            // Set the rotation
            title.setRotation(coords.rotation);
    
            title.repaint();
        }
    }

    public static void main(String[] args) {
        new SuperTitles();
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
