/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec.gui;

import javax.swing.JPanel;

import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

import namerec.util.Config;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BaseTaggerPanel extends JPanel {

	private JCheckBox basetaggerCb = null;
	private JTextField taggerfilesTf = null;
	private JButton jButton = null;
	/**
	 * This is the default constructor
	 */
	public BaseTaggerPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(400, 200);
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridy = 0;
		gridBagConstraints14.gridx = 1;
		gridBagConstraints14.gridy = 0;
		gridBagConstraints14.weightx = 1.0;
		gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints14.insets = new java.awt.Insets(0,10,0,10);
		gridBagConstraints15.gridx = 2;
		gridBagConstraints15.gridy = 0;
		this.add(getBasetaggerCb(), gridBagConstraints12);
		this.add(getTaggerfilesTf(), gridBagConstraints14);
		this.add(getJButton(), gridBagConstraints15);
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getBasetaggerCb() {
		if (basetaggerCb == null) {
			basetaggerCb = new JCheckBox();
			basetaggerCb.setText("Use basetagger");
			basetaggerCb.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
                    boolean selected=e.getStateChange() == ItemEvent.SELECTED;
                    getTaggerfilesTf().setEnabled(selected);
                    getJButton().setEnabled(selected);
                }
			});
            basetaggerCb.setSelected(true);
            basetaggerCb.setSelected(false);
		}
		return basetaggerCb;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getTaggerfilesTf() {
		if (taggerfilesTf == null) {
			taggerfilesTf = new JTextField();
            taggerfilesTf.setText(new File(".").getAbsolutePath());
            taggerfilesTf.setPreferredSize(new java.awt.Dimension(400,19));
            taggerfilesTf.setToolTipText("Directory for taggerfiles...");
		}
		return taggerfilesTf;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("...");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				    JFileChooser fc = new JFileChooser(".");
                    fc.setDialogTitle("Directory for taggerfiles...");
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int result=fc.showOpenDialog(BaseTaggerPanel.this);
                    if(result == JFileChooser.APPROVE_OPTION)
                        getTaggerfilesTf().setText(fc.getSelectedFile().getAbsolutePath());
                }
			});
		}
		return jButton;
	}
    public void addYourConfig(Config cfg) {
        cfg.set("OPTION.USETAGGER", Boolean.toString(getBasetaggerCb().isSelected()));
        cfg.set("IN.TAGGERDIR",getTaggerfilesTf().getText());        
    }
  }
