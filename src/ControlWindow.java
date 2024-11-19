package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;


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
    
        // Add window focus listener to bring the window to the front
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                toFront();
                requestFocusInWindow();
            }
        });
    
        setFocusable(true);
        requestFocusInWindow();

        setAlwaysOnTop(true);
    
        setVisible(true);
    }
    
    private void chooseFile() {
        // Create the JFileChooser
        JFileChooser fileChooser = new JFileChooser(lastUsedPath);
        fileChooser.setDialogTitle("Choose a file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
        // Show the file chooser dialog
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            projectorWindow.loadLines(filePath);
    
            // Save the last used path
            lastUsedPath = selectedFile.getParent();
            Preferences prefs = Preferences.userNodeForPackage(ControlWindow.class);
            prefs.put("lastUsedPath", lastUsedPath);
    
            updatePreview();
        }
    
        // Re-enable always-on-top for the control window
        setAlwaysOnTop(true);
    
        // Request focus for the ControlWindow
        SwingUtilities.invokeLater(() -> {
            toFront();
            requestFocusInWindow();
        });
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