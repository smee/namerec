package de.uni_leipzig.asv.toolbox.gui;
import javax.swing.JPanel;

import javax.swing.BoxLayout;

import de.uni_leipzig.asv.toolbox.util.Config;

import java.awt.GridBagConstraints;
/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileConfigPanel extends JPanel {

	private FileEntryPanel classnameTf = null;
	private FileEntryPanel regexpTf = null;
	private FileEntryPanel wissenTf = null;
	private FileEntryPanel simplepatternTf = null;
	private FileEntryPanel nepatternTf = null;
	private FileEntryPanel itemsfoundTf = null;
	private FileEntryPanel contextTf = null;
	private FileEntryPanel maybeTf = null;
	/**
	 * This is the default constructor
	 */
	public FileConfigPanel() {
		super();
		initialize();
        loadFromConfig(new Config());
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private  void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(300,200);
		this.add(getWissenTf(), null);
		this.add(getSimplepatternTf(), null);
		this.add(getNepatternTf(), null);
		this.add(getRegexpTf(), null);
		this.add(getClassnameTf(), null);
		this.add(getItemsfoundTf(), null);
		this.add(getContextTf(), null);
		this.add(getMaybeTf(), null);
	}

	/**
	 * This method initializes fileEntryPanel	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getClassnameTf() {
		if (classnameTf == null) {
			classnameTf = new FileEntryPanel();
			classnameTf.setName("Classnames:");
		}
		return classnameTf;
	}
	/**
	 * This method initializes fileEntryPanel11	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getRegexpTf() {
		if (regexpTf == null) {
			regexpTf = new FileEntryPanel();
			regexpTf.setName("Regexps:");
		}
		return regexpTf;
	}
	/**
	 * This method initializes fileEntryPanel	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getWissenTf() {
		if (wissenTf == null) {
			wissenTf = new FileEntryPanel();
			wissenTf.setName("Known names:");
		}
		return wissenTf;
	}
	/**
	 * This method initializes fileEntryPanel	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getSimplepatternTf() {
		if (simplepatternTf == null) {
			simplepatternTf = new FileEntryPanel();
			simplepatternTf.setName("Pattern:");
		}
		return simplepatternTf;
	}
	/**
	 * This method initializes fileEntryPanel1	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getNepatternTf() {
		if (nepatternTf == null) {
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			nepatternTf = new FileEntryPanel();
			gridBagConstraints29.gridx = -1;
			gridBagConstraints29.gridy = -1;
			nepatternTf.setName("NE pattern:");
		}
		return nepatternTf;
	}
	/**
	 * This method initializes fileEntryPanel2	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getItemsfoundTf() {
		if (itemsfoundTf == null) {
			itemsfoundTf = new FileEntryPanel();
			itemsfoundTf.setName("OUT - Found names:");
		}
		return itemsfoundTf;
	}
	/**
	 * This method initializes fileEntryPanel3	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getContextTf() {
		if (contextTf == null) {
			contextTf = new FileEntryPanel();
			contextTf.setName("OUT - Contexts:");
		}
		return contextTf;
	}
	/**
	 * This method initializes fileEntryPanel4	
	 * 	
	 * @return namerec.gui.FileEntryPanel	
	 */    
	private FileEntryPanel getMaybeTf() {
		if (maybeTf == null) {
			maybeTf = new FileEntryPanel();
			maybeTf.setName("OUT - Possibly new names:");
		}
		return maybeTf;
	}
    public void loadFromConfig(Config cfg) {
        getWissenTf().setText(cfg.getString("IN.KNOWLEDGE","wissenAkt.txt"));
        getSimplepatternTf().setText(cfg.getString("IN.PATFILE","patPers.txt"));
        getNepatternTf().setText(cfg.getString("IN.PATFILENE","pats2.txt"));
        getRegexpTf().setText(cfg.getString("IN.REGEXP","regexps.txt"));
        getClassnameTf().setText(cfg.getString("IN.CLASSNAMES","klassNamen.txt"));
        getItemsfoundTf().setText(cfg.getString("OUT.ITEMSFOUND","itemsFound.txt"));
        getContextTf().setText(cfg.getString("OUT.CONTEXT","contexts.txt"));
        getMaybeTf().setText(cfg.getString("OUT.MAYBE","maybe.txt"));
    }
    public void saveToConfig(Config c) {
        c.set("IN.KNOWLEDGE",getWissenTf().getText());
        c.set("IN.PATFILE",getSimplepatternTf().getText());
        c.set("IN.PATFILENE",getNepatternTf().getText());
        c.set("IN.REGEXP",getRegexpTf().getText());
        c.set("IN.CLASSNAMES",getClassnameTf().getText());
        c.set("OUT.ITEMSFOUND",getItemsfoundTf().getText());
        c.set("OUT.CONTEXT",getContextTf().getText());
        c.set("OUT.MAYBE",getMaybeTf().getText());
    }
}
