
//Titel:      Wortschatz-Tool
//Version:
//Copyright:  Copyright (c) 1999
//Autor:     C. Biemann
//Organisation:    Uni Leipzig
//Beschreibung:Ihre Beschreibung
package com.biemann.pendel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import com.borland.jbcl.layout.*;
import javax.swing.border.*;
import de.wortschatz.*;
import java.io.*;
import java.util.*;


public class PendelPanel extends WortschatzModul {
    NameTable inItemsNameTable = new NameTable();
    NameTable inItemsBackNameTable = new NameTable();
    NameTable regexpNameTable = new NameTable();
    NameTable klassKeysNameTable=new NameTable();
    Vector classRules=new Vector();
    Vector extrPats=new Vector();
    Pendel pendelProcess=null;
    Watcher watcherProcess=null;

// zeug, das das wortschatzModul braucht
   public String getToolTip() {return "extract semantic relations by search and verification";};
   public void activated() {repaint();};
   public JPanel getModulePanel() {return this;};
   public char getMnemonic() {return (char)(88);};
   public String getName() {return "Pendel Tool";};
   public Icon getIcon() { return this.createImageIcon("Pen.jpg");}


  JPanel pendelFramePanel = new JPanel();  // Main Panel
  JTabbedPane PendelTabbedPane = new JTabbedPane();
  JPanel inputItemsPanel = new JPanel();
  JPanel outputItemsPanel = new JPanel();
  JPanel outputRulesPanel = new JPanel();
  JPanel filePanel = new JPanel();
  JButton inItemClearButton = new JButton();
  JButton inItemsAddFileButton = new JButton();
  JTextField inItemFileNamePane = new JTextField();
  JButton inItemSaveFileButton = new JButton();
  JTextField inItemSavefileNamePane = new JTextField();
  JTextField classRulesSaveFileNamePane = new JTextField();
  JButton classRulesDeleteutton = new JButton();
  JButton classRulesClearButton= new JButton();
  JButton classRulesAddButton = new JButton();
  JTextField classRulesAddLeftField = new JTextField();
  JPanel inputFeaturesPanel = new JPanel();
  JButton classRulesSaveFileButton = new JButton();
  JButton classRulesAddFileButton = new JButton();
  JButton tagEncodeAutoButton = new JButton();
  JTextField classRulesFileNamePane = new JTextField();
  JLabel outItemsUnusedlLabel = new JLabel();
  JScrollPane outItemsUnusedScrollPane = new JScrollPane();
  JLabel outItemsAllItemsLabel = new JLabel();
  JPanel tagSystemPanel = new JPanel();
  JLabel paraSearchLabel = new JLabel();
  JPanel parametersPanel = new JPanel();
  JScrollPane outItemsAllScrollPane = new JScrollPane();
  JTable outItemsAllTable = new JTable();
  JButton paraDefaultButton = new JButton();
  JLabel paraVerLabel = new JLabel();
  JLabel paraThreshLabel = new JLabel();
  JTextField paraThreshPane = new JTextField();
  JLabel paraCountLabel = new JLabel();
  JTextField paraMinCountPane = new JTextField();
  JLabel paraThreshRuleLabel = new JLabel();
  JLabel paraCountRuleLabel = new JLabel();
  JTextField paraThreshRulePane = new JTextField();
  JTextField paraMinCountRulePane = new JTextField();
  JLabel paraDBnameLabel = new JLabel();
  JLabel paraUserLabel = new JLabel();
  JLabel paraPasswdLabel = new JLabel();
  JTextField paraDBnamePane = new JTextField();
  JTextField paraUserPane = new JTextField();
  JPasswordField paraPasswdField = new JPasswordField();
  JLabel tagEncodeLabel = new JLabel();
  JLabel tagRegexpLabel = new JLabel();
  JTable tagRegexpTable = new JTable();
  JCheckBox tntCheckBox = new JCheckBox();
  JLabel tagTntLabel = new JLabel();
  JTable tagTntTable = new JTable();
  JLabel outRulesTestLabel = new JLabel();
  JScrollPane outRulesTestPane = new JScrollPane();
  JTable outRulesTestTable = new JTable();
  JLabel outRulesUseLabel = new JLabel();
  JScrollPane outRulesUsePane = new JScrollPane();
  JTable outRulesUseTable = new JTable();
  JPanel fileOutPanel = new JPanel();
  JLabel fileOutLabel = new JLabel();
  JTable outItemsUnusedTable = new JTable();
  JCheckBox fileOutNewItemCheckbox = new JCheckBox();
  JTextField fileOutNewItemsPane = new JTextField();
  JCheckBox fileOutMaybeItemCheckbox = new JCheckBox();
  JTextField fileOutMaybeItemsPane = new JTextField();
  JCheckBox fileOutRuleContextCheckbox = new JCheckBox();
  JTextField fileOutRuleContextPane = new JTextField();
  JCheckBox fileOutExtrPatsCheckbox = new JCheckBox();
  JTextField fileOutExtrPatsPane = new JTextField();
  JCheckBox fileOutLogCheckbox = new JCheckBox();
  JTextField fileOutLogPane = new JTextField();
  JPanel fileConfPanel = new JPanel();
  JLabel fileConfLabel = new JLabel();
  JButton fileConfLoadButton = new JButton();
  JTextField fileConfLoadField = new JTextField();
  JButton fileConfSaveButton = new JButton();
  JTextField fileConfSaveField = new JTextField();
  JTextField classRulesAddGoalField = new JTextField();
  JPanel classRulesPanel = new JPanel();
  JLabel classRulesLabel = new JLabel();
  JTextField extrPatsFileNamePane = new JTextField();
  JButton extrPatsClearButton = new JButton();
  JButton extrPatsAddButton = new JButton();
  JButton extrPatsSaveFileButton = new JButton();
  JButton extrPatsAddFileButton = new JButton();
  JTextField extrPatsAddPatField = new JTextField();
  JScrollPane extrPatsScrollPane = new JScrollPane();
  JPanel extrPatternsPanel = new JPanel();
  JLabel extrPatsLabel = new JLabel();
  JTextField extrPatsSaveFileNamePane = new JTextField();
  JButton extrPatsDeleteButton = new JButton();
  JTable extrPatsTable = new JTable();
  JLabel inItemsLabel = new JLabel();
  JScrollPane inItemScrollPane = new JScrollPane();
  JScrollPane tagRegexpScrollPane = new JScrollPane();
  JTable inItemTable = new JTable();
  JLabel paraLabel = new JLabel();
  JLabel tagLabel = new JLabel();
  JButton tagLoadCodeButton = new JButton();
  JButton tagCodeSaveButton = new JButton();
  JTextField tagCodeLoadPane = new JTextField();
  JTextField tagCodeSavePane = new JTextField();
  JButton tagTntLoadButton = new JButton();
  JButton tagTntSaveButton = new JButton();
  JTextField tagTntLoadPane = new JTextField();
  JTextField tagTntSavePane = new JTextField();
  JButton tagRegexpLoadButton = new JButton();
  JButton tagRegexpSaveButton = new JButton();
  JTextField tagRegexpLoadPane = new JTextField();
  JTextField tagRegexpSavePane = new JTextField();
  JButton outItemsStartButton = new JButton();
  JButton outItemsStopButton = new JButton();
  JButton inItemAddButton = new JButton();
  JButton inItemDeleteButton = new JButton();
  JTextField inItemAddItemField = new JTextField();
  JTextField inItemAddClassField = new JTextField();
  JLabel inItemItemLabel = new JLabel();
  JLabel inItemClassLabel = new JLabel();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  JButton tagRegexpAddButton = new JButton();
  JButton tagRegexpClearButton = new JButton();
  JTextField tagRegexpAddClassField = new JTextField();
  JTextField tagRegexpAddRegexpField = new JTextField();
  JLabel tagRegexpAddRegexpLabel = new JLabel();
  TitledBorder titledBorder3;
  JLabel tagRegexpAddClassLabel = new JLabel();
  TitledBorder titledBorder4;
  JButton tagRegexpDeleteButton = new JButton();
  JScrollPane tagEncodeScrollPane = new JScrollPane();
  JTable tagEncodeTable = new JTable();
  JButton tagEncodeAddButton = new JButton();
  JButton tagEncodeDeleteButton = new JButton();
  JTextField tagEncodeAddClassField = new JTextField();
  JTextField tagEncodeAddCodeField = new JTextField();
  JLabel tagEncodeClassLabel = new JLabel();
  JLabel tagEncodeCodeLable = new JLabel();
  JButton tagEncodeClearButton = new JButton();
  JScrollPane classRulesScrollPane = new JScrollPane();
  JTable classRulesTable = new JTable();
  JLabel classRulesLeftLabel = new JLabel();
  JLabel classRulesGoalLabel = new JLabel();
  JLabel extPatPatLabel = new JLabel();
  JTextField extrPatsAddClassLField = new JTextField();
  JLabel extrPatsClassLabel = new JLabel();
  JTextField paraSearchNrField = new JTextField();
  JTextField paraVerNrField = new JTextField();
  JLabel inItemBackLabel = new JLabel();
  JButton inItemsBackClearButton = new JButton();
  JButton inItemsBackLoadButton = new JButton();
  JButton inItemsBackSaveFileButtonm = new JButton();
  JTextField inItemsBackLoadField = new JTextField();
  JTextField inItemsBackSaveField = new JTextField();
  JScrollPane inItemBackScrollPane = new JScrollPane();
  JTable inItemBackTable = new JTable();
  JButton inItemBackAddButton = new JButton();
  JButton inItemBackDeleteButton = new JButton();
  JLabel inItemBackItemLabel = new JLabel();
  JLabel inItemBackClassLabel = new JLabel();
  JTextField inItemBackAddItemField = new JTextField();
  JTextField inItemBackAddClassField = new JTextField();
  JButton outItemsPauseButton = new JButton();
  JButton outItemsDeleteUnusedButton = new JButton();
  JButton outItemsDeleteAllButton = new JButton();



  //Frame konstruieren
  public PendelPanel(WortschatzTool wTool)
  {
    super(wTool);
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }


  //Initialisierung der Komponente
  private void jbInit() throws Exception, IOException  {
    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    titledBorder3 = new TitledBorder("");
    titledBorder4 = new TitledBorder("");
 //   this.getContentPane().setLayout(null);
    this.setLayout(null);
    this.setSize(new Dimension(800, 550));
 //   this.setTitle("Wortschatz-Tool");
    pendelFramePanel.setBackground(Color.lightGray);
    pendelFramePanel.setBounds(new Rectangle(0,0, 790, 540));
    pendelFramePanel.setLayout(null);
    PendelTabbedPane.setBackground(Color.lightGray);
    PendelTabbedPane.setBounds(new Rectangle(0,0, 790,540 ));

  // Input Items Panel

    inputFeaturesPanel.setLayout(null);

  // Input Features
   // classRules
    classRulesPanel.setBorder(titledBorder1);
    classRulesPanel.setBounds(new Rectangle(5, 5, 780, 240));
    classRulesPanel.setLayout(null);

    classRulesLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    classRulesLabel.setText("Classification Rules");
    classRulesLabel.setBounds(new Rectangle(5, 5, 120, 20));

    classRulesClearButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        classRulesClearButton_actionPerformed(e);
      }
    });
    classRulesClearButton.setBounds(new Rectangle(120, 5, 70, 25));
    classRulesClearButton.setText("Clear");
    classRulesClearButton.setBackground(Color.red);

    classRulesAddFileButton.setMargin(new Insets(0, 0, 0, 0));
    classRulesAddFileButton.setText("Add from File");
    classRulesAddFileButton.setBounds(new Rectangle(200, 5, 100, 25));

    classRulesFileNamePane.setText("filename here");
    classRulesFileNamePane.setBounds(new Rectangle(305, 5, 160, 25));

    classRulesSaveFileButton.setText("Save to file");
    classRulesSaveFileButton.setBounds(new Rectangle(500, 5, 100, 25));
    classRulesSaveFileNamePane.setBounds(new Rectangle(605, 5, 160, 25));
    classRulesSaveFileNamePane.setText("enter filename here");

    classRulesAddButton.setBounds(new Rectangle(5, 180, 70, 25));
    classRulesAddButton.setText("Add");
    classRulesAddLeftField.setBounds(new Rectangle(80, 180, 160, 25));


    classRulesDeleteutton.setBounds(new Rectangle(5, 210, 120, 25));
    classRulesDeleteutton.setText("Delete selected");
    classRulesAddGoalField.setBounds(new Rectangle(245, 180, 160, 25));
    classRulesScrollPane.setBounds(new Rectangle(5, 35, 770, 120));
    classRulesTable.setCellSelectionEnabled(true);
    classRulesTable.setColumnSelectionAllowed(false);
    classRulesLeftLabel.setBorder(titledBorder1);
    classRulesLeftLabel.setHorizontalAlignment(SwingConstants.CENTER);
    classRulesLeftLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    classRulesLeftLabel.setText("Left Side");
    classRulesLeftLabel.setBounds(new Rectangle(80, 160, 160, 20));
    classRulesGoalLabel.setBorder(titledBorder1);
    classRulesGoalLabel.setHorizontalAlignment(SwingConstants.CENTER);
    classRulesGoalLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    classRulesGoalLabel.setText("Goal Class");
    classRulesGoalLabel.setBounds(new Rectangle(245, 160, 160, 20));

    classRulesClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       classRulesClearButton_actionPerformed(e);
      }
    });
    classRulesAddFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        classRulesAddFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    classRulesSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        classRulesSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    classRulesAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        classRulesAddButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    classRulesDeleteutton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        classRulesDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });


  // extr Patterns Pane

    extrPatternsPanel.setBorder(titledBorder1);
    extrPatternsPanel.setBounds(new Rectangle(5, 250, 780, 240));
    extrPatternsPanel.setLayout(null);
    extrPatsAddFileButton.setBounds(new Rectangle(200, 5, 100, 25));
    extrPatsAddFileButton.setMargin(new Insets(0, 0, 0, 0));
    extrPatsAddFileButton.setText("Add from File");
    extrPatsFileNamePane.setBounds(new Rectangle(305, 5, 160, 25));
    extrPatsFileNamePane.setText("enter filename here");
    extrPatsClearButton.setBounds(new Rectangle(120, 5, 70, 25));
    extrPatsClearButton.setBackground(Color.red);
    extrPatsClearButton.setText("Clear");
    extrPatsAddButton.setBounds(new Rectangle(5, 180, 70, 25));
    extrPatsAddButton.setText("Add");
    extrPatsAddPatField.setBounds(new Rectangle(80, 180, 200, 25));
    extrPatsSaveFileButton.setBounds(new Rectangle(500, 5, 100, 25));
    extrPatsSaveFileButton.setText("Save to file");
    extrPatsSaveFileNamePane.setBounds(new Rectangle(605, 5, 160, 25));
    extrPatsSaveFileNamePane.setText("enter filename here");
    extrPatsDeleteButton.setBounds(new Rectangle(5, 210, 120, 25));
    extrPatsDeleteButton.setText("Delete selected");
    extrPatsScrollPane.setBounds(new Rectangle(5, 35, 770, 120));
    extrPatsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    extrPatsLabel.setText("Extraction Patterns");
    extrPatsLabel.setBounds(new Rectangle(5, 5, 120, 20));
    extPatPatLabel.setBorder(titledBorder1);
    extPatPatLabel.setHorizontalAlignment(SwingConstants.CENTER);
    extPatPatLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    extPatPatLabel.setText("Pattern");
    extPatPatLabel.setBounds(new Rectangle(80, 160, 200, 20));
    extrPatsAddClassLField.setBounds(new Rectangle(280, 180, 160, 25));
    extrPatsClassLabel.setBorder(titledBorder1);
    extrPatsClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
    extrPatsClassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    extrPatsClassLabel.setText("Classification String");
    extrPatsClassLabel.setBounds(new Rectangle(280, 160, 160, 20));

    extrPatsClearButton.addActionListener(new java.awt.event.ActionListener() {

      public void actionPerformed(ActionEvent e) {
        extrPatsClearButton_actionPerformed(e);
      }
    });
    extrPatsAddFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        extrPatsAddFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    extrPatsSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        extrPatsSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    extrPatsAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        extrPatsAddButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    extrPatsDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        extrPatsDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

   // inputItems Panel

    inputItemsPanel.setLayout(null);

    inItemsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    inItemsLabel.setText("Start Items");
    inItemsLabel.setBounds(new Rectangle(5, 10, 200,20));

    inItemClearButton.setBackground(Color.red);
    inItemClearButton.setText("Clear");
    inItemClearButton.setBounds(new Rectangle(5, 40, 80, 55));
    inItemsAddFileButton.setActionCommand("loadItemsFromFile");
    inItemsAddFileButton.setMargin(new Insets(0, 0, 0, 0));
    inItemsAddFileButton.setText("Add from File");
    inItemsAddFileButton.setBounds(new Rectangle(100, 40, 100, 25));
    inItemFileNamePane.setText("filename here");
    inItemFileNamePane.setBounds(new Rectangle(210, 40, 170, 25));


    inItemSaveFileButton.setText("Save to File");
    inItemSaveFileButton.setBounds(new Rectangle(100, 70, 100, 25));
    inItemSavefileNamePane.setText("enter filename here");
    inItemSavefileNamePane.setBounds(new Rectangle(210, 70, 170, 25));

    inItemScrollPane.setBounds(new Rectangle(5, 100, 380, 260));

    inItemAddButton.setText("Add");
    inItemAddButton.setBounds(new Rectangle(10, 390, 70, 25));
    inItemDeleteButton.setMargin(new Insets(0, 0, 0, 0));
    inItemDeleteButton.setText("Delete selected");
    inItemDeleteButton.setBounds(new Rectangle(10, 420, 120, 25));
    inItemAddItemField.setBounds(new Rectangle(90, 390, 170, 25));
    inItemAddClassField.setBounds(new Rectangle(265, 390, 90, 25));
    inItemItemLabel.setBorder(titledBorder1);
    inItemItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
    inItemItemLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    inItemItemLabel.setText("Item");
    inItemItemLabel.setBounds(new Rectangle(90, 370, 170, 20));
    inItemClassLabel.setBorder(titledBorder2);
    inItemClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
    inItemClassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    inItemClassLabel.setText("Class");
    inItemClassLabel.setBounds(new Rectangle(265, 370, 90, 20));


    inItemClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       inItemClearButton_actionPerformed(e);
      }
    });
    inItemsAddFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemsAddFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemsSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemAddButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

       inItemBackLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    inItemBackLabel.setText("Background Knowledge Items");
    inItemBackLabel.setBounds(new Rectangle(400, 10, 230, 20));
    inItemsBackClearButton.setBackground(Color.red);
    inItemsBackClearButton.setText("Clear");
    inItemsBackClearButton.setBounds(new Rectangle(400, 40, 80, 55));
    inItemsBackLoadButton.setMargin(new Insets(0, 0, 0, 0));
    inItemsBackLoadButton.setText("Add from File");
    inItemsBackLoadButton.setBounds(new Rectangle(500, 40, 100, 25));
    inItemsBackSaveFileButtonm.setText("Save to File");
    inItemsBackSaveFileButtonm.setBounds(new Rectangle(500, 70, 100, 25));
    inItemsBackLoadField.setText("Background knowledge file");
    inItemsBackLoadField.setBounds(new Rectangle(610, 40, 170, 25));
    inItemsBackSaveField.setText("enter filename here");
    inItemsBackSaveField.setBounds(new Rectangle(610, 70, 170, 25));
    inItemBackScrollPane.setBounds(new Rectangle(400, 100, 380, 260));
    inItemBackAddButton.setText("Add");
    inItemBackAddButton.setBounds(new Rectangle(400, 390, 70, 25));
    inItemBackDeleteButton.setText("Delete selected");
    inItemBackDeleteButton.setBounds(new Rectangle(400, 420, 120, 25));
    inItemBackItemLabel.setBorder(titledBorder1);
    inItemBackItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
    inItemBackItemLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    inItemBackItemLabel.setText("Item");
    inItemBackItemLabel.setBounds(new Rectangle(475, 370, 170, 20));
    inItemBackClassLabel.setBorder(titledBorder1);
    inItemBackClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
    inItemBackClassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    inItemBackClassLabel.setText("Class");
    inItemBackClassLabel.setBounds(new Rectangle(650, 370, 90, 20));
    inItemBackAddItemField.setBounds(new Rectangle(475, 390, 170, 25));
    inItemBackAddClassField.setBounds(new Rectangle(650, 390, 90, 25));

    inItemsBackClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       inItemBackClearButton_actionPerformed(e);
      }
    });
    inItemsBackLoadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemsBackAddFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemsBackSaveFileButtonm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemsBackSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemBackAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemBackAddButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    inItemBackDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        inItemBackDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });


    // ParametersPanel
    parametersPanel.setLayout(null);
    parametersPanel.setBounds(new Rectangle(2, 28, 785, 509));
    parametersPanel.setBounds(new Rectangle(2, 28, 785, 509));

    paraLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    paraLabel.setText("Parameters and Settings");
    paraLabel.setBounds(new Rectangle(5,10, 200, 20));

    paraDefaultButton.setBackground(Color.green);
    paraDefaultButton.setText("Default Values");
    paraDefaultButton.setBounds(new Rectangle(5, 40, 120,25));
    paraDefaultButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        paraDefaultButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

    paraSearchLabel.setText("max. Nr of Search Sentences");
    paraSearchLabel.setBounds(new Rectangle(10, 70, 180, 20));


    paraVerLabel.setText("max. Nr of Verification Sentences");
    paraVerLabel.setBounds(new Rectangle(10, 110, 180, 20));

    paraThreshLabel.setText("Threshold Item acceptance");
    paraThreshLabel.setBounds(new Rectangle(10, 150, 180, 20));
    paraThreshPane.setText("0.1");
    paraThreshPane.setBounds(new Rectangle(200, 150, 60, 25));

    paraCountLabel.setText("minimum Count for Acceptance");
    paraCountLabel.setBounds(new Rectangle(10, 190, 180, 25));
    paraMinCountPane.setText("2");
    paraMinCountPane.setBounds(new Rectangle(200, 190, 60, 25));

    paraThreshRuleLabel.setText("Threshold Rule Acceptance");
    paraThreshRuleLabel.setBounds(new Rectangle(300, 150, 180, 20));
    paraCountRuleLabel.setText("min. Count Rule Acceptance");
    paraCountRuleLabel.setBounds(new Rectangle(300, 190, 180, 20));
    paraThreshRulePane.setText("0.1");
    paraThreshRulePane.setBounds(new Rectangle(500, 150, 60, 25));
    paraMinCountRulePane.setText("10");
    paraMinCountRulePane.setBounds(new Rectangle(500, 190, 60, 25));
    paraDBnameLabel.setText("Database Name");
    paraDBnameLabel.setBounds(new Rectangle(10, 350, 180, 20));
    paraUserLabel.setText("User");
    paraUserLabel.setBounds(new Rectangle(10, 390, 180, 20));
    paraPasswdLabel.setText("Password");
    paraPasswdLabel.setBounds(new Rectangle(10, 430, 180, 20));
    paraDBnamePane.setText("jdbc:mysql://lipsia/wortschatz");
    paraDBnamePane.setBounds(new Rectangle(200, 350, 250, 25));
    paraUserPane.setText("biemann");
    paraUserPane.setBounds(new Rectangle(200, 390, 160, 25));
    paraPasswdField.setText("");
    paraPasswdField.setBounds(new Rectangle(200, 430, 160, 25));

    paraSearchNrField.setText("100");
    paraSearchNrField.setBounds(new Rectangle(200, 70, 60, 25));
    paraVerNrField.setText("30");
    paraVerNrField.setBounds(new Rectangle(200, 110, 60, 25));

    // TagsPanel
    tagSystemPanel.setLayout(null);
    tagLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    tagLabel.setText("Tag System Settings");
    tagLabel.setBounds(new Rectangle(5, 10, 150, 20));
    tagEncodeLabel.setText("Tags Encoding");
    tagEncodeLabel.setBounds(new Rectangle(10, 50, 120, 20));
    tagRegexpLabel.setText("Regexp Tagging");
    tagRegexpLabel.setBounds(new Rectangle(400, 50, 150, 20));
    tntCheckBox.setText("Use TNT-Tagged Corpus");
    tntCheckBox.setBounds(new Rectangle(200, 20, 250, 25));
    tagTntLabel.setText("TNT Tag Mapping");
    tagTntLabel.setBounds(new Rectangle(200, 50, 150, 20));
    tagTntTable.setBounds(new Rectangle(200, 150, 150, 300));
    tagLoadCodeButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagLoadCodeButton.setMargin(new Insets(0, 0, 0, 0));
    tagLoadCodeButton.setText("Load");
    tagLoadCodeButton.setBounds(new Rectangle(10, 80, 50, 25));
    tagCodeSaveButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagCodeSaveButton.setMargin(new Insets(0, 0, 0, 0));
    tagCodeSaveButton.setText("Save");
    tagCodeSaveButton.setBounds(new Rectangle(10, 110, 50, 25));
    tagCodeLoadPane.setText("filename here");
    tagCodeLoadPane.setFont(new java.awt.Font("Serif", 0, 10));
    tagCodeLoadPane.setBounds(new Rectangle(80, 80, 70, 25));
    tagCodeSavePane.setText("filename");
    tagCodeSavePane.setFont(new java.awt.Font("Serif", 0, 10));
    tagCodeSavePane.setBounds(new Rectangle(80, 110, 70, 25));

    tagEncodeScrollPane.setBounds(new Rectangle(10, 140, 140, 250));
    tagEncodeAddButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagEncodeAddButton.setMargin(new Insets(0, 0, 0, 0));
    tagEncodeAddButton.setText("Add");
    tagEncodeAddButton.setBounds(new Rectangle(10, 430, 40, 25));
    tagEncodeDeleteButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagEncodeDeleteButton.setMargin(new Insets(0, 0, 0, 0));
    tagEncodeDeleteButton.setText("Delete selected");
    tagEncodeDeleteButton.setBounds(new Rectangle(10, 460, 70, 25));
    tagEncodeAutoButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagEncodeAutoButton.setMargin(new Insets(0, 0, 0, 0));
    tagEncodeAutoButton.setBackground(Color.green);
    tagEncodeAutoButton.setText("Auto Fill");
    tagEncodeAutoButton.setBounds(new Rectangle(80, 460, 70, 25));

    tagEncodeAddClassField.setBounds(new Rectangle(55, 430, 40, 25));
    tagEncodeAddCodeField.setBounds(new Rectangle(100, 430, 50, 25));
    tagEncodeClassLabel.setFont(new java.awt.Font("Dialog", 0, 10));
    tagEncodeClassLabel.setBorder(titledBorder1);
    tagEncodeClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
    tagEncodeClassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    tagEncodeClassLabel.setText("Class");
    tagEncodeClassLabel.setBounds(new Rectangle(55, 400, 40, 20));
    tagEncodeCodeLable.setFont(new java.awt.Font("Dialog", 0, 10));
    tagEncodeCodeLable.setBorder(titledBorder1);
    tagEncodeCodeLable.setHorizontalAlignment(SwingConstants.CENTER);
    tagEncodeCodeLable.setHorizontalTextPosition(SwingConstants.CENTER);
    tagEncodeCodeLable.setText("Code");
    tagEncodeCodeLable.setBounds(new Rectangle(100, 400, 50, 20));

    tagEncodeAutoButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      try {
        tagEncodeAutoButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}

      }
    });

    tagEncodeClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       tagEncodeClearButton_actionPerformed(e);
      }
    });
    tagEncodeAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagEncodeAddButton_actionPerformed(e);
        } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagCodeSaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagEncodesSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagLoadCodeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagEncodeLoadButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagEncodeDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagEncodeDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });




    tagTntLoadButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagTntLoadButton.setMargin(new Insets(0, 0, 0, 0));
    tagTntLoadButton.setText("Load");
    tagTntLoadButton.setBounds(new Rectangle(200, 80, 50, 25));
    tagTntSaveButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagTntSaveButton.setMargin(new Insets(0, 0, 0, 0));
    tagTntSaveButton.setText("Save");
    tagTntSaveButton.setBounds(new Rectangle(200, 110, 50, 25));
    tagTntLoadPane.setText("filename");
    tagTntLoadPane.setFont(new java.awt.Font("Serif", 0, 10));
    tagTntLoadPane.setBounds(new Rectangle(270, 80, 70, 25));
    tagTntSavePane.setText("filename");
    tagTntSavePane.setFont(new java.awt.Font("Serif", 0, 10));
    tagTntSavePane.setBounds(new Rectangle(270, 110, 70, 25));

    tagRegexpLoadButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagRegexpLoadButton.setMargin(new Insets(0, 0, 0, 0));
    tagRegexpLoadButton.setText("Load");
    tagRegexpLoadButton.setBounds(new Rectangle(460, 80, 50, 25));
    tagRegexpLoadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          tagRegexpLoadButton_actionPerformed(e);
        } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagRegexpSaveButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagRegexpSaveButton.setMargin(new Insets(0, 0, 0, 0));
    tagRegexpSaveButton.setText("Save");
    tagRegexpSaveButton.setBounds(new Rectangle(600, 80, 50, 25));
    tagRegexpSaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagRegexpSaveButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagRegexpLoadPane.setText("filename here");
    tagRegexpLoadPane.setFont(new java.awt.Font("Serif", 0, 10));
    tagRegexpLoadPane.setBounds(new Rectangle(515, 80, 70, 25));
    tagRegexpSavePane.setText("filename");
    tagRegexpSavePane.setFont(new java.awt.Font("Serif", 0, 10));
    tagRegexpSavePane.setBounds(new Rectangle(655, 80, 70, 25));
    tagRegexpScrollPane.setBounds(new Rectangle(400, 120, 330, 270));

    tagRegexpAddButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagRegexpAddButton.setMargin(new Insets(0, 0, 0, 0));
    tagRegexpAddButton.setText("Add");
    tagRegexpAddButton.setBounds(new Rectangle(400, 430, 50, 25));
    tagRegexpAddButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagRegexpAddButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    tagRegexpClearButton.setBackground(Color.red);
    tagRegexpClearButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagRegexpClearButton.setMargin(new Insets(0, 0, 0, 0));
    tagRegexpClearButton.setText("Clear");
    tagRegexpClearButton.setBounds(new Rectangle(400, 80, 50, 25));
    tagRegexpClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       tagRegexpClearButton_actionPerformed(e);
      }
    });
    tagRegexpAddClassField.setBounds(new Rectangle(635, 430, 80, 25));
    tagRegexpAddRegexpField.setBounds(new Rectangle(460, 430, 170, 25));
    tagRegexpAddRegexpLabel.setBorder(titledBorder3);
    tagRegexpAddRegexpLabel.setHorizontalAlignment(SwingConstants.CENTER);
    tagRegexpAddRegexpLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    tagRegexpAddRegexpLabel.setText("Regular Expression");
    tagRegexpAddRegexpLabel.setBounds(new Rectangle(460, 400, 170, 20));
    tagRegexpAddClassLabel.setBorder(titledBorder4);
    tagRegexpAddClassLabel.setText("Class");
    tagRegexpAddClassLabel.setBounds(new Rectangle(635, 400, 80, 20));
    tagRegexpDeleteButton.setFont(new java.awt.Font("Dialog", 0, 10));
    tagRegexpDeleteButton.setMargin(new Insets(0, 0, 0, 0));
    tagRegexpDeleteButton.setText("Delete selected");
    tagRegexpDeleteButton.setBounds(new Rectangle(400, 460, 120, 25));
    tagRegexpDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        tagRegexpDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });


    //OurputItemsPanel
    outputItemsPanel.setLayout(null);
    outItemsUnusedlLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    outItemsUnusedlLabel.setText("Unused Items");
    outItemsUnusedlLabel.setBounds(new Rectangle(5, 10, 200, 20));
    outItemsUnusedScrollPane.setBounds(new Rectangle(10, 50, 490, 180));
    outItemsAllItemsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    outItemsAllItemsLabel.setText("All Items");
    outItemsAllItemsLabel.setBounds(new Rectangle(5, 250, 200, 20));
    outItemsAllScrollPane.setBounds(new Rectangle(10, 280, 490, 180));
    outItemsStartButton.setBackground(Color.green);
    outItemsStartButton.setText("Start");
    outItemsStartButton.setBounds(new Rectangle(540, 60, 100, 100));
    outItemsStartButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        pendelStart_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });


    outItemsStopButton.setBackground(Color.red);
    outItemsStopButton.setText("Stop");
    outItemsStopButton.setBounds(new Rectangle(540, 340, 100, 100));
    outItemsStopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pendelStop_actionPerformed(e);
      }
    });

    outItemsUnusedTable.setBounds(new Rectangle(0, 0, 480, 130));
    outItemsPauseButton.setBackground(Color.yellow);
    outItemsPauseButton.setText("Resume");
    outItemsPauseButton.setVisible(false);
    outItemsPauseButton.setBounds(new Rectangle(540, 200, 100, 100));
    outItemsPauseButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      try {
         pendelPause_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}



      }
    });

    outItemsDeleteUnusedButton.setText("Delete Selected");
    outItemsDeleteUnusedButton.setBounds(new Rectangle(370, 20, 130, 25));
    outItemsDeleteUnusedButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      try {
         outItemsDeleteAllButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}



      }
    });

    outItemsDeleteAllButton.setText("Delete Selected");
    outItemsDeleteAllButton.setBounds(new Rectangle(370, 250, 130, 25));
    outItemsDeleteAllButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      try {
         outItemsDeleteAllButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}



      }
    });

    // File Panel
    filePanel.setLayout(null);
    fileOutPanel.setBorder(BorderFactory.createEtchedBorder());
    fileOutPanel.setBounds(new Rectangle(5, 5, 780, 245));
    fileOutPanel.setLayout(null);
    fileOutLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    fileOutLabel.setText("Output Files");
    fileOutLabel.setBounds(new Rectangle(5, 10, 200, 20));
    fileOutNewItemCheckbox.setText("New Items");
    fileOutNewItemCheckbox.setBounds(new Rectangle(10, 50, 90, 25));
    fileOutNewItemsPane.setText("items.txt");
    fileOutNewItemsPane.setBounds(new Rectangle(110, 50, 160, 25));
    fileOutMaybeItemCheckbox.setText("Maybe Items");
    fileOutMaybeItemCheckbox.setBounds(new Rectangle(10, 100, 90, 25));
    fileOutMaybeItemsPane.setText("maybes.txt");
    fileOutMaybeItemsPane.setBounds(new Rectangle(110, 100, 160, 25));
    fileOutRuleContextCheckbox.setText("Rule Contexts");
    fileOutRuleContextCheckbox.setBounds(new Rectangle(300, 50, 120, 25));
    fileOutRuleContextPane.setText("contexts.txt");
    fileOutRuleContextPane.setBounds(new Rectangle(480, 50, 160, 25));
    fileOutExtrPatsCheckbox.setText("Extraction Patterns Results");
    fileOutExtrPatsCheckbox.setBounds(new Rectangle(300, 100, 170, 25));
    fileOutExtrPatsPane.setText("entities.txt");
    fileOutExtrPatsPane.setBounds(new Rectangle(480, 100, 160, 25));
    fileOutLogCheckbox.setText("create log file");
    fileOutLogCheckbox.setBounds(new Rectangle(200, 200, 100, 25));
    fileOutLogPane.setText("log1.txt");
    fileOutLogPane.setBounds(new Rectangle(300, 200, 120, 25));
    fileConfPanel.setBounds(new Rectangle(5, 260 , 780, 210));
    fileConfPanel.setLayout(null);
    fileConfLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    fileConfLabel.setText("Configuration Files");
    fileConfLabel.setBounds(new Rectangle(5, 10, 180, 20));
    fileConfLoadButton.setText("Load Configuration from");
    fileConfLoadButton.setBounds(new Rectangle(10, 50, 170, 25));
    fileConfLoadField.setText("standard.conf");
    fileConfLoadField.setBounds(new Rectangle(200, 50, 160, 25));
    fileConfSaveButton.setText("Save Configuration to");
    fileConfSaveButton.setBounds(new Rectangle(10, 100, 170, 25));
    fileConfSaveField.setText("actual.conf");
    fileConfSaveField.setBounds(new Rectangle(200, 100, 160, 25));
    fileConfSaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        fileConfSaveButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
     fileConfLoadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        fileConfLoadButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

    // OutRulesPanel
    outputRulesPanel.setLayout(null);
    outRulesTestLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    outRulesTestLabel.setText("Rules in Test Phase");
    outRulesTestLabel.setBounds(new Rectangle(5, 10, 200, 20));
    outRulesTestPane.setBounds(new Rectangle(10, 40, 700, 180));
    outRulesUseLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    outRulesUseLabel.setText("Rules in Use");
    outRulesUseLabel.setBounds(new Rectangle(5, 250, 200, 20));
    outRulesUsePane.setBounds(new Rectangle(10, 280, 700, 180));




    tagEncodeClearButton.setBackground(Color.red);
    tagEncodeClearButton.setMargin(new Insets(0, 0, 0, 0));
    tagEncodeClearButton.setText("Clear");
    tagEncodeClearButton.setBounds(new Rectangle(101, 48, 47, 26));



    inItemTable.setName("");


    // Hier ADDs



//    this.getContentPane().add(pendelFramePanel, null);
    this.add(pendelFramePanel, null);
    pendelFramePanel.add(PendelTabbedPane, null);
    PendelTabbedPane.add(filePanel, "File Management");
    filePanel.add(fileOutPanel, null);
    fileOutPanel.add(fileOutLabel, null);
    fileOutPanel.add(fileOutNewItemCheckbox, null);
    fileOutPanel.add(fileOutNewItemsPane, null);
    fileOutPanel.add(fileOutMaybeItemCheckbox, null);
    fileOutPanel.add(fileOutMaybeItemsPane, null);
    fileOutPanel.add(fileOutRuleContextCheckbox, null);
    fileOutPanel.add(fileOutExtrPatsCheckbox, null);
    fileOutPanel.add(fileOutRuleContextPane, null);
    fileOutPanel.add(fileOutExtrPatsPane, null);
    fileOutPanel.add(fileOutLogCheckbox, null);
    fileOutPanel.add(fileOutLogPane, null);
    filePanel.add(fileConfPanel, null);
    fileConfPanel.add(fileConfLabel, null);
    fileConfPanel.add(fileConfLoadButton, null);
    fileConfPanel.add(fileConfSaveButton, null);
    fileConfPanel.add(fileConfLoadField, null);
    fileConfPanel.add(fileConfSaveField, null);
    PendelTabbedPane.add(parametersPanel, "Parameters and Settings");
    parametersPanel.add(paraSearchLabel, null);
    parametersPanel.add(paraDefaultButton, null);
    parametersPanel.add(paraVerLabel, null);
    parametersPanel.add(paraThreshLabel, null);
    parametersPanel.add(paraThreshPane, null);
    parametersPanel.add(paraMinCountPane, null);
    parametersPanel.add(paraThreshRuleLabel, null);
    parametersPanel.add(paraCountRuleLabel, null);
    parametersPanel.add(paraThreshRulePane, null);
    parametersPanel.add(paraMinCountRulePane, null);
    parametersPanel.add(paraDBnameLabel, null);
    parametersPanel.add(paraUserLabel, null);
    parametersPanel.add(paraPasswdLabel, null);
    parametersPanel.add(paraDBnamePane, null);
    parametersPanel.add(paraUserPane, null);
    parametersPanel.add(paraPasswdField, null);
    parametersPanel.add(paraCountLabel, null);
    parametersPanel.add(paraLabel, null);
    parametersPanel.add(paraSearchNrField, null);
    parametersPanel.add(paraVerNrField, null);
    PendelTabbedPane.add(inputFeaturesPanel, "Rules and Patterns");
    inputFeaturesPanel.add(classRulesPanel, null);
    classRulesPanel.add(classRulesLabel, null);
    classRulesPanel.add(classRulesClearButton, null);
    classRulesPanel.add(classRulesAddFileButton, null);
    classRulesPanel.add(classRulesFileNamePane, null);
    classRulesPanel.add(classRulesSaveFileNamePane, null);
    classRulesPanel.add(classRulesSaveFileButton, null);
    classRulesPanel.add(classRulesScrollPane, null);
    classRulesPanel.add(classRulesDeleteutton, null);
    classRulesPanel.add(classRulesAddButton, null);
    classRulesPanel.add(classRulesAddGoalField, null);
    classRulesPanel.add(classRulesAddLeftField, null);
    classRulesPanel.add(classRulesLeftLabel, null);
    classRulesPanel.add(classRulesGoalLabel, null);
    classRulesScrollPane.getViewport().add(classRulesTable, null);
    inputFeaturesPanel.add(extrPatternsPanel, null);
    extrPatternsPanel.add(extrPatsLabel, null);
    extrPatternsPanel.add(extrPatsClearButton, null);
    extrPatternsPanel.add(extrPatsAddFileButton, null);
    extrPatternsPanel.add(extrPatsFileNamePane, null);
    extrPatternsPanel.add(extrPatsSaveFileButton, null);
    extrPatternsPanel.add(extrPatsSaveFileNamePane, null);
    extrPatternsPanel.add(extrPatsScrollPane, null);
    extrPatternsPanel.add(extrPatsAddButton, null);
    extrPatternsPanel.add(extrPatsDeleteButton, null);
    extrPatternsPanel.add(extrPatsAddPatField, null);
    extrPatternsPanel.add(extPatPatLabel, null);
    extrPatternsPanel.add(extrPatsAddClassLField, null);
    extrPatternsPanel.add(extrPatsClassLabel, null);
    extrPatsScrollPane.getViewport().add(extrPatsTable, null);
    PendelTabbedPane.add(inputItemsPanel, "Input Items");
    inputItemsPanel.add(inItemsLabel, null);
    inputItemsPanel.add(inItemClearButton, null);
    inputItemsPanel.add(inItemScrollPane, null);
    inputItemsPanel.add(inItemDeleteButton, null);
    inputItemsPanel.add(inItemAddButton, null);
    inputItemsPanel.add(inItemAddItemField, null);
    inputItemsPanel.add(inItemAddClassField, null);
    inputItemsPanel.add(inItemItemLabel, null);
    inputItemsPanel.add(inItemClassLabel, null);
    inputItemsPanel.add(inItemFileNamePane, null);
    inputItemsPanel.add(inItemSavefileNamePane, null);
    inputItemsPanel.add(inItemBackLabel, null);
    inputItemsPanel.add(inItemsAddFileButton, null);
    inputItemsPanel.add(inItemSaveFileButton, null);
    inputItemsPanel.add(inItemsBackLoadField, null);
    inputItemsPanel.add(inItemsBackSaveField, null);
    inputItemsPanel.add(inItemBackScrollPane, null);
    inItemBackScrollPane.getViewport().add(inItemBackTable, null);
    inputItemsPanel.add(inItemsBackClearButton, null);
    inputItemsPanel.add(inItemsBackLoadButton, null);
    inputItemsPanel.add(inItemsBackSaveFileButtonm, null);
    inputItemsPanel.add(inItemBackAddButton, null);
    inputItemsPanel.add(inItemBackDeleteButton, null);
    inputItemsPanel.add(inItemBackItemLabel, null);
    inputItemsPanel.add(inItemBackClassLabel, null);
    inputItemsPanel.add(inItemBackAddItemField, null);
    inputItemsPanel.add(inItemBackAddClassField, null);
    PendelTabbedPane.add(tagSystemPanel, "Tag System");
    tagSystemPanel.add(tagLabel, null);
    tagSystemPanel.add(tagEncodeLabel, null);
    tagSystemPanel.add(tagTntLabel, null);
    tagSystemPanel.add(tagRegexpLabel, null);
    tagSystemPanel.add(tagLoadCodeButton, null);
    tagSystemPanel.add(tntCheckBox, null);
    tagSystemPanel.add(tagCodeSaveButton, null);
    tagSystemPanel.add(tagCodeLoadPane, null);
    tagSystemPanel.add(tagCodeSavePane, null);
    tagSystemPanel.add(tagTntLoadButton, null);
    tagSystemPanel.add(tagTntSaveButton, null);
    tagSystemPanel.add(tagTntLoadPane, null);
    tagSystemPanel.add(tagTntTable, null);
    tagSystemPanel.add(tagTntSavePane, null);
    tagSystemPanel.add(tagRegexpSavePane, null);
    tagSystemPanel.add(tagRegexpScrollPane, null);
    tagSystemPanel.add(tagRegexpSaveButton, null);
    tagSystemPanel.add(tagRegexpLoadPane, null);
    tagSystemPanel.add(tagRegexpLoadButton, null);
    tagSystemPanel.add(tagRegexpClearButton, null);
    tagSystemPanel.add(tagRegexpAddRegexpField, null);
    tagSystemPanel.add(tagRegexpAddClassField, null);
    tagSystemPanel.add(tagRegexpAddRegexpLabel, null);
    tagSystemPanel.add(tagRegexpAddClassLabel, null);
    tagSystemPanel.add(tagRegexpAddButton, null);
    tagSystemPanel.add(tagRegexpDeleteButton, null);
    tagSystemPanel.add(tagEncodeScrollPane, null);
    tagSystemPanel.add(tagEncodeAddButton, null);
    tagSystemPanel.add(tagEncodeDeleteButton, null);
    tagSystemPanel.add(tagEncodeAutoButton, null);
    tagSystemPanel.add(tagEncodeAddCodeField, null);
    tagSystemPanel.add(tagEncodeAddClassField, null);
    tagSystemPanel.add(tagEncodeClassLabel, null);
    tagSystemPanel.add(tagEncodeCodeLable, null);
    tagSystemPanel.add(tagEncodeClearButton, null);
    tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
    tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
    PendelTabbedPane.add(outputRulesPanel, "Output Rules");
    outputRulesPanel.add(outRulesTestLabel, null);
    outputRulesPanel.add(outRulesTestPane, null);
    outputRulesPanel.add(outRulesUseLabel, null);
    outputRulesPanel.add(outRulesUsePane, null);
    PendelTabbedPane.add(outputItemsPanel, "Output Items");
    outputItemsPanel.add(outItemsUnusedlLabel, null);
    outputItemsPanel.add(outItemsUnusedScrollPane, null);
    outputItemsPanel.add(outItemsAllItemsLabel, null);
    outputItemsPanel.add(outItemsAllScrollPane, null);
    outputItemsPanel.add(outItemsStartButton, null);
    outputItemsPanel.add(outItemsStopButton, null);
    outputItemsPanel.add(outItemsPauseButton, null);
    outputItemsPanel.add(outItemsDeleteUnusedButton, null);
    outputItemsPanel.add(outItemsDeleteAllButton, null);
    outItemsAllScrollPane.getViewport().add(outItemsAllTable, null);
    outItemsUnusedScrollPane.getViewport().add(outItemsUnusedTable, null);
    outRulesUsePane.getViewport().add(outRulesUseTable, null);
    outRulesTestPane.getViewport().add(outRulesTestTable, null);
    inItemScrollPane.getViewport().add(inItemTable, null);



    // Data Init



  }

  //Überschreiben, damit das Programm bei Herunterfahren des Systems beendet werden kann
//  protected void processWindowEvent(WindowEvent e) {
//    super.processWindowEvent(e);
//    if(e.getID() == WindowEvent.WINDOW_CLOSING) {
//      System.exit(0);
//    }
//  }

  private static Vector vecUnite(Vector een, Vector twee) {
        Vector stringRules = new Vector(); // Für Doublettencheck
        Pattern actPat;

        Vector terug=new Vector();
        for(Enumeration e=een.elements();e.hasMoreElements();) {
            actPat=(Pattern)e.nextElement();
            if (!(stringRules.contains(actPat.toString()))) {
              terug.addElement(actPat);
              stringRules.addElement(actPat.toString());
            }
        } // rof Enum eenM

        for(Enumeration e=twee.elements();e.hasMoreElements();) {
            actPat=(Pattern)e.nextElement();
            if (!(stringRules.contains(actPat.toString()))) {
              terug.addElement(actPat);
              stringRules.addElement(actPat.toString());
            }
        } // rof Enum twee


        return terug;
    } // end vecUnite


  private static JTable rules2jTable(Vector patterns) {
    Pattern actRule;
    String[] actPattern;
    String actGoalClass;
    int actGoalPos;
    int hits;
    int misses;
    double rating;

    String columns[]={"Pattern","Goal Class","Hits","Misses","Rating"};
    String rows[][]=new String[patterns.size()][5];
    int i=0;
    for (Enumeration e=patterns.elements();e.hasMoreElements();) {
      actRule=(Pattern)e.nextElement();
      actPattern=actRule.pattern;
      String patString="";
      for (int j=0;j<actRule.length;j++) { // Zielpos markieren
        if (j==actRule.goalPos)
          //&&(actPattern[j].substring(actPattern[j].length()-1,1)!="*"))
          {patString+=actPattern[j]+"* ";}
        else {patString+=actPattern[j]+" ";  }
      } // rof
      patString=patString.substring(0,patString.length()-1);

      rows[i][0]=patString;
      rows[i][1]=actRule.goalClass;
      rows[i][2]=new String().valueOf(actRule.hits);
      rows[i][3]=new String().valueOf(actRule.misses);
      rows[i][4]=new String().valueOf(actRule.rating);

      i++;
    } // rof Enum e



    return new JTable(rows,columns);

  }

   private static JTable pats2jTable(Vector patterns) {
    Pattern actRule;
    String[] actPattern;
    String actGoalClass;
    int actGoalPos;
    int hits;
    int misses;
    double rating;

    String columns[]={"Pattern","Hits"};
    String rows[][]=new String[patterns.size()][5];
    int i=0;
    for (Enumeration e=patterns.elements();e.hasMoreElements();) {
      actRule=(Pattern)e.nextElement();
      actPattern=actRule.pattern;
      String patString="";
      for (int j=0;j<actRule.length;j++) { // Zielpos markieren
        if (j==actRule.goalPos)
          //&&(actPattern[j].substring(actPattern[j].length()-1,1)!="*"))
          {patString+=actPattern[j]+"* ";}
        else {patString+=actPattern[j]+" ";  }
      } // rof
      patString=patString.substring(0,patString.length()-1);

      rows[i][0]=patString;
      rows[i][1]=actRule.goalClass;
      rows[i][2]=new String().valueOf(actRule.hits);
      rows[i][3]=new String().valueOf(actRule.misses);
      rows[i][4]=new String().valueOf(actRule.rating);

      i++;
    } // rof Enum e



    return new JTable(rows,columns);

  }  // end pats2jTable



  private static JTable nameTable2jTable(NameTable source) {


    String actItem;
    String actClass;

    String columns[]={"Item","Class" };
    String rows[][]= new String[source.size()][2];

    int i=0;
    for (Enumeration e=source.keys();e.hasMoreElements();) {
      actItem=(String)e.nextElement();
      actClass=(String)source.get(actItem);
      rows[i][0]=actItem;
      rows[i][1]=actClass;
      i++;
    } // rof enum e

    JTable returnTable= new JTable(rows,columns);

    return returnTable;
  }

  // extrPats Buttons
  void extrPatsClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear!");
      extrPats=new Vector();
      extrPatsTable=pats2jTable(extrPats);
      extrPatsScrollPane.getViewport().add(extrPatsTable, null);
      this.setVisible(true);
      this.repaint();
  }
  void extrPatsAddFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=extrPatsFileNamePane.getText();
       Matcher loader=new Matcher();
       Vector newExtrPats=loader.loadPatterns(filename);
       extrPats=vecUnite(extrPats,newExtrPats);
       extrPatsTable=pats2jTable(extrPats);
       extrPatsScrollPane.getViewport().add(extrPatsTable, null);
       this.setVisible(true);
       this.repaint();
  }
   void extrPatsAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newPattern=extrPatsAddPatField.getText();
       String newGoal=extrPatsAddClassLField.getText();

       Pattern newPat=new Pattern();

       StringTokenizer newPatPats=new StringTokenizer(newPattern," ");
       int i=0;
       String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
       while (newPatPats.hasMoreTokens()) {
           String dummy=newPatPats.nextToken();
            tempPats[i]=dummy;
           i++;
       } //ehliw
       newPat.length=i;
       newPat.goalPos=-1;
       newPat.goalClass=newGoal;
       newPat.pattern=new String[i] ;
       for(int j=0;j<newPat.length;j++) {newPat.pattern[j]=tempPats[j];}
       newPat.hits=0;
       newPat.misses=0;
       newPat.rating=0.0;
       newPat.partialInit();
       Vector newExtrPats=new Vector();
       newExtrPats.addElement(newPat);
       extrPats=vecUnite(extrPats,newExtrPats);
       extrPatsTable=pats2jTable(extrPats);
       extrPatsScrollPane.getViewport().add(extrPatsTable, null);
       this.setVisible(true);
       this.repaint();

  }

   void extrPatsDeleteButton_actionPerformed(ActionEvent e) throws IOException {

         String delItem="";
         int rowCount=extrPatsTable.getSelectedRowCount();
         int[] selectedRows=extrPatsTable.getSelectedRows();
         for (int ij=0;ij<rowCount;ij++) {
               delItem=(String)extrPatsTable.getValueAt(selectedRows[ij],0);
               System.out.println("contains: "+delItem);
               StringTokenizer delPats=new StringTokenizer(delItem," ");
               int gC=-1, i=0;
               String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
               while (delPats.hasMoreTokens()) {
                     String dummy=delPats.nextToken();
                     tempPats[i]=dummy;
                     i++;
               } //ehliw
               Pattern delPat=new Pattern();
               for(Enumeration f=extrPats.elements();f.hasMoreElements();) {
                Pattern actPat=(Pattern)f.nextElement();
                int flag=0;
                if (actPat.length==i) {
                 for(int j=0;j<actPat.length;j++) {
                 String dummy1=actPat.pattern[j];
                 String dummy2=tempPats[j];

                  if (dummy1.endsWith("*")) {dummy1=dummy1.substring(0,dummy1.length()-1);}
                  if (dummy2.endsWith("*")) {dummy2=dummy2.substring(0,dummy2.length()-1);}

                  if (!(dummy1.equals(dummy2))) {flag=1;}
                 }
                 if (flag==0) {delPat=actPat;}
                }
           }

           extrPats.removeElement(delPat);
         } //rof

       extrPatsTable=pats2jTable(extrPats);
       extrPatsScrollPane.getViewport().add(extrPatsTable, null);
       this.setVisible(true);
       this.repaint();

    }

  void extrPatsSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=extrPatsSaveFileNamePane.getText();
       Matcher writer= new Matcher();
       writer.patterns=extrPats;
       writer.saveFile(filename);
      System.out.println("File saved: "+filename);
  }

  // classRule Buttons
  void classRulesClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear!");
      classRules=new Vector();
      classRulesTable=rules2jTable(classRules);
      classRulesScrollPane.getViewport().add(classRulesTable, null);
      this.setVisible(true);
      this.repaint();
  }
  void classRulesAddFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=classRulesFileNamePane.getText();
       Matcher loader=new Matcher();
       Vector newClassRules=loader.loadPatterns(filename);
       classRules=vecUnite(classRules,newClassRules);
       classRulesTable=rules2jTable(classRules);
       classRulesScrollPane.getViewport().add(classRulesTable, null);
       this.setVisible(true);
       this.repaint();
  }
   void classRulesAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newPattern=classRulesAddLeftField.getText();
       String newGoal=classRulesAddGoalField.getText();

       Pattern newPat=new Pattern();

       StringTokenizer newPatPats=new StringTokenizer(newPattern," ");
       int gC=0, i=0;
       String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
       while (newPatPats.hasMoreTokens()) {
           String dummy=newPatPats.nextToken();
           if (dummy.endsWith("*")) {gC=i;dummy=dummy.substring(0,dummy.length()-1);}
           tempPats[i]=dummy;
           i++;
       } //ehliw
       newPat.length=i;
       newPat.goalPos=gC;
       newPat.goalClass=newGoal;
       newPat.pattern=new String[i] ;
       for(int j=0;j<newPat.length;j++) {newPat.pattern[j]=tempPats[j];}
       newPat.hits=0;
       newPat.misses=0;
       newPat.rating=0.0;
       Vector newClassRules=new Vector();
       newPat.partialInit();
       newClassRules.addElement(newPat);
       classRules=vecUnite(classRules,newClassRules);
       classRulesTable=rules2jTable(classRules);
       classRulesScrollPane.getViewport().add(classRulesTable, null);
       this.setVisible(true);
       this.repaint();

  }

   void classRulesDeleteButton_actionPerformed(ActionEvent e) throws IOException {
         String delItem="";
         int rowCount=classRulesTable.getSelectedRowCount();
         int[] selectedRows=classRulesTable.getSelectedRows();
         for (int ij=0;ij<rowCount;ij++) {
               delItem=(String)classRulesTable.getValueAt(selectedRows[ij],0);
               System.out.println("contains: "+delItem);
               StringTokenizer delPats=new StringTokenizer(delItem," ");
               int gC=-1, i=0;
               String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
               while (delPats.hasMoreTokens()) {
                    String dummy=delPats.nextToken();
                    if (dummy.endsWith("*")) {gC=i;dummy=dummy.substring(0,dummy.length()-1);}
                    tempPats[i]=dummy;
                    i++;
               } //ehliw
               Pattern delPat=new Pattern();
               for(Enumeration f=classRules.elements();f.hasMoreElements();) {
                     Pattern actPat=(Pattern)f.nextElement();
                     int flag=0;
                     if (actPat.length==i) {
                      for(int j=0;j<actPat.length;j++) {
                       if (!(tempPats[j].equals(actPat.pattern[j]))) {flag=1;}
                      }
                     if ((flag==0)&&(gC==actPat.goalPos)) {delPat=actPat;}
                     }
               }
               classRules.removeElement(delPat);
         } //rof
       classRulesTable=rules2jTable(classRules);
       classRulesScrollPane.getViewport().add(classRulesTable, null);
       this.setVisible(true);
       this.repaint();
    }

  void classRulesSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=classRulesSaveFileNamePane.getText();
       Matcher writer= new Matcher();
       writer.patterns=classRules;
       writer.saveFile(filename);
      System.out.println("File saved: "+filename);
  }



  // InItems Buttons

  void inItemClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear!");
       inItemsNameTable=new NameTable();
       inItemTable=nameTable2jTable(inItemsNameTable);
       inItemScrollPane.getViewport().add(inItemTable, null);
       this.setVisible(true);
       this.repaint();
  }
  void inItemsAddFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=inItemFileNamePane.getText();
       NameTable loader=new NameTable();
       loader.loadFile(filename);
       inItemsNameTable.insert(loader);
       inItemTable=nameTable2jTable(inItemsNameTable);
       inItemScrollPane.getViewport().add(inItemTable, null);
       this.setVisible(true);
  }
   void inItemAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newItem=inItemAddItemField.getText();
       String newClass=inItemAddClassField.getText();
       NameTable adder=new NameTable();
       adder.put(newItem, newClass);
       inItemsNameTable.insert(adder);
       inItemTable=nameTable2jTable(inItemsNameTable);
       inItemScrollPane.getViewport().add(inItemTable, null);
       this.setVisible(true);
  }

   void inItemDeleteButton_actionPerformed(ActionEvent e) throws IOException {
         String delItem="";
         int rowCount=inItemTable.getSelectedRowCount();
         int[] selectedRows=inItemTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)inItemTable.getValueAt(selectedRows[i],0);
               System.out.println("contains: "+delItem);
               inItemsNameTable.remove(delItem);
         } //rof

       inItemTable=nameTable2jTable(inItemsNameTable);
       inItemScrollPane.getViewport().add(inItemTable, null);
       this.setVisible(true);
       this.repaint();
  }


  void inItemsSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=inItemSavefileNamePane.getText();
       inItemsNameTable.writeFile(filename,false);
       System.out.println("File saved: "+filename);
  }


   // InItemsBack Buttons

  void inItemBackClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear!");
       inItemsBackNameTable=new NameTable();
       inItemBackTable=nameTable2jTable(inItemsBackNameTable);
       inItemBackScrollPane.getViewport().add(inItemBackTable, null);
       this.setVisible(true);
       this.repaint();
  }
  void inItemsBackAddFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=inItemsBackLoadField.getText();
       NameTable loader=new NameTable();
       loader.loadFile(filename);
       inItemsBackNameTable.insert(loader);
       inItemBackTable=nameTable2jTable(inItemsBackNameTable);
       inItemBackScrollPane.getViewport().add(inItemBackTable, null);
       this.setVisible(true);
  }
   void inItemBackAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newItem=inItemBackAddItemField.getText();
       String newClass=inItemBackAddClassField.getText();
       NameTable adder=new NameTable();
       adder.put(newItem, newClass);
       inItemsBackNameTable.insert(adder);
       inItemBackTable=nameTable2jTable(inItemsBackNameTable);
       inItemBackScrollPane.getViewport().add(inItemBackTable, null);
       this.setVisible(true);
  }

   void inItemBackDeleteButton_actionPerformed(ActionEvent e) throws IOException {
         String delItem="";
         int rowCount=inItemBackTable.getSelectedRowCount();
         int[] selectedRows=inItemBackTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)inItemBackTable.getValueAt(selectedRows[i],0);
               System.out.println("contains: "+delItem);
               inItemsBackNameTable.remove(delItem);
         } //rof

       inItemBackTable=nameTable2jTable(inItemsBackNameTable);
       inItemBackScrollPane.getViewport().add(inItemBackTable, null);
       this.setVisible(true);
       this.repaint();
  }


  void inItemsBackSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=inItemsBackSaveField.getText();
       inItemsBackNameTable.writeFile(filename,false);
       System.out.println("File saved: "+filename);
  }


  // tagEncode Buttons

   void tagEncodeClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear KlassKeys");
       klassKeysNameTable=new NameTable();
       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       this.setVisible(true);
       this.repaint();
  }

  void tagEncodeLoadButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=tagCodeLoadPane.getText();
       NameTable loader=new NameTable();
       loader.loadFile(filename);
       klassKeysNameTable.insert(loader);
       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       this.setVisible(true);
  }
   void tagEncodeAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newItem=tagEncodeAddClassField.getText();
       String newClass=tagEncodeAddCodeField.getText();
       NameTable adder=new NameTable();
       adder.put(newItem, newClass);
       klassKeysNameTable.insert(adder);
       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       this.setVisible(true);
  }
  void tagEncodeAutoButton_actionPerformed(ActionEvent e) throws IOException {
       NameTable autoTable=new NameTable();

       for (Enumeration en=inItemsNameTable.elements();en.hasMoreElements();) {
           String inElem=(String)en.nextElement();
           autoTable.put(inElem,"temp");
       }
       for (Enumeration en=inItemsBackNameTable.elements();en.hasMoreElements();) {
           String inElem=(String)en.nextElement();
           autoTable.put(inElem,"temp");
       }
       for (Enumeration en=regexpNameTable.elements();en.hasMoreElements();) {
           String inElem=(String)en.nextElement();
           autoTable.put(inElem,"temp");
       }
       for (Enumeration en=classRules.elements();en.hasMoreElements();) {
           Pattern inPattern=(Pattern)en.nextElement();
           String inElem=inPattern.goalClass;
           autoTable.put(inElem,"temp");
           for (int j=0;j<inPattern.length;j++) {
              inElem=inPattern.pattern[j];
              autoTable.put(inElem,"temp");
           }
       }

        for (Enumeration en=extrPats.elements();en.hasMoreElements();) {
           Pattern inPattern=(Pattern)en.nextElement();
           for (int j=0;j<inPattern.length;j++) {
              String inElem=inPattern.pattern[j];
              autoTable.put(inElem,"temp");
           }
       }

       // now put the 2^i
       long code=1;
       for (Enumeration en=autoTable.keys();en.hasMoreElements();) {
           String inElem=(String)en.nextElement();
           String codeString=""+code;
           autoTable.put(inElem,codeString);
           code=code*2;
       }

       System.out.println(autoTable.toString());
       klassKeysNameTable = new NameTable();
       klassKeysNameTable.insert(autoTable);
       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       this.setVisible(true);
       this.repaint();

  }

   void tagEncodeDeleteButton_actionPerformed(ActionEvent e) throws IOException {
       String delItem="";
         int rowCount=tagEncodeTable.getSelectedRowCount();
         int[] selectedRows=tagEncodeTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)tagEncodeTable.getValueAt(selectedRows[i],0);
               System.out.println("deletion of: "+delItem);
               klassKeysNameTable.remove(delItem);
         } //rof


       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       this.setVisible(true);
       this.repaint();
  }


  void tagEncodesSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=tagCodeSavePane.getText();
       klassKeysNameTable.writeFile(filename,false);
       System.out.println("File saved: "+filename);
  }


  //tagRegexpButtons

   void tagRegexpClearButton_actionPerformed(ActionEvent e) {
      System.out.println("Clear Regexps");
       regexpNameTable=new NameTable();
       tagRegexpTable=nameTable2jTable(regexpNameTable);
       tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
       this.setVisible(true);
       this.repaint();
  }

  void tagRegexpLoadButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=tagRegexpLoadPane.getText();
       NameTable loader=new NameTable();
       loader.loadFile(filename);
       regexpNameTable.insert(loader);
       tagRegexpTable=nameTable2jTable(regexpNameTable);
       tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
       this.setVisible(true);
  }
  void tagRegexpSaveButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=tagRegexpSavePane.getText();
       regexpNameTable.writeFile(filename,false);
       System.out.println("Regexp File saved: "+filename);
  }

     void tagRegexpAddButton_actionPerformed(ActionEvent e) throws IOException {
       String newRegexp=tagRegexpAddRegexpField.getText();
       String newClass=tagRegexpAddClassField.getText();
       NameTable adder=new NameTable();
       adder.put( newRegexp, newClass);
       regexpNameTable.insert(adder);
       tagRegexpTable=nameTable2jTable(regexpNameTable);
       tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
       this.setVisible(true);
  }

     void tagRegexpDeleteButton_actionPerformed(ActionEvent e) throws IOException {


       String delItem="";
         int rowCount=tagRegexpTable.getSelectedRowCount();
         int[] selectedRows=tagRegexpTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)tagRegexpTable.getValueAt(selectedRows[i],0);
               System.out.println("deletion of: "+delItem);
               regexpNameTable.remove(delItem);
         } //rof

       tagRegexpTable=nameTable2jTable(regexpNameTable);
       tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
       this.setVisible(true);
       this.repaint();
  }

  // Parameter Actions
     void paraDefaultButton_actionPerformed(ActionEvent e) throws IOException {
       paraMinCountPane.setText("2");
       paraMinCountRulePane.setText("10");
       paraDBnamePane.setText("jdbc:mysql://lipsia/wortschatz");
       paraPasswdField.setText("");
       paraSearchNrField.setText("100");
       paraThreshPane.setText("0.1");
       paraThreshRulePane.setText("0.1");
       paraUserPane.setText("biemann");
       paraVerNrField.setText("30");

       this.setVisible(true);
       this.repaint();

  }





  void fileConfSaveButton_actionPerformed(ActionEvent e) throws IOException {
       Vector saveVector=new Vector();
       // file panel
       String outFileNICheck;
       if (fileOutNewItemCheckbox.isSelected()) {outFileNICheck="true";} else {outFileNICheck="false";}
       String outFileNIString=fileOutNewItemsPane.getText();
       String outFileMICheck;
       if (fileOutMaybeItemCheckbox.isSelected()) {outFileMICheck="true";} else {outFileMICheck="false";}
       String outFileMIString=fileOutMaybeItemsPane.getText();
       String outFileRCCheck;
       if (fileOutRuleContextCheckbox.isSelected()) {outFileRCCheck="true";} else {outFileRCCheck="false";}
       String outFileRCString=fileOutRuleContextPane.getText();
       String outFileEPCheck;
       if (fileOutExtrPatsCheckbox.isSelected()) {outFileEPCheck="true";} else {outFileEPCheck="false";}
       String outFileEPString=fileOutExtrPatsPane.getText();
       String outFileLOCheck;
       if (fileOutLogCheckbox.isSelected()) {outFileLOCheck="true";} else {outFileLOCheck="false";}
       String outFileLOString=fileOutLogPane.getText();
       // para panel
       String paraSNR=paraSearchNrField.getText();
       String paraVNR=paraVerNrField.getText();
       String paraTI=paraThreshPane.getText();
       String paraTR=paraThreshRulePane.getText();
       String paraMI=paraMinCountPane.getText();
       String paraMR=paraMinCountRulePane.getText();
       String paraDB=paraDBnamePane.getText();
       String paraUS=paraUserPane.getText();
       String paraPW=paraPasswdField.getText();
       // rule panel
       Vector ruleClassRules=classRules;
       Vector ruleextrPats=extrPats;
       // inItems panel
       NameTable itemsInItems=inItemsNameTable;
       NameTable itemsInBackItems=inItemsBackNameTable;
       // tag system

       NameTable tagKlass=klassKeysNameTable;
       NameTable tagRegexp=regexpNameTable;
       // outRules
       Vector outRulesUse=Pendel.rules;
       // outItems
       NameTable outItemsUnused=Pendel.actItems;
       NameTable outItemsAll=Pendel.allesWissen;

       saveVector.addElement(outFileNICheck);
       saveVector.addElement(outFileNIString);
       saveVector.addElement(outFileMICheck);
       saveVector.addElement(outFileMIString);
       saveVector.addElement(outFileRCCheck);
       saveVector.addElement(outFileRCString);
       saveVector.addElement(outFileEPCheck);
       saveVector.addElement(outFileEPString);
       saveVector.addElement(outFileLOCheck);
       saveVector.addElement(outFileLOString);

       saveVector.addElement(paraSNR);
       saveVector.addElement(paraVNR);
       saveVector.addElement(paraTI);
       saveVector.addElement(paraTR);
       saveVector.addElement(paraMI);
       saveVector.addElement(paraMR);
       saveVector.addElement(paraDB);
       saveVector.addElement(paraUS);
       saveVector.addElement(paraPW);

       saveVector.addElement(ruleClassRules);
       saveVector.addElement(ruleextrPats);
       saveVector.addElement(itemsInItems);
       saveVector.addElement(itemsInBackItems);
       saveVector.addElement(tagKlass);
       saveVector.addElement(tagRegexp);
       saveVector.addElement(outRulesUse);
       saveVector.addElement(outItemsUnused);
       saveVector.addElement(outItemsAll);

       String filename=fileConfSaveField.getText();
       try {
              ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(filename));
              oos.writeObject(saveVector);
              oos.close();
       } catch (IOException ex) {};

       this.setVisible(true);
       this.repaint();
  }

    void fileConfLoadButton_actionPerformed(ActionEvent e) throws IOException {
       Vector loadVector=new Vector();
       String filename=fileConfLoadField.getText();
       String inString="";

       try{
              ObjectInputStream ois= new ObjectInputStream(new FileInputStream(filename));
              loadVector=(Vector)ois.readObject();
              ois.close();


       } catch (IOException ex) {System.out.println(ex.getMessage());}
         catch (ClassNotFoundException cnfe) { /* readObject() can throw this */ }

       System.out.println(loadVector.toString());

       Enumeration len=loadVector.elements();
       String outFileNICheck=(String)len.nextElement();
       if (outFileNICheck.equals("true")) {fileOutNewItemCheckbox.setSelected(true);}
          else {fileOutNewItemCheckbox.setSelected(false);}
       String outFileNIString=(String)len.nextElement();
       fileOutNewItemsPane.setText(outFileNIString);

       String outFileMICheck=(String)len.nextElement();
       if (outFileMICheck.equals("true")) {fileOutMaybeItemCheckbox.setSelected(true);}
          else {fileOutMaybeItemCheckbox.setSelected(false);}
       String outFileMIString=(String)len.nextElement();
       fileOutMaybeItemsPane.setText(outFileMIString);

       String outFileRCCheck=(String)len.nextElement();
       if (outFileRCCheck.equals("true")) {fileOutRuleContextCheckbox.setSelected(true);}
          else {fileOutRuleContextCheckbox.setSelected(false);}
       String outFileRCString=(String)len.nextElement();
       fileOutRuleContextPane.setText(outFileRCString);

       String outFileEPCheck=(String)len.nextElement();
       if (outFileEPCheck.equals("true")) {fileOutExtrPatsCheckbox.setSelected(true);}
          else {fileOutExtrPatsCheckbox.setSelected(false);}
       String outFileEPString=(String)len.nextElement();
       fileOutExtrPatsPane.setText(outFileEPString);

       String outFileLOCheck=(String)len.nextElement();
       if (outFileLOCheck.equals("true")) {fileOutLogCheckbox.setSelected(true);}
          else {fileOutLogCheckbox.setSelected(false);}
       String outFileLOString=(String)len.nextElement();
       fileOutLogPane.setText(outFileLOString);
       paraSearchNrField.setText((String)len.nextElement());
       paraVerNrField.setText((String)len.nextElement());
       paraThreshPane.setText((String)len.nextElement());
       paraThreshRulePane.setText((String)len.nextElement());
       paraMinCountPane.setText((String)len.nextElement());
       paraMinCountRulePane.setText((String)len.nextElement());
       paraDBnamePane.setText((String)len.nextElement());
       paraUserPane.setText((String)len.nextElement());
       paraPasswdField.setText((String)len.nextElement());

       classRules=(Vector)len.nextElement();
       extrPats=(Vector)len.nextElement();
       inItemsNameTable=(NameTable)len.nextElement();
       inItemsBackNameTable=(NameTable)len.nextElement();
       klassKeysNameTable=(NameTable)len.nextElement();
       regexpNameTable=(NameTable)len.nextElement();
       Pendel.rules=(Vector)len.nextElement();
       Pendel.actItems=(NameTable)len.nextElement();
       Pendel.allesWissen=(NameTable)len.nextElement();



  // einfüllen und repainten
       inItemTable=nameTable2jTable(inItemsNameTable);
       inItemScrollPane.getViewport().add(inItemTable, null);
       inItemBackTable=nameTable2jTable(inItemsBackNameTable);
       inItemBackScrollPane.getViewport().add(inItemBackTable, null);
       tagEncodeTable=nameTable2jTable(klassKeysNameTable);
       tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
       tagRegexpTable=nameTable2jTable(regexpNameTable);
       tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
       classRulesTable=rules2jTable(classRules);
       classRulesScrollPane.getViewport().add(classRulesTable, null);
       extrPatsTable=pats2jTable(extrPats);
       extrPatsScrollPane.getViewport().add(extrPatsTable, null);

       softsupdate();
       this.setVisible(true);
       outItemsPauseButton.setText("Resume");
       outItemsPauseButton.setVisible(true);
       this.repaint();

  }



  public void softsupdate() {
       outItemsAllTable=nameTable2jTable(Pendel.allesWissen);
       outItemsAllScrollPane.getViewport().add(outItemsAllTable, null);
       NameTable joiner=new NameTable();
       joiner.insert(Pendel.actItems);
       joiner.insert(Pendel.neueItems);
       outItemsUnusedTable=nameTable2jTable(joiner);
       outItemsUnusedScrollPane.getViewport().add(outItemsUnusedTable, null);
       outRulesUseTable=rules2jTable(Pendel.rules);
       outRulesUsePane.getViewport().add(outRulesUseTable, null);





       this.setVisible(true);
       this.repaint();

  }

  //outItemsButtons

      void outItemsDeleteAllButton_actionPerformed(ActionEvent e) throws IOException {


       String delItem="";
         int rowCount=outItemsAllTable.getSelectedRowCount();
         int[] selectedRows=outItemsAllTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)outItemsAllTable.getValueAt(selectedRows[i],0);
               System.out.println("deletion of: "+delItem);
               pendelProcess.allesWissen.remove(delItem);
         } //rof

       outItemsAllTable=nameTable2jTable(pendelProcess.allesWissen);
       outItemsAllScrollPane.getViewport().add(outItemsAllTable, null);
       this.setVisible(true);
       this.repaint();
  }


  void outItemsDeleteUnusedButton_actionPerformed(ActionEvent e) throws IOException {


       String delItem="";
         int rowCount=outItemsUnusedTable.getSelectedRowCount();
         int[] selectedRows=outItemsUnusedTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)outItemsUnusedTable.getValueAt(selectedRows[i],0);
               System.out.println("deletion of: "+delItem);
               pendelProcess.actItems.remove(delItem);
         } //rof

       outItemsUnusedTable=nameTable2jTable(pendelProcess.actItems);
       outItemsUnusedScrollPane.getViewport().add(outItemsUnusedTable, null);
       this.setVisible(true);
       this.repaint();
  }


  // PEndel Pause
       void pendelPause_actionPerformed(ActionEvent e) throws IOException {
            String buttonState=outItemsPauseButton.getText();
            if (buttonState.equals("Pause")) {
                outItemsPauseButton.setText("Continue");
                pendelProcess.pause();
                watcherProcess.watchThread.suspend();
                outItemsDeleteUnusedButton.setVisible(true);
                outItemsDeleteAllButton.setVisible(true);
            } else
            if (buttonState.equals("Continue")) {
                outItemsPauseButton.setText("Pause");
                pendelProcess.weiter();
                watcherProcess.watchThread.resume();
                outItemsDeleteUnusedButton.setVisible(false);
                outItemsDeleteAllButton.setVisible(false);
            } else
            if (buttonState.equals("Resume")) {
                outItemsPauseButton.setText("Pause");
                outItemsPauseButton.setVisible(true);
                outItemsPauseButton.repaint();
                outItemsDeleteUnusedButton.setVisible(true);
                outItemsDeleteAllButton.setVisible(true);

                // Parameters
                int n_search=new Integer(paraSearchNrField.getText()).intValue();
                int n_cands=new Integer(paraVerNrField.getText()).intValue();
                int min_count=new Integer(paraMinCountPane.getText()).intValue();
                int min_rulecount= new Integer(paraMinCountRulePane.getText()).intValue();
                double thresh_item=new Double(paraThreshPane.getText()).doubleValue();
                double thresh_rule=new Double(paraThreshRulePane.getText()).doubleValue();
                String dbString=paraDBnamePane.getText()+"?user="+paraUserPane.getText()+"&password="+paraPasswdField.getText();

                String fileItems=fileOutNewItemsPane.getText();
                String fileContexts=fileOutRuleContextPane.getText();
                String fileMaybes=fileOutMaybeItemsPane.getText();
                String fileEntities=fileOutExtrPatsPane.getText();
                String fileLog=fileOutLogPane.getText();
                boolean boolItems=fileOutNewItemCheckbox.isSelected();
                boolean boolContexts=fileOutRuleContextCheckbox.isSelected();
                boolean boolMaybes=fileOutMaybeItemCheckbox.isSelected();
                boolean  boolEntities=fileOutExtrPatsCheckbox.isSelected();
                boolean boolLog=fileOutLogCheckbox.isSelected();

                pendelProcess=new Pendel(n_search,n_cands,min_count,min_rulecount,
                        thresh_item,thresh_rule,dbString,
                        klassKeysNameTable,regexpNameTable,
                        Pendel.rules,extrPats, Pendel.actItems, Pendel.allesWissen,
                        fileItems,boolItems,fileContexts,boolContexts,
                        fileMaybes, boolMaybes, fileEntities, boolEntities,
                        fileLog,boolLog);

                watcherProcess = new Watcher(this);
            }
            outItemsPauseButton.repaint();
       }

  // PendelStart
      void pendelStart_actionPerformed(ActionEvent e) throws IOException {
      System.out.println("Start!");
      outItemsPauseButton.setText("Pause");
      outItemsPauseButton.setVisible(true);
      outItemsDeleteUnusedButton.setVisible(false);
      outItemsDeleteAllButton.setVisible(false);
      outItemsPauseButton.repaint();

      // Parameters
      int n_search=new Integer(paraSearchNrField.getText()).intValue();
      int n_cands=new Integer(paraVerNrField.getText()).intValue();
      int min_count=new Integer(paraMinCountPane.getText()).intValue();
      int min_rulecount= new Integer(paraMinCountRulePane.getText()).intValue();
      double thresh_item=new Double(paraThreshPane.getText()).doubleValue();
      double thresh_rule=new Double(paraThreshRulePane.getText()).doubleValue();
      String dbString=paraDBnamePane.getText()+"?user="+paraUserPane.getText()+"&password="+paraPasswdField.getText();

      String fileItems=fileOutNewItemsPane.getText();
      String fileContexts=fileOutRuleContextPane.getText();
      String fileMaybes=fileOutMaybeItemsPane.getText();
      String fileEntities=fileOutExtrPatsPane.getText();
      String fileLog=fileOutLogPane.getText();
      boolean boolItems=fileOutNewItemCheckbox.isSelected();
      boolean boolContexts=fileOutRuleContextCheckbox.isSelected();
      boolean boolMaybes=fileOutMaybeItemCheckbox.isSelected();
      boolean  boolEntities=fileOutExtrPatsCheckbox.isSelected();
      boolean boolLog=fileOutLogCheckbox.isSelected();

      if (pendelProcess!=null) {pendelProcess.stop();
                                watcherProcess.running=false;
                                }

      NameTable inItems=new NameTable();
      inItems.insert(inItemsNameTable);

      pendelProcess=new Pendel(n_search,n_cands,min_count,min_rulecount,
                        thresh_item,thresh_rule,dbString,
                        klassKeysNameTable,regexpNameTable,
                        classRules,extrPats, inItems, inItemsBackNameTable,
                        fileItems,boolItems,fileContexts,boolContexts,
                        fileMaybes, boolMaybes, fileEntities, boolEntities,
                        fileLog,boolLog);

     watcherProcess = new Watcher(this);

  }


    void pendelStop_actionPerformed(ActionEvent e) {
        if (pendelProcess!=null) {pendelProcess.stop();}
        if (watcherProcess!=null) watcherProcess.stop();
        outItemsPauseButton.setText("Resume");
        outItemsPauseButton.setVisible(false);

        outItemsDeleteUnusedButton.setVisible(true);
        outItemsDeleteAllButton.setVisible(true);

        this.repaint();
        pendelProcess.actItems=new NameTable();
        pendelProcess.allesWissen=new NameTable();
        softsupdate();
        System.out.println("Stop!");
     }


}


