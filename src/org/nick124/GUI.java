package org.nick124;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

import org.nick124.FileChooser.JnaFileChooser;

import com.sun.jna.Platform;

public class GUI {
    private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private LineNumberGutter lineNumberGutter;
    private JMenu optionsMenu;

    // Theme Colors
    private final Color LIGHT_BG = Color.WHITE;
    private final Color LIGHT_FG = Color.BLACK;
    private final Color LIGHT_GUTTER_BG = new Color(240, 240, 240);
    private final Color LIGHT_GUTTER_FG = Color.GRAY;

    private final Color DARK_BG = new Color(43, 43, 43);
    private final Color DARK_FG = new Color(169, 183, 198);
    private final Color DARK_GUTTER_BG = new Color(49, 51, 53);
    private final Color DARK_GUTTER_FG = new Color(92, 96, 100);

    private boolean isDarkTheme = false;
    private int currentFontSize = 14;

    /**
     * Create the application.
     */
    private GUI() {
    	initialize();
    }
    private static class SINGLETONHELPER {
        private static final GUI INSTANCE = new GUI();
    }
    public static GUI getInstance() {
    	return SINGLETONHELPER.INSTANCE;
    }
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI window = new GUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private java.io.File currentFile = null;
    
    public void setFile(java.io.File f) {currentFile = f;}
    
    private void saveFile(boolean isSaveAs) {
        if (isSaveAs || currentFile == null) {
            java.awt.FileDialog fileDialog = new java.awt.FileDialog(frame, "Save File As", java.awt.FileDialog.SAVE);
            fileDialog.setVisible(true);
            
            String directory = fileDialog.getDirectory();
            String file = fileDialog.getFile();
            
            if (directory != null && file != null) {
                currentFile = new java.io.File(directory, file);
            } else {
                return;
            }
        }
        try (java.io.FileWriter writer = new java.io.FileWriter(currentFile)) {
            textArea.write(writer);
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(frame, 
                "Error saving file: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    private void openFile() {
//    		JnaFileChooser fc = new JnaFileChooser();
//    		fc.setMode(JnaFileChooser.Mode.Directories);
    	java.awt.FileDialog fileDialog = new java.awt.FileDialog(frame, "Open File", java.awt.FileDialog.LOAD);
        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String file = fileDialog.getFile();
        
        if (directory != null && file != null) {
            textArea.setText("");
            currentFile = new java.io.File(directory, file);
            
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(currentFile))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    if (!firstLine) {
                        textArea.append("\n");
                    }
                    textArea.append(line);
                    firstLine = false;
                }
            } catch (java.io.IOException e) {
                javax.swing.JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage());
            }
        }
    }
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Pulse IDE");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Create Menu Bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem mntmNewProject = new JMenuItem("New Project...");
        fileMenu.add(mntmNewProject);
        
        JMenuItem mntmNewMenuItem = new JMenuItem("Open Project");
        fileMenu.add(mntmNewMenuItem);
        mntmNewMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }});
        
        JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save");
        mntmNewMenuItem_1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        	    java.awt.event.KeyEvent.VK_S, 
        	    java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        fileMenu.add(mntmNewMenuItem_1);
        
        JMenuItem mntmNewMenuItem_2 = new JMenuItem("Save as...");
        fileMenu.add(mntmNewMenuItem_2);
        mntmNewMenuItem_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(true);
            }});
        
        mntmNewMenuItem_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(false);
            }});

        // Options Menu
        optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        JMenuItem toggleThemeItem = new JMenuItem("Toggle Dark Theme");
        optionsMenu.add(toggleThemeItem);

        optionsMenu.addSeparator();

        JMenuItem increaseFontItem = new JMenuItem("Increase Font Size");
        JMenuItem decreaseFontItem = new JMenuItem("Decrease Font Size");
        optionsMenu.add(increaseFontItem);
        optionsMenu.add(decreaseFontItem);

        optionsMenu.addSeparator();

        JMenuItem toggleLineNumbersItem = new JMenuItem("Toggle Line Numbers");
        optionsMenu.add(toggleLineNumbersItem);

        // Text Area setup
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, currentFontSize));
        textArea.setMargin(new Insets(5, 5, 5, 5));

        // Line Number Gutter setup
        lineNumberGutter = new LineNumberGutter(textArea);
        
        // Wrap everything in a JScrollPane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberGutter);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Apply baseline layout colors
        applyTheme();

        // --- Action Listeners ---

        // File -> New Project
        mntmNewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCreateProjectDialog();
            }
        });

        // Options -> Toggle Dark Theme
        toggleThemeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isDarkTheme = !isDarkTheme;
                applyTheme();
            }
        });

        // Options -> Increase Font Size
        increaseFontItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentFontSize < 40) {
                    currentFontSize += 2;
                    updateFont();
                }
            }
        });

        // Options -> Decrease Font Size
        decreaseFontItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentFontSize > 10) {
                    currentFontSize -= 2;
                    updateFont();
                }
            }
        });

        // Options -> Toggle Line Numbers
        toggleLineNumbersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (scrollPane.getRowHeader() != null && scrollPane.getRowHeader().getView() != null) {
                    scrollPane.setRowHeaderView(null);
                } else {
                    scrollPane.setRowHeaderView(lineNumberGutter);
                }
                scrollPane.revalidate();
                scrollPane.repaint();
            }
        });
    }

    /**
     * Applies colors based on light or dark selection
     */
    private void applyTheme() {
        if (isDarkTheme) {
            textArea.setBackground(DARK_BG);
            textArea.setForeground(DARK_FG);
            textArea.setCaretColor(DARK_FG);
            lineNumberGutter.setBackground(DARK_GUTTER_BG);
            lineNumberGutter.setForeground(DARK_GUTTER_FG);
        } else {
            textArea.setBackground(LIGHT_BG);
            textArea.setForeground(LIGHT_FG);
            textArea.setCaretColor(LIGHT_FG);
            lineNumberGutter.setBackground(LIGHT_GUTTER_BG);
            lineNumberGutter.setForeground(LIGHT_GUTTER_FG);
        }
        frame.repaint();
    }

    /**
     * Updates font sizes across components synchronously
     */
    private void updateFont() {
        Font newFont = new Font("Monospaced", Font.PLAIN, currentFontSize);
        textArea.setFont(newFont);
        lineNumberGutter.setFont(newFont);
    }

    /**
     * Opens the Create Project dialog (CreateProjectUI) as a modal dialog
     * centered over the main frame.
     */
    private void openCreateProjectDialog() {
        JDialog dialog = new JDialog(frame, "Create New Project", true);
        CreateProjectUI createProjectUI = new CreateProjectUI();
        dialog.setContentPane(createProjectUI.getRootComponent());
        dialog.setSize(500, 260);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    /**
     * Component that renders line numbers for a target JTextArea.
     */
    private static class LineNumberGutter extends JPanel {
        private final JTextArea textArea;

        public LineNumberGutter(JTextArea textArea) {
            this.textArea = textArea;
            setFont(textArea.getFont());

            // Listen for document structural changes to trigger dynamic line counts
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) { repaint(); }
                public void insertUpdate(DocumentEvent e) { repaint(); }
                public void removeUpdate(DocumentEvent e) { repaint(); }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(getGutterWidth(), textArea.getHeight());
        }

        private int getGutterWidth() {
            int lineCount = Math.max(1, textArea.getLineCount());
            int digits = Math.max(2, String.valueOf(lineCount).length());
            return getFontMetrics(getFont()).charWidth('0') * digits + 15;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(getFont());
            g.setColor(getForeground());

            FontMetrics metrics = g.getFontMetrics();
            int fontHeight = metrics.getHeight();
            int fontAscent = metrics.getAscent();

            Element root = textArea.getDocument().getDefaultRootElement();
            int lineCount = root.getElementCount();
            int gutterWidth = getWidth();

            for (int i = 0; i < lineCount; i++) {
                String label = String.valueOf(i + 1);
                int labelWidth = metrics.stringWidth(label);
                
                // Calculate position matching JTextArea line coordinates
                int yOffset = (i * fontHeight) + fontAscent + textArea.getInsets().top;
                
                // Right-align numbers inside the gutter with 8px buffer space
                g.drawString(label, gutterWidth - labelWidth - 8, yOffset);
            }
        }
    }
}