import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.Element;

public class LineNumberGutter extends JPanel {
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
