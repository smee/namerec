package com.biemann.pretree;
//Titel:      Wortschatz-Tool  Pretree
//Version:
//Copyright:  Copyright (c) 2003
//Autor:     C. Biemann
//Organisation:    Uni Leipzig
//Beschreibung:Ihre Beschreibung
//package pretree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import com.borland.jbcl.layout.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import de.wortschatz.*;


public class PretreePanel extends WortschatzModul {


    boolean d=true;       // Anzeigen ein/aus
// Data
   Pretree pretree=new Pretree();
   NameTable TrainSetNameTable= new NameTable();
   NameTable TestSetNameTable= new NameTable();

// Zeugs für WortschtzModul
  public String getToolTip() {return "Train and Test Prefix Compression Tries for String Classification";}
   public void activated() {repaint();}
   public JPanel getModulePanel() {return this;}
   public char getMnemonic() {return (char)(99);}
   public String getName() {return "Pretrie Tool";}
   public Icon getIcon() { return this.createImageIcon("Pre.jpg");}



// Oberfläche
  JPanel PretreeFramePanel = new JPanel();
  JTabbedPane PretreeTabbedPane = new JTabbedPane();
  JPanel TrainPanel = new JPanel();
  JPanel TestPanel = new JPanel();
  JScrollPane TrainSetScrollPane = new JScrollPane();
  JTable TrainSetTable = new JTable();
  JButton ClearTrainButton = new JButton();
  JButton TrainAddFileButton = new JButton();
  JButton TrainSaveFileButton = new JButton();
  JTextField TrainAddFileTextField = new JTextField();
  JTextField TrainSaveFileTextField = new JTextField();
  JLabel TrainSetLabel = new JLabel();
  JButton TrainingAddLineButton = new JButton();
  JTextField TrainingAddWordField = new JTextField();
  JTextField TrainingAddClassField = new JTextField();
  JButton TrainingSetDeleteButton = new JButton();
  JLabel TrainTreeLabel = new JLabel();
  JButton TrainButton = new JButton();
  JProgressBar TrainProgressBar = new JProgressBar(0,100);
  JButton TrainPruneButton = new JButton();
  JButton TrainSaveTreeButton = new JButton();
  JTextField TrainSaveTreePane = new JTextField();
  JLabel TrainParamaterLabel = new JLabel();
  JLabel TrainPruneLabel = new JLabel();
  JTextField TrainPruneTextField = new JTextField();
  JCheckBox TrainReverseCheckBox = new JCheckBox();
  JCheckBox TrainICCheckbox = new JCheckBox();
  JLabel TestTreeParameterLabel = new JLabel();
  JCheckBox TestReverseCheckbox = new JCheckBox();
  JCheckBox TestICCheckbox = new JCheckBox();
  JButton TestTreeLoadButton = new JButton();
  JTextField TestLoadTextField = new JTextField();
  JLabel TestCharacteristicsLabel = new JLabel();
  JLabel TestNodesLabel = new JLabel();
  JLabel TestNrInhaltLabel = new JLabel();
  JLabel TestClassesLabel = new JLabel();
  JLabel TestClassesInhaltLabel = new JLabel();
  JScrollPane TestTextScrollPane = new JScrollPane();
  JTextArea TestTextArea = new JTextArea();
  JScrollPane TestWordScrollPane = new JScrollPane();
  JTextArea TestWordTextArea = new JTextArea();
  JButton TestFileButton = new JButton();
  JTextField TestFileTextField = new JTextField();
  JButton TestWordButton = new JButton();
  JTextField TestWordField = new JTextField();

  public PretreePanel(WortschatzTool wTool) {
      super(wTool);
    try  {

      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    PretreeFramePanel.setBounds(new Rectangle(4, 49, 610, 469));
    PretreeFramePanel.setLayout(null);
//    this.setResizable(false);
//    this.setTitle("Pretree");
//    this.getContentPane().setLayout(null);
    PretreeFramePanel.setBackground(Color.lightGray);
    PretreeFramePanel.setBounds(new Rectangle(0,0, 790, 540));
    PretreeFramePanel.setLayout(null);
    PretreeTabbedPane.setBackground(Color.lightGray);
    PretreeTabbedPane.setBounds(new Rectangle(0,0, 790,540 ));
    PretreeTabbedPane.setMinimumSize(new Dimension(790, 550));
    PretreeTabbedPane.setPreferredSize(new Dimension(790, 550));
//   this.getContentPane().setLayout(null);
   this.setSize(new Dimension(800, 550));
    TrainPanel.setLayout(null);
    TestPanel.setLayout(null);


    // train panel


    TrainSetScrollPane.setBounds(new Rectangle(30, 100, 300, 300));
    ClearTrainButton.setActionCommand("Clear");
    ClearTrainButton.setMargin(new Insets(0, 0, 0, 0));
    ClearTrainButton.setText("Clear");
    ClearTrainButton.setBounds(new Rectangle(30, 50,60, 45));
    TrainAddFileButton.setMargin(new Insets(0, 0, 0, 0));
    TrainAddFileButton.setText("Add from File");
    TrainAddFileButton.setBounds(new Rectangle(100, 50, 100, 20));
    TrainSaveFileButton.setText("Save to File");
    TrainSaveFileButton.setBounds(new Rectangle(100, 75, 100, 20));
    TrainAddFileTextField.setText("enter filename here");
    TrainAddFileTextField.setBounds(new Rectangle(205, 50, 120, 20));
    TrainSaveFileTextField.setText("enter filename here");
    TrainSaveFileTextField.setBounds(new Rectangle(205, 75, 120, 20));
    TrainSetLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    TrainSetLabel.setText("Training Set ");
    TrainSetLabel.setBounds(new Rectangle(25, 30, 110, 20));
    TrainingAddLineButton.setText("Add");
    TrainingAddLineButton.setBounds(new Rectangle(30, 410, 70, 20));
    TrainingAddWordField.setBounds(new Rectangle(105, 410, 120, 20));
    TrainingAddClassField.setBounds(new Rectangle(230, 410, 60, 20));
    TrainingSetDeleteButton.setText("Delete Selected");
    TrainingSetDeleteButton.setBounds(new Rectangle(30, 435, 120, 20));
    TrainTreeLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    TrainTreeLabel.setText("Tree Functions");
    TrainTreeLabel.setBounds(new Rectangle(425, 150, 130, 25));
    TrainButton.setText("Train");
    TrainButton.setBounds(new Rectangle(430, 200, 80, 60));
    TrainProgressBar.setBounds(new Rectangle(530, 210, 200, 25));
    TrainPruneButton.setText("Prune");
    TrainPruneButton.setBounds(new Rectangle(430, 270, 80, 60));
    TrainSaveTreeButton.setMargin(new Insets(0, 0, 0, 0));
    TrainSaveTreeButton.setText("Save to File");
    TrainSaveTreeButton.setBounds(new Rectangle(430, 340, 80, 60));
    TrainSaveTreePane.setText("enter filename here");
    TrainSaveTreePane.setBounds(new Rectangle(530, 360, 180, 20));
    TrainParamaterLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    TrainParamaterLabel.setText("Training Paramaters");
    TrainParamaterLabel.setBounds(new Rectangle(425, 30, 120, 30));
    TrainPruneLabel.setText("Pruning Threshold");
    TrainPruneLabel.setBounds(new Rectangle(540, 280, 120, 20));
    TrainPruneTextField.setText("50");
    TrainPruneTextField.setBounds(new Rectangle(650, 280, 50, 20));
    TrainReverseCheckBox.setText("Reverse");
    TrainReverseCheckBox.setBounds(new Rectangle(430, 80, 90, 20));
    TrainICCheckbox.setText("Ignore Case");
    TrainICCheckbox.setBounds(new Rectangle(430, 110, 90, 20));

    TrainAddFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        TrainAddFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    TrainSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        TrainSaveFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

    TrainingSetDeleteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        TrainingSetDeleteButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });
    ClearTrainButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ClearTrainButton_actionPerformed(e);
      }
    });

    TrainingAddLineButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
             try {
            TrainingAddLineButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}

      }
    });

    TrainButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TrainButton_actionPerformed(e);
      }
    });

    TrainPruneButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TrainPruneButton_actionPerformed(e);
      }
    });
    TrainSaveTreeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        TrainSaveTreeButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}

      }
    });

    // test panel

    TestTreeParameterLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    TestTreeParameterLabel.setText("Tree Parameters");
    TestTreeParameterLabel.setBounds(new Rectangle(10, 20, 140, 20));
    TestReverseCheckbox.setText("Reverse");
    TestReverseCheckbox.setBounds(new Rectangle(15, 70, 90, 20));
    TestICCheckbox.setText("Ignore Case");
    TestICCheckbox.setBounds(new Rectangle(15, 100, 90, 20));
    TestTreeLoadButton.setMargin(new Insets(0, 0, 0, 0));
    TestTreeLoadButton.setText("Load from File");
    TestTreeLoadButton.setBounds(new Rectangle(15, 45, 90, 20));
    TestLoadTextField.setText("enter filename here");
    TestLoadTextField.setBounds(new Rectangle(110, 45, 148, 20));
    TestCharacteristicsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    TestCharacteristicsLabel.setText("Tree Characteristics");
    TestCharacteristicsLabel.setBounds(new Rectangle(350, 20, 120, 20));
    TestNodesLabel.setText("Nr. of Nodes:");
    TestNodesLabel.setBounds(new Rectangle(350, 50, 90, 20));
    TestNrInhaltLabel.setText("0");
    TestNrInhaltLabel.setBounds(new Rectangle(440, 50, 100, 20));
    TestClassesLabel.setText("Nr. of Classes:");
    TestClassesLabel.setBounds(new Rectangle(350, 75, 90, 20));
    TestClassesInhaltLabel.setText("0");
    TestClassesInhaltLabel.setBounds(new Rectangle(440, 75, 100, 20));
    TestTextScrollPane.setBounds(new Rectangle(20, 145, 320, 240));
    TestWordScrollPane.setBounds(new Rectangle(360, 145, 320, 240));
    TestFileButton.setText("Test from File");
    TestFileButton.setBounds(new Rectangle(20, 400, 120, 20));
    TestFileTextField.setText("enter testfile name here");
    TestFileTextField.setBounds(new Rectangle(150, 400, 189, 20));
    TestWordButton.setMargin(new Insets(0, 0, 0, 0));
    TestWordButton.setText("Classify Word");
    TestWordButton.setBounds(new Rectangle(360, 400, 120, 20));
    TestWordField.setText("enter word here");
    TestWordField.setBounds(new Rectangle(490, 400, 189, 20));

    TestTreeLoadButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
       try {
        TestTreeLoadButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}
      }
    });

    TestWordButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {

        TestWordButton_actionPerformed(e);

      }
    });

      TestFileButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
      try {
        TestFileButton_actionPerformed(e);
       } catch (IOException f) {System.out.println(f.getMessage());}

      }
    });

  // adds
  //  this.getContentPane().add(PretreeFramePanel, null);
    this.add(PretreeFramePanel, null);
    PretreeFramePanel.add(PretreeTabbedPane, null);
    PretreeTabbedPane.add(TrainPanel, "TrainPanel");
    TrainPanel.add(TrainSetScrollPane, null);
    TrainPanel.add(ClearTrainButton, null);
    TrainPanel.add(TrainSaveFileButton, null);
    TrainPanel.add(TrainAddFileButton, null);
    TrainPanel.add(TrainSetLabel, null);
    TrainPanel.add(TrainingAddLineButton, null);
    TrainPanel.add(TrainingAddWordField, null);
    TrainPanel.add(TrainingAddClassField, null);
    TrainPanel.add(TrainingSetDeleteButton, null);
    TrainPanel.add(TrainAddFileTextField, null);
    TrainPanel.add(TrainSaveFileTextField, null);
    TrainPanel.add(TrainSaveTreeButton, null);
    TrainPanel.add(TrainSaveTreePane, null);
    TrainPanel.add(TrainPruneButton, null);
    TrainPanel.add(TrainButton, null);
    //TrainPanel.add(TrainProgressBar, null);
    TrainPanel.add(TrainTreeLabel, null);
    TrainPanel.add(TrainPruneLabel, null);
    TrainPanel.add(TrainPruneTextField, null);
    TrainPanel.add(TrainReverseCheckBox, null);
    TrainPanel.add(TrainICCheckbox, null);
    TrainPanel.add(TrainParamaterLabel, null);
    PretreeTabbedPane.add(TestPanel, "TestPanel");
    TestPanel.add(TestTreeParameterLabel, null);
    TestPanel.add(TestReverseCheckbox, null);
    TestPanel.add(TestICCheckbox, null);
    TestPanel.add(TestTreeLoadButton, null);
    TestPanel.add(TestLoadTextField, null);
    TestPanel.add(TestCharacteristicsLabel, null);
    TestPanel.add(TestNodesLabel, null);
    TestPanel.add(TestNrInhaltLabel, null);
    TestPanel.add(TestClassesInhaltLabel, null);
    TestPanel.add(TestClassesLabel, null);
    TestPanel.add(TestTextScrollPane, null);
    TestTextScrollPane.getViewport().add(TestTextArea, null);
    TestPanel.add(TestWordScrollPane, null);
    TestPanel.add(TestFileButton, null);
    TestPanel.add(TestFileTextField, null);
    TestPanel.add(TestWordButton, null);
    TestPanel.add(TestWordField, null);
    TestWordScrollPane.getViewport().add(TestWordTextArea, null);
    TrainSetScrollPane.getViewport().add(TrainSetTable, null);



  } // end JBinit

  //Überschreiben, damit das Programm bei Herunterfahren des Systems beendet werden kann
//  protected void processWindowEvent(WindowEvent e) {
//    super.processWindowEvent(e);
//    if(e.getID() == WindowEvent.WINDOW_CLOSING) {
//      System.exit(0);
//    }
//  }



  // functions
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





  // Button Listener

    void ClearTrainButton_actionPerformed(ActionEvent e) {
       if (d) System.out.println("Clear!");
       TrainSetNameTable=new NameTable();
       TrainSetTable=nameTable2jTable(TrainSetNameTable);
       TrainSetScrollPane.getViewport().add(TrainSetTable, null);
       this.setVisible(true);
       this.repaint();
  }
  void TrainAddFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=TrainAddFileTextField.getText();
       NameTable loader=new NameTable();
       loader.loadFile(filename);
       TrainSetNameTable.insert(loader);
       TrainSetTable=nameTable2jTable(TrainSetNameTable);
       TrainSetScrollPane.getViewport().add(TrainSetTable, null);
       this.setVisible(true);
  }

   void TrainingSetDeleteButton_actionPerformed(ActionEvent e) throws IOException {
         String delItem="";
         int rowCount=TrainSetTable.getSelectedRowCount();
         int[] selectedRows=TrainSetTable.getSelectedRows();
         for (int i=0;i<rowCount;i++) {
               delItem=(String)TrainSetTable.getValueAt(selectedRows[i],0);
               if (d) System.out.println("Deleting: "+delItem);
               TrainSetNameTable.remove(delItem);
         } //rof
         TrainSetTable=nameTable2jTable(TrainSetNameTable);
         TrainSetScrollPane.getViewport().add(TrainSetTable, null);
         this.setVisible(true);
         this.repaint();
  }

   void TrainingAddLineButton_actionPerformed(ActionEvent e) throws IOException {
       String newItem=TrainingAddWordField.getText();
       String newClass=TrainingAddClassField.getText();
       NameTable adder=new NameTable();
       adder.put(newItem, newClass);
       TrainSetNameTable.insert(adder);
       TrainSetTable=nameTable2jTable(TrainSetNameTable);
       TrainSetScrollPane.getViewport().add(TrainSetTable, null);
       this.setVisible(true);
       this.repaint();
  }


  void TrainSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=TrainSaveFileTextField.getText();
       TrainSetNameTable.writeFile(filename,false);
       System.out.println("File saved: "+filename);
  }

  void TrainPruneButton_actionPerformed(ActionEvent e)  {
       int thresh= new Integer(TrainPruneTextField.getText()).intValue();
       pretree.setThresh(((double)thresh)/100.0);
       pretree.prune();
  }


   void TrainSaveTreeButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=TrainSaveTreePane.getText();
       pretree.speichere(filename);
   }

// testpanel buttons
    void TestTreeLoadButton_actionPerformed(ActionEvent e) throws IOException {
       String filename=TestLoadTextField.getText();
       pretree=new Pretree();
       pretree.lade(filename);
       boolean reverse=TestReverseCheckbox.isSelected();
       boolean ignoreCase=TestICCheckbox.isSelected();
       pretree.setIgnoreCase(ignoreCase);
       pretree.setReverse(reverse);
       TestNrInhaltLabel.setText(""+pretree.getNrOfNodes());
       TestClassesInhaltLabel.setText(""+pretree.getNrOfClasses());
       this.repaint();


   }

     void TestWordButton_actionPerformed(ActionEvent e)  {
       boolean reverse=TestReverseCheckbox.isSelected();
       boolean ignoreCase=TestICCheckbox.isSelected();
       pretree.setIgnoreCase(ignoreCase);
       pretree.setReverse(reverse);
       String word=TestWordField.getText();
       String wclass=pretree.classify(word);
       String reason=pretree.giveReason(word);
       String printtext="\n"+reason+"\n'"+word+"' classified as '"+wclass+"'\n";
       //if (d) System.out.println(printtext);
       TestWordTextArea.setText(TestWordTextArea.getText()+"\n"+reason+"\n'"+word+"' classified as '"+wclass+"'\n");
       TestWordTextArea.repaint();
   }

     void TestFileButton_actionPerformed(ActionEvent e)  throws IOException {
       boolean reverse=TestReverseCheckbox.isSelected();
       boolean ignoreCase=TestICCheckbox.isSelected();
       pretree.setIgnoreCase(ignoreCase);
       pretree.setReverse(reverse);

       TestSetNameTable= new NameTable();
       String filename=TestFileTextField.getText();
       System.out.println("Loading "+filename+" ...");
       TestTextArea.repaint();
       TestSetNameTable.loadFile(filename);
       System.out.println("Classifying ...");
       TestTextArea.repaint();
       String word="";
       String wclass="";
       String pclass="";
       String reasonStr="";
       int right=0, wrong=0, undecided=0;
       for (Enumeration f=TestSetNameTable.keys();f.hasMoreElements();) {
           word=(String)f.nextElement();
           wclass=(String)TestSetNameTable.get(word);
           pclass=pretree.classify(word);
           if (wclass.equals(pclass)) {right++;}
           else if (pclass.equals("undecided")) {
                undecided++;
                reasonStr+="\n'"+word+"' classified as '"+pclass+"' instead of '"+wclass+"'\n"+pretree.giveReason(word);
                }
           else {
                wrong++;
                reasonStr+="\n'"+word+"' classified as '"+pclass+"' instead of '"+wclass+"'\n"+pretree.giveReason(word);
           } // esle fi esle
       } // for enum f
       double prec=(double)right/((double)right+(double)wrong);
       double recall=(double)right/((double)right+(double)wrong+(double)undecided);
       reasonStr+="\nEvaluation:\n Precision:\t"+prec+"\n Recall:\t"+recall;


       TestTextArea.setText(reasonStr);
       TestTextArea.repaint();
   }




  void TrainButton_actionPerformed(ActionEvent e) {
       String aktword="";
       String wclass="";
       boolean reverse=TrainReverseCheckBox.isSelected();
       boolean ignoreCase=TrainICCheckbox.isSelected();
       pretree=new Pretree();
       pretree.setIgnoreCase(ignoreCase);
       pretree.setReverse(reverse);

       int progress=0;
       int percentage=0;
       int maxprogress=TrainSetNameTable.size();
       int progresssteps=(int)((double)maxprogress/100.0);
       if (progresssteps==0) progresssteps=1;

       TrainProgressBar.setValue(0);
       TrainProgressBar.setStringPainted(true);


       for(Enumeration f=TrainSetNameTable.keys();f.hasMoreElements();) {
           aktword=(String)f.nextElement();
           wclass=(String)TrainSetNameTable.get(aktword);
           pretree.train(aktword,wclass);
           //if (d) System.out.println("Training: "+aktword+" -> "+wclass);
           progress++;
           if ((progress % progresssteps)==0) {  // set progress bar
             percentage= (int)((double)(progress)*100/(double)(maxprogress));
             System.out.println(percentage+"% done");
           }
       } // rof Enum e
       System.out.println("Training done.");
       TestNrInhaltLabel.setText(""+pretree.getNrOfNodes());
       TestClassesInhaltLabel.setText(""+pretree.getNrOfClasses());
       this.repaint();



  }





} // end class PendelPanel