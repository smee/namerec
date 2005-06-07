package com.bordag.sgz;

import javax.swing.*;
import java.awt.*;


/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class Disambiguator extends JFrame
{
  JMenuBar MenuBar = new JMenuBar();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenuItem jMenuItem4 = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JTextField jTextField1 = new JTextField();
  JProgressBar jProgressBar1 = new JProgressBar();
  JButton ButtonStartDis = new JButton();

  public Disambiguator()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Hier ist die main, wo die Klasse sich selbst instanziiert und anzeigt.
   * Weiter geht's also mit der Funktion "public KatrinGrafik()" weiter oben.
   */
  public static void main(String[] args)
  {
		Disambiguator myself = new Disambiguator();
		myself.show();
  }
  private void jbInit() throws Exception
  {
    this.getContentPane().setLayout(null);
    this.setSize(800,600);
    jMenu1.setText("File");
    jMenuItem2.setText("Load");
    jMenuItem3.setText("Save");
    jMenuItem4.setText("Quit");
    jMenu2.setText("About");
    jTextField1.setText("jTextField1");
    jTextField1.setBounds(new Rectangle(21, 22, 314, 20));
    jProgressBar1.setBounds(new Rectangle(12, 279, 370, 16));
    ButtonStartDis.setToolTipText("Start Disambiguation");
    ButtonStartDis.setActionCommand("startDis");
    ButtonStartDis.setText("Go");
    ButtonStartDis.setBounds(new Rectangle(350, 19, 39, 29));
    MenuBar.add(jMenu1);
    MenuBar.add(jMenu2);
    jMenu1.add(jMenuItem2);
    jMenu1.add(jMenuItem3);
    jMenu1.addSeparator();
    jMenu1.add(jMenuItem4);
    this.getContentPane().add(jTextField1, null);
    this.getContentPane().add(jProgressBar1, null);
    this.getContentPane().add(ButtonStartDis, null);
    this.getContentPane().add(jMenu1);
  }

}