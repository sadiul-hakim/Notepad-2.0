package notepad;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import org.json.JSONObject;

public class NotePad extends JFrame implements ActionListener {

    private static final ShowCrypto showCrypto = new ShowCrypto();

    private JMenuItem newItem;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem print;
    private JMenuItem exit;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;
    private JMenuItem selectAll;
    private JMenuItem about;
    private JMenuItem font;
    private JMenuItem theme;
    private JMenuItem sha256;
    private JMenuItem md5;
    private JMenuItem sha1;
    private JCheckBoxMenuItem lineWrap;
    private JMenuItem newWindow;

    //popup menu items
    private JMenuItem toUpperCase;
    private JMenuItem toLowerCaseCase;
    private JMenuItem puCopy;
    private JMenuItem puCut;
    private JMenuItem puPaste;

    public static JTextArea mainArea;
    private JScrollPane areaScrollPane;
    private int mainAreaFontSize = 12;
    private Font areaFont;

    private JMenuItem newFileTool;
    private JMenuItem saveTool;
    private JMenuItem printTool;
    private JMenuItem undoTool;
    private JMenuItem redoTool;
    private JMenuItem zoomInTool;
    private JMenuItem zoomOutTool;

    private UndoManager undoManager = new UndoManager();
    private JSONObject json;

    private static final JFileChooser fileChooser = new JFileChooser();
//    private static final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Only text file (.txt)", "txt");
    private File openedFile = null;
    private static Optional<String> defaultTheme = Optional.ofNullable("com.jtattoo.plaf.luna.LunaLookAndFeel");

    //initial values
    public static Optional<String> initialFontName = Optional.ofNullable(null);
    public static Optional<Integer> initialFontStyle = Optional.ofNullable(null);
    public static Optional<Integer> initialFontSize = Optional.ofNullable(null);
    public static Optional<Color> initialColor = Optional.ofNullable(null);
    public static Optional<String> initialTheme = Optional.ofNullable(null);

    //upcoing data
    Optional<String> selectedName = Optional.ofNullable(null);
    Optional<Integer> selectedStyle = Optional.ofNullable(null);
    Optional<Integer> selectedSize = Optional.ofNullable(null);
    Optional<Color> colorSelected = Optional.ofNullable(null);
    Optional<String> themeSelected = Optional.ofNullable(null);

    //constructor
    NotePad(Optional<String> selectedName, Optional<Integer> selectedStyle, Optional<Integer> selectedSize, Optional<Color> colorSelected, Optional<String> themeSelected) {
        //setting data
        this.selectedName = selectedName;
        this.selectedStyle = selectedStyle;
        this.selectedSize = selectedSize;
        this.colorSelected = colorSelected;
        this.themeSelected = themeSelected;

        //vars
        ImageIcon image = new ImageIcon(ClassLoader.getSystemResource("images/Notepad-icon.png"));

        if (this.themeSelected.isPresent()) {
            defaultTheme = Optional.ofNullable(this.themeSelected.get());
        }

        this.setTitle("Notepad");
        this.setIconImage(image.getImage());

        try {
            UIManager.setLookAndFeel(defaultTheme.get());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            Logger.getLogger(NotePad.class.getName()).log(Level.SEVERE, null, e);
        }

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        frameMenu();
        mainAreaAction();
        handleToolBar();
        handlePopupMenu();

    }

    private void handlePopupMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();

        toLowerCaseCase = new JMenuItem("Lower Case");
        toUpperCase = new JMenuItem("Upper Case");
        puCopy = new JMenuItem("Copy");
        puCut = new JMenuItem("Cut");
        puPaste = new JMenuItem("Paste");
        

        toLowerCaseCase.addActionListener(this);
        toUpperCase.addActionListener(this);
        puCopy.addActionListener(this);
        puCut.addActionListener(this);
        puPaste.addActionListener(this);
        

        popupMenu.add(toUpperCase);
        popupMenu.add(toLowerCaseCase);
        popupMenu.add(puCopy);
        popupMenu.add(puCut);
        popupMenu.add(puPaste);

        mainArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(mainArea, e.getX(), e.getY());
                }
            }

        });

        mainArea.add(popupMenu);
    }

    private void mainAreaAction() {
        String fontName;
        int fontStyle;
        int fontSize;
        Color fontColor;
        //vars
        mainArea = new JTextArea();
        areaScrollPane = new JScrollPane(mainArea);
        areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainArea.setWrapStyleWord(true);
        mainArea.setMargin(new Insets(5, 5, 0, 0));
        //take docs
        mainArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        if (selectedName.isPresent()) {
            fontName = selectedName.get();
        } else {
            fontName = initialFontName.get();
        }

        if (selectedStyle.isPresent()) {
            fontStyle = selectedStyle.get();

        } else {
            fontStyle = initialFontStyle.get();
        }

        if (selectedSize.isPresent()) {
            fontSize = selectedSize.get();

        } else {
            fontSize = initialFontSize.get();
        }

        if (colorSelected.isPresent()) {
            fontColor = colorSelected.get();

        } else {
            fontColor = Color.BLACK;
        }

        areaFont = new Font(fontName, fontStyle, fontSize);
        mainArea.setFont(areaFont);
        mainArea.setForeground(fontColor);

        this.add(areaScrollPane);

    }

    private void frameMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu view = new JMenu("View");
        JMenu tools = new JMenu("Tools");
        JMenu encrypt = new JMenu("Encrypt");
        JMenu help = new JMenu("Help");

        //file menu item
        newItem = new JMenuItem("New");
        newWindow = new JMenuItem("New Window");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        saveAs = new JMenuItem("Save As");
        print = new JMenuItem("Print");
        exit = new JMenuItem("Exit");
        file.add(newItem);
        file.add(newWindow);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.add(print);
        file.add(exit);

        //edit menu items
        cut = new JMenuItem("Cut");
        copy = new JMenuItem("Copy");
        paste = new JMenuItem("Paste");
        selectAll = new JMenuItem("SelectAll");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);

        //encrypt menu
        sha1 = new JMenuItem("sha-1");
        sha256 = new JMenuItem("sha-256");
        md5 = new JMenuItem("md5");
        encrypt.add(sha1);
        encrypt.add(sha256);
        encrypt.add(md5);

        //help menu item
        about = new JMenuItem("About");
        help.add(about);

        //format
        font = new JMenuItem("Font");
        theme = new JMenuItem("Theme");
        lineWrap = new JCheckBoxMenuItem("Line Wrap");

        font.addActionListener(this);
        theme.addActionListener(this);
        lineWrap.addActionListener(this);

        view.add(font);
        view.add(theme);
        view.add(lineWrap);

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(view);
        menuBar.add(tools);
        menuBar.add(encrypt);
        menuBar.add(help);

        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK));
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));

        about.addActionListener(this);
        newItem.addActionListener(this);
        newWindow.addActionListener(this);
        open.addActionListener(this);
        save.addActionListener(this);
        saveAs.addActionListener(this);
        print.addActionListener(this);
        exit.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);
        sha1.addActionListener(this);
        sha256.addActionListener(this);
        md5.addActionListener(this);

        this.setJMenuBar(menuBar);
    }

    private void handleToolBar() {

        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        this.add(toolbar, BorderLayout.NORTH);
        toolbar.setFloatable(false);
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageIcon newFile = new ImageIcon(ClassLoader.getSystemResource("images/Files-New-File-icon.png"));
        ImageIcon saveIcon = new ImageIcon(ClassLoader.getSystemResource("images/Actions-document-save-icon.png"));
        ImageIcon printIcon = new ImageIcon(ClassLoader.getSystemResource("images/print-icon.png"));

        ImageIcon undoIcon = new ImageIcon(ClassLoader.getSystemResource("images/Arrows-Undo-icon.png"));
        ImageIcon redoIcon = new ImageIcon(ClassLoader.getSystemResource("images/Arrows-Redo-icon.png"));
        ImageIcon zoomInIcon = new ImageIcon(ClassLoader.getSystemResource("images/Zoom-In-icon.png"));
        ImageIcon zoomOutIcon = new ImageIcon(ClassLoader.getSystemResource("images/Misc-Zoom-Out-icon.png"));

        newFileTool = new JMenuItem(newFile);
        saveTool = new JMenuItem(saveIcon);
        printTool = new JMenuItem(printIcon);
        undoTool = new JMenuItem(undoIcon);
        redoTool = new JMenuItem(redoIcon);
        zoomInTool = new JMenuItem(zoomInIcon);
        zoomOutTool = new JMenuItem(zoomOutIcon);

        newFileTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        undoTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        redoTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomInTool.setCursor(new Cursor(Cursor.HAND_CURSOR));
        zoomOutTool.setCursor(new Cursor(Cursor.HAND_CURSOR));

        newFileTool.addActionListener(this);
        saveTool.addActionListener(this);
        printTool.addActionListener(this);
        undoTool.addActionListener(this);
        redoTool.addActionListener(this);
        zoomInTool.addActionListener(this);
        zoomOutTool.addActionListener(this);

        newFileTool.setToolTipText("New File");
        printTool.setToolTipText("Print");
        saveTool.setToolTipText("Save");
        undoTool.setToolTipText("Undo");
        redoTool.setToolTipText("Redo");
        zoomInTool.setToolTipText("Zoom In");
        zoomOutTool.setToolTipText("Zoom Out");

        toolbar.add(newFileTool);
        toolbar.add(saveTool);
        toolbar.add(printTool);
        toolbar.add(undoTool);
        toolbar.add(redoTool);
        toolbar.add(zoomInTool);
        toolbar.add(zoomOutTool);

    }

    public static void main(String[] args) {

        try {
            Path path = Path.of(ClassLoader.getSystemResource("notepad/settings.json").toURI());
            String text = Files.readString(path);
            JSONObject json = new JSONObject(text);

            System.out.println("Ran");

            String name = json.getString("FontName");
            int style = json.getInt("FontStyle");
            int size = json.getInt("FontSize");
            String color = json.get("FontColor").toString();
            String theme = json.get("Theme").toString();

            initialFontName = Optional.ofNullable(name);
            initialFontStyle = Optional.ofNullable(style);
            initialFontSize = Optional.ofNullable(size);
            initialColor = Optional.ofNullable(Color.getColor(color, Color.black));
            initialTheme = Optional.ofNullable(theme);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NotePad frame = new NotePad(initialFontName, initialFontStyle, initialFontSize, initialColor, initialTheme);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == about) {
            About about = new About();
            about.setVisible(true);
        } else if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == newItem || e.getSource() == newFileTool) {
            mainArea.setText(null);
        } 
        else if (e.getSource() == newWindow) {
            new NotePad(selectedName, selectedStyle, selectedSize, colorSelected, themeSelected).setVisible(true);
        } 
        else if (e.getSource() == open) {
//            fileChooser.setAcceptAllFileFilterUsed(false);
//            fileChooser.addChoosableFileFilter(extensionFilter);

            int action = fileChooser.showOpenDialog(null);

            if (action != JFileChooser.APPROVE_OPTION) {
                return;
            } else {
                openedFile = fileChooser.getSelectedFile().getAbsoluteFile();
                String name = fileChooser.getName(openedFile);

                this.setTitle(name + " - Notepad");
                try {

                    BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                    mainArea.read(reader, null);
                } catch (Exception ex) {

                }

            }

        } else if (e.getSource() == saveAs) {

//            fileChooser.setAcceptAllFileFilterUsed(false);
//            fileChooser.addChoosableFileFilter(extensionFilter);
            int action = fileChooser.showSaveDialog(null);

            if (action != JFileChooser.APPROVE_OPTION) {
                return;
            } else {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                
//                if (!filename.endsWith(".txt")) {
//                    filename = filename + ".txt";
//                }

                try {

                    BufferedWriter writter = new BufferedWriter(new FileWriter(filename));
                    mainArea.write(writter);
                    openedFile = new File(filename);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } else if (e.getSource() == print || e.getSource() == printTool) {
            try {
                mainArea.print();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == cut) {
            mainArea.cut();
        } else if (e.getSource() == copy) {
            mainArea.copy();
        } else if (e.getSource() == paste) {
            mainArea.paste();
        } else if (e.getSource() == selectAll) {
            mainArea.selectAll();
        } else if (e.getSource() == save || e.getSource() == saveTool) {
            //contains bug
            String text = mainArea.getText();
            if (openedFile == null) {
//                fileChooser.setAcceptAllFileFilterUsed(false);
//                fileChooser.addChoosableFileFilter(extensionFilter);

                int action = fileChooser.showSaveDialog(null);

                if (action != JFileChooser.APPROVE_OPTION) {
                    return;
                } else {
                    String filename = fileChooser.getSelectedFile().getAbsolutePath();
//                    if (!filename.endsWith(".txt")) {
//                        filename = filename + ".txt";
//                        System.out.println(filename);
//                    }

                    try {
                        BufferedWriter writter = new BufferedWriter(new FileWriter(filename));
                        mainArea.write(writter);
                        openedFile = new File(filename);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                Path path = Path.of(openedFile.toString());
                try {
                    Files.writeString(path, text);
                } catch (Exception ex) {

                }
            }
        } else if (e.getSource() == undoTool) {
            System.out.println("Hakim");
            undoManager.undo();
        } else if (e.getSource() == redoTool) {
            undoManager.redo();
        } else if (e.getSource() == zoomInTool) {
            if (mainAreaFontSize <= 36) {
                Font mainAreaFont = mainArea.getFont();
                String name = mainAreaFont.getName();
                int size = mainAreaFont.getSize();
                int style = mainAreaFont.getStyle();
                size += 4;
                areaFont = new Font(name, style, size);
                mainArea.setFont(areaFont);
            }
        } else if (e.getSource() == zoomOutTool) {
            if (mainAreaFontSize >= 12) {
                Font mainAreaFont = mainArea.getFont();
                String name = mainAreaFont.getName();
                int size = mainAreaFont.getSize();
                int style = mainAreaFont.getStyle();
                size -= 4;
                areaFont = new Font(name, style, size);
                mainArea.setFont(areaFont);
            }
        } else if (e.getSource() == font) {

            FontPallet frame = new FontPallet(selectedName, selectedStyle, selectedSize, colorSelected, defaultTheme);
            frame.setVisible(true);
            this.dispose();

        } else if (e.getSource() == theme) {
            ThemePallet frame = new ThemePallet(selectedName, selectedStyle, selectedSize, colorSelected, defaultTheme);
            frame.setVisible(true);

            this.dispose();

        } else if (e.getSource() == lineWrap) {
            mainArea.setLineWrap(true);
        } else if (e.getSource() == sha1) {
            ShowCrypto.jTextArea1.setText(encrypt(mainArea.getText(), "SHA-1"));
            showCrypto.setVisible(true);
        } else if (e.getSource() == sha256) {
            ShowCrypto.jTextArea1.setText(encrypt(mainArea.getText(), "SHA-256"));
            showCrypto.setVisible(true);
            
        } else if (e.getSource() == md5) {
            ShowCrypto.jTextArea1.setText(encrypt(mainArea.getText(), "MD5"));
            showCrypto.setVisible(true);
            
        }
        else if (e.getSource() == toUpperCase) {
            char[] selectedText = mainArea.getSelectedText().toUpperCase().toCharArray();
            int start = mainArea.getSelectionStart();
            int end = mainArea.getSelectionEnd();

            char[] allText = mainArea.getText().toCharArray();

            int j = 0;
            for (int i = start; i < end; i++) {
                allText[i] = selectedText[j];
                j++;
            }

            mainArea.setText(new String(allText));

        } else if (e.getSource() == toLowerCaseCase) {
            char[] selectedText = mainArea.getSelectedText().toLowerCase().toCharArray();
            int start = mainArea.getSelectionStart();
            int end = mainArea.getSelectionEnd();

            char[] allText = mainArea.getText().toCharArray();

            int j = 0;
            for (int i = start; i < end; i++) {
                allText[i] = selectedText[j];
                j++;
            }

            mainArea.setText(new String(allText));
        } else if (e.getSource() == puCopy) {
            mainArea.copy();
        } else if (e.getSource() == puCut) {
            mainArea.cut();
        } else if (e.getSource() == puPaste) {
            mainArea.paste();
        }

    }

    public String encrypt(String text, String alg) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance(alg);
            byte[] crypttoChar = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            
            for (byte b : crypttoChar) {
                String hex=Integer.toHexString(0xff & b);
                if(hex.length()==1){
                    sb.append('0');
                }
                sb.append(hex);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NotePad.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();

    }

}
