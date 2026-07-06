package org.nick124;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import org.nick124.FileChooser.CreateFileTree;
import org.nick124.FileChooser.Win11FileDialog;
import org.nick124.Settings.ConfigureSettings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GUI {
    public static JFrame frame;
    public static JTextArea textArea;
    public static JScrollPane scrollPane;
    public static LineNumberGutter lineNumberGutter;
    public static JMenu optionsMenu;
    public static JSplitPane splitPane;
    public static JTree explorerTree;
    public static JScrollPane explorerScroll;
    public static DefaultTreeCellRenderer treeCellRenderer;

    public static boolean isDarkTheme = false;
    public static int currentFontSize = 14;

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
            @Override
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
    private Path projectPath = null;

    public void setFile(java.io.File f) {currentFile = f;}

    private void saveFile(boolean isSaveAs) {
        if (isSaveAs || currentFile == null) {
//        		JnaFileChooser fc = new JnaFileChooser();
//        		currentFile = fc.getSelectedFile();
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

    private void loadSettings() {
        if (projectPath != null) {
            File root = projectPath.toFile();
            File metadata = new File(root, ".pulse");
            if (metadata.exists()) {
                File settingsFile = new File(metadata, ".settings");
                if (settingsFile.exists()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        if (!settingsFile.exists()) {
                            ObjectNode json = mapper.createObjectNode();
                            json.put("Theme", "DARK");
                            json.put("Font Size", "14");
                            json.put("Line Numbering", "true");
                            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(settingsFile))) {
                                writer.write(json.toPrettyString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ObjectNode json = (ObjectNode) mapper.readTree(settingsFile);
                            ConfigureSettings.applySettings(json);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }        
        }
    }


    private void openProject() {
//    	JnaFileChooser fc = new JnaFileChooser();
    	projectPath = Win11FileDialog.openFolder();
    	if (projectPath != null) {
    	    File root = projectPath.toFile();

    	    explorerTree.setModel(
    	        new DefaultTreeModel(CreateFileTree.createNode(root))
    	    );
            explorerTree.expandRow(0);
    	    File metadata = new File(root, ".pulse");

    	    if (!metadata.exists()) {
    	        if (metadata.mkdir()) {
    	        	File data = new File(metadata, ".settings");
    	        	try {data.createNewFile();} catch (IOException e) {e.printStackTrace();}
    	        		ObjectMapper mapper = new ObjectMapper();
    	        		ObjectNode json = mapper.createObjectNode();
    	        		json.put("Theme", "DARK");
    	        		json.put("Font Size", "14");
    	        		json.put("Line Numbering", "true");
    	        		try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(data))) {
	    	        		writer.write(json.toPrettyString());
    	        		} catch (IOException e) {e.printStackTrace();}
    	        } else {
                    File data = new File(metadata, ".settings");
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode json = mapper.createObjectNode();
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(data))) {
                        json = (ObjectNode) mapper.readTree(reader);
                        ConfigureSettings.applySettings(json);                      
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ConfigureSettings.applySettings(json);
    	        }
    	    } else {
    	        File data = new File(metadata, ".settings");
    	        ObjectMapper mapper = new ObjectMapper();
    	        ObjectNode json = mapper.createObjectNode();
    	        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(data))) {
    	            json = (ObjectNode) mapper.readTree(reader);
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	        ConfigureSettings.applySettings(json);
            }
    	}
    }

    private void openFile(File f) {
    	currentFile = f;
    	textArea.setText("");
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
    private void openFile() {
//    	JnaFileChooser fc = new JnaFileChooser();
    	Path filePath = Win11FileDialog.openFile();
//    		currentFile = fc.getSelectedFile();
//
//        if (currentFile != null) {
//            textArea.setText("");
//            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(currentFile))) {
//                String line;
//                boolean firstLine = true;
//                while ((line = br.readLine()) != null) {
//                    if (!firstLine) {
//                        textArea.append("\n");
//                    }
//                    textArea.append(line);
//                    firstLine = false;
//                }
//            } catch (java.io.IOException e) {
//                javax.swing.JOptionPane.showMessageDialog(frame, "Error opening file: " + e.getMessage());
//            }
//        }
//        }
    }
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Pulse IDE");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            @Override
			public void actionPerformed(ActionEvent e) {
                openProject();
            }});

        JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save");
        mntmNewMenuItem_1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        	    java.awt.event.KeyEvent.VK_S,
        	    java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        fileMenu.add(mntmNewMenuItem_1);

        JMenuItem mntmNewMenuItem_2 = new JMenuItem("Save as...");
        fileMenu.add(mntmNewMenuItem_2);
        mntmNewMenuItem_2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                saveFile(true);
            }});

        mntmNewMenuItem_1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                saveFile(false);
            }});

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

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, currentFontSize));
        textArea.setMargin(new Insets(5, 5, 5, 5));

        lineNumberGutter = new LineNumberGutter(textArea);

        // Wrap everything in a JScrollPane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumberGutter);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        explorerTree = new JTree();
        explorerTree.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
            		return;
            	}

            	javax.swing.tree.TreePath path = explorerTree.getPathForLocation(e.getX(), e.getY());
            	if (path == null) {
            		return;
            	}

            	Object value = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            	if (value instanceof File file && file.isFile()) {
            		openFile(file);
            	}
        	}
        });
        explorerTree.setModel(new DefaultTreeModel(
        	new DefaultMutableTreeNode("JTree") {
        		{
        		}
        	}
        ));
        explorerTree.setRootVisible(true);

        treeCellRenderer = new DefaultTreeCellRenderer() {

            FileSystemView fsv = FileSystemView.getFileSystemView();

            @Override
            public Component getTreeCellRendererComponent(
                    JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {

                super.getTreeCellRendererComponent(
                        tree, value, sel, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object obj = node.getUserObject();

                if (obj instanceof File file) {
                    setText(file.getName().isEmpty()
                            ? file.getAbsolutePath()
                            : file.getName());

                    setIcon(fsv.getSystemIcon(file));
                }

                return this;
            }
        };
        explorerTree.setCellRenderer(treeCellRenderer);


        explorerScroll = new JScrollPane(explorerTree);
        explorerScroll.setPreferredSize(new Dimension(250, 0));
        explorerScroll.setBorder(BorderFactory.createEmptyBorder());

        // Split Pane
        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                explorerScroll,
                scrollPane);

        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(4);
        splitPane.setBorder(null);

        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        // Apply baseline layout colors
        ConfigureSettings.applyTheme();

        mntmNewProject.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                openCreateProjectDialog();
            }
        });

        // Options -> Toggle Dark Theme
        toggleThemeItem.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                isDarkTheme = !isDarkTheme;
                ConfigureSettings.applyTheme();
            }
        });

        // Options -> Increase Font Size
        increaseFontItem.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (currentFontSize < 40) {
                    currentFontSize += 2;
                    updateFont();
                }
            }
        });

        // Options -> Decrease Font Size
        decreaseFontItem.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (currentFontSize > 10) {
                    currentFontSize -= 2;
                    updateFont();
                }
            }
        });

        // Options -> Toggle Line Numbers
        toggleLineNumbersItem.addActionListener(new ActionListener() {
            @Override
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
    explorerTree.addTreeSelectionListener(e -> {

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode) explorerTree.getLastSelectedPathComponent();

        if (node == null) {
			return;
		}

        File file = (File) node.getUserObject();

        if (!file.isFile()) {
			return;
		}

        openFile(file);
    });
    }

    public static void updateFont() {
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
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    /**
     * Component that renders line numbers for a target JTextArea.
     */
    public static class LineNumberGutter extends JPanel {
        private final JTextArea textArea;

        public LineNumberGutter(JTextArea textArea) {
            this.textArea = textArea;
            setFont(textArea.getFont());

            // Listen for document structural changes to trigger dynamic line counts
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                @Override
				public void changedUpdate(DocumentEvent e) { repaint(); }
                @Override
				public void insertUpdate(DocumentEvent e) { repaint(); }
                @Override
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