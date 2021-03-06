/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_leipzig.asv.toolbox.gui;

import javax.swing.JPanel;


import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import de.uni_leipzig.asv.toolbox.util.Config;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DBConfigPanel extends JPanel {

	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField hostnameTf = null;
	private JTextField dbnameTf = null;
	private JTextField usernameTf = null;
	private JPasswordField passwordTf = null;
    private JCheckBox writeBack = null;
	/**
	 * This is the default constructor
	 */
	public DBConfigPanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		jLabel3 = new JLabel();
		jLabel2 = new JLabel();
		jLabel1 = new JLabel();
		jLabel = new JLabel();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(null, null), "DB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		jLabel.setText("Hostname/IP:");
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		jLabel1.setText("DB name:");
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		jLabel2.setText("Username:");
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		jLabel3.setText("Password:");
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 0;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 1;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 2;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 3;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
		this.add(jLabel, gridBagConstraints1);
		this.add(jLabel1, gridBagConstraints2);
		this.add(jLabel2, gridBagConstraints3);
		this.add(jLabel3, gridBagConstraints4);
		this.add(getHostnameTf(), gridBagConstraints5);
		this.add(getDbnameTf(), gridBagConstraints6);
		this.add(getUsernameTf(), gridBagConstraints7);
		this.add(getPasswordTf(), gridBagConstraints8);
        GridBagConstraints gbc9=new GridBagConstraints();
        gbc9.gridx=0;
        gbc9.gridy=4;
        gbc9.insets=new Insets(10,10,10,10);
		this.add(getWriteBackCB(), gbc9);
	}
    public void saveToConfig(Config c, String suffix) {
        c.set("DB.HOST"+suffix,getHostnameTf().getText());
        c.set("DB.DBNAME"+suffix,getDbnameTf().getText());
        c.set("DB.USERNAME"+suffix,getUsernameTf().getText());
        c.set("DB.PASSWORD"+suffix,getPasswordTf().getText());
        if(getWriteBackCB().isVisible())
            c.set("DB.WRITEBACK",
                    Boolean.toString(getWriteBackCB().isSelected()));
    }
    public void loadFromConfig(Config cfg, String suffix) {
        getHostnameTf().setText(cfg.getString("DB.HOST"+suffix,""));
        getDbnameTf().setText(cfg.getString("DB.DBNAME"+suffix,""));
        getUsernameTf().setText(cfg.getString("DB.USERNAME"+suffix,""));
        getPasswordTf().setText(cfg.getString("DB.PASSWORD"+suffix,""));
        if(getWriteBackCB().isVisible())
            getWriteBackCB().setSelected(cfg.getBoolean("DB.WRITEBACK",true));
    }
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getHostnameTf() {
		if (hostnameTf == null) {
			hostnameTf = new JTextField();
		}
		return hostnameTf;
	}
    public void showWriteBackCb(boolean val) {
        getWriteBackCB().setVisible(val);
    }
    private JCheckBox getWriteBackCB() {
        if(writeBack == null) {
            writeBack = new JCheckBox("Enable writeback");
            writeBack.setSelected(true);
            writeBack.setVisible(false);
            writeBack.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    boolean sel=e.getStateChange()==ItemEvent.SELECTED;
                    getHostnameTf().setEnabled(sel);
                    getDbnameTf().setEnabled(sel);
                    getPasswordTf().setEnabled(sel);
                    getUsernameTf().setEnabled(sel);
                }
            });
        }
        return writeBack;
    }
	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getDbnameTf() {
		if (dbnameTf == null) {
			dbnameTf = new JTextField();
		}
		return dbnameTf;
	}
	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getUsernameTf() {
		if (usernameTf == null) {
			usernameTf = new JTextField();
		}
		return usernameTf;
	}
	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */    
	private JPasswordField getPasswordTf() {
		if (passwordTf == null) {
			passwordTf = new JPasswordField();
		}
		return passwordTf;
	}
    public void setTitle(String t) {
        this.setBorder(javax.swing.BorderFactory.createTitledBorder( t ));
    }
    }
