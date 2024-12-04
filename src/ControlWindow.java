package src;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;

class ControlWindow extends JFrame {
    private ProjectorWindow projectorWindow;
    private String lastUsedPath;
    private JTextPane previewArea;
    private boolean isFirstFile = true;
    private boolean isLocked = false; // Add this field

    public ControlWindow(ProjectorWindow projectorWindow) {
        this.projectorWindow = projectorWindow;

        // Determine the directory of the JAR file
        try {
            File jarFile = new File(ControlWindow.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            lastUsedPath = jarFile.getParent();

            // Sets lastUsedPath to CWD instead of internal VSCode dir
            if (lastUsedPath.contains(".config/Code")) {
                lastUsedPath = System.getProperty("user.dir");
            }
        } catch (Exception e) {
            lastUsedPath = System.getProperty("user.dir");
        }
    
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

        // Add lock button
        JButton lockButton = new JButton("Lock");
        lockButton.setPreferredSize(new Dimension(100, lockButton.getPreferredSize().height));
        lockButton.addActionListener(e -> {
            isLocked = !isLocked;
            lockButton.setText(isLocked ? "Unlock" : "Lock");
            fontComboBox.setEnabled(!isLocked);
            fontStyleComboBox.setEnabled(!isLocked);
            requestFocusInWindow();
        });
        topPanel.add(lockButton);

        // Add help button
        JButton helpButton = new JButton("?");
        helpButton.setPreferredSize(new Dimension(40, helpButton.getPreferredSize().height));
        helpButton.addActionListener(e -> {
            String message = 
                "<html>" +
                    "<div style='margin-right: 20px; margin-top: 0; margin-left: 0px; font-size: 11px;'>" +
                        "<h2 style='margin-bottom: 0px;'>Keyboard Shortcuts:</h2>" +
                        "<ul style='margin-left: 0; padding-left: 10px;'>" +
                            "<li><b>Next Line:</b> Down arrow (S)</li>" +
                            "<li><b>Previous Line:</b> Up arrow (W)</li>" +
                            "<li><b>Move text:</b> CTRL + Arrow keys (CTRL + WASD)</li>" +
                            "<li><b>Rotate text:</b> CTRL + SHIFT + Left/Right arrow (CTRL + SHIFT + A/D)</li>" +
                        "</ul>" +
                        "<h2 style='margin-bottom: 0px;'>Buttons:</h2>" +
                        "<ul style='margin-left: 0; padding-left: 10px;'>" +
                            "<li><b>Lock / Unlock:</b> Disables or Enables modifying the projected text</li>" +
                        "</ul>" +
                        "<h2 style='margin-bottom: 0px;'>About SuperTitles:</h2>" +
                        "Star on GitHub! " +
                        "<a href='https://github.com/nikerl/supertitles/' style='color:#29B6F6'>https://github.com/nikerl/supertitles/</a>" +
                        "<br>" +
                        "Licence: GPL-3.0" +
                    "</div>" +
                "</html>";
        
            JEditorPane editorPane = new JEditorPane("text/html", message);
            editorPane.setEditable(false);
            editorPane.setOpaque(false);
            editorPane.addHyperlinkListener(event -> {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        
            JOptionPane.showMessageDialog(this, editorPane, "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        topPanel.add(helpButton);
    
        add(topPanel, BorderLayout.NORTH);
    
        // Add preview area
        previewArea = new JTextPane();
        previewArea.setContentType("text/html");
        previewArea.setEditable(false);
        previewArea.setFocusable(false);
        previewArea.setBackground(new Color(15, 15, 15));
        previewArea.setForeground(Color.WHITE);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        previewArea.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(600, 0)); // Set preferred width
        
        // Center the scrollPane in a panel using GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1.0;
        
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBackground(new Color(15, 15, 15));
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        paddedPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(paddedPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);

        updatePreview();
    
        // Add key listener to the control window
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isLocked) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                        projectorWindow.nextLine();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                        projectorWindow.previousLine();
                    } else {
                        return; // Ignore other keys
                    }
                } else {
                    projectorWindow.dispatchEvent(e);
                }
                updatePreview();
            }
        });

        previewArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int offset = previewArea.viewToModel2D(e.getPoint());
                try {
                    Element element = previewArea.getStyledDocument().getCharacterElement(offset);
                    AttributeSet as = element.getAttributes();
                    String lineStr = (String) as.getAttribute("data-line");
                    if (lineStr != null) {
                        int actualLine = Integer.parseInt(lineStr);
                        jumpToLine(actualLine);
                    }
                } catch (Exception ex) {
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

            setTitle("SuperTitles  --  " + selectedFile.getName());

            // Save the last used path
            lastUsedPath = selectedFile.getParent();
            Preferences prefs = Preferences.userNodeForPackage(ControlWindow.class);
            prefs.put("lastUsedPath", lastUsedPath);
    
            updatePreview();

            // Attempt to fix preview area text jumping round on first file
            if (isFirstFile) {
                projectorWindow.loadLines(filePath);
                updatePreview();
                isFirstFile = false;
            }
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

        final String BG_COLOR = "#0F0F0F"; // Background
        final String FG_COLOR = "#131313"; // Foreground
        final String CL_COLOR = "#3E3E3E"; // Current line
    
        // Build the preview text formatted as HTML
        previewText.append("<html><body style='font-family:monospace; color:white; background-color:").append(BG_COLOR).append(";'>");
        if (lines.isEmpty()) {
            previewText.append("<center>")
                .append("<div data-line='").append("0")
                .append("' style='background-color:").append(CL_COLOR).append(";'><b>")
                .append("Choose a file to display")
                .append("</b></div></center>");
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] splitLine = line.split("<br>");
    
            for (String split : splitLine) {
                String backgroundColor = (i % 2 == 0) ? BG_COLOR : FG_COLOR;
                if (i == currentIndex) {
                    previewText.append("<div data-line='").append(i)
                        .append("' style='background-color:").append(CL_COLOR).append(";'><b>")
                        .append(">>&nbsp;").append(split)
                        .append("</b></div>");
                } else {
                    previewText.append("<div data-line='").append(i)
                        .append("' style='background-color:").append(backgroundColor).append(";'>")
                        .append("&nbsp;&nbsp;&nbsp;").append(split)
                        .append("</div>");
                }
            }
        }
        previewText.append("</body></html>");
    
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, previewArea);
    
        // Update text
        previewArea.setText(previewText.toString());
    
        SwingUtilities.invokeLater(() -> {
            try {
                if (scrollPane != null) {
                    int lineHeight = previewArea.getFontMetrics(previewArea.getFont()).getHeight();
                    int visibleLines = scrollPane.getViewport().getHeight() / lineHeight;
                    int targetLine = Math.max(0, currentIndex - (visibleLines / 3));
                    int targetScroll = targetLine * lineHeight;
                    
                    scrollPane.getVerticalScrollBar().setValue(targetScroll);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void jumpToLine(int line) {
        projectorWindow.setCurrentIndex(line);
        updatePreview();
        projectorWindow.updateTitle();
    }
}