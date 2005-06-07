package com.bordag.wtool;


// app specific imports
import com.bordag.sgz.util.*;
import com.bordag.sgz.*;
import com.bordag.sgz.algorithms.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class DisambiguationPanel extends JPanel
{
  JLabel collocPerStepLabel = new JLabel();
  JTextField collocPerStepTextField = new JTextField();
  JLabel maxCollLabel = new JLabel();
  JTextField maxCollTextField = new JTextField();
  JLabel maxRunsLabel = new JLabel();
  JTextField maxRunsTextField = new JTextField();
  JLabel minCollSigLabel = new JLabel();
  JTextField minCollSigTextField = new JTextField();
  JLabel minWordNumLabel = new JLabel();
  JTextField minWordNumTextField = new JTextField();
  JLabel clusterTresholdLabel = new JLabel();
  JSlider clusterThresholdSlider = new JSlider();
  JLabel collocQueryLabel = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea collocQueryTextArea = new JTextArea();
  JLabel inputWordLabel = new JLabel();
  JTextField inputWordTextField = new JTextField();
  JButton startButton = new JButton();

  ConnectionPanel connectionPanel = null;
  DisambiguatorThread disThread = null;
  Frame parentFrame = null;
  DisambiguationPanel instance = null;

  public DisambiguationPanel(ConnectionPanel cPanel, Frame parentFrame)
  {
    this.parentFrame = parentFrame;
    this.connectionPanel = cPanel;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    this.instance = this;
  }

  private void jbInit() throws Exception
  {
    collocPerStepLabel.setText("collocations per step window");
    collocPerStepLabel.setBounds(new Rectangle(15, 8, 188, 20));
    this.setLayout(null);
    collocPerStepTextField.setText(Options.getInstance().getDisKollokationenPerStep());
    collocPerStepTextField.setBounds(new Rectangle(15, 29, 167, 20));
    collocPerStepTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        collocPerStepTextField_focusLost(e);
      }
    });
    maxCollLabel.setText("max collocations");
    maxCollLabel.setBounds(new Rectangle(200, 8, 132, 20));
    maxCollTextField.setText(Options.getInstance().getDisMaxKollokationen());
    maxCollTextField.setBounds(new Rectangle(200, 30, 152, 20));
    maxCollTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        maxCollTextField_focusLost(e);
      }
    });
    maxRunsLabel.setText("max runs");
    maxRunsLabel.setBounds(new Rectangle(400, 10, 97, 20));
    maxRunsTextField.setText(Options.getInstance().getDisMaxRuns());
    maxRunsTextField.setBounds(new Rectangle(400, 30, 152, 20));
    maxRunsTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        maxRunsTextField_focusLost(e);
      }
    });
    minCollSigLabel.setText("minimum significance of collocations");
    minCollSigLabel.setBounds(new Rectangle(15, 61, 226, 20));
    minCollSigTextField.setText(Options.getInstance().getDisMinSignifikanz());
    minCollSigTextField.setBounds(new Rectangle(15, 86, 232, 20));
    minCollSigTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        minCollSigTextField_focusLost(e);
      }
    });
    minWordNumLabel.setToolTipText("ignore all words which have a wordnumber less than the given value");
    minWordNumLabel.setText("minimum wordnumber");
    minWordNumLabel.setBounds(new Rectangle(300, 61, 202, 20));
    minWordNumTextField.setToolTipText("ignore all words which have a wordnumber less than the given value");
    minWordNumTextField.setText(Options.getInstance().getDisMinWordNr());
    minWordNumTextField.setBounds(new Rectangle(300, 91, 236, 20));
    minWordNumTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        minWordNumTextField_focusLost(e);
      }
    });
    clusterTresholdLabel.setToolTipText("lesser value means that less clusters will be generated");
    clusterTresholdLabel.setText("Treshold for clustering");
    clusterTresholdLabel.setBounds(new Rectangle(15, 124, 344, 20));
    clusterThresholdSlider.setValue(new Integer(Options.getInstance().getDisClusterThreshold()).intValue());
    clusterThresholdSlider.setSnapToTicks(false);
    clusterThresholdSlider.setToolTipText("lesser value means that less clusters will be generated");
    clusterThresholdSlider.setBounds(new Rectangle(15, 150, 506, 51));
    clusterThresholdSlider.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        clusterThresholdSlider_mouseReleased(e);
      }
    });
		Hashtable d1 = new Hashtable();
		d1.put(new Integer(0),new JLabel("0"));
		d1.put(new Integer(50),new JLabel("50"));
		d1.put(new Integer(100),new JLabel("100"));
		clusterThresholdSlider.setLabelTable(d1);
    clusterThresholdSlider.setMajorTickSpacing(25);
    clusterThresholdSlider.setMinorTickSpacing(5);
		clusterThresholdSlider.setPaintLabels(true);
    clusterThresholdSlider.setPaintTicks(true);
    clusterThresholdSlider.setPaintTrack(true);

    collocQueryLabel.setText("query to retrieve collocations of a given word");
    collocQueryLabel.setBounds(new Rectangle(16, 203, 330, 20));
    jScrollPane1.setBounds(new Rectangle(14, 227, 536, 108));
    collocQueryTextArea.setText(Options.getInstance().getDisQueryKollokationen());
    collocQueryTextArea.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        collocQueryTextArea_focusLost(e);
      }
    });
    inputWordLabel.setText("input word : ");
    inputWordLabel.setBounds(new Rectangle(15, 354, 71, 20));
    inputWordTextField.setText(Options.getInstance().getDisLastWord());
    inputWordTextField.setBounds(new Rectangle(106, 354, 215, 20));
    inputWordTextField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        inputWordTextField_focusLost(e);
      }
    });
    startButton.setText("start");
    startButton.setBounds(new Rectangle(369, 353, 123, 20));
    startButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        startButton_actionPerformed(e);
      }
    });
    this.setForeground(Color.darkGray);
    this.add(collocPerStepLabel, null);
    this.add(collocPerStepTextField, null);
    this.add(maxCollLabel, null);
    this.add(maxCollTextField, null);
    this.add(maxRunsLabel, null);
    this.add(maxRunsTextField, null);
    this.add(minCollSigLabel, null);
    this.add(minCollSigTextField, null);
    this.add(minWordNumLabel, null);
    this.add(minWordNumTextField, null);
    this.add(clusterTresholdLabel, null);
    this.add(clusterThresholdSlider, null);
    this.add(jScrollPane1, null);
    jScrollPane1.getViewport().add(collocQueryTextArea, null);
    this.add(inputWordLabel, null);
    this.add(inputWordTextField, null);
    this.add(startButton, null);
    this.add(collocQueryLabel, null);
  }

  void collocPerStepTextField_focusLost(FocusEvent e)
  {
    if ( this.collocPerStepTextField.getText() != null && this.collocPerStepTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisKollokationenPerStep(this.collocPerStepTextField.getText());
    }
  }

  void maxCollTextField_focusLost(FocusEvent e)
  {
    if ( this.maxCollTextField.getText() != null && this.maxCollTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisMaxKollokationen(this.maxCollTextField.getText());
    }
  }

  void maxRunsTextField_focusLost(FocusEvent e)
  {
    if ( this.maxRunsTextField.getText() != null && this.maxRunsTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisMaxRuns(this.maxRunsTextField.getText());
    }
  }

  void minCollSigTextField_focusLost(FocusEvent e)
  {
    if ( this.minCollSigTextField.getText() != null && this.minCollSigTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisMinSignifikanz(this.minCollSigTextField.getText());
    }
  }

  void minWordNumTextField_focusLost(FocusEvent e)
  {
    if ( this.minWordNumTextField.getText() != null && this.minWordNumTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisMinWordNr(this.minWordNumTextField.getText());
    }
  }

  void clusterThresholdSlider_mouseReleased(MouseEvent e)
  {
    System.out.println("Value is: "+clusterThresholdSlider.getValue());
		Options.getInstance().setDisClusterThreshold(""+clusterThresholdSlider.getValue());
  }

  void collocQueryTextArea_focusLost(FocusEvent e)
  {
    if ( this.collocQueryTextArea.getText() != null && this.collocQueryTextArea.getText().length() > 0 )
    {
      Options.getInstance().setDisQueryKollokationen(this.collocQueryTextArea.getText());
    }
  }

  void inputWordTextField_focusLost(FocusEvent e)
  {
    if ( this.inputWordTextField.getText() != null && this.inputWordTextField.getText().length() > 0 )
    {
      Options.getInstance().setDisLastWord(this.inputWordTextField.getText());
    }
  }

  void startButton_actionPerformed(ActionEvent e)
  {
    if ( disThread == null && this.connectionPanel.connection != null )
    {
      disThread = new DisambiguatorThread();
      this.startButton.setText("STOP");
    }
    else if ( disThread == null && this.connectionPanel.connection == null )
    {
    }
    else if ( disThread != null )
    {
      disThread.dispose();
      disThread = null;
      this.startButton.setText("START");
    }
  }

  class DisambiguatorThread implements Runnable
  {
    private Thread myThread = null;
    private NonThreadedDisambiguator dis = null;
    private DisOutputDialog dialog = null;

    public DisambiguatorThread()
    {
      myThread = new Thread(this);
      myThread.start();
    }

    public void run()
    {
      Output.println("Starting disambiguation");

      ComparableStringBuffer word = new ComparableStringBuffer(Options.getInstance().getDisLastWord());
      dis = new NonThreadedDisambiguator(connectionPanel.connection, word);

      dialog = new DisOutputDialog(instance, parentFrame, "Disambiguator progress", false, dis);
      dialog.setSize(800,600);
      dialog.setLocation(parentFrame.getLocation());
      dialog.show();

      dis.runAlgorithm();
    }

    /**
     * Tries to stop the thread
     */
    public void dispose()
    {
      if ( dis != null )
      {
        System.out.println("Operation stop requested, wait for termination");
        dis.dispose();
        dialog = null;
        dis = null;
      }
    }
  }

}