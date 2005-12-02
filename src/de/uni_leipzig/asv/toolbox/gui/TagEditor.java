/*
 * Created on 20.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_leipzig.asv.toolbox.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TagEditor extends AbstractCellEditor implements TableCellEditor {
    KlassTagPanel p =new KlassTagPanel();
    JButton b= new JButton("Edit");
    final JFrame f=new JFrame("Edit tag");
    
    public TagEditor() {
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fireEditingStopped();    
            }
        });
        Container c=f.getContentPane();
        JScrollPane scroll=new JScrollPane(p);
        c.add(scroll);
        f.pack();
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dimension dim=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                f.setLocation(dim.width/2,dim.height/2);
                f.setVisible(true);
            }
        });
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if(isSelected) {
            p.setTag((String)value);
            return b;
        }
        return null;
    }

    public Object getCellEditorValue() {
        return RecognizerPanel.addBarsTo(p.getTag());
    }

}
