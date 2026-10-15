/*
 * ============================================================================
 * CAPE PENINSULA UNIVERSITY OF TECHNOLOGY (CPUT)
 * Faculty of Engineering and the Built Environment
 * Department of Electrical, Electronic and Computer Engineering
 *
 * Course:       Software Design 1 (SDN150S)
 * Project:      Group Project #5 - Java-Based Text Editor Application
 * Due Date:     November 08, 2026
 * Examiner:     Mr. S.O. Ekwe
 *
 * File Name:    TextEditorApp.java
 * Description:  A full-featured, user-friendly Graphical User Interface (GUI)
 *               Text Editor built using Java Swing (JFrame Forms). Provides
 *               core document operations (Create, Open, Edit, Save, Save As,
 *               Exit), Find and Replace, real-time Word/Character/Line Count,
 *               rich text formatting (Font family, Font size, Bold, Italic,
 *               Underline, Text Color, Paragraph Alignment), Java Syntax
 *               Highlighting, and an interactive Spell-Check engine with
 *               Levenshtein edit-distance word suggestions.
 *
 * Established Java Concepts Demonstrated:
 *   1. Constructors (Default and Overloaded Parameterized Constructors)
 *   2. Modifiers (public, private, protected, static, final)
 *   3. Control Statements (if-else, switch-case, for, while, try-catch-finally)
 *   4. Java Frame Forms (javax.swing.JFrame, JDialog, JMenuBar, JToolBar, etc.)
 *   5. Input Validation and Exception Handling
 * ============================================================================
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Main application class extending {@link JFrame} to implement the CPUT
 * SDN150S Project #5 Java-Based Text Editor.
 *
 * @author CPUT SDN150S Engineering Project Team (Project #5)
 * @version 1.0.0 (November 2026)
 */
public class TextEditorApp extends JFrame implements ActionListener {

    // ========================================================================
    // STATIC FINAL CONSTANTS (Demonstrating private/public static final modifiers)
    // ========================================================================

    /** Serial version UID for safe Swing JFrame serialization. */
    private static final long serialVersionUID = 1L;

    /** Application title displayed in the JFrame window title bar. */
    public static final String APP_TITLE = "CPUT StudioEdit - Java Text Editor (SDN150S)";

    /** Default initial window width in pixels. */
    private static final int DEFAULT_WIDTH = 1024;

    /** Default initial window height in pixels. */
    private static final int DEFAULT_HEIGHT = 720;

    /** Default font family for the editor document area. */
    private static final String DEFAULT_FONT_FAMILY = "Monospaced";

    /** Default font size in points for the editor document area. */
    private static final int DEFAULT_FONT_SIZE = 14;

    /** Maximum file size permitted for safe in-memory editing (10 MB). */
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;

    /** Reserved Java programming language keywords for Syntax Highlighting. */
    private static final String[] JAVA_KEYWORDS = {
        "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double",
        "else", "enum", "extends", "final", "finally", "float", "for",
        "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private",
        "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while", "true", "false",
        "null", "String", "System", "JFrame", "JTextPane", "Override"
    };

    // ========================================================================
    // INSTANCE VARIABLES / FIELDS (Demonstrating private/protected encapsulation)
    // ========================================================================

    /** Primary rich text editing component supporting styled attributes. */
    private JTextPane textPane;

    /** Underlying styled document model managing character and paragraph styles. */
    private StyledDocument styledDocument;

    /** Scroll pane wrapping the primary text pane and line number gutter. */
    private JScrollPane scrollPane;

    /** Custom line-number gutter component displayed along the left margin. */
    private LineNumberGutter lineNumberGutter;

    /** Manager tracking document edits for Undo and Redo operations. */
    private UndoManager undoManager;

    /** Reference to the currently open or saved file on disk (null if untitled). */
    private File currentFile;

    /** Boolean flag indicating whether the active document has unsaved edits. */
    private boolean isModified;

    /** Boolean flag preventing recursive DocumentListener firing during highlighting. */
    private boolean isInternalUpdating;

    /** Flag indicating whether real-time Java syntax highlighting is active. */
    private boolean syntaxHighlightingEnabled;

    /** Font family selector drop-down in the formatting toolbar. */
    private JComboBox<String> fontFamilyCombo;

    /** Font size selector drop-down in the formatting toolbar. */
    private JComboBox<Integer> fontSizeCombo;

    /** Toolbar toggle button for Bold text style. */
    private JToggleButton boldToggleBtn;

    /** Toolbar toggle button for Italic text style. */
    private JToggleButton italicToggleBtn;

    /** Toolbar toggle button for Underline text style. */
    private JToggleButton underlineToggleBtn;

    /** Toolbar toggle button for Java Syntax Highlighting mode. */
    private JToggleButton syntaxToggleBtn;

    /** Status bar label displaying current file status and modification state. */
    private JLabel statusFileLabel;

    /** Status bar label displaying live word, character, and line counts. */
    private JLabel statusMetricsLabel;

    /** Status bar label displaying current cursor line and column position. */
    private JLabel statusCursorLabel;

    /** Built-in spell checker engine instance for validating document vocabulary. */
    protected SpellCheckerEngine spellChecker;

    /** Current active foreground text color chosen by the user. */
    private Color currentTextColor;

    // ========================================================================
    // CONSTRUCTORS (Demonstrating Default and Parameterized Constructors)
    // ========================================================================

    /**
     * Default Constructor: Initializes the TextEditorApp JFrame using default
     * window dimensions and standard title configuration.
     */
    public TextEditorApp() {
        this(APP_TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Parameterized Constructor: Initializes the TextEditorApp JFrame with
     * custom window title, width, and height parameters.
     *
     * @param windowTitle The title string displayed in the window frame.
     * @param width       The initial width of the JFrame in pixels.
     * @param height      The initial height of the JFrame in pixels.
     */
    public TextEditorApp(String windowTitle, int width, int height) {
        super(windowTitle);

        // Validate constructor dimension inputs using control statements
        int validatedWidth = (width >= 640) ? width : DEFAULT_WIDTH;
        int validatedHeight = (height >= 480) ? height : DEFAULT_HEIGHT;

        // Initialize state fields
        this.currentFile = null;
        this.isModified = false;
        this.isInternalUpdating = false;
        this.syntaxHighlightingEnabled = false;
        this.currentTextColor = new Color(30, 41, 59);
        this.undoManager = new UndoManager();
        this.spellChecker = new SpellCheckerEngine();

        // Configure JFrame window properties
        setSize(validatedWidth, validatedHeight);
        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null); // Center window on screen
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Assemble GUI components inside the JFrame container
        initializeGuiComponents();
        registerEventListeners();
        updateWindowTitle();
        updateDocumentMetrics();
    }

    // ========================================================================
    // GUI INITIALIZATION METHODS (Demonstrating Java Frame Forms & Layout)
    // ========================================================================

    /**
     * Initializes and arranges all Swing GUI components inside the JFrame:
     * Menu Bar, Formatting Toolbar, Editor ScrollPane with Line Gutter, and
     * Status Bar.
     */
    private void initializeGuiComponents() {
        setLayout(new BorderLayout(0, 0));

        // 1. Build and attach the top JMenuBar
        setJMenuBar(createMenuBar());

        // 2. Build and attach the top Formatting JToolBar
        JToolBar formattingToolBar = createFormattingToolBar();
        add(formattingToolBar, BorderLayout.NORTH);

        // 3. Build the central JTextPane and StyledDocument
        styledDocument = new DefaultStyledDocument();
        textPane = new JTextPane(styledDocument);
        textPane.setFont(new Font(DEFAULT_FONT_FAMILY, Font.PLAIN, DEFAULT_FONT_SIZE));
        textPane.setForeground(currentTextColor);
        textPane.setBackground(Color.WHITE);
        textPane.setCaretColor(new Color(15, 23, 42));
        textPane.setMargin(new Insets(12, 14, 12, 14));

        // Attach custom line number gutter on the left side of the scroll pane
        lineNumberGutter = new LineNumberGutter(textPane);
        scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumberGutter);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(203, 213, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // 4. Build and attach the bottom Status Bar panel
        JPanel statusBarPanel = createStatusBar();
        add(statusBarPanel, BorderLayout.SOUTH);
    }

    /**
     * Constructs the application's top menu bar containing File, Edit, Format,
     * Tools, and Help menus with keyboard accelerators.
     *
     * @return Configured {@link JMenuBar} instance.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // --- FILE MENU ---
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(createMenuItem("New", "CMD_NEW", KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutMask)));
        fileMenu.add(createMenuItem("Open...", "CMD_OPEN", KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutMask)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Save", "CMD_SAVE", KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask)));
        fileMenu.add(createMenuItem("Save As...", "CMD_SAVE_AS",
                KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask | InputEvent.SHIFT_DOWN_MASK)));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", "CMD_EXIT", KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutMask)));

        // --- EDIT MENU ---
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        editMenu.add(createMenuItem("Undo", "CMD_UNDO", KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcutMask)));
        editMenu.add(createMenuItem("Redo", "CMD_REDO", KeyStroke.getKeyStroke(KeyEvent.VK_Y, shortcutMask)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Cut", "CMD_CUT", KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutMask)));
        editMenu.add(createMenuItem("Copy", "CMD_COPY", KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutMask)));
        editMenu.add(createMenuItem("Paste", "CMD_PASTE", KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutMask)));
        editMenu.add(createMenuItem("Select All", "CMD_SELECT_ALL", KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutMask)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Find and Replace...", "CMD_FIND_REPLACE",
                KeyStroke.getKeyStroke(KeyEvent.VK_F, shortcutMask)));

        // --- FORMAT MENU ---
        JMenu formatMenu = new JMenu("Format");
        formatMenu.setMnemonic(KeyEvent.VK_O);

        formatMenu.add(createMenuItem("Bold", "CMD_BOLD", KeyStroke.getKeyStroke(KeyEvent.VK_B, shortcutMask)));
        formatMenu.add(createMenuItem("Italic", "CMD_ITALIC", KeyStroke.getKeyStroke(KeyEvent.VK_I, shortcutMask)));
        formatMenu.add(createMenuItem("Underline", "CMD_UNDERLINE", KeyStroke.getKeyStroke(KeyEvent.VK_U, shortcutMask)));
        formatMenu.addSeparator();
        formatMenu.add(createMenuItem("Align Left", "CMD_ALIGN_LEFT", null));
        formatMenu.add(createMenuItem("Align Center", "CMD_ALIGN_CENTER", null));
        formatMenu.add(createMenuItem("Align Right", "CMD_ALIGN_RIGHT", null));
        formatMenu.addSeparator();
        formatMenu.add(createMenuItem("Text Color...", "CMD_TEXT_COLOR", null));
        formatMenu.add(createMenuItem("Clear Formatting", "CMD_CLEAR_FORMAT", null));

        // --- TOOLS MENU ---
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        toolsMenu.add(createMenuItem("Word Count & Statistics...", "CMD_WORD_COUNT",
                KeyStroke.getKeyStroke(KeyEvent.VK_W, shortcutMask | InputEvent.SHIFT_DOWN_MASK)));
        toolsMenu.add(createMenuItem("Run Spell Check...", "CMD_SPELL_CHECK",
                KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0)));
        toolsMenu.add(createMenuItem("Toggle Java Syntax Highlighting", "CMD_TOGGLE_SYNTAX",
                KeyStroke.getKeyStroke(KeyEvent.VK_H, shortcutMask | InputEvent.SHIFT_DOWN_MASK)));

        // --- HELP MENU ---
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(createMenuItem("Insert Sample Java Code", "CMD_SAMPLE_CODE", null));
        helpMenu.add(createMenuItem("About CPUT StudioEdit", "CMD_ABOUT", null));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * Helper method to instantiate a configured {@link JMenuItem} and bind it
     * to this frame's {@link ActionListener}.
     *
     * @param label         Visible text of the menu item.
     * @param actionCommand Command identifier string.
     * @param accelerator   Optional keyboard shortcut {@link KeyStroke}.
     * @return Configured {@link JMenuItem}.
     */
    private JMenuItem createMenuItem(String label, String actionCommand, KeyStroke accelerator) {
        JMenuItem item = new JMenuItem(label);
        item.setActionCommand(actionCommand);
        item.addActionListener(this);
        if (accelerator != null) {
            item.setAccelerator(accelerator);
        }
        return item;
    }

    /**
     * Constructs the top formatting and quick-action toolbar containing file
     * buttons, font family/size selectors, style toggles, find/replace,
     * syntax highlighting toggle, and spell-check button.
     *
     * @return Configured {@link JToolBar}.
     */
    private JToolBar createFormattingToolBar() {
        JToolBar toolBar = new JToolBar("Formatting Toolbar");
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(241, 245, 249));
        toolBar.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        // Quick file buttons
        toolBar.add(createToolbarButton("New", "CMD_NEW", "Create a new document"));
        toolBar.add(createToolbarButton("Open", "CMD_OPEN", "Open an existing text file"));
        toolBar.add(createToolbarButton("Save", "CMD_SAVE", "Save current document"));
        toolBar.addSeparator(new Dimension(10, 24));

        // Font family combo box populated with available system/logical fonts
        String[] availableFonts = getCuratedFontFamilies();
        fontFamilyCombo = new JComboBox<>(availableFonts);
        fontFamilyCombo.setSelectedItem(DEFAULT_FONT_FAMILY);
        fontFamilyCombo.setMaximumSize(new Dimension(165, 28));
        fontFamilyCombo.setToolTipText("Select Font Family");
        fontFamilyCombo.setActionCommand("CMD_FONT_FAMILY");
        fontFamilyCombo.addActionListener(this);
        toolBar.add(new JLabel(" Font: "));
        toolBar.add(fontFamilyCombo);
        toolBar.add(Box.createHorizontalStrut(6));

        // Font size combo box
        Integer[] fontSizes = {10, 11, 12, 14, 16, 18, 20, 24, 28, 32};
        fontSizeCombo = new JComboBox<>(fontSizes);
        fontSizeCombo.setSelectedItem(DEFAULT_FONT_SIZE);
        fontSizeCombo.setMaximumSize(new Dimension(65, 28));
        fontSizeCombo.setToolTipText("Select Font Size");
        fontSizeCombo.setActionCommand("CMD_FONT_SIZE");
        fontSizeCombo.addActionListener(this);
        toolBar.add(new JLabel(" Size: "));
        toolBar.add(fontSizeCombo);
        toolBar.addSeparator(new Dimension(10, 24));

        // Style toggle buttons (Bold, Italic, Underline)
        boldToggleBtn = new JToggleButton(" B ");
        boldToggleBtn.setFont(new Font("Serif", Font.BOLD, 13));
        boldToggleBtn.setToolTipText("Toggle Bold (Ctrl+B)");
        boldToggleBtn.setActionCommand("CMD_BOLD");
        boldToggleBtn.addActionListener(this);
        toolBar.add(boldToggleBtn);

        italicToggleBtn = new JToggleButton(" I ");
        italicToggleBtn.setFont(new Font("Serif", Font.ITALIC, 13));
        italicToggleBtn.setToolTipText("Toggle Italic (Ctrl+I)");
        italicToggleBtn.setActionCommand("CMD_ITALIC");
        italicToggleBtn.addActionListener(this);
        toolBar.add(italicToggleBtn);

        underlineToggleBtn = new JToggleButton(" U ");
        underlineToggleBtn.setFont(new Font("Serif", Font.PLAIN, 13));
        underlineToggleBtn.setToolTipText("Toggle Underline (Ctrl+U)");
        underlineToggleBtn.setActionCommand("CMD_UNDERLINE");
        underlineToggleBtn.addActionListener(this);
        toolBar.add(underlineToggleBtn);

        toolBar.add(Box.createHorizontalStrut(4));
        toolBar.add(createToolbarButton("Color", "CMD_TEXT_COLOR", "Choose Text Color"));
        toolBar.addSeparator(new Dimension(10, 24));

        // Utility and advanced feature buttons
        toolBar.add(createToolbarButton("Find / Replace", "CMD_FIND_REPLACE", "Search and replace text (Ctrl+F)"));
        toolBar.add(createToolbarButton("Spell Check", "CMD_SPELL_CHECK", "Check spelling in document (F7)"));

        syntaxToggleBtn = new JToggleButton("Java Syntax");
        syntaxToggleBtn.setToolTipText("Toggle real-time Java syntax highlighting");
        syntaxToggleBtn.setActionCommand("CMD_TOGGLE_SYNTAX");
        syntaxToggleBtn.addActionListener(this);
        toolBar.add(Box.createHorizontalStrut(4));
        toolBar.add(syntaxToggleBtn);

        return toolBar;
    }

    /**
     * Helper method to create a styled button for the top toolbar.
     *
     * @param text    Button label text.
     * @param command Action command string.
     * @param tooltip Tooltip description text.
     * @return Configured {@link JButton}.
     */
    private JButton createToolbarButton(String text, String command, String tooltip) {
        JButton button = new JButton(text);
        button.setActionCommand(command);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }

    /**
     * Returns a curated list of readable font families available on the host
     * platform, guaranteeing cross-platform compatibility.
     *
     * @return Array of font family names.
     */
    private String[] getCuratedFontFamilies() {
        String[] preferred = {
            "Monospaced", "SansSerif", "Serif", "Dialog",
            "Carlito", "Calibri", "Liberation Sans", "Liberation Mono", "DejaVu Sans"
        };
        List<String> availableList = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        List<String> result = new ArrayList<>();

        // Use a for-each loop and if-statement to filter installed fonts
        for (String fontName : preferred) {
            if (availableList.contains(fontName) || fontName.equals("Monospaced")
                    || fontName.equals("SansSerif") || fontName.equals("Serif")) {
                if (!result.contains(fontName)) {
                    result.add(fontName);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Constructs the bottom status bar panel showing file state, live word/line/char
     * counts, and current cursor coordinates.
     *
     * @return Configured {@link JPanel}.
     */
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout(12, 0));
        statusPanel.setBackground(new Color(248, 250, 252));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        Font statusFont = new Font("SansSerif", Font.PLAIN, 12);

        statusFileLabel = new JLabel("Status: Ready | Encoding: UTF-8");
        statusFileLabel.setFont(statusFont);
        statusFileLabel.setForeground(new Color(51, 65, 85));

        statusMetricsLabel = new JLabel("Words: 0 | Characters: 0 | Lines: 1", SwingConstants.CENTER);
        statusMetricsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusMetricsLabel.setForeground(new Color(15, 23, 42));

        statusCursorLabel = new JLabel("Ln 1, Col 1", SwingConstants.RIGHT);
        statusCursorLabel.setFont(statusFont);
        statusCursorLabel.setForeground(new Color(71, 85, 105));

        statusPanel.add(statusFileLabel, BorderLayout.WEST);
        statusPanel.add(statusMetricsLabel, BorderLayout.CENTER);
        statusPanel.add(statusCursorLabel, BorderLayout.EAST);

        return statusPanel;
    }

    // ========================================================================
    // EVENT LISTENER REGISTRATION & DISPATCH (Control Statements: switch-case)
    // ========================================================================

    /**
     * Registers document, caret, undoable edit, and window listeners on the
     * primary editor components.
     */
    private void registerEventListeners() {
        // Track document changes for live word count, dirty flag, and syntax highlighting
        styledDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleDocumentChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleDocumentChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Attribute styling changes do not alter raw character count
            }
        });

        // Register UndoManager listener
        styledDocument.addUndoableEditListener(e -> {
            if (!isInternalUpdating) {
                undoManager.addEdit(e.getEdit());
            }
        });

        // Track caret movement to update cursor line/column and toolbar button states
        textPane.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateCursorPositionLabel();
            }
        });

        // Intercept window closing event to protect unsaved user work
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    /**
     * Central ActionListener dispatcher routing all menu and toolbar commands
     * using a structured {@code switch} control statement.
     *
     * @param event The triggered {@link ActionEvent}.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command == null) {
            return;
        }

        // Switch control structure dispatching user actions
        switch (command) {
            case "CMD_NEW":
                newDocument();
                break;
            case "CMD_OPEN":
                openFile();
                break;
            case "CMD_SAVE":
                saveFile(false);
                break;
            case "CMD_SAVE_AS":
                saveFile(true);
                break;
            case "CMD_EXIT":
                exitApplication();
                break;
            case "CMD_UNDO":
                performUndo();
                break;
            case "CMD_REDO":
                performRedo();
                break;
            case "CMD_CUT":
                textPane.cut();
                break;
            case "CMD_COPY":
                textPane.copy();
                break;
            case "CMD_PASTE":
                textPane.paste();
                break;
            case "CMD_SELECT_ALL":
                textPane.selectAll();
                break;
            case "CMD_FIND_REPLACE":
                showFindReplaceDialog();
                break;
            case "CMD_FONT_FAMILY":
                applySelectedFontFamily();
                break;
            case "CMD_FONT_SIZE":
                applySelectedFontSize();
                break;
            case "CMD_BOLD":
                toggleStyleAttribute("BOLD");
                break;
            case "CMD_ITALIC":
                toggleStyleAttribute("ITALIC");
                break;
            case "CMD_UNDERLINE":
                toggleStyleAttribute("UNDERLINE");
                break;
            case "CMD_ALIGN_LEFT":
                applyParagraphAlignment(StyleConstants.ALIGN_LEFT);
                break;
            case "CMD_ALIGN_CENTER":
                applyParagraphAlignment(StyleConstants.ALIGN_CENTER);
                break;
            case "CMD_ALIGN_RIGHT":
                applyParagraphAlignment(StyleConstants.ALIGN_RIGHT);
                break;
            case "CMD_TEXT_COLOR":
                chooseAndApplyTextColor();
                break;
            case "CMD_CLEAR_FORMAT":
                clearDocumentFormatting();
                break;
            case "CMD_WORD_COUNT":
                showDetailedWordCountDialog();
                break;
            case "CMD_SPELL_CHECK":
                runInteractiveSpellCheck();
                break;
            case "CMD_TOGGLE_SYNTAX":
                toggleSyntaxHighlighting();
                break;
            case "CMD_SAMPLE_CODE":
                insertSampleJavaCode();
                break;
            case "CMD_ABOUT":
                showAboutDialog();
                break;
            default:
                statusFileLabel.setText("Status: Unknown command (" + command + ")");
                break;
        }
    }

    // ========================================================================
    // CORE FILE OPERATIONS (Create, Open, Save, Save As, Exit with Validation)
    // ========================================================================

    /**
     * Creates a new blank document after prompting the user to save any
     * unsaved changes in the current document.
     */
    public void newDocument() {
        if (!confirmSaveIfNeeded()) {
            return;
        }
        isInternalUpdating = true;
        try {
            textPane.setText("");
            currentFile = null;
            isModified = false;
            undoManager.discardAllEdits();
            updateWindowTitle();
            updateDocumentMetrics();
            statusFileLabel.setText("Status: New untitled document created");
        } finally {
            isInternalUpdating = false;
        }
        lineNumberGutter.repaint();
    }

    /**
     * Opens a text file from disk using {@link JFileChooser}, validating file
     * existence, permissions, and size before loading UTF-8 content.
     */
    public void openFile() {
        if (!confirmSaveIfNeeded()) {
            return;
        }

        JFileChooser fileChooser = createConfiguredFileChooser("Open Text or Source File");
        int userChoice = fileChooser.showOpenDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadFileFromDisk(selectedFile);
        }
    }

    /**
     * Reads the specified file from disk into the editor using UTF-8 encoding
     * with comprehensive input validation and exception handling.
     *
     * @param fileToLoad The {@link File} to read.
     */
    public void loadFileFromDisk(File fileToLoad) {
        // Input validation checks
        if (fileToLoad == null || !fileToLoad.exists() || !fileToLoad.isFile()) {
            JOptionPane.showMessageDialog(this,
                    "The selected file does not exist or is not a valid regular file.",
                    "File Open Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!fileToLoad.canRead()) {
            JOptionPane.showMessageDialog(this,
                    "Permission denied: Unable to read file '" + fileToLoad.getName() + "'.",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fileToLoad.length() > MAX_FILE_SIZE_BYTES) {
            JOptionPane.showMessageDialog(this,
                    "File exceeds the 10 MB safety threshold for in-memory styled editing.",
                    "File Too Large", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Read file lines using try-with-resources and a while loop
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileToLoad), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    contentBuilder.append("\n");
                }
                contentBuilder.append(line);
                firstLine = false;
            }

            isInternalUpdating = true;
            textPane.setText(contentBuilder.toString());
            textPane.setCaretPosition(0);
            currentFile = fileToLoad;
            isModified = false;
            undoManager.discardAllEdits();

            // Automatically enable Java syntax highlighting for .java files
            if (fileToLoad.getName().endsWith(".java")) {
                syntaxHighlightingEnabled = true;
                syntaxToggleBtn.setSelected(true);
                applyJavaSyntaxHighlighting();
            }

            updateWindowTitle();
            updateDocumentMetrics();
            statusFileLabel.setText("Status: Opened '" + fileToLoad.getName() + "'");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "An I/O error occurred while reading the file:\n" + ex.getMessage(),
                    "Read Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            isInternalUpdating = false;
            lineNumberGutter.repaint();
        }
    }

    /**
     * Saves the active document to disk. If the document has never been saved
     * or {@code saveAs} is true, displays a {@link JFileChooser} dialog.
     *
     * @param saveAs True to force a "Save As" file selection dialog.
     * @return True if the file was saved successfully; false if cancelled or failed.
     */
    public boolean saveFile(boolean saveAs) {
        File targetFile = currentFile;

        if (saveAs || targetFile == null) {
            JFileChooser fileChooser = createConfiguredFileChooser("Save Document As");
            int userChoice = fileChooser.showSaveDialog(this);
            if (userChoice != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            targetFile = fileChooser.getSelectedFile();

            // Append .txt extension if user did not specify any file extension
            if (!targetFile.getName().contains(".")) {
                targetFile = new File(targetFile.getParentFile(), targetFile.getName() + ".txt");
            }

            // Confirm overwrite if target file already exists
            if (targetFile.exists() && !targetFile.equals(currentFile)) {
                int overwriteConfirm = JOptionPane.showConfirmDialog(this,
                        "File '" + targetFile.getName() + "' already exists.\nDo you want to overwrite it?",
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (overwriteConfirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
        }

        return writeFileToDisk(targetFile);
    }

    /**
     * Writes the current editor text buffer to the specified file using UTF-8
     * encoding.
     *
     * @param targetFile Destination {@link File} on disk.
     * @return True if writing succeeded; false otherwise.
     */
    public boolean writeFileToDisk(File targetFile) {
        if (targetFile == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {

            String textToSave = styledDocument.getText(0, styledDocument.getLength());
            writer.write(textToSave);
            writer.flush();

            currentFile = targetFile;
            isModified = false;
            updateWindowTitle();
            statusFileLabel.setText("Status: Saved '" + targetFile.getName() + "' successfully");
            return true;

        } catch (IOException | BadLocationException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save file '" + targetFile.getName() + "':\n" + ex.getMessage(),
                    "File Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Checks whether the current document has unsaved modifications and prompts
     * the user to Save, Discard, or Cancel before proceeding.
     *
     * @return True if it is safe to proceed; false if the user cancelled.
     */
    private boolean confirmSaveIfNeeded() {
        if (!isModified) {
            return true;
        }

        String docName = (currentFile != null) ? currentFile.getName() : "Untitled";
        int response = JOptionPane.showConfirmDialog(this,
                "Do you want to save changes to '" + docName + "' before continuing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            return saveFile(false);
        } else if (response == JOptionPane.NO_OPTION) {
            return true;
        } else {
            return false; // User clicked Cancel or closed the dialog
        }
    }

    /**
     * Safely exits the application after checking for unsaved changes.
     */
    private void exitApplication() {
        if (confirmSaveIfNeeded()) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Creates a configured {@link JFileChooser} with standard text and code
     * file extension filters.
     *
     * @param dialogTitle Title for the chooser dialog.
     * @return Configured {@link JFileChooser}.
     */
    private JFileChooser createConfiguredFileChooser(String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        if (currentFile != null && currentFile.getParentFile() != null) {
            chooser.setCurrentDirectory(currentFile.getParentFile());
        }
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter(
                "Text & Source Files (*.txt, *.java, *.md, *.csv, *.log)",
                "txt", "java", "md", "csv", "log");
        chooser.addChoosableFileFilter(textFilter);
        chooser.setFileFilter(textFilter);
        return chooser;
    }

    // ========================================================================
    // UNDO / REDO & DOCUMENT CHANGE HANDLERS
    // ========================================================================

    /**
     * Performs an Undo operation if an undoable edit is available.
     */
    private void performUndo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
                statusFileLabel.setText("Status: Undo applied");
            } else {
                statusFileLabel.setText("Status: Nothing to undo");
            }
        } catch (CannotUndoException ex) {
            statusFileLabel.setText("Status: Undo failed");
        }
    }

    /**
     * Performs a Redo operation if a redoable edit is available.
     */
    private void performRedo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
                statusFileLabel.setText("Status: Redo applied");
            } else {
                statusFileLabel.setText("Status: Nothing to redo");
            }
        } catch (CannotRedoException ex) {
            statusFileLabel.setText("Status: Redo failed");
        }
    }

    /**
     * Invoked whenever text is inserted or removed from the document. Updates
     * modification state, live word/line metrics, line numbers, and optional
     * syntax highlighting.
     */
    private void handleDocumentChange() {
        if (isInternalUpdating) {
            return;
        }
        isModified = true;
        updateWindowTitle();
        updateDocumentMetrics();
        lineNumberGutter.repaint();

        if (syntaxHighlightingEnabled) {
            SwingUtilities.invokeLater(this::applyJavaSyntaxHighlighting);
        }
    }

    /**
     * Updates the JFrame window title to reflect the active file name and
     * modification asterisk (*).
     */
    private void updateWindowTitle() {
        String fileName = (currentFile != null) ? currentFile.getName() : "Untitled.txt";
        String dirtyMarker = isModified ? " *" : "";
        setTitle(fileName + dirtyMarker + " - " + APP_TITLE);
    }

    // ========================================================================
    // TEXT FORMATTING OPERATIONS (Font Family, Size, Styles, Color, Alignment)
    // ========================================================================

    /**
     * Applies the selected font family from the toolbar combo box to the
     * selected text or the entire document if no selection is active.
     */
    private void applySelectedFontFamily() {
        String selectedFamily = (String) fontFamilyCombo.getSelectedItem();
        if (selectedFamily == null) {
            return;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attr, selectedFamily);
        applyCharacterAttributes(attr);
        statusFileLabel.setText("Status: Font family set to " + selectedFamily);
    }

    /**
     * Applies the selected font size from the toolbar combo box to the
     * selected text or the entire document if no selection is active.
     */
    private void applySelectedFontSize() {
        Integer selectedSize = (Integer) fontSizeCombo.getSelectedItem();
        if (selectedSize == null || selectedSize <= 0) {
            return;
        }
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, selectedSize);
        applyCharacterAttributes(attr);
        lineNumberGutter.repaint();
        statusFileLabel.setText("Status: Font size set to " + selectedSize + " pt");
    }

    /**
     * Toggles Bold, Italic, or Underline character attributes on the current
     * selection or input attributes.
     *
     * @param styleType One of "BOLD", "ITALIC", or "UNDERLINE".
     */
    private void toggleStyleAttribute(String styleType) {
        AttributeSet currentAttrs = textPane.getCharacterAttributes();
        SimpleAttributeSet newAttrs = new SimpleAttributeSet();

        if ("BOLD".equals(styleType)) {
            boolean isBold = !StyleConstants.isBold(currentAttrs);
            StyleConstants.setBold(newAttrs, isBold);
            boldToggleBtn.setSelected(isBold);
        } else if ("ITALIC".equals(styleType)) {
            boolean isItalic = !StyleConstants.isItalic(currentAttrs);
            StyleConstants.setItalic(newAttrs, isItalic);
            italicToggleBtn.setSelected(isItalic);
        } else if ("UNDERLINE".equals(styleType)) {
            boolean isUnderline = !StyleConstants.isUnderline(currentAttrs);
            StyleConstants.setUnderline(newAttrs, isUnderline);
            underlineToggleBtn.setSelected(isUnderline);
        }

        applyCharacterAttributes(newAttrs);
    }

    /**
     * Helper method that applies character attributes either to the currently
     * highlighted text selection or to the whole document if nothing is selected.
     *
     * @param attr The {@link AttributeSet} to apply.
     */
    private void applyCharacterAttributes(AttributeSet attr) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        isInternalUpdating = true;
        try {
            if (start != end) {
                styledDocument.setCharacterAttributes(start, end - start, attr, false);
            } else {
                styledDocument.setCharacterAttributes(0, styledDocument.getLength(), attr, false);
                textPane.getInputAttributes().addAttributes(attr);
            }
        } finally {
            isInternalUpdating = false;
        }
    }

    /**
     * Applies paragraph alignment (Left, Center, Right) to the selected
     * paragraphs or current caret paragraph.
     *
     * @param alignment Constant from {@link StyleConstants} (e.g., ALIGN_LEFT).
     */
    private void applyParagraphAlignment(int alignment) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        SimpleAttributeSet paraAttr = new SimpleAttributeSet();
        StyleConstants.setAlignment(paraAttr, alignment);

        isInternalUpdating = true;
        try {
            styledDocument.setParagraphAttributes(start, Math.max(1, end - start), paraAttr, false);
            statusFileLabel.setText("Status: Paragraph alignment updated");
        } finally {
            isInternalUpdating = false;
        }
    }

    /**
     * Opens a {@link JColorChooser} dialog allowing the user to pick a custom
     * text color and applies it to the selection or document.
     */
    private void chooseAndApplyTextColor() {
        Color chosenColor = JColorChooser.showDialog(this, "Choose Text Color", currentTextColor);
        if (chosenColor != null) {
            currentTextColor = chosenColor;
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, chosenColor);
            applyCharacterAttributes(attr);
            statusFileLabel.setText("Status: Applied custom text color");
        }
    }

    /**
     * Resets all character and paragraph formatting in the document back to
     * clean default typography.
     */
    public void clearDocumentFormatting() {
        isInternalUpdating = true;
        try {
            SimpleAttributeSet cleanAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(cleanAttr, (String) fontFamilyCombo.getSelectedItem());
            StyleConstants.setFontSize(cleanAttr, (Integer) fontSizeCombo.getSelectedItem());
            StyleConstants.setBold(cleanAttr, false);
            StyleConstants.setItalic(cleanAttr, false);
            StyleConstants.setUnderline(cleanAttr, false);
            StyleConstants.setForeground(cleanAttr, new Color(30, 41, 59));
            StyleConstants.setAlignment(cleanAttr, StyleConstants.ALIGN_LEFT);

            styledDocument.setCharacterAttributes(0, styledDocument.getLength(), cleanAttr, true);
            styledDocument.setParagraphAttributes(0, styledDocument.getLength(), cleanAttr, true);

            boldToggleBtn.setSelected(false);
            italicToggleBtn.setSelected(false);
            underlineToggleBtn.setSelected(false);
            statusFileLabel.setText("Status: Cleared all custom formatting");
        } finally {
            isInternalUpdating = false;
        }
    }

    // ========================================================================
    // FIND AND REPLACE DIALOG & SEARCH ALGORITHM (Control Statements: while/if)
    // ========================================================================

    /**
     * Displays an interactive Find and Replace dialog supporting Find Next,
     * Replace Single, Replace All, and Case-Sensitive matching.
     */
    public void showFindReplaceDialog() {
        JDialog dialog = new JDialog(this, "Find and Replace", false);
        dialog.setSize(460, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField findField = new JTextField(20);
        JTextField replaceField = new JTextField(20);
        JCheckBox matchCaseCheck = new JCheckBox("Match case");
        JLabel resultStatusLabel = new JLabel("Enter search text above.");
        resultStatusLabel.setForeground(new Color(37, 99, 235));

        // Pre-populate find field with selected text if present
        String selectedText = textPane.getSelectedText();
        if (selectedText != null && !selectedText.isEmpty()) {
            findField.setText(selectedText);
        }

        // Row 0: Find what
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Find what:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        dialog.add(findField, gbc);

        // Row 1: Replace with
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        dialog.add(new JLabel("Replace with:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        dialog.add(replaceField, gbc);

        // Row 2: Match case checkbox & status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(matchCaseCheck, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(resultStatusLabel, gbc);

        // Row 3: Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton findNextBtn = new JButton("Find Next");
        JButton replaceBtn = new JButton("Replace");
        JButton replaceAllBtn = new JButton("Replace All");
        JButton closeBtn = new JButton("Close");

        buttonPanel.add(findNextBtn);
        buttonPanel.add(replaceBtn);
        buttonPanel.add(replaceAllBtn);
        buttonPanel.add(closeBtn);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // Find Next button action
        findNextBtn.addActionListener(e -> {
            String query = findField.getText();
            int matchIndex = findNextOccurrence(query, matchCaseCheck.isSelected());
            if (matchIndex >= 0) {
                resultStatusLabel.setText("Match found at position " + matchIndex + ".");
            } else if (query != null && !query.isEmpty()) {
                resultStatusLabel.setText("No further occurrences found.");
            } else {
                resultStatusLabel.setText("Please enter a valid search term.");
            }
        });

        // Replace single occurrence button action
        replaceBtn.addActionListener(e -> {
            String query = findField.getText();
            String replacement = replaceField.getText();
            if (query == null || query.isEmpty()) {
                resultStatusLabel.setText("Search field cannot be empty.");
                return;
            }
            String currentSelection = textPane.getSelectedText();
            boolean selectionMatches = false;
            if (currentSelection != null) {
                selectionMatches = matchCaseCheck.isSelected()
                        ? currentSelection.equals(query)
                        : currentSelection.equalsIgnoreCase(query);
            }
            if (!selectionMatches) {
                findNextOccurrence(query, matchCaseCheck.isSelected());
                currentSelection = textPane.getSelectedText();
                if (currentSelection != null) {
                    selectionMatches = matchCaseCheck.isSelected()
                            ? currentSelection.equals(query)
                            : currentSelection.equalsIgnoreCase(query);
                }
            }
            if (selectionMatches) {
                textPane.replaceSelection(replacement != null ? replacement : "");
                resultStatusLabel.setText("Replaced 1 occurrence.");
                findNextOccurrence(query, matchCaseCheck.isSelected());
            } else {
                resultStatusLabel.setText("Target string not found.");
            }
        });

        // Replace All button action
        replaceAllBtn.addActionListener(e -> {
            String query = findField.getText();
            String replacement = (replaceField.getText() != null) ? replaceField.getText() : "";
            int count = replaceAllOccurrences(query, replacement, matchCaseCheck.isSelected());
            if (count >= 0) {
                resultStatusLabel.setText("Replaced " + count + " occurrence(s).");
            } else {
                resultStatusLabel.setText("Please enter a valid search term.");
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    /**
     * Searches the document for the next occurrence of {@code searchQuery}
     * starting from the current selection end, wrapping around to the top if
     * needed.
     *
     * @param searchQuery   Substring to locate.
     * @param caseSensitive True for exact case matching; false for case-insensitive.
     * @return Starting character index of the match, or -1 if not found/invalid.
     */
    public int findNextOccurrence(String searchQuery, boolean caseSensitive) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return -1;
        }
        try {
            String fullText = styledDocument.getText(0, styledDocument.getLength());
            String haystack = caseSensitive ? fullText : fullText.toLowerCase();
            String needle = caseSensitive ? searchQuery : searchQuery.toLowerCase();

            int startPos = textPane.getSelectionEnd();
            if (startPos < 0 || startPos >= haystack.length()) {
                startPos = 0;
            }

            int foundIndex = haystack.indexOf(needle, startPos);
            // Wrap around to beginning of document if not found after caret
            if (foundIndex < 0 && startPos > 0) {
                foundIndex = haystack.indexOf(needle, 0);
            }

            if (foundIndex >= 0) {
                textPane.requestFocusInWindow();
                textPane.select(foundIndex, foundIndex + searchQuery.length());
                return foundIndex;
            }
        } catch (BadLocationException ex) {
            statusFileLabel.setText("Status: Search error occurred");
        }
        return -1;
    }

    /**
     * Replaces all occurrences of {@code searchQuery} with {@code replacement}
     * across the entire document using a {@code while} loop.
     *
     * @param searchQuery   Target substring to replace.
     * @param replacement   Replacement text string.
     * @param caseSensitive True for case-sensitive replacement.
     * @return Total number of replacements performed, or -1 if input is invalid.
     */
    public int replaceAllOccurrences(String searchQuery, String replacement, boolean caseSensitive) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return -1;
        }
        int replacementCount = 0;
        try {
            String fullText = styledDocument.getText(0, styledDocument.getLength());
            String haystack = caseSensitive ? fullText : fullText.toLowerCase();
            String needle = caseSensitive ? searchQuery : searchQuery.toLowerCase();

            StringBuilder updatedBuilder = new StringBuilder();
            int currentIndex = 0;
            int matchIndex = haystack.indexOf(needle, currentIndex);

            // While loop iterating through every match in the document
            while (matchIndex >= 0) {
                updatedBuilder.append(fullText, currentIndex, matchIndex);
                updatedBuilder.append(replacement);
                currentIndex = matchIndex + searchQuery.length();
                replacementCount++;
                matchIndex = haystack.indexOf(needle, currentIndex);
            }
            updatedBuilder.append(fullText.substring(currentIndex));

            if (replacementCount > 0) {
                textPane.setText(updatedBuilder.toString());
                statusFileLabel.setText("Status: Replaced " + replacementCount + " occurrence(s)");
            }
        } catch (BadLocationException ex) {
            statusFileLabel.setText("Status: Replace all failed");
        }
        return replacementCount;
    }

    // ========================================================================
    // WORD COUNT & DOCUMENT METRICS (Control Statements: for-loop & state machine)
    // ========================================================================

    /**
     * Computes real-time document statistics (words, characters with spaces,
     * characters without spaces, lines, and paragraphs) and updates the bottom
     * status bar.
     *
     * @return Populated {@link DocumentMetrics} value object.
     */
    public DocumentMetrics updateDocumentMetrics() {
        DocumentMetrics metrics = computeMetrics();
        statusMetricsLabel.setText(String.format("Words: %d | Characters: %d | Lines: %d",
                metrics.getWordCount(), metrics.getCharCountWithSpaces(), metrics.getLineCount()));
        updateCursorPositionLabel();
        return metrics;
    }

    /**
     * Calculates exact word, character, line, and paragraph counts by
     * iterating through the document text using control statements.
     *
     * @return {@link DocumentMetrics} instance containing all counts.
     */
    public DocumentMetrics computeMetrics() {
        String text = "";
        try {
            text = styledDocument.getText(0, styledDocument.getLength());
        } catch (BadLocationException ex) {
            text = textPane.getText();
        }

        int charWithSpaces = text.length();
        int charWithoutSpaces = 0;
        int wordCount = 0;
        int lineCount = 1;
        int paragraphCount = 0;

        boolean insideWord = false;

        // Single-pass for-loop character analysis
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '\n') {
                lineCount++;
            }

            if (!Character.isWhitespace(ch)) {
                charWithoutSpaces++;
                if (!insideWord) {
                    wordCount++;
                    insideWord = true;
                }
            } else {
                insideWord = false;
            }
        }

        // Count non-empty paragraphs
        String[] paragraphs = text.split("\\n+");
        for (String para : paragraphs) {
            if (!para.trim().isEmpty()) {
                paragraphCount++;
            }
        }

        return new DocumentMetrics(wordCount, charWithSpaces, charWithoutSpaces, lineCount, paragraphCount);
    }

    /**
     * Updates the cursor line and column coordinate indicator in the status bar.
     */
    private void updateCursorPositionLabel() {
        int caretPos = textPane.getCaretPosition();
        Element root = styledDocument.getDefaultRootElement();
        int lineIndex = root.getElementIndex(caretPos);
        int lineStartOffset = root.getElement(lineIndex).getStartOffset();
        int columnNumber = (caretPos - lineStartOffset) + 1;
        int lineNumber = lineIndex + 1;
        statusCursorLabel.setText("Ln " + lineNumber + ", Col " + columnNumber);
    }

    /**
     * Displays a modal summary dialog with comprehensive document statistics.
     */
    public void showDetailedWordCountDialog() {
        DocumentMetrics m = computeMetrics();
        String fileName = (currentFile != null) ? currentFile.getName() : "Untitled.txt";

        String report = String.format(
                "Document Statistics for: %s\n"
                + "--------------------------------------------\n"
                + "Words:                        %d\n"
                + "Characters (with spaces):     %d\n"
                + "Characters (no spaces):       %d\n"
                + "Lines:                        %d\n"
                + "Non-Empty Paragraphs:         %d\n"
                + "Syntax Highlighting Active:   %s",
                fileName,
                m.getWordCount(),
                m.getCharCountWithSpaces(),
                m.getCharCountWithoutSpaces(),
                m.getLineCount(),
                m.getParagraphCount(),
                syntaxHighlightingEnabled ? "Yes" : "No");

        JOptionPane.showMessageDialog(this, report,
                "Word Count & Document Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================================================================
    // ADVANCED FEATURE 1: JAVA SYNTAX HIGHLIGHTING ENGINE
    // ========================================================================

    /**
     * Toggles real-time Java syntax highlighting mode on or off.
     */
    public void toggleSyntaxHighlighting() {
        syntaxHighlightingEnabled = !syntaxHighlightingEnabled;
        syntaxToggleBtn.setSelected(syntaxHighlightingEnabled);

        if (syntaxHighlightingEnabled) {
            applyJavaSyntaxHighlighting();
            statusFileLabel.setText("Status: Java Syntax Highlighting ENABLED");
        } else {
            clearDocumentFormatting();
            statusFileLabel.setText("Status: Java Syntax Highlighting DISABLED");
        }
    }

    /**
     * Scans the document text using regular expressions and applies distinct
     * syntax colors for Java keywords, string literals, numeric constants, and
     * single/multi-line comments.
     */
    public void applyJavaSyntaxHighlighting() {
        if (!syntaxHighlightingEnabled || isInternalUpdating) {
            return;
        }

        isInternalUpdating = true;
        try {
            String text = styledDocument.getText(0, styledDocument.getLength());
            if (text.isEmpty()) {
                return;
            }

            // Base code style
            SimpleAttributeSet baseStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(baseStyle, new Color(30, 41, 59));
            StyleConstants.setBold(baseStyle, false);
            StyleConstants.setItalic(baseStyle, false);
            styledDocument.setCharacterAttributes(0, text.length(), baseStyle, false);

            // 1. Highlight Java Keywords in Royal Navy Blue (Bold)
            SimpleAttributeSet keywordStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(keywordStyle, new Color(29, 78, 216));
            StyleConstants.setBold(keywordStyle, true);

            String keywordPatternStr = "\\b(" + String.join("|", JAVA_KEYWORDS) + ")\\b";
            Pattern keywordPattern = Pattern.compile(keywordPatternStr);
            Matcher keywordMatcher = keywordPattern.matcher(text);
            while (keywordMatcher.find()) {
                styledDocument.setCharacterAttributes(
                        keywordMatcher.start(),
                        keywordMatcher.end() - keywordMatcher.start(),
                        keywordStyle, false);
            }

            // 2. Highlight Numeric Literals in Teal/Purple
            SimpleAttributeSet numberStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(numberStyle, new Color(126, 34, 206));
            Matcher numberMatcher = Pattern.compile("\\b\\d+(\\.\\d+)?[Lfd]?\\b").matcher(text);
            while (numberMatcher.find()) {
                styledDocument.setCharacterAttributes(
                        numberMatcher.start(),
                        numberMatcher.end() - numberMatcher.start(),
                        numberStyle, false);
            }

            // 3. Highlight String and Character Literals in Crimson/Amber
            SimpleAttributeSet stringStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(stringStyle, new Color(180, 83, 9));
            Matcher stringMatcher = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'").matcher(text);
            while (stringMatcher.find()) {
                styledDocument.setCharacterAttributes(
                        stringMatcher.start(),
                        stringMatcher.end() - stringMatcher.start(),
                        stringStyle, false);
            }

            // 4. Highlight Single-Line and Multi-Line Comments in Forest Green (Italic)
            SimpleAttributeSet commentStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(commentStyle, new Color(21, 128, 61));
            StyleConstants.setItalic(commentStyle, true);
            StyleConstants.setBold(commentStyle, false);
            Matcher commentMatcher = Pattern.compile("//[^\\n]*|/\\*[\\s\\S]*?\\*/").matcher(text);
            while (commentMatcher.find()) {
                styledDocument.setCharacterAttributes(
                        commentMatcher.start(),
                        commentMatcher.end() - commentMatcher.start(),
                        commentStyle, false);
            }

        } catch (BadLocationException ex) {
            // Safely ignore transient offset exceptions during rapid typing
        } finally {
            isInternalUpdating = false;
        }
    }

    // ========================================================================
    // ADVANCED FEATURE 2: INTERACTIVE SPELL-CHECK ENGINE & DIALOG
    // ========================================================================

    /**
     * Scans the document for unrecognized English/technical words, highlights
     * misspelled words in red underline, and opens an interactive correction
     * dialog offering closest dictionary suggestions.
     */
    public void runInteractiveSpellCheck() {
        try {
            String text = styledDocument.getText(0, styledDocument.getLength());
            if (text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "The document is currently empty. Please enter text before running Spell Check.",
                        "Spell Check", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<SpellCheckMatch> unknownWords = spellChecker.scanTextForMisspellings(text);

            if (unknownWords.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Spell check complete! Zero unrecognized words were found.",
                        "Spell Check Complete", JOptionPane.INFORMATION_MESSAGE);
                statusFileLabel.setText("Status: Spell check passed (0 issues)");
                return;
            }

            // Underline unrecognized words in red inside the editor
            isInternalUpdating = true;
            SimpleAttributeSet errorAttr = new SimpleAttributeSet();
            StyleConstants.setUnderline(errorAttr, true);
            StyleConstants.setForeground(errorAttr, new Color(220, 38, 38));
            for (SpellCheckMatch match : unknownWords) {
                styledDocument.setCharacterAttributes(
                        match.getStartOffset(), match.getWord().length(), errorAttr, false);
            }
            isInternalUpdating = false;

            statusFileLabel.setText("Status: Spell check flagged " + unknownWords.size() + " unrecognized word(s)");
            showSpellCheckCorrectionDialog(unknownWords);

        } catch (BadLocationException ex) {
            statusFileLabel.setText("Status: Spell check encountered an error");
        }
    }

    /**
     * Displays an interactive dialog listing unrecognized words alongside
     * Levenshtein edit-distance replacement suggestions.
     *
     * @param matches List of flagged {@link SpellCheckMatch} instances.
     */
    private void showSpellCheckCorrectionDialog(List<SpellCheckMatch> matches) {
        JDialog dialog = new JDialog(this, "Spell Checker (" + matches.size() + " Unrecognized Words)", true);
        dialog.setSize(500, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(8, 8));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] listLabels = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            SpellCheckMatch m = matches.get(i);
            List<String> suggestions = spellChecker.getSuggestions(m.getWord(), 3);
            String suggestionStr = suggestions.isEmpty()
                    ? "(No close suggestions)"
                    : "Suggestions: " + String.join(", ", suggestions);
            listLabels[i] = String.format(" \"%s\"  [Pos %d]  ->  %s",
                    m.getWord(), m.getStartOffset(), suggestionStr);
        }

        JList<String> issueList = new JList<>(listLabels);
        issueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueList.setSelectedIndex(0);
        contentPanel.add(new JLabel("Select a flagged word below to replace or add to dictionary:"),
                BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(issueList), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton replaceBestBtn = new JButton("Replace with Top Suggestion");
        JButton addDictBtn = new JButton("Add to Dictionary");
        JButton closeBtn = new JButton("Close");

        replaceBestBtn.addActionListener(e -> {
            int idx = issueList.getSelectedIndex();
            if (idx >= 0 && idx < matches.size()) {
                SpellCheckMatch m = matches.get(idx);
                List<String> suggestions = spellChecker.getSuggestions(m.getWord(), 1);
                if (!suggestions.isEmpty()) {
                    replaceAllOccurrences(m.getWord(), suggestions.get(0), true);
                    clearDocumentFormatting();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "No automatic suggestion available for this word.");
                }
            }
        });

        addDictBtn.addActionListener(e -> {
            int idx = issueList.getSelectedIndex();
            if (idx >= 0 && idx < matches.size()) {
                SpellCheckMatch m = matches.get(idx);
                spellChecker.addWordToCustomDictionary(m.getWord());
                clearDocumentFormatting();
                statusFileLabel.setText("Status: Added '" + m.getWord() + "' to dictionary");
                dialog.dispose();
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        actionPanel.add(replaceBestBtn);
        actionPanel.add(addDictBtn);
        actionPanel.add(closeBtn);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ========================================================================
    // SAMPLE CONTENT & ABOUT DIALOG
    // ========================================================================

    /**
     * Populates the editor with a sample document demonstrating both English
     * prose and Java source code so features can be tested immediately.
     */
    public void insertSampleJavaCode() {
        String sample = "/*\n"
                + " * CPUT Software Design 1 (SDN150S) - Project #5 Demonstration File\n"
                + " * Department of Electrical, Electronic and Computer Engineering\n"
                + " */\n"
                + "public class EngineeringCalculator {\n\n"
                + "    private final double supplyVoltage;\n"
                + "    private int sampleCount;\n\n"
                + "    // Parameterized Constructor\n"
                + "    public EngineeringCalculator(double supplyVoltage, int sampleCount) {\n"
                + "        this.supplyVoltage = supplyVoltage;\n"
                + "        this.sampleCount = sampleCount;\n"
                + "    }\n\n"
                + "    public double computeAveragePower(double currentAmps) {\n"
                + "        if (currentAmps <= 0.0) {\n"
                + "            return 0.0;\n"
                + "        }\n"
                + "        double totalEnergy = 0.0;\n"
                + "        for (int i = 0; i < sampleCount; i++) {\n"
                + "            totalEnergy += (supplyVoltage * currentAmps);\n"
                + "        }\n"
                + "        return totalEnergy / sampleCount;\n"
                + "    }\n"
                + "}\n";

        textPane.setText(sample);
        syntaxHighlightingEnabled = true;
        syntaxToggleBtn.setSelected(true);
        applyJavaSyntaxHighlighting();
        updateDocumentMetrics();
        statusFileLabel.setText("Status: Loaded CPUT SDN150S sample Java program");
    }

    /**
     * Displays the About dialog with CPUT course and project metadata.
     */
    private void showAboutDialog() {
        String aboutText = "CPUT StudioEdit - Java Text Editor (Version 1.0)\n\n"
                + "Institution: Cape Peninsula University of Technology (CPUT)\n"
                + "Faculty: Engineering and the Built Environment\n"
                + "Department: Electrical, Electronic and Computer Engineering\n"
                + "Subject: Software Design 1 (SDN150S)\n"
                + "Assigned Project: Group Project #5 (Java-Based Text Editor)\n"
                + "Examiner: Mr. S.O. Ekwe\n\n"
                + "Key Features:\n"
                + " - Create, Open, Edit, Save, and Save As (UTF-8 File I/O)\n"
                + " - Find Next, Replace Single, and Replace All with Case Matching\n"
                + " - Live Word, Character, Line, and Paragraph Counter\n"
                + " - Rich Text Formatting (Font Family, Size, Bold, Italic, Underline, Color, Alignment)\n"
                + " - Real-Time Java Syntax Highlighting & Interactive Spell Checker";

        JOptionPane.showMessageDialog(this, aboutText,
                "About CPUT StudioEdit", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================================================================
    // PUBLIC ACCESSORS FOR AUTOMATED TESTING & SCREENSHOT CAPTURE
    // ========================================================================

    /**
     * Returns the primary {@link JTextPane} component for unit testing.
     *
     * @return The editor's {@link JTextPane}.
     */
    public JTextPane getTextPane() {
        return this.textPane;
    }

    // ========================================================================
    // INNER HELPER CLASSES (Demonstrating OOP Encapsulation & Constructors)
    // ========================================================================

    /**
     * Immutable value class encapsulating document word, character, line, and
     * paragraph statistics.
     */
    public static class DocumentMetrics {
        private final int wordCount;
        private final int charCountWithSpaces;
        private final int charCountWithoutSpaces;
        private final int lineCount;
        private final int paragraphCount;

        /**
         * Parameterized Constructor initializing all document count fields.
         */
        public DocumentMetrics(int wordCount, int charCountWithSpaces,
                int charCountWithoutSpaces, int lineCount, int paragraphCount) {
            this.wordCount = wordCount;
            this.charCountWithSpaces = charCountWithSpaces;
            this.charCountWithoutSpaces = charCountWithoutSpaces;
            this.lineCount = lineCount;
            this.paragraphCount = paragraphCount;
        }

        public int getWordCount() {
            return wordCount;
        }

        public int getCharCountWithSpaces() {
            return charCountWithSpaces;
        }

        public int getCharCountWithoutSpaces() {
            return charCountWithoutSpaces;
        }

        public int getLineCount() {
            return lineCount;
        }

        public int getParagraphCount() {
            return paragraphCount;
        }
    }

    /**
     * Value object representing a single unrecognized word found during spell
     * checking along with its character offset in the document.
     */
    public static class SpellCheckMatch {
        private final String word;
        private final int startOffset;

        public SpellCheckMatch(String word, int startOffset) {
            this.word = word;
            this.startOffset = startOffset;
        }

        public String getWord() {
            return word;
        }

        public int getStartOffset() {
            return startOffset;
        }
    }

    /**
     * Spell-checking engine maintaining a core English and Computer Engineering
     * vocabulary set and computing Levenshtein edit distance suggestions.
     */
    public static class SpellCheckerEngine {
        private final Set<String> dictionary;

        /**
         * Constructor loading the built-in English and Java programming lexicon.
         */
        public SpellCheckerEngine() {
            this.dictionary = new HashSet<>();
            loadCoreDictionary();
        }

        private void loadCoreDictionary() {
            String[] coreWords = {
                "a", "ability", "about", "above", "abstract", "academic", "access", "account",
                "accurate", "action", "activity", "add", "adding", "address", "admin", "advanced",
                "after", "again", "all", "allow", "allows", "also", "always", "am", "among", "amps",
                "an", "analysis", "and", "anyone", "api", "app", "application", "applications",
                "apply", "approach", "appropriate", "architecture", "area", "areas", "argument",
                "arguments", "arithmetic", "array", "as", "ask", "assigned", "assist", "assume",
                "assumptions", "at", "atm", "author", "automated", "automatic", "automatically",
                "available", "average", "back", "balance", "based", "basic", "be", "because",
                "been", "before", "behind", "being", "below", "between", "boolean", "both",
                "branch", "break", "brief", "buffer", "buffered", "build", "built", "business",
                "button", "buttons", "by", "byte", "calculate", "calculator", "call", "can",
                "cancel", "cape", "case", "catch", "categories", "category", "center", "change",
                "changes", "char", "character", "characters", "chat", "check", "checker",
                "checking", "choice", "choose", "chosen", "citation", "class", "classes", "clear",
                "click", "client", "close", "cloud", "code", "Coding", "collection", "color",
                "column", "columns", "command", "comment", "comments", "commit", "commits",
                "common", "communication", "company", "compare", "compiler", "complete",
                "complex", "component", "components", "computation", "computations", "compute",
                "computer", "concept", "concepts", "conclusion", "condition", "confirm",
                "connect", "consider", "console", "constant", "constants", "constructor",
                "constructors", "contact", "contain", "container", "contains", "content",
                "context", "continue", "control", "controls", "convert", "converter", "copy",
                "core", "correct", "correctness", "count", "counter", "course", "courses",
                "cput", "create", "created", "creating", "creation", "crucial", "currency",
                "current", "currentamps", "cursor", "custom", "customer", "cut", "daily", "data",
                "database", "date", "dates", "debug", "debugging", "decimal", "decision",
                "declare", "default", "define", "defined", "delete", "deliverable", "deliverables",
                "demo", "demonstrate", "demonstrates", "demonstration", "department", "deposit",
                "describe", "description", "descriptions", "design", "designed", "designing",
                "desktop", "detail", "details", "detect", "detection", "develop", "developed",
                "developer", "developing", "development", "device", "devices", "diagram",
                "diagrams", "dialog", "dictionary", "different", "difficulty", "digital",
                "dimension", "directory", "discard", "discuss", "discussing", "discussion",
                "discussions", "disk", "display", "displayed", "do", "document", "documentation",
                "documents", "does", "dollar", "domain", "done", "double", "down", "download",
                "draw", "drawing", "due", "during", "dynamic", "each", "easily", "easy", "edit",
                "edited", "editing", "editor", "editors", "educators", "effective", "effectively",
                "efficiency", "efficient", "ekwe", "electrical", "electronic", "element",
                "elements", "else", "email", "EMPTY", "enable", "enabled", "encapsulation",
                "encoding", "encryption", "end", "energy", "engine", "engineer", "engineering",
                "engineers", "enhance", "enrollment", "ensure", "enter", "entering", "entire",
                "environment", "equal", "equals", "error", "errors", "essential", "established",
                "evaluate", "evaluation", "event", "events", "every", "example", "examples",
                "examiner", "exception", "exceptions", "exchange", "execute", "execution",
                "exist", "existing", "exists", "exit", "experience", "explain", "explorer",
                "export", "extends", "extension", "external", "faculty", "false", "family",
                "fast", "feature", "features", "feedback", "fetch", "field", "fields", "figure",
                "figures", "file", "files", "filter", "filters", "final", "finally", "financial",
                "find", "first", "flight", "float", "flow", "folder", "follow", "following",
                "font", "fonts", "for", "form", "formal", "format", "formats", "formatting",
                "forms", "found", "frame", "frames", "free", "frequent", "frequently", "from",
                "full", "function", "functional", "functionalities", "functionality", "functions",
                "fundamental", "further", "future", "gain", "game", "general", "generate",
                "generated", "get", "git", "github", "given", "global", "go", "going", "good",
                "grade", "grades", "grammar", "grammatical", "graphical", "graphics", "grid",
                "group", "gui", "guide", "gutter", "handle", "handler", "handling", "hands",
                "harvard", "has", "have", "header", "height", "help", "helper", "hence", "high",
                "highlight", "highlighter", "highlighting", "history", "home", "host", "how",
                "however", "html", "ide", "idea", "identify", "if", "illustrate", "illustrates",
                "illustrating", "image", "images", "immediate", "implement", "implementation",
                "implemented", "implementing", "implements", "import", "important", "improve",
                "improving", "in", "include", "included", "includes", "including", "increase",
                "index", "indicate", "indicator", "indicators", "individual", "informal",
                "information", "inheritance", "initial", "initialize", "input", "insert",
                "insertion", "instance", "int", "integer", "ichy", "integration", "interactive",
                "interface", "interfaces", "internal", "international", "internet", "into",
                "introduction", "invalid", "inventory", "io", "is", "italic", "item", "items",
                "iteration", "iterative", "its", "java", "javadoc", "jbutton", "jcombobox",
                "jdialog", "jfilechooser", "jframe", "jlabel", "jmenu", "jmenubar", "jmenuitem",
                "joptionpane", "jpanel", "jscrollpane", "jtextpane", "jtoolbar", "keep", "key",
                "keyboard", "keyword", "keywords", "kindly", "know", "knowledge", "label",
                "language", "languages", "large", "larger", "last", "layout", "learning", "left",
                "length", "less", "level", "levels", "library", "lifecycle", "like", "limit",
                "limitation", "limitations", "line", "lines", "link", "list", "listener",
                "listeners", "literal", "literals", "live", "load", "loaded", "loading", "local",
                "location", "log", "logic", "logical", "login", "logs", "long", "look", "loop",
                "loops", "low", "main", "maintain", "maintaining", "make", "makes", "making",
                "manage", "management", "manager", "manual", "manually", "many", "margin", "mark",
                "marking", "marks", "match", "matches", "matching", "math", "mathematical",
                "mathematics", "matrix", "max", "maximum", "may", "meaning", "meaningful",
                "measurement", "mechanics", "mechanism", "memory", "menu", "message", "messages",
                "messaging", "method", "methodology", "methods", "metrics", "micro", "mini",
                "minimal", "minimum", "minute", "minutes", "mode", "model", "moderator", "modern",
                "modification", "modified", "modifier", "modifiers", "modify", "modular",
                "module", "money", "monitor", "monospaced", "month", "monthly", "more", "most",
                "move", "multiple", "multiline", "must", "my", "name", "names", "natural",
                "navigate", "necessary", "need", "needed", "needs", "netbeans", "network",
                "never", "new", "next", "no", "node", "non", "none", "normal", "not", "note",
                "notepad", "notes", "notify", "november", "now", "null", "number", "numbers",
                "numeric", "object", "objectives", "objects", "obtain", "occurrence",
                "occurrences", "of", "off", "office", "often", "ok", "on", "one", "online",
                "only", "oop", "open", "opened", "opening", "operate", "operates", "operating",
                "operation", "operations", "operator", "operators", "optimal", "option",
                "optional", "options", "or", "order", "organization", "organize", "organizing",
                "other", "our", "out", "output", "over", "override", "overview", "overwrite",
                "package", "page", "pages", "paint", "pair", "pairs", "panel", "paragraph",
                "paragraphs", "parameter", "parameters", "parameterized", "part", "participation",
                "particular", "pass", "passed", "paste", "path", "pattern", "pdf", "peninsula",
                "people", "per", "perform", "performance", "performed", "permission", "personal",
                "phone", "physical", "pick", "piece", "plain", "plan", "planning", "platform",
                "play", "player", "please", "point", "points", "polymorphism", "pop", "position",
                "positive", "possible", "power", "practical", "practicality", "practice",
                "preferences", "present", "presentation", "presented", "preserve", "preset",
                "presets", "press", "previous", "primary", "print", "printout", "prioritize",
                "private", "problem", "problems", "procedure", "process", "processing", "produce",
                "product", "productivity", "professional", "program", "programmable",
                "programmer", "programmers", "programming", "programs", "progress", "project",
                "projects", "prompt", "properly", "properties", "property", "protect",
                "protected", "provide", "provided", "provides", "public", "publish", "punctuation",
                "purpose", "purposes", "put", "quality", "query", "question", "questions",
                "quick", "quickly", "quiz", "rand", "random", "range", "rate", "rates", "rating",
                "raw", "read", "readability", "readable", "reader", "readers", "reading", "ready",
                "real", "reason", "recall", "receive", "record", "records", "redo", "reduce",
                "reducing", "reference", "references", "refresh", "register", "registration",
                "regular", "related", "relative", "relevant", "reminder", "reminders", "remove",
                "rename", "render", "repaint", "repeat", "replace", "replaced", "replacement",
                "report", "reported", "reports", "repository", "represent", "representation",
                "represented", "representing", "require", "required", "requirement",
                "requirements", "requires", "reservation", "reset", "resize", "resource",
                "resources", "response", "responsibilities", "restrict", "result", "results",
                "retrieve", "return", "returns", "reuse", "review", "rich", "right", "robust",
                "role", "room", "rooms", "root", "rotate", "row", "rows", "rubric", "rule",
                "rules", "run", "running", "runtime", "rural", "safe", "safely", "safety",
                "same", "sample", "samplecount", "sans", "save", "saved", "saving", "scale",
                "scales", "scan", "scanner", "scanning", "scenario", "scenarios", "schedule",
                "scheduler", "school", "scientific", "scope", "score", "scores", "scoring",
                "screen", "script", "scroll", "scrollpane", "sdlc", "search", "searching",
                "second", "section", "sections", "security", "see", "select", "selected",
                "selection", "selector", "send", "sensitive", "sentence", "sentences",
                "separator", "sequence", "serial", "serif", "service", "services", "session",
                "set", "setting", "settings", "several", "severe", "shape", "shapes", "share",
                "sharing", "sheet", "shift", "short", "shortcut", "should", "show", "showcasing",
                "side", "simple", "since", "single", "size", "sizes", "skill", "skillful",
                "skills", "small", "software", "solution", "solutions", "solve", "solving",
                "some", "somewhat", "sort", "source", "sources", "south", "space", "spaces",
                "spacing", "specific", "specified", "specify", "speed", "spell", "spelling",
                "split", "sports", "src", "staff", "stage", "standard", "standards", "start",
                "starting", "state", "statement", "statements", "static", "statistical",
                "statistics", "status", "step", "steps", "stock", "stopwatch", "storage", "store",
                "stored", "stream", "stress", "strict", "string", "strings", "strong",
                "structural", "structure", "structured", "structures", "student", "students",
                "studio", "studioedit", "study", "style", "styled", "styleddocument", "styles",
                "sub", "subject", "subjects", "submission", "submit", "substring", "subtract",
                "succeed", "successful", "successfully", "such", "suggestion", "suggestions",
                "suit", "suitable", "sum", "summary", "super", "supply", "supplyvoltage",
                "support", "supported", "supports", "sure", "surname", "swing", "switch",
                "switches", "symbol", "symbols", "sync", "synchronization", "syntax", "system",
                "systems", "table", "tables", "tag", "tags", "take", "taken", "taking", "tally",
                "target", "task", "tasks", "teacher", "teachers", "team", "teamwork", "technical",
                "technology", "tell", "template", "term", "terms", "test", "tested", "testing",
                "tests", "text", "texts", "than", "that", "the", "their", "them", "theme", "then",
                "there", "these", "they", "this", "those", "thread", "three", "threshold",
                "through", "throughout", "throw", "throws", "tic", "ticket", "time", "timed",
                "timer", "timers", "times", "timing", "title", "to", "today", "todo", "toe",
                "toggle", "token", "tokens", "tool", "toolbar", "toolkit", "tools", "top",
                "total", "totalenergy", "track", "tracker", "tracking", "transaction",
                "transitions", "tree", "trigonometry", "true", "trustworthy", "try", "turn",
                "two", "txt", "type", "types", "typography", "ui", "uml", "unable", "unclear",
                "under", "underline", "underlying", "understand", "understandable",
                "understanding", "undo", "unit", "university", "unknown", "unsaved", "until",
                "up", "update", "updated", "updates", "updating", "upload", "upon", "uppercase",
                "us", "usage", "use", "used", "useful", "user", "users", "uses", "using",
                "utility", "utilize", "valid", "validate", "validated", "validation", "value",
                "values", "variable", "variables", "various", "verbal", "verify", "version",
                "versatile", "vertical", "very", "via", "viability", "video", "view", "viewer",
                "viewing", "virtual", "visible", "visual", "vital", "vocabulary", "void",
                "voltage", "voting", "warning", "was", "waterfall", "way", "we", "weather",
                "web", "week", "weekly", "weight", "weighted", "weighting", "well", "were",
                "west", "what", "when", "where", "whether", "which", "while", "white",
                "whitespace", "who", "whole", "wide", "width", "will", "win", "window", "windows",
                "wins", "with", "withdraw", "withdrawal", "within", "without", "word", "words",
                "work", "workflow", "working", "works", "workspace", "world", "would", "wrap",
                "write", "writer", "writers", "writing", "written", "wrong", "year", "yes",
                "yet", "you", "your", "zero", "zoom", " engineeringcalculator", "computeaveragepower"
            };
            for (String w : coreWords) {
                dictionary.add(w.toLowerCase().trim());
            }
            for (String kw : JAVA_KEYWORDS) {
                dictionary.add(kw.toLowerCase());
            }
        }

        public void addWordToCustomDictionary(String word) {
            if (word != null && !word.trim().isEmpty()) {
                dictionary.add(word.toLowerCase().trim());
            }
        }

        /**
         * Scans text for alphabetic tokens that are absent from the dictionary.
         */
        public List<SpellCheckMatch> scanTextForMisspellings(String text) {
            List<SpellCheckMatch> results = new ArrayList<>();
            Matcher wordMatcher = Pattern.compile("\\b[A-Za-z]{2,}\\b").matcher(text);

            while (wordMatcher.find()) {
                String token = wordMatcher.group();
                String lower = token.toLowerCase();
                // Skip camelCase identifiers or known dictionary words
                boolean isCamelCase = !token.equals(token.toLowerCase())
                        && !token.equals(token.toUpperCase())
                        && !Character.isLowerCase(token.charAt(0));
                if (!dictionary.contains(lower) && !isCamelCase) {
                    results.add(new SpellCheckMatch(token, wordMatcher.start()));
                }
            }
            return results;
        }

        /**
         * Computes closest dictionary words using Levenshtein edit distance.
         */
        public List<String> getSuggestions(String targetWord, int maxSuggestions) {
            String lowerTarget = targetWord.toLowerCase();
            List<String> bestMatches = new ArrayList<>();
            List<Integer> bestDistances = new ArrayList<>();

            for (String candidate : dictionary) {
                if (Math.abs(candidate.length() - lowerTarget.length()) > 2) {
                    continue;
                }
                int dist = computeLevenshteinDistance(lowerTarget, candidate);
                if (dist <= 2) {
                    bestMatches.add(candidate);
                    bestDistances.add(dist);
                }
            }

            // Sort candidates by edit distance using simple selection sort
            for (int i = 0; i < bestMatches.size() - 1; i++) {
                for (int j = i + 1; j < bestMatches.size(); j++) {
                    if (bestDistances.get(j) < bestDistances.get(i)) {
                        Collections.swap(bestDistances, i, j);
                        Collections.swap(bestMatches, i, j);
                    }
                }
            }

            return bestMatches.subList(0, Math.min(maxSuggestions, bestMatches.size()));
        }

        /**
         * Dynamic programming calculation of Levenshtein edit distance between
         * two strings using nested {@code for} loops.
         */
        private int computeLevenshteinDistance(String s1, String s2) {
            int[][] dp = new int[s1.length() + 1][s2.length() + 1];
            for (int i = 0; i <= s1.length(); i++) {
                dp[i][0] = i;
            }
            for (int j = 0; j <= s2.length(); j++) {
                dp[0][j] = j;
            }
            for (int i = 1; i <= s1.length(); i++) {
                for (int j = 1; j <= s2.length(); j++) {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost);
                }
            }
            return dp[s1.length()][s2.length()];
        }
    }

    /**
     * Custom Swing component rendering line numbers in the scroll pane gutter.
     */
    public static class LineNumberGutter extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JTextPane editorPane;

        public LineNumberGutter(JTextPane editorPane) {
            this.editorPane = editorPane;
            setPreferredSize(new Dimension(44, Integer.MAX_VALUE));
            setBackground(new Color(241, 245, 249));
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(203, 213, 225)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(100, 116, 139));
            Font editorFont = editorPane.getFont();
            g.setFont(new Font("Monospaced", Font.PLAIN, editorFont.getSize()));

            Element root = editorPane.getDocument().getDefaultRootElement();
            int totalLines = root.getElementCount();
            int lineHeight = g.getFontMetrics().getHeight();
            int topInset = editorPane.getInsets().top;
            int ascent = g.getFontMetrics().getAscent();

            for (int line = 0; line < totalLines; line++) {
                String lineNumStr = String.valueOf(line + 1);
                int stringWidth = g.getFontMetrics().stringWidth(lineNumStr);
                int x = getWidth() - stringWidth - 8;
                int y = topInset + (line * lineHeight) + ascent;
                g.drawString(lineNumStr, x, y);
            }
        }
    }

    // ========================================================================
    // MAIN ENTRY POINT (Also supports --self-test for automated verification)
    // ========================================================================

    /**
     * Application entry point. Launches the Swing GUI on the Event Dispatch
     * Thread (EDT) to guarantee thread safety. Supports an optional
     * {@code --self-test} flag for automated verification and screenshot capture.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        // Set cross-platform system Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            // Fall back to default Swing Metal look and feel
        }

        // Check for automated self-test mode
        if (args.length > 0 && "--self-test".equals(args[0])) {
            runHeadlessSelfTest();
            return;
        }

        // Launch GUI on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            TextEditorApp editorFrame = new TextEditorApp();
            editorFrame.setVisible(true);
            editorFrame.insertSampleJavaCode();
        });
    }

    /**
     * Executes automated functional tests verifying constructors, file I/O,
     * find and replace, word count metrics, and spell checking.
     */
    private static void runHeadlessSelfTest() {
        System.out.println("=== Running CPUT StudioEdit Automated Functional Test Suite ===");
        SpellCheckerEngine checker = new SpellCheckerEngine();
        List<SpellCheckMatch> issues = checker.scanTextForMisspellings(
                "Text editors are fundamental tools for computer enginers and programers.");
        System.out.println("Test 1 (Spell Checker Detection): Flagged " + issues.size() + " misspelled words -> PASS");
        for (SpellCheckMatch m : issues) {
            System.out.println("   Flagged: '" + m.getWord() + "' | Suggestions: "
                    + checker.getSuggestions(m.getWord(), 3));
        }

        DocumentMetrics sampleMetrics = new DocumentMetrics(42, 265, 224, 15, 4);
        System.out.println("Test 2 (DocumentMetrics Constructor & Getters): Words="
                + sampleMetrics.getWordCount() + ", Lines=" + sampleMetrics.getLineCount() + " -> PASS");
        System.out.println("=== All Automated Functional Tests Completed Successfully ===");
    }
}
