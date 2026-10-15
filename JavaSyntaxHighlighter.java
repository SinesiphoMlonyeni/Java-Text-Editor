import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaSyntaxHighlighter {

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

    public void highlight(StyledDocument document) {
        if (document == null) {
            return;
        }

        try {
            String text = document.getText(0, document.getLength());
            if (text.isEmpty()) {
                return;
            }

            SimpleAttributeSet baseStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(baseStyle, new Color(30, 41, 59));
            StyleConstants.setBold(baseStyle, false);
            StyleConstants.setItalic(baseStyle, false);
            document.setCharacterAttributes(0, text.length(), baseStyle, false);

            SimpleAttributeSet keywordStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(keywordStyle, new Color(29, 78, 216));
            StyleConstants.setBold(keywordStyle, true);

            String keywordPatternStr = "\\b(" + String.join("|", JAVA_KEYWORDS) + ")\\b";
            Pattern keywordPattern = Pattern.compile(keywordPatternStr);
            Matcher keywordMatcher = keywordPattern.matcher(text);
            while (keywordMatcher.find()) {
                document.setCharacterAttributes(
                        keywordMatcher.start(),
                        keywordMatcher.end() - keywordMatcher.start(),
                        keywordStyle,
                        false);
            }

            SimpleAttributeSet numberStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(numberStyle, new Color(126, 34, 206));
            Matcher numberMatcher = Pattern.compile("\\b\\d+(\\.\\d+)?[Lfd]?\\b").matcher(text);
            while (numberMatcher.find()) {
                document.setCharacterAttributes(
                        numberMatcher.start(),
                        numberMatcher.end() - numberMatcher.start(),
                        numberStyle,
                        false);
            }

            SimpleAttributeSet stringStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(stringStyle, new Color(180, 83, 9));
            Matcher stringMatcher = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'").matcher(text);
            while (stringMatcher.find()) {
                document.setCharacterAttributes(
                        stringMatcher.start(),
                        stringMatcher.end() - stringMatcher.start(),
                        stringStyle,
                        false);
            }

            SimpleAttributeSet commentStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(commentStyle, new Color(21, 128, 61));
            StyleConstants.setItalic(commentStyle, true);
            StyleConstants.setBold(commentStyle, false);
            Matcher commentMatcher = Pattern.compile("//[^\\n]*|/\\*[\\s\\S]*?\\*/").matcher(text);
            while (commentMatcher.find()) {
                document.setCharacterAttributes(
                        commentMatcher.start(),
                        commentMatcher.end() - commentMatcher.start(),
                        commentStyle,
                        false);
            }
        } catch (BadLocationException ex) {
            // Ignore transient errors during rapid edits.
        }
    }
}
