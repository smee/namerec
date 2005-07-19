package namerec.gui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class KlassTagPanel extends JPanel {
    JCheckBox[] boxes=new JCheckBox[32];
    
    public KlassTagPanel() {
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
        JScrollPane pane=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        for (int i = 0; i < boxes.length; i++) {
            boxes[i]=new JCheckBox();
            panel.add(boxes[i]);
        } 
        add(pane);
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
}
