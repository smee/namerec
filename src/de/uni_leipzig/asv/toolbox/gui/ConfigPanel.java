/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_leipzig.asv.toolbox.gui;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.util.Hashtable;

import javax.swing.JSpinner;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

import de.uni_leipzig.asv.toolbox.util.Config;


import java.lang.String;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigPanel extends JPanel {

	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JSpinner threadnumSpinner = null;
	private JSlider jSlider = null;
	private JSpinner startnoSpinner = null;
	private JSpinner endnoSpinner = null;
	private JLabel jLabel4 = null;
	private JSpinner candnoSpinner = null;
	private JLabel jLabel5 = null;
	private BaseTaggerPanel baseTaggerPanel = null;
	private JLabel jLabel6 = null;
	private JCheckBox nerecogCb = null;
	private JLabel jLabel7 = null;
	private JSpinner timeSpinner = null;
	private String string = null;   //  @jve:decl-index=0:
	private JLabel jLabel8 = null;
	private JTextField jTextField = null;
	/**
	 * This is the default constructor
	 */
	public ConfigPanel() {
		super();
		initialize();
	}
    public void saveToConfig(Config cfg) {
        cfg.set("OPTION.ACCEPTITEM",Double.toString(getJSlider().getValue()/100.0));
        cfg.set("OPTION.STARTNO",((Integer)getStartnoSpinner().getValue()).toString());
        cfg.set("OPTION.ENDNO",((Integer)getEndnoSpinner().getValue()).toString());
        cfg.set("OPTION.NUMOFTHREADS",((Integer)getThreadnumSpinner().getValue()).toString());
        cfg.set("OPTION.CANDIDATESNO",((Integer)getCandnoSpinner().getValue()).toString());
        cfg.set("OPTION.NERECOG",Boolean.toString(getNerecogCb().isSelected()));
        cfg.set("OPTION.SAMPLES",((Integer)getTimeSpinner().getValue()).toString());
        cfg.set("OPTION.VERSION",getVersionTf().getText());
        getBaseTaggerPanel().addYourConfig(cfg);
    }
    public void loadFromConfig(Config cfg) {
        float f=Float.parseFloat(cfg.getString("OPTION.ACCEPTITEM","0.9"));
        getJSlider().setValue(Math.round(f*100));
        getStartnoSpinner().setValue(new Integer(cfg.getInteger("OPTION.STARTNO",1)));
        getEndnoSpinner().setValue(new Integer(cfg.getInteger("OPTION.ENDNO",-1)));
        getThreadnumSpinner().setValue(new Integer(cfg.getInteger("OPTION.NUMOFTHREADS",10)));
        getCandnoSpinner().setValue(new Integer(cfg.getInteger("OPTION.CANDIDATESNO",30)));
        getNerecogCb().setSelected(cfg.getBoolean("OPTION.NERECOG",true));
        getTimeSpinner().setValue(new Integer(cfg.getInteger("OPTION.SAMPLES",30)));
        getVersionTf().setText(cfg.getString("OPTION.VERSION","NameRec 1.1neu"));
    }
    /**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		jLabel8 = new JLabel();
		GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
		jLabel7 = new JLabel();
		jLabel6 = new JLabel();
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		jLabel5 = new JLabel();
		jLabel4 = new JLabel();
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
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		//this.setSize(800, 600);
		//this.setPreferredSize(new java.awt.Dimension(800,600));
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		jLabel.setText("No. of verification threads:");
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
		jLabel1.setText("No. of first sentence:");
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		jLabel2.setText("No. of last sentence (-1 means \"all\"):");
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 4;
		gridBagConstraints4.insets = new java.awt.Insets(0,10,10,10);
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		jLabel3.setText("Threshold for accepting a name:");
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 0;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 4;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints6.gridwidth = 3;
		gridBagConstraints8.gridx = 3;
		gridBagConstraints8.gridy = 1;
		gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.gridy = 1;
		gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints10.insets = new java.awt.Insets(10,10,10,10);
		jLabel4.setText("No. of candidate sentences:");
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 1;
		gridBagConstraints11.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints16.gridx = 0;
		gridBagConstraints16.gridy = 5;
		gridBagConstraints16.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;
		jLabel5.setText("BaseTagger Options:");
		gridBagConstraints17.gridx = 1;
		gridBagConstraints17.gridy = 5;
		gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints17.insets = new java.awt.Insets(0,10,10,10);
		gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints17.gridwidth = 3;
		gridBagConstraints18.gridx = 0;
		gridBagConstraints18.gridy = 6;
		gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints18.insets = new java.awt.Insets(10,10,10,10);
		jLabel6.setText("Run NE-recognition (postprocessing):");
		gridBagConstraints19.gridx = 1;
		gridBagConstraints19.gridy = 6;
		gridBagConstraints19.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints20.gridx = 0;
		gridBagConstraints20.gridy = 8;
		gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints20.insets = new java.awt.Insets(10,10,10,10);
		jLabel7.setText("Words/time estimation:");
		gridBagConstraints21.gridx = 1;
		gridBagConstraints21.gridy = 8;
		gridBagConstraints21.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints24.gridx = 0;
		gridBagConstraints24.gridy = 7;
		gridBagConstraints24.insets = new java.awt.Insets(10,10,10,0);
		gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
		jLabel8.setText("Versionname:");
		gridBagConstraints25.gridx = 1;
		gridBagConstraints25.gridy = 7;
		gridBagConstraints25.weightx = 1.0;
		gridBagConstraints25.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints25.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints12.gridx = 3;
		gridBagConstraints12.gridy = 0;
		gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints12.insets = new java.awt.Insets(10,10,10,10);
		this.setPreferredSize(new java.awt.Dimension(800,550));
		this.setSize(806, 378);
		this.add(jLabel, gridBagConstraints1);
		this.add(jLabel1, gridBagConstraints2);
		this.add(jLabel2, gridBagConstraints3);
		this.add(jLabel3, gridBagConstraints4);
		this.add(getThreadnumSpinner(), gridBagConstraints5);
		this.add(getJSlider(), gridBagConstraints6);
		this.add(getEndnoSpinner(), gridBagConstraints8);
		this.add(jLabel4, gridBagConstraints10);
		this.add(getCandnoSpinner(), gridBagConstraints11);
		this.add(jLabel5, gridBagConstraints16);
		this.add(getBaseTaggerPanel(), gridBagConstraints17);
		this.add(jLabel6, gridBagConstraints18);
		this.add(getNerecogCb(), gridBagConstraints19);
		this.add(jLabel7, gridBagConstraints20);
		this.add(getTimeSpinner(), gridBagConstraints21);
		this.add(jLabel8, gridBagConstraints24);
		this.add(getVersionTf(), gridBagConstraints25);
		this.add(getStartnoSpinner(), gridBagConstraints12);
	}
	/**
	 * This method initializes jSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */    
	private JSpinner getThreadnumSpinner() {
		if (threadnumSpinner == null) {
			threadnumSpinner = new JSpinner();
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(new Integer(1));
            model.setValue(new Integer(10));
            threadnumSpinner.setModel(model);
			threadnumSpinner.setPreferredSize(new java.awt.Dimension(50,20));
			threadnumSpinner.setMinimumSize(new java.awt.Dimension(60,20));
		}
		return threadnumSpinner;
	}
	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getJSlider() {
		if (jSlider == null) {
            jSlider = new JSlider();
			jSlider.setMaximum(100);
			jSlider.setMajorTickSpacing(25);
			jSlider.setMinorTickSpacing(5);
			jSlider.setPaintTicks(true);
            jSlider.setPaintLabels(true);
            jSlider.setSnapToTicks(true);
            jSlider.setValue(90);
            Hashtable t = new Hashtable();
            t.put(new Integer(100), new JLabel("1.0"));
            t.put(new Integer(0), new JLabel("0.0"));
            t.put(new Integer(50), new JLabel("0.5"));
            jSlider.setLabelTable(t);
		}
		return jSlider;
	}
	/**
	 * This method initializes jSpinner1	
	 * 	
	 * @return javax.swing.JSpinner	
	 */    
	private JSpinner getStartnoSpinner() {
		if (startnoSpinner == null) {
			SpinnerNumberModel spinnerNumberModel9 = new SpinnerNumberModel();  //  @jve:decl-index=0:
            spinnerNumberModel9.setMinimum(new Integer(0));
            spinnerNumberModel9.setValue(new Integer(1));
			startnoSpinner = new JSpinner();
			startnoSpinner.setModel(spinnerNumberModel9);
			startnoSpinner.setPreferredSize(new java.awt.Dimension(50,20));
			startnoSpinner.setMinimumSize(new java.awt.Dimension(60,20));
		}
		return startnoSpinner;
	}
	/**
	 * This method initializes jSpinner2	
	 * 	
	 * @return javax.swing.JSpinner	
	 */    
	private JSpinner getEndnoSpinner() {
		if (endnoSpinner == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(new Integer(-1));
            model.setValue(new Integer(-1));
            
			endnoSpinner = new JSpinner();
            endnoSpinner.setModel(model);
            endnoSpinner.setPreferredSize(new java.awt.Dimension(50,20));
            endnoSpinner.setMinimumSize(new java.awt.Dimension(60,20));
		}
		return endnoSpinner;
	}
	/**
	 * This method initializes jSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */    
	private JSpinner getCandnoSpinner() {
		if (candnoSpinner == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMinimum(new Integer(1));
            model.setValue(new Integer(30));
			candnoSpinner = new JSpinner(model);
			candnoSpinner.setPreferredSize(new java.awt.Dimension(50,20));
			candnoSpinner.setMinimumSize(new java.awt.Dimension(60,20));
		}
		return candnoSpinner;
	}
	/**
	 * This method initializes baseTaggerPanel	
	 * 	
	 * @return namerec.gui.BaseTaggerPanel	
	 */    
	private BaseTaggerPanel getBaseTaggerPanel() {
		if (baseTaggerPanel == null) {
			baseTaggerPanel = new BaseTaggerPanel();
		}
		return baseTaggerPanel;
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getNerecogCb() {
		if (nerecogCb == null) {
			nerecogCb = new JCheckBox();
		}
		return nerecogCb;
	}
	/**
	 * This method initializes jSpinner	
	 * 	
	 * @return javax.swing.JSpinner	
	 */    
	private JSpinner getTimeSpinner() {
		if (timeSpinner == null) {
			SpinnerNumberModel spinnerNumberModel22 = new SpinnerNumberModel();  //  @jve:decl-index=0:
			timeSpinner = new JSpinner();
			timeSpinner.setMinimumSize(new java.awt.Dimension(60,20));
			timeSpinner.setModel(spinnerNumberModel22);
			spinnerNumberModel22.setMinimum(new Integer(10));
            spinnerNumberModel22.setValue(new Integer(100));
		}
		return timeSpinner;
	}
	/**
	 * This method initializes string	
	 * 	
	 * @return java.lang.String	
	 */    
	private String getString() {
		if (string == null) {
			string = new String();
		}
		return string;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getVersionTf() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText("NameRec 1.1neu");
			jTextField.setMaximumSize(new java.awt.Dimension(350,19));
			jTextField.setMinimumSize(new java.awt.Dimension(150,19));
		}
		return jTextField;
	}


          }  //  @jve:decl-index=0:visual-constraint="10,64"
