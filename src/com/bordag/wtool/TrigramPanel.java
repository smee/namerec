package com.bordag.wtool;


// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;
import java.beans.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class TrigramPanel extends JPanel
{
  JLabel triMaxCollLabel = new JLabel();
  JTextField MaxNumCollTextField = new JTextField();
  JLabel triMinRecursionLabel = new JLabel();
  JTextField minNumRecTextField = new JTextField();
  JLabel minSigLabel = new JLabel();
  JTextField minCollSigTextField = new JTextField();
  JTextField maxNumRecTextField = new JTextField();
  JLabel minWordNrLabel = new JLabel();
  JTextField minWordNumTextField = new JTextField();
  JLabel queryCollLabel = new JLabel();
  JLabel maxLabel = new JLabel();
  JLabel minLabel1 = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea QueryCollTextArea = new JTextArea();

  public TrigramPanel()
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
  private void jbInit() throws Exception
  {
    triMaxCollLabel.setText("maximum number of collocations per tripel");
    triMaxCollLabel.setBounds(new Rectangle(15, 12, 329, 20));
    this.setLayout(null);
    MaxNumCollTextField.setText(Options.getInstance().getTriMaxKollokationen());
    MaxNumCollTextField.setBounds(new Rectangle(15, 30, 242, 20));
    MaxNumCollTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        MaxNumCollTextField_focusLost(e);
      }
    });
    triMinRecursionLabel.setText("minimum / maximum number of recursions");
    triMinRecursionLabel.setBounds(new Rectangle(15, 72, 332, 20));
    minNumRecTextField.setText(Options.getInstance().getTriMinRecursion());
    minNumRecTextField.setBounds(new Rectangle(56, 94, 71, 20));
    minNumRecTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        minNumRecTextField_focusLost(e);
      }
    });
    minSigLabel.setText("minimum collocation significance");
    minSigLabel.setBounds(new Rectangle(15, 136, 311, 20));
    minCollSigTextField.setText(Options.getInstance().getTriMinSignifikanz());
    minCollSigTextField.setBounds(new Rectangle(15, 158, 326, 20));
    minCollSigTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        minCollSigTextField_focusLost(e);
      }
    });
    maxNumRecTextField.setText(Options.getInstance().getTriMaxRecursion());
    maxNumRecTextField.setBounds(new Rectangle(189, 94, 71, 20));
    maxNumRecTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        maxNumRecTextField_focusLost(e);
      }
    });
    minWordNrLabel.setText("ignore all wordnumbers below given value");
    minWordNrLabel.setBounds(new Rectangle(15, 207, 311, 20));
    minWordNumTextField.setText(Options.getInstance().getTriMinWordNr());
    minWordNumTextField.setBounds(new Rectangle(15, 234, 370, 20));
    minWordNumTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        minWordNumTextField_focusLost(e);
      }
    });
    queryCollLabel.setText("Query for retrieving collocations");
    queryCollLabel.setBounds(new Rectangle(15, 269, 340, 20));
    maxLabel.setText("max:");
    maxLabel.setBounds(new Rectangle(151, 94, 40, 20));
    minLabel1.setBounds(new Rectangle(15, 94, 34, 20));
    minLabel1.setText("min:");
    jScrollPane1.setBounds(new Rectangle(16, 297, 485, 175));
    QueryCollTextArea.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        QueryCollTextArea_focusLost(e);
      }
    });
    QueryCollTextArea.setText(Options.getInstance().getTriQueryKollokationen());
    this.add(triMinRecursionLabel, null);
    this.add(minSigLabel, null);
    this.add(minWordNrLabel, null);
    this.add(minWordNumTextField, null);
    this.add(minCollSigTextField, null);
    this.add(MaxNumCollTextField, null);
    this.add(queryCollLabel, null);
    this.add(triMaxCollLabel, null);
    this.add(minLabel1, null);
    this.add(minNumRecTextField, null);
    this.add(maxNumRecTextField, null);
    this.add(maxLabel, null);
    this.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(QueryCollTextArea, null);
  }

  void MaxNumCollTextField_focusLost(FocusEvent e)
  {
    if ( this.MaxNumCollTextField.getText() != null && this.MaxNumCollTextField.getText().length() > 0 )
    {
      Options.getInstance().setTriMaxKollokationen(this.MaxNumCollTextField.getText());
    }
  }

  void minNumRecTextField_focusLost(FocusEvent e)
  {
    if ( this.minNumRecTextField.getText() != null && this.minNumRecTextField.getText().length() > 0 )
    {
      Options.getInstance().setTriMinRecursion(this.minNumRecTextField.getText());
    }
  }

  void maxNumRecTextField_focusLost(FocusEvent e)
  {
    if ( this.maxNumRecTextField.getText() != null && this.maxNumRecTextField.getText().length() > 0 )
    {
      Options.getInstance().setTriMaxRecursion(this.maxNumRecTextField.getText());
    }
  }

  void minCollSigTextField_focusLost(FocusEvent e)
  {
    if ( this.minCollSigTextField.getText() != null && this.minCollSigTextField.getText().length() > 0 )
    {
      Options.getInstance().setTriMinSignifikanz(this.minCollSigTextField.getText());
    }
  }

  void minWordNumTextField_focusLost(FocusEvent e)
  {
    if ( this.minWordNumTextField.getText() != null && this.minWordNumTextField.getText().length() > 0 )
    {
      Options.getInstance().setTriMinWordNr(this.minWordNumTextField.getText());
    }
  }

  void QueryCollTextArea_focusLost(FocusEvent e)
  {
    if ( this.QueryCollTextArea.getText() != null && this.QueryCollTextArea.getText().length() > 0 )
    {
      Options.getInstance().setTriQueryKollokationen(this.QueryCollTextArea.getText());
      QueryCollTextArea.setText(Options.getInstance().getTriQueryKollokationen());
      QueryCollTextArea.setCaretPosition(0);
    }
  }
}