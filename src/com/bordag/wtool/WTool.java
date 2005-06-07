package com.bordag.wtool;

// standard imports
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;
import de.wortschatz.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */
public class WTool extends WortschatzModul
{
  JTabbedPane jTabbedPane1 = null;
  TitledBorder titledBorder1;
  ConnectionPanel connectionPanel = null;
  OptionsPanel optionsPanel = null;
  TrigramPanel trigramsPanel = null;
  DisambiguationPanel disambiguatorPanel = null;
  ExperimentPanel ExperimentPanel = null;
  ExperimentPanel2 ExperimentPanel2 = null;
  Frame parentFrame = null;

  public WTool(WortschatzTool wTool)
  {
    super(wTool);
    this.parentFrame = wTool.getParentFrame();
    this.jTabbedPane1 = new JTabbedPane();
    this.connectionPanel = new ConnectionPanel();
    this.optionsPanel = new OptionsPanel();
    this.trigramsPanel = new TrigramPanel();
    this.disambiguatorPanel = new DisambiguationPanel(connectionPanel,this.parentFrame);
    this.ExperimentPanel = new ExperimentPanel(connectionPanel);
    this.ExperimentPanel2 = new ExperimentPanel2(connectionPanel);

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("");

    jTabbedPane1.setDoubleBuffered(true);
    jTabbedPane1.setBounds(new Rectangle(0, 0, 800, 600));
    disambiguatorPanel.setToolTipText("set up disambiguator and start it");
    disambiguatorPanel.setLayout(null);
    trigramsPanel.setToolTipText("Options for Trigram calculation");
    trigramsPanel.setLayout(null);
    optionsPanel.setToolTipText("General options");
    optionsPanel.setLayout(null);
    connectionPanel.setToolTipText("connection to the DB");
    connectionPanel.setLayout(null);
    ExperimentPanel.setLayout(null);
    ExperimentPanel2.setLayout(null);

    this.add(jTabbedPane1, null);
    jTabbedPane1.add(connectionPanel, "DB connection");
    jTabbedPane1.add(optionsPanel, "Options");
    jTabbedPane1.add(trigramsPanel, "Trigramms");
    jTabbedPane1.add(disambiguatorPanel, "Disambiguator");
    jTabbedPane1.add(ExperimentPanel, "Experiment");
    jTabbedPane1.add(ExperimentPanel2, "Experiment2");
  }

  public void activated()
  {
    repaint();
  }

  public JPanel getModulePanel()
  {
    return this;
  }

  public char getMnemonic()
  {
    return 'D';
  }

  public String getName()
  {
    return "Disambiguator";
  }

  public Icon getIcon()
  {
    return this.createImageIcon("Dis.jpg");
  }

  public String getToolTip()
  {
    return "Disambiguate words using graph theory.";
  }


  public static void main(String[] args)
  {
	  JFrame frame = new JFrame();
    frame.setSize(new Dimension(800,600));
	  WindowListener l = new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}};
  	frame.addWindowListener(l);

    frame.setTitle("Wortschatz Tool   v0.11  -  01.04.2003");
    WTool wtool = new WTool(null);
	  frame.getContentPane().add(wtool, null);
	  frame.pack();
	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	  frame.setLocation(
  	  screenSize.width/2 - frame.getSize().width/2,
	    screenSize.height/2 - frame.getSize().height/2);
	  frame.show();
  }
}