package src;
import javax.swing.*;
import java.awt.*;


public class SuperTitles {
    public static void main(String[] args) {
        try {
            // Set system Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        SwingUtilities.invokeLater(() -> {
            ProjectorWindow projectorWindow = new ProjectorWindow();
            new ControlWindow(projectorWindow);
        });
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