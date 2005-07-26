import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Foo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame f=new JFrame();
        JTextPane p=new JTextPane();
        f.getContentPane().add(p);
        append(p,Color.RED,"Rote Wörter,");
        append(p,Color.BLUE,"Blaue Wörter");
        f.pack();
        f.setVisible(true);
    }
    private static void append(JTextPane p,Color c, String s) { // better implementation--uses
        // StyleContext
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
            StyleConstants.Foreground, c);

        int len = p.getDocument().getLength(); // same value as
                           // getText().length();
        p.setCaretPosition(len); // place caret at the end (with no selection)
        p.setCharacterAttributes(aset, false);
        p.replaceSelection(s); // there is no selection, so inserts at caret
      }
}
