/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec.gui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import namerec.util.Config;
import namerec.util.FileSelector;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileEntryPanel extends JPanel {

	private JTextField jTextField = null;
	private JButton loadButton = null;
	private JLabel jLabel = null;
    private String name;
	/**
	 * This is the default constructor
	 */
	public FileEntryPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		jLabel = new JLabel();
		GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints26.gridx = 1;
		gridBagConstraints26.gridy = 0;
		gridBagConstraints26.weightx = 1.0;
		gridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints26.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints26.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints27.gridx = 2;
		gridBagConstraints27.gridy = 0;
		gridBagConstraints27.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints28.gridx = 0;
		gridBagConstraints28.gridy = 0;
		gridBagConstraints28.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
		jLabel.setText("Filename:");
		this.add(getJTextField(), gridBagConstraints26);
		this.add(getLoadButton(), gridBagConstraints27);
		this.add(jLabel, gridBagConstraints28);
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setEnabled(true);
			jTextField.setMinimumSize(new java.awt.Dimension(250,19));
			jTextField.setMaximumSize(new java.awt.Dimension(500,19));
		}
		return jTextField;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getLoadButton() {
		if (loadButton == null) {
			loadButton = new JButton();
			loadButton.setText("...");
            loadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    File f=FileSelector.getUserSelectedFile(FileEntryPanel.this,"Find filename", null,FileSelector.OPEN_DIALOG);
                    if(f != null) {
                        getJTextField().setText(f.getAbsolutePath());
                    }                    
                }
                
            });
		}
		return loadButton;
	}
    public void setName(String name) {
        this.name=name;
        jLabel.setText(name);
    }
    public String getName() {
        return name;
    }
    public String getText() {
        return getJTextField().getText();
    }
    public void setText(String t) {
        getJTextField().setText(t);
    }
    public void setTooltip(String t) {
        setToolTipText(t);
        for(int i=0; i< getComponentCount(); i++)
            ((JComponent)getComponent(i)).setToolTipText(t);
    }
  }
