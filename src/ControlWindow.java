package src;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

class ControlWindow extends JFrame {
    private ProjectorWindow projectorWindow;
    private String lastUsedPath = System.getProperty("user.dir");
    private JTextArea previewArea;
    private final int PREVIEW_WINDOW_PAST = 10;
    private final int PREVIEW_WINDOW_FUTURE = 20;
    private List<Integer> previewToActualLineMap;

    public ControlWindow(ProjectorWindow projectorWindow) {
        this.projectorWindow = projectorWindow;
    
        // Set up the JFrame
        setTitle("SuperTitles");
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
        nextButton.setPreferredSize(new Dimension(100, nextButton.getPreferredSize().height));
        nextButton.addActionListener(e -> {
            projectorWindow.nextLine();
            updatePreview();
            requestFocusInWindow();
        });
        topPanel.add(nextButton);
    
        JButton previousButton = new JButton("Previous");
        previousButton.setPreferredSize(new Dimension(100, previousButton.getPreferredSize().height));
        previousButton.addActionListener(e -> {
            projectorWindow.previousLine();
            updatePreview();
            requestFocusInWindow();
        });
        topPanel.add(previousButton);

        // Add font selection dropdown
        String[] fonts = {"Arial", "Monospaced", "Serif"};
        JComboBox<String> fontComboBox = new JComboBox<>(fonts);
        fontComboBox.setSelectedItem("Serif");
        fontComboBox.setPreferredSize(new Dimension(150, fontComboBox.getPreferredSize().height));
        fontComboBox.addActionListener(e -> {
            String selectedFont = (String) fontComboBox.getSelectedItem();
            projectorWindow.setFontTypeFace(selectedFont);
            requestFocusInWindow();
        });
        topPanel.add(fontComboBox);

        // Add font style selection dropdown
        String[] fontStyles = {"Plain", "Bold", "Italic"};
        JComboBox<String> fontStyleComboBox = new JComboBox<>(fontStyles);
        fontStyleComboBox.setSelectedItem("Plain");
        fontStyleComboBox.setPreferredSize(new Dimension(150, fontStyleComboBox.getPreferredSize().height));
        fontStyleComboBox.addActionListener(e -> {
            String selectedFontStyle = (String) fontStyleComboBox.getSelectedItem();
            int fontStyle = 0;
            if (selectedFontStyle.equals("Plain")) fontStyle = Font.PLAIN;
            if (selectedFontStyle.equals("Bold")) fontStyle = Font.BOLD;
            if (selectedFontStyle.equals("Italic")) fontStyle = Font.ITALIC;
            projectorWindow.setFontStyle(fontStyle);
            requestFocusInWindow();
        });
        topPanel.add(fontStyleComboBox);

        // Add help button
        JButton helpButton = new JButton("?");
        helpButton.setPreferredSize(new Dimension(helpButton.getPreferredSize().height, helpButton.getPreferredSize().height));
        helpButton.addActionListener(e -> {
            String message = "Keyboard Shortcuts:\n\n" +
                             "Next Line: Down arrow\n" +
                             "Previous Line: Up arrow\n" +
                             "Move text: CTRL + Arrow keys\n" +
                             "Rotate text: CTRL + SHIFT + Left/Right arrow";
            JOptionPane.showMessageDialog(this, message, "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        topPanel.add(helpButton);
    
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

        previewArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = previewArea.viewToModel2D(e.getPoint());
                try {
                    int line = previewArea.getLineOfOffset(offset);
                    jumpToLine(line);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
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
        
        // Set file filter to only show text files
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

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
        previewToActualLineMap = new ArrayList<>();

        int start = Math.max(0, currentIndex - PREVIEW_WINDOW_PAST);
        int end = Math.min(lines.size(), Math.max(currentIndex + PREVIEW_WINDOW_FUTURE, PREVIEW_WINDOW_FUTURE + PREVIEW_WINDOW_PAST));

        for (int i = start; i < end; i++) {
            String line = lines.get(i);
            String[] splitLine = line.split("<br>");
            
            for (int j = 0; j < splitLine.length; j++) {
                previewToActualLineMap.add(i);
                if (i == currentIndex) {
                    previewText.append(">> ").append(splitLine[j]).append("\n");
                } else {
                    previewText.append("   ").append(splitLine[j]).append("\n");
                }
            }
        }
        previewArea.setText(previewText.toString());
    }

    private void jumpToLine(int line) {
        if (line < previewToActualLineMap.size()) {
            int actualLine = previewToActualLineMap.get(line);
            projectorWindow.setCurrentIndex(actualLine);
            updatePreview();
            projectorWindow.updateTitle();
        }
    }
}