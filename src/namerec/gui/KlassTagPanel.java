package namerec.gui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class KlassTagPanel extends JPanel {
    JCheckBox[] boxes=new JCheckBox[32];
    
    public KlassTagPanel() {
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        for (int i = 0; i < boxes.length; i++) {
            if(i>0 && i%4==0)
                add(new JLabel("|"));
            boxes[i]=new JCheckBox();
            add(boxes[i]);
        } 
    }
    public String getTag() {
        String tag="";
        for (int i = 0; i < boxes.length; i++) {
            if(boxes[i].isSelected()) {
                tag+="1";
            }else {
                tag+="0";
            }
        }
        return tag;
    }
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getToolTipText()
     */
    public String getToolTipText() {
        return getTag();
    }
}
