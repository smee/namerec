package com.bordag.wtool;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;
import java.beans.*;

/**
 * Panel for General Options
 * @author Stefan Bordag
 */
public class OptionsPanel extends JPanel
{
  JTextField tbinTextField = new JTextField();
  JLabel t_binLabel = new JLabel();
  JCheckBox ShowClusCoeffCB = new JCheckBox();
  JLabel word2numberLabel = new JLabel();
  JTextField word2NumberField = new JTextField();
  JTextField number2WordField = new JTextField();
  JLabel number2WordQuery = new JLabel();
  JCheckBox outFileCB = new JCheckBox();
  JTextField outputFileTextField = new JTextField();
  JCheckBox stopWordCB = new JCheckBox();
  JTextField stopWordTextField = new JTextField();

  public OptionsPanel()
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
    ShowClusCoeffCB.setBounds(new Rectangle(15, 10, 200, 20));
    ShowClusCoeffCB.setActionCommand("ShowClusCoeffCB");
    ShowClusCoeffCB.setText("show Cluster Coefficient");
    ShowClusCoeffCB.setBackground(UIManager.getColor("info"));
    t_binLabel.setBounds(new Rectangle(15, 45, 200, 20));
    t_binLabel.setText("query anchor");
    tbinTextField.setBounds(new Rectangle(15, 65, 200, 20));
    tbinTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        tbinTextField_focusLost(e);
      }
    });
    tbinTextField.setText(Options.getInstance().getGenQueryAnchor());
    this.setLayout(null);
    word2numberLabel.setText("word to number conversion query");
    word2numberLabel.setBounds(new Rectangle(15, 100, 200, 20));
    word2NumberField.setText(Options.getInstance().getGenQueryWord2Number());
    word2NumberField.setBounds(new Rectangle(15, 120, 400, 20));
    word2NumberField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        word2NumberField_focusLost(e);
      }
    });
    number2WordField.setText(Options.getInstance().getGenQueryNumber2Word());
    number2WordField.setBounds(new Rectangle(15, 175, 400, 20));
    number2WordField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        number2WordField_focusLost(e);
      }
    });
    number2WordQuery.setText("number to word query");
    number2WordQuery.setBounds(new Rectangle(15, 155, 200, 20));

    if ( Options.getInstance().getGenUseOutputFile().equalsIgnoreCase(Options.TRUE) )
    {
      outFileCB.setSelected(true);
    }
    else
    {
      outputFileTextField.disable();
    }
    outFileCB.setText("use output file");
    outFileCB.setBackground(UIManager.getColor("info"));
    outFileCB.setBounds(new Rectangle(15, 220, 400, 20));
    outFileCB.addItemListener(new java.awt.event.ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        outFileCB_itemStateChanged(e);
      }
    });
    outputFileTextField.setText(Options.getInstance().getGenOutputFile());
    outputFileTextField.setBounds(new Rectangle(15, 240, 400, 20));
    outputFileTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        outputFileTextField_focusLost(e);
      }
    });

    if ( Options.getInstance().getGenUseStopwortFile().equalsIgnoreCase(Options.TRUE) )
    {
      stopWordCB.setSelected(true);
    }
    else
    {
      stopWordTextField.disable();
    }
    stopWordCB.setText("use stop word list");
    stopWordCB.setToolTipText("a stop word list contains all word which should be ignored during " +
    "calculation");
    stopWordCB.setBackground(UIManager.getColor("info"));
    stopWordCB.setBounds(new Rectangle(15, 285, 400, 20));
    stopWordCB.addItemListener(new java.awt.event.ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        stopWordCB_itemStateChanged(e);
      }
    });
    stopWordTextField.setText(Options.getInstance().getGenStopwortFile());
    stopWordTextField.setBounds(new Rectangle(15, 305, 400, 20));
    stopWordTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        stopWordTextField_focusLost(e);
      }
    });
    this.setBackground(SystemColor.info);
    this.add(tbinTextField, null);
    this.add(t_binLabel, null);
    this.add(ShowClusCoeffCB, null);
    this.add(word2numberLabel, null);
    this.add(word2NumberField, null);
    this.add(number2WordField, null);
    this.add(number2WordQuery, null);
    this.add(outFileCB, null);
    this.add(outputFileTextField, null);
    this.add(stopWordCB, null);
    this.add(stopWordTextField, null);
  }

  void outFileCB_itemStateChanged(ItemEvent e)
  {
    if ( e.getStateChange() == ItemEvent.DESELECTED )
    {
      Options.getInstance().setGenUseOutputFile(Options.FALSE);
      this.outputFileTextField.disable();
      this.outputFileTextField.repaint();
    }
    else
    {
      Options.getInstance().setGenUseOutputFile(Options.TRUE);
      this.outputFileTextField.enable();
      this.outputFileTextField.repaint();
    }
  }

  void stopWordCB_itemStateChanged(ItemEvent e)
  {
    if ( e.getStateChange() == ItemEvent.DESELECTED )
    {
      Options.getInstance().setGenUseStopwortFile(Options.FALSE);
      this.stopWordTextField.disable();
      this.stopWordTextField.repaint();
    }
    else
    {
      Options.getInstance().setGenUseStopwortFile(Options.TRUE);
      this.stopWordTextField.enable();
      this.stopWordTextField.repaint();
    }
  }

  void tbinTextField_focusLost(FocusEvent e)
  {
    if ( this.tbinTextField.getText() != null && this.tbinTextField.getText().length() > 0 )
    {
      Options.getInstance().setGenQueryAnchor(this.tbinTextField.getText());
    }
  }

  void word2NumberField_focusLost(FocusEvent e)
  {
    if ( this.word2NumberField.getText() != null && this.word2NumberField.getText().length() > 0 )
    {
      Options.getInstance().setGenQueryWord2Number(this.word2NumberField.getText());
    }
  }

  void number2WordField_focusLost(FocusEvent e)
  {
    if ( this.number2WordField.getText() != null && this.number2WordField.getText().length() > 0 )
    {
      Options.getInstance().setGenQueryNumber2Word(this.number2WordField.getText());
    }
  }

  void outputFileTextField_focusLost(FocusEvent e)
  {
    if ( this.outputFileTextField.getText() != null && this.outputFileTextField.getText().length() > 0 )
    {
      Options.getInstance().setGenOutputFile(this.outputFileTextField.getText());
    }
  }

  void stopWordTextField_focusLost(FocusEvent e)
  {
    if ( this.stopWordTextField.getText() != null && this.stopWordTextField.getText().length() > 0 )
    {
      Options.getInstance().setGenStopwortFile(this.stopWordTextField.getText());
    }
  }

}