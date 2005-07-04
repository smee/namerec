package namerec.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class CyclicDocument extends PlainDocument implements Document {

    private int maxlen;
    public CyclicDocument(int maxlen) {
        this.maxlen=maxlen;
        
    }
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if(getLength()>maxlen)
            remove(0,getLength()-maxlen-str.length());
        super.insertString(offs, str, a);
    }

}
