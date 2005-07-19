
//Titel:      Wortschatz-Tool
//Version:
//Copyright:  Copyright (c) 1999
//Autor:     C. Biemann
//Organisation:    Uni Leipzig
//Beschreibung:Ihre Beschreibung
package namerec.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import namerec.FileDataSource;
import namerec.MatcherNam;
import namerec.NameTable;
import namerec.Pattern;
import namerec.Recognizer;
import namerec.SatzDatasource;
import namerec.util.Config;
import namerec.util.FileSelector;
import namerec.util.SwingWorker;

import com.biemann.pendel.Pendel;
import com.biemann.pendel.Watcher;
import com.bordag.klf.util.StringUtils;

import de.wortschatz.WortschatzModul;
import de.wortschatz.WortschatzTool;
import javax.swing.JSpinner;
import javax.swing.JTextArea;


public class RecognizerPanel extends WortschatzModul {
    NameTable inItemsNameTable = new NameTable();
    NameTable inItemsBackNameTable = new NameTable();
    NameTable regexpNameTable = new NameTable();
    NameTable klassKeysNameTable=new NameTable();
    Vector classRules=new Vector();
    Vector extrPats=new Vector();
    Pendel pendelProcess=null;
    Watcher watcherProcess=null;
    
//  zeug, das das wortschatzModul braucht
    public String getToolTip() {return "extract names and titles from sentences";};
    public void activated() {repaint();};
    public JPanel getModulePanel() {return this;};
    public char getMnemonic() {return (char)(88);};
    public String getName() {return "NameRec";};
    public Icon getIcon() { return this.createImageIcon("Pen.jpg");}
    
    
    JPanel pendelFramePanel = new JPanel();  // Main Panel
    JTabbedPane PendelTabbedPane = new JTabbedPane();
    JPanel inputItemsPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JButton classRulesDeleteutton = new JButton();
    JButton classRulesClearButton= new JButton();
    JButton classRulesAddButton = new JButton();
    JTextField classRulesAddLeftField = new JTextField();
    JPanel inputFeaturesPanel = new JPanel();
    JButton classRulesSaveFileButton = new JButton();
    JButton classRulesAddFileButton = new JButton();
    JButton tagEncodeAutoButton = new JButton();
    JTextField classRulesFileNamePane = new JTextField();
    JLabel paraSearchLabel = new JLabel();
    JPanel parametersPanel = new JPanel();
    JButton paraDefaultButton = new JButton();
    JLabel paraVerLabel = new JLabel();
    JLabel paraThreshLabel = new JLabel();
    JTextField paraThreshPane = new JTextField();
    JTable outRulesTestTable = new JTable();
    JTable outRulesUseTable = new JTable();
    JPanel fileOutPanel = new JPanel();
    JLabel fileOutLabel = new JLabel();
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
    JButton fileConfSaveButton = new JButton();
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
    JButton extrPatsDeleteButton = new JButton();
    JTable extrPatsTable = new JTable();
    JTable inItemTable = new JTable();
    JLabel paraLabel = new JLabel();
    TitledBorder titledBorder1;
    TitledBorder titledBorder2;
    TitledBorder titledBorder3;
    TitledBorder titledBorder4;
    JScrollPane classRulesScrollPane = new JScrollPane();
    JTable classRulesTable = new JTable();
    JLabel classRulesLeftLabel = new JLabel();
    JLabel classRulesGoalLabel = new JLabel();
    JLabel extPatPatLabel = new JLabel();
    JTextField extrPatsAddClassLField = new JTextField();
    JLabel extrPatsClassLabel = new JLabel();
    JTextField numOfThreadField = new JTextField();
    JTextField paraVerNrField = new JTextField();
    JLabel inItemBackLabel = new JLabel();
    JButton inItemsBackClearButton = new JButton();
    JButton inItemsBackLoadButton = new JButton();
    JButton inItemsBackSaveFileButtonm = new JButton();
    JTextField inItemsBackLoadField = new JTextField();
    JScrollPane inItemBackScrollPane = new JScrollPane();
    JTable inItemBackTable = new JTable();
    JButton inItemBackAddButton = new JButton();
    JButton inItemBackDeleteButton = new JButton();
    JLabel inItemBackItemLabel = new JLabel();
    JLabel inItemBackClassLabel = new JLabel();
    JTextField inItemBackAddItemField = new JTextField();
    JTextField inItemBackAddClassField = new JTextField();
    private JLabel jLabel = null;
    private JTextField versionTf = null;
    private BaseTaggerPanel baseTaggerPanel = null;
    private DBConfigPanel DBConfigPanelAkt = null;
    private DBConfigPanel DBConfigPanelWs = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JSpinner startNoSpiner = null;
    private JSpinner endnoSpinner = null;
    private JPanel tagSystemPanel;
    private JLabel tagLabel=new JLabel(),tagEncodeLabel=new JLabel(),tagRegexpLabel=new JLabel(),
    tagEncodeCodeLable=new JLabel(),tagEncodeClassLabel=new JLabel(),
    tagRegexpAddClassLabel=new JLabel(),tagRegexpAddRegexpLabel=new JLabel();
    private JCheckBox tntCheckBox=new JCheckBox();
    private JButton tagLoadCodeButton=new JButton(),tagEncodeAddButton=new JButton(),tagEncodeDeleteButton=new JButton(),
    tagCodeSaveButton=new JButton(),tagEncodeClearButton=new JButton(),tagRegexpLoadButton=new JButton(),
    tagRegexpSaveButton=new JButton(),tagRegexpAddButton=new JButton(),tagRegexpClearButton=new JButton(),
    tagRegexpDeleteButton=new JButton();
    private JTextField tagCodeLoadPane=new JTextField(),
    tagEncodeAddClassField=new JTextField(),
    tagRegexpLoadPane=new JTextField(),
    tagRegexpAddRegexpField=new JTextField(),tagRegexpAddClassField=new JTextField();
    private JScrollPane tagEncodeScrollPane=new JScrollPane(),tagRegexpScrollPane=new JScrollPane();
    private JTable tagEncodeTable=new JTable(),tagRegexpTable=new JTable();
    private JLabel jLabel3 = null;
    private JSpinner samplesSpinner = null;
    private JPanel runnerPanel = null;
    private JTextArea textArea = null;
    private JButton startButton = null;
    private JLabel jLabel4 = null;
    private JTextField sentenceTf = null;
    private JButton singleSentenceButton = null;
    private JScrollPane scrollPane;
    private JPanel sourcePanel;
    private JLabel jLabel5 = null;
    private JTextField fileSourceTf = null;
    private JButton fileSourceButton = null;
    private JButton runFileDatasourceButton = null;
    private JCheckBox runNECb = null;
	private JScrollPane jScrollPane = null;
	private KlassTagPanel klassTagPanel = null;
	private JButton jButton = null;
    //Frame konstruieren
    public RecognizerPanel(WortschatzTool wTool)
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
        jLabel3 = new JLabel();
        jLabel3.setBounds(1, 191, 185, 15);
        jLabel3.setText("Sentences between time est.");
        jLabel2 = new JLabel();
        jLabel2.setBounds(299, 143, 175, 26);
        jLabel2.setText("<html>No. of last sentence <br>(-1 means all)</html>");
        jLabel1 = new JLabel();
        jLabel1.setBounds(300, 111, 151, 22);
        jLabel1.setText("No. of first sentence");
        jLabel = new JLabel();
        jLabel.setBounds(300, 76, 178, 19);
        jLabel.setText("Versionsid");
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
        classRulesLabel.setText("Name/Title Rules");
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
        
        classRulesFileNamePane.setText("pats2.txt");
        classRulesFileNamePane.setBounds(new Rectangle(305, 5, 160, 25));
        
        classRulesSaveFileButton.setText("Save to file");
        classRulesSaveFileButton.setBounds(new Rectangle(500, 5, 100, 25));
        classRulesSaveFileButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Save NE patterns to...", null,FileSelector.SAVE_DIALOG);
        		if(f != null) {
        			try {
        				String filename=f.getAbsolutePath();
        				MatcherNam writer= new MatcherNam(null);
        				writer.patterns=extrPats;
        				writer.saveFile(filename);
        				classRulesFileNamePane.setText(filename);
        			} catch (IOException ioe) {System.out.println(ioe.getMessage());}
        		}
        	}				
        });
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
        
        classRulesAddFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    classRulesAddFileButton_actionPerformed(e);
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
        extrPatsFileNamePane.setText("patPers.txt");
        extrPatsClearButton.setBounds(new Rectangle(120, 5, 70, 25));
        extrPatsClearButton.setBackground(Color.red);
        extrPatsClearButton.setText("Clear");
        extrPatsAddButton.setBounds(new Rectangle(5, 180, 70, 25));
        extrPatsAddButton.setText("Add");
        extrPatsAddPatField.setBounds(new Rectangle(80, 180, 200, 25));
        extrPatsSaveFileButton.setBounds(new Rectangle(500, 5, 100, 25));
        extrPatsSaveFileButton.setText("Save to file");
        extrPatsDeleteButton.setBounds(new Rectangle(5, 210, 120, 25));
        extrPatsDeleteButton.setText("Delete selected");
        extrPatsScrollPane.setBounds(new Rectangle(5, 35, 770, 120));
        extrPatsLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        extrPatsLabel.setText("NE Patterns");
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
        
        
        
        
        
        
        
        
        
        inItemBackLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        inItemBackLabel.setText("Known names and titles");
        inItemBackLabel.setBounds(new java.awt.Rectangle(31,14,230,20));
        inItemsBackClearButton.setBackground(Color.red);
        inItemsBackClearButton.setText("Clear");
        inItemsBackClearButton.setBounds(new java.awt.Rectangle(31,44,80,55));
        inItemsBackLoadButton.setMargin(new Insets(0, 0, 0, 0));
        inItemsBackLoadButton.setText("Add from File");
        inItemsBackLoadButton.setBounds(new java.awt.Rectangle(120,44,121,25));
        inItemsBackSaveFileButtonm.setText("Save to File");
        inItemsBackSaveFileButtonm.setBounds(new java.awt.Rectangle(120,75,123,25));
        inItemsBackLoadField.setText("wissenAkt.txt");
        inItemsBackLoadField.setBounds(new java.awt.Rectangle(254,45,170,25));
        inItemBackScrollPane.setBounds(new java.awt.Rectangle(31,120,380,260));
        inItemBackAddButton.setText("Add");
        inItemBackAddButton.setBounds(new java.awt.Rectangle(31,410,70,25));
        inItemBackDeleteButton.setText("Delete selected");
        inItemBackDeleteButton.setBounds(new java.awt.Rectangle(31,440,120,25));
        inItemBackItemLabel.setBorder(titledBorder1);
        inItemBackItemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inItemBackItemLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        inItemBackItemLabel.setText("Item");
        inItemBackItemLabel.setBounds(new java.awt.Rectangle(106,390,170,20));
        inItemBackClassLabel.setBorder(titledBorder1);
        inItemBackClassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inItemBackClassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        inItemBackClassLabel.setText("Class");
        inItemBackClassLabel.setBounds(new java.awt.Rectangle(281,390,90,20));
        inItemBackAddItemField.setBounds(new java.awt.Rectangle(106,410,170,25));
        inItemBackAddClassField.setBounds(new java.awt.Rectangle(281,410,90,25));
        
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
        
        paraSearchLabel.setText("Nr. of verification threads");
        paraSearchLabel.setBounds(new Rectangle(10, 70, 180, 20));
        
        
        paraVerLabel.setText("max. Nr of Verification Sentences");
        paraVerLabel.setBounds(new Rectangle(10, 110, 180, 20));
        
        paraThreshLabel.setText("Threshold Item acceptance");
        paraThreshLabel.setBounds(new Rectangle(10, 150, 180, 20));
        paraThreshPane.setText("0.1");
        paraThreshPane.setBounds(new Rectangle(200, 150, 60, 25));
        
        
        
        numOfThreadField.setText("10");
        numOfThreadField.setBounds(new Rectangle(200, 70, 60, 25));
        paraVerNrField.setText("30");
        paraVerNrField.setBounds(new Rectangle(200, 110, 60, 25));
        
        
        
        
                
        // File Panel
        filePanel.setLayout(null);
        fileOutPanel.setBorder(BorderFactory.createEtchedBorder());
        fileOutPanel.setBounds(new Rectangle(5, 5, 780, 245));
        fileOutPanel.setLayout(null);
        fileOutLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        fileOutLabel.setText("Output Files");
        fileOutLabel.setBounds(new Rectangle(5, 10, 200, 20));
        fileOutNewItemCheckbox.setText("New Items");
        fileOutNewItemCheckbox.setSelected(true);
        fileOutNewItemCheckbox.setBounds(new java.awt.Rectangle(10,50,112,25));
        fileOutNewItemsPane.setText("itemsFound.txt");
        fileOutNewItemsPane.setBounds(new java.awt.Rectangle(135,50,160,25));
        fileOutMaybeItemCheckbox.setText("Maybe Items");
        fileOutMaybeItemCheckbox.setSelected(true);
        fileOutMaybeItemCheckbox.setBounds(new java.awt.Rectangle(10,100,112,25));
        fileOutMaybeItemsPane.setText("maybes.txt");
        fileOutMaybeItemsPane.setBounds(new java.awt.Rectangle(135,100,160,25));
        fileOutRuleContextCheckbox.setText("Rule Contexts");
        fileOutRuleContextCheckbox.setSelected(true);
        fileOutRuleContextCheckbox.setBounds(new java.awt.Rectangle(325,50,120,25));
        fileOutRuleContextPane.setText("contexts.txt");
        fileOutRuleContextPane.setBounds(new java.awt.Rectangle(505,50,160,25));
        fileOutExtrPatsCheckbox.setText("Found complex names");
        fileOutExtrPatsCheckbox.setSelected(true);
        fileOutExtrPatsCheckbox.setBounds(new java.awt.Rectangle(325,100,170,25));
        fileOutExtrPatsPane.setText("NEs.txt");
        fileOutExtrPatsPane.setBounds(new java.awt.Rectangle(505,100,160,25));
        fileOutLogCheckbox.setText("create log file");
        fileOutLogCheckbox.setSelected(true);
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
        fileConfSaveButton.setText("Save Configuration to");
        fileConfSaveButton.setBounds(new Rectangle(10, 100, 170, 25));
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
        
        
        
        inItemTable.setName("");
        
//      TagsPanel
        tagSystemPanel=new JPanel();
        tagSystemPanel.setLayout(null);
        tagLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        tagLabel.setText("Tag System Settings");
        tagLabel.setBounds(new Rectangle(5, 10, 150, 20));
        tagEncodeLabel.setText("Tags Encoding");
        tagEncodeLabel.setBounds(new Rectangle(10, 50, 120, 20));
        tagRegexpLabel.setText("Regexp Tagging");
        tagRegexpLabel.setBounds(new Rectangle(400, 50, 150, 20));
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
        tagCodeLoadPane.setBounds(new java.awt.Rectangle(80,80,145,25));

        tagEncodeScrollPane.setBounds(new java.awt.Rectangle(10,140,356,250));
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
        tagEncodeClearButton = new JButton();
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
        tagRegexpScrollPane.setBounds(new Rectangle(400, 120, 400, 270));

        tagRegexpAddButton.setFont(new java.awt.Font("Dialog", 0, 10));
        tagRegexpAddButton.setMargin(new Insets(0, 0, 0, 0));
        tagRegexpAddButton.setText("Add");
        tagRegexpAddButton.setBounds(new Rectangle(400, 330, 50, 25));
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
        
        // Hier ADDs
        
        
        
//      this.getContentPane().add(pendelFramePanel, null);
        this.add(pendelFramePanel, null);
        pendelFramePanel.add(PendelTabbedPane, null);
        PendelTabbedPane.add(new WelcomePanel(),"Welcome");
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
        PendelTabbedPane.add(parametersPanel, "Parameters and Settings");
        parametersPanel.add(paraSearchLabel, null);
        parametersPanel.add(paraDefaultButton, null);
        parametersPanel.add(paraVerLabel, null);
        parametersPanel.add(paraThreshLabel, null);
        parametersPanel.add(paraThreshPane, null);
        parametersPanel.add(paraLabel, null);
        parametersPanel.add(numOfThreadField, null);
        parametersPanel.add(jLabel, null);
        parametersPanel.add(getVersionTf(), null);
        parametersPanel.add(getBaseTaggerPanel(), null);
        parametersPanel.add(getDBConfigPanelAkt(), null);
        parametersPanel.add(getDBConfigPanelWs(), null);
        parametersPanel.add(paraVerNrField, null);
        PendelTabbedPane.add(tagSystemPanel, "Tag System");
        tagSystemPanel.add(tagLabel, null);
        tagSystemPanel.add(tagEncodeLabel, null);
        tagSystemPanel.add(tagRegexpLabel, null);
        tagSystemPanel.add(tagLoadCodeButton, null);
        tagSystemPanel.add(tntCheckBox, null);
        tagSystemPanel.add(tagCodeSaveButton, null);
        tagSystemPanel.add(tagCodeLoadPane, null);
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
        tagSystemPanel.add(tagEncodeAddClassField, null);
        tagSystemPanel.add(tagEncodeClassLabel, null);
        tagSystemPanel.add(tagEncodeCodeLable, null);
        tagSystemPanel.add(tagEncodeClearButton, null);
        tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
        tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
        PendelTabbedPane.add(inputFeaturesPanel, "Rules and Patterns");
        inputFeaturesPanel.add(classRulesPanel, null);
        classRulesPanel.add(classRulesLabel, null);
        classRulesPanel.add(classRulesClearButton, null);
        classRulesPanel.add(classRulesAddFileButton, null);
        classRulesPanel.add(classRulesFileNamePane, null);
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
        extrPatternsPanel.add(extrPatsScrollPane, null);
        extrPatternsPanel.add(extrPatsAddButton, null);
        extrPatternsPanel.add(extrPatsDeleteButton, null);
        extrPatternsPanel.add(extrPatsAddPatField, null);
        extrPatternsPanel.add(extPatPatLabel, null);
        extrPatternsPanel.add(extrPatsAddClassLField, null);
        extrPatternsPanel.add(extrPatsClassLabel, null);
        extrPatsScrollPane.getViewport().add(extrPatsTable, null);
        PendelTabbedPane.add(inputItemsPanel, "Known names");
        PendelTabbedPane.addTab("Run", null, getRunnerPanel(), null);
        inputItemsPanel.add(inItemBackLabel, null);
        inputItemsPanel.add(inItemsBackLoadField, null);
        inputItemsPanel.add(inItemBackScrollPane, null);
        inItemBackScrollPane.getViewport().add(inItemBackTable, null);
        inputItemsPanel.add(inItemsBackClearButton, null);
        inputItemsPanel.add(inItemsBackLoadButton, null);
        inputItemsPanel.add(inItemsBackSaveFileButtonm, null);
        inputItemsPanel.add(inItemBackAddButton, null);
        tagSystemPanel.add(getJScrollPane(), null);
        inputItemsPanel.add(inItemBackDeleteButton, null);
        inputItemsPanel.add(inItemBackItemLabel, null);
        inputItemsPanel.add(inItemBackClassLabel, null);
        inputItemsPanel.add(inItemBackAddItemField, null);
        inputItemsPanel.add(inItemBackAddClassField, null);
        parametersPanel.add(jLabel1, null);
        parametersPanel.add(jLabel2, null);
        parametersPanel.add(getStartNoSpiner(), null);
        parametersPanel.add(getEndnoSpinner(), null);
        parametersPanel.add(jLabel3, null);
        parametersPanel.add(getSamplesSpinner(), null);
        
    }
//  tagEncode Buttons

    void tagEncodeClearButton_actionPerformed(ActionEvent e) {
        klassKeysNameTable=new NameTable();
        tagEncodeTable=nameTable2jTable(klassKeysNameTable);
        tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
        this.setVisible(true);
        this.repaint();
   }

    void tagEncodeLoadButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find regexp file...", null,FileSelector.OPEN_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            loadKlassnamesFrom(filename);
        }
    }
    /**
     * @param filename
     * @throws IOException
     */
    private void loadKlassnamesFrom(String filename) throws IOException {
        NameTable loader=NameTable.loadFromFile(filename);
        klassKeysNameTable.putAll(loader);
        tagCodeLoadPane.setText(filename);
        tagEncodeTable=nameTable2jTable(klassKeysNameTable);
        tagCodeLoadPane.setText(filename);
        tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
        this.setVisible(true);
    }
    void tagEncodeAddButton_actionPerformed(ActionEvent e) throws IOException {
        String newItem=tagEncodeAddClassField.getText();
        String newClass=getKlassTagPanel().getTag();
        NameTable adder=new NameTable();
        adder.put(newItem, newClass);
        klassKeysNameTable.putAll(adder);
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
        int code=1;
        for (Enumeration en=autoTable.keys();en.hasMoreElements();) {
            String inElem=(String)en.nextElement();
            String codeString=""+org.apache.commons.lang.StringUtils.leftPad(Integer.toBinaryString(code),32,'0');
            autoTable.put(inElem,codeString);
            code=code*2;
        }

        klassKeysNameTable = new NameTable();
        klassKeysNameTable.putAll(autoTable);
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
                klassKeysNameTable.remove(delItem);
          } //rof


        tagEncodeTable=nameTable2jTable(klassKeysNameTable);
        tagEncodeScrollPane.getViewport().add(tagEncodeTable, null);
        this.setVisible(true);
        this.repaint();
   }


    void tagEncodesSaveFileButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Save patterns to...", null,FileSelector.SAVE_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            klassKeysNameTable.writeFile(filename,false);
        }
    }


   //tagRegexpButtons

    void tagRegexpClearButton_actionPerformed(ActionEvent e) {
        regexpNameTable=new NameTable();
        tagRegexpTable=nameTable2jTable(regexpNameTable);
        tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
        this.setVisible(true);
        this.repaint();
   }

    void tagRegexpLoadButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find regexp file...", null,FileSelector.OPEN_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            loadRegexpFrom(filename);
        }
    }
    /**
     * @param filename
     * @throws IOException
     */
    private void loadRegexpFrom(String filename) throws IOException {
        tagRegexpLoadPane.setText(filename);
    	NameTable loader=NameTable.loadFromFile(filename);
        regexpNameTable.putAll(loader);
        tagRegexpLoadPane.setText(filename);
        tagRegexpTable=nameTable2jTable(regexpNameTable);
        tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
        this.setVisible(true);
    }
   void tagRegexpSaveButton_actionPerformed(ActionEvent e) throws IOException {
       File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Save regexps to...", null,FileSelector.SAVE_DIALOG);
       if(f != null) {
           String filename=f.getAbsolutePath();
           regexpNameTable.writeFile(filename,false);
       }}

      void tagRegexpAddButton_actionPerformed(ActionEvent e) throws IOException {
        String newRegexp=tagRegexpAddRegexpField.getText();
        String newClass=tagRegexpAddClassField.getText();
        NameTable adder=new NameTable();
        adder.put( newRegexp, newClass);
        regexpNameTable.putAll(adder);
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
                regexpNameTable.remove(delItem);
          } //rof

        tagRegexpTable=nameTable2jTable(regexpNameTable);
        tagRegexpScrollPane.getViewport().add(tagRegexpTable, null);
        this.setVisible(true);
        this.repaint();
   }

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
            Object obj=e.nextElement();
            actPat=(Pattern)obj;
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
            rows[i][2]=String.valueOf(actRule.hits);
            rows[i][3]=String.valueOf(actRule.misses);
            rows[i][4]=String.valueOf(actRule.rating);
            
            i++;
        } // rof Enum e
        
        
        
        return new JTable(rows,columns);
        
    }
    
    private static JTable pats2jTable(Vector patterns) {
        Pattern actRule;
        String[] actPattern;
        
        String columns[]={"Pattern","Classification string"};
        String rows[][]=new String[patterns.size()][2];
        int i=0;
        for (Enumeration e=patterns.elements();e.hasMoreElements();) {
            actRule=(Pattern)e.nextElement();
            actPattern=actRule.pattern;
            String patString="";
            for (int j=0;j<actRule.length;j++) { // Zielpos markieren
                patString+=actPattern[j]+" ";  
            } // rof
            if(patString.length() > 0)
                patString=patString.substring(0,patString.length()-1);
            
            rows[i][0]=patString;
            rows[i][1]=actRule.goalClass;
            
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
        DefaultTableModel model = new DefaultTableModel(rows,columns);
        TableSorter sorter=new TableSorter(model);
        JTable returnTable= new JTable(sorter);
        sorter.setTableHeader(returnTable.getTableHeader());
        returnTable.getColumnModel().getColumn(0).setWidth(50);
        return returnTable;
    }
    
    // extrPats Buttons
    void extrPatsClearButton_actionPerformed(ActionEvent e) {
        extrPats=new Vector();
        extrPatsTable=pats2jTable(extrPats);
        extrPatsScrollPane.getViewport().add(extrPatsTable, null);
        this.setVisible(true);
        this.repaint();
    }
    void extrPatsAddFileButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find NE pattern file...", null,FileSelector.OPEN_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            loadNEsFrom(filename);
        }
    }
    /**
	 * @param filename
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadNEsFrom(String filename) throws IOException, FileNotFoundException {
		Vector newExtrPats=new MatcherNam(null).loadPatterns(filename);
		extrPats=vecUnite(extrPats,newExtrPats);
		extrPatsTable=pats2jTable(extrPats);
		extrPatsScrollPane.getViewport().add(extrPatsTable, null);
		extrPatsFileNamePane.setText(filename);
		this.setVisible(true);
		this.repaint();
	}
	void extrPatsAddButton_actionPerformed(ActionEvent e) throws IOException {
        String newPattern=extrPatsAddPatField.getText();
        String newGoal=extrPatsAddClassLField.getText();
        
        
        StringTokenizer newPatPats=new StringTokenizer(newPattern," ");
        int i=0;
        String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
        while (newPatPats.hasMoreTokens()) {
            String dummy=newPatPats.nextToken();
            tempPats[i]=dummy;
            i++;
        } //ehliw
        Pattern newPat=new Pattern(newGoal,i,-1,new String[i]);
        for(int j=0;j<newPat.length;j++) {newPat.pattern[j]=tempPats[j];}
        newPat.hits=0;
        newPat.misses=0;
        newPat.rating=0.0;
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
            StringTokenizer delPats=new StringTokenizer(delItem," ");
            int gC=-1, i=0;
            String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
            while (delPats.hasMoreTokens()) {
                String dummy=delPats.nextToken();
                tempPats[i]=dummy;
                i++;
            } //ehliw
            Pattern delPat=null;
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
    	File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Save patterns to...", null,FileSelector.SAVE_DIALOG);
    	if(f != null) {
    		String filename=f.getAbsolutePath();
    		MatcherNam writer= new MatcherNam(null);
    		writer.patterns=extrPats;
    		writer.saveFile(filename);
    	}
    }
    
    // classRule Buttons
    void classRulesClearButton_actionPerformed(ActionEvent e) {
        classRules=new Vector();
        classRulesTable=rules2jTable(classRules);
        classRulesScrollPane.getViewport().add(classRulesTable, null);
        this.setVisible(true);
        this.repaint();
    }
    void classRulesAddFileButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find pattern file...", null,FileSelector.OPEN_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            loadPatternFrom(filename);
        }
    }
    /**
	 * @param filename
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadPatternFrom(String filename) throws IOException, FileNotFoundException {
		classRulesFileNamePane.setText(filename);
		MatcherNam loader=new MatcherNam(null);
		Vector newClassRules=loader.loadPatterns(filename);
		classRules=vecUnite(classRules,newClassRules);
		classRulesTable=rules2jTable(classRules);
		classRulesScrollPane.getViewport().add(classRulesTable, null);
		classRulesFileNamePane.setText(filename);
		this.setVisible(true);
		this.repaint();
	}
	void classRulesAddButton_actionPerformed(ActionEvent e) throws IOException {
        String newPattern=classRulesAddLeftField.getText();
        String newGoal=classRulesAddGoalField.getText();
        
        
        StringTokenizer newPatPats=new StringTokenizer(newPattern," ");
        int gC=0, i=0;
        String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
        while (newPatPats.hasMoreTokens()) {
            String dummy=newPatPats.nextToken();
            if (dummy.endsWith("*")) {gC=i;dummy=dummy.substring(0,dummy.length()-1);}
            tempPats[i]=dummy;
            i++;
        } //ehliw
        Pattern newPat=new Pattern(Integer.toString(gC),i,Integer.parseInt(newGoal),new String[i]);
        for(int j=0;j<newPat.length;j++) {newPat.pattern[j]=tempPats[j];}
        newPat.hits=0;
        newPat.misses=0;
        newPat.rating=0.0;
        Vector newClassRules=new Vector();
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
            StringTokenizer delPats=new StringTokenizer(delItem," ");
            int gC=-1, i=0;
            String[] tempPats=new String[100]; // sollte reichen :) (unsauber)
            while (delPats.hasMoreTokens()) {
                String dummy=delPats.nextToken();
                if (dummy.endsWith("*")) {gC=i;dummy=dummy.substring(0,dummy.length()-1);}
                tempPats[i]=dummy;
                i++;
            } //ehliw
            Pattern delPat=null;
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
    
        
    
    // InItemsBack Buttons
    
    void inItemBackClearButton_actionPerformed(ActionEvent e) {
        inItemsBackNameTable=new NameTable();
        inItemBackTable=nameTable2jTable(inItemsBackNameTable);
        inItemBackScrollPane.getViewport().add(inItemBackTable, null);
        this.setVisible(true);
        this.repaint();
    }
    void inItemsBackAddFileButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find known names and titles...", null,FileSelector.OPEN_DIALOG);
        if(f != null) {
            String filename=f.getAbsolutePath();
            loadNamesFrom(filename);
        }  
    }
    /**
	 * @param filename
	 * @throws IOException
	 */
	private void loadNamesFrom(String filename) throws IOException {
		inItemsBackLoadField.setText(filename);
		NameTable loader=NameTable.loadFromFile(filename);
		inItemsBackNameTable.putAll(loader);
		inItemBackTable=nameTable2jTable(inItemsBackNameTable);
		inItemBackScrollPane.getViewport().add(inItemBackTable, null);
		this.setVisible(true);
	}
	void inItemBackAddButton_actionPerformed(ActionEvent e) throws IOException {
        String newItem=inItemBackAddItemField.getText();
        String newClass=inItemBackAddClassField.getText();
        NameTable adder=new NameTable();
        adder.put(newItem, newClass);
        inItemsBackNameTable.putAll(adder);
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
            inItemsBackNameTable.remove(delItem);
        } //rof
        
        inItemBackTable=nameTable2jTable(inItemsBackNameTable);
        inItemBackScrollPane.getViewport().add(inItemBackTable, null);
        this.setVisible(true);
        this.repaint();
    }
    
    
    
    
        
    // Parameter Actions
    void paraDefaultButton_actionPerformed(ActionEvent e) throws IOException {
        numOfThreadField.setText("10");
        paraThreshPane.setText("0.1");
        paraVerNrField.setText("30");
        
        this.setVisible(true);
        this.repaint();
        
    }
    
    
    
    
    
    void fileConfSaveButton_actionPerformed(ActionEvent e) throws IOException {
        File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Save configuration...", 
                getConfigFileFilter(),FileSelector.SAVE_DIALOG);
        if(f != null) {
            Config c=getConfigFromGui();
            c.updateConfigFile(f.getAbsolutePath());
        }
        
    }
    public Config getConfigFromGui() {
        Config c=new Config();
        getDBConfigPanelAkt().saveToConfig(c,"AKT");
        getDBConfigPanelWs().saveToConfig(c,"WS");
        getBaseTaggerPanel().addYourConfig(c);
        c.set("OPTION.ACCEPTITEM",paraThreshPane.getText());
        c.set("OPTION.STARTNO",((Integer)startNoSpiner.getValue()).toString());
        c.set("OPTION.ENDNO",((Integer)endnoSpinner.getValue()).toString());
        c.set("OPTION.NUMOFTHREADS",numOfThreadField.getText());
        c.set("OPTION.CANDIDATESNO",paraVerNrField.getText());
        c.set("OPTION.VERSION",getVersionTf().getText());
        c.set("OPTION.NERECOG",Boolean.toString(runNECb.isSelected()));
        c.set("IN.KNOWLEDGE",inItemsBackLoadField.getText());
        c.set("IN.PATFILENE",extrPatsFileNamePane.getText());
        c.set("IN.PATFILE",classRulesFileNamePane.getText());
        c.set("IN.REGEXP",tagRegexpLoadPane.getText());
        c.set("IN.CLASSNAMES",tagCodeLoadPane.getText());
        c.set("OUT.ITEMSFOUND",fileOutNewItemsPane.getText());
        c.set("OUT.CONTEXT",fileOutRuleContextPane.getText());
        c.set("OUT.MAYBE",fileOutMaybeItemsPane.getText());
        c.set("OUT.COMPLEXNAMES",fileOutExtrPatsPane.getText());
        c.set("OUT.LOGFILE",fileOutLogPane.getText());
        return c;
    }
    void fileConfLoadButton_actionPerformed(ActionEvent e) throws IOException {
    	File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Open configuration...", 
    			getConfigFileFilter(),FileSelector.OPEN_DIALOG);
    	if(f != null) {
    		Config cfg = new Config(f);
    		paraThreshPane.setText(cfg.getString("OPTION.ACCEPTITEM","0.9"));
    		startNoSpiner.setValue(new Integer(cfg.getInteger("OPTION.STARTNO",1)));
    		endnoSpinner.setValue(new Integer(cfg.getInteger("OPTION.ENDNO",-1)));
    		numOfThreadField.setText(cfg.getString("OPTION.NUMOFTHREADS","10"));
    		paraVerNrField.setText(cfg.getString("OPTION.CANDIDATESNO","30"));
    		getVersionTf().setText(cfg.getString("OPTION.VERSION","NameRec 1.1neu"));
            runNECb.setSelected(cfg.getBoolean("OPTION.NERECOG",true));
            samplesSpinner.setValue(new Integer(cfg.getInteger("OPTION.SAMPLES",100)));
    		
    		getBaseTaggerPanel().loadFromConfig(cfg);
    		getDBConfigPanelAkt().loadFromConfig(cfg,"AKT");
    		getDBConfigPanelWs().loadFromConfig(cfg,"WS");
    		loadNamesFrom(cfg.getString("IN.KNOWLEDGE","wissenAkt.txt"));
    		loadNEsFrom(cfg.getString("IN.PATFILENE","patPers.txt"));
    		loadPatternFrom(cfg.getString("IN.PATFILE","pats2.txt"));
    		loadRegexpFrom(cfg.getString("IN.REGEXP","regexps.txt"));
    		loadKlassnamesFrom(cfg.getString("IN.CLASSNAMES","klassNames.txt"));
    		
    		String itemsFound = cfg.getString("OUT.ITEMSFOUND","itemsFound.txt");
    		fileOutNewItemsPane.setText(itemsFound);
    		fileOutNewItemCheckbox.setSelected(true);
    		String context = cfg.getString("OUT.CONTEXT","contexts.txt");
    		fileOutRuleContextPane.setText(context);
    		fileOutRuleContextCheckbox.setSelected(true);
    		String maybe = cfg.getString("OUT.MAYBE","maybe.txt");
    		fileOutMaybeItemsPane.setText(maybe);
    		fileOutMaybeItemCheckbox.setSelected(true);
    		String nes = cfg.getString("OUT.COMPLEXNAMES","NEs.txt");
    		fileOutExtrPatsPane.setText(nes);
    		fileOutExtrPatsCheckbox.setSelected(true);
    		String logfile = cfg.getString("OUT.LOGFILE","log.txt");
            fileOutLogPane.setText(logfile);
            fileOutLogCheckbox.setSelected(true);
    	}
    }
    
    
    
    /**
	 * @return
	 */
	private FileFilter getConfigFileFilter() {
		return new FileFilter(){

			public boolean accept(File f) {
				return f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".cfg");
			}

			public String getDescription() {
				return "Config (*.cfg)";
			}
			
		};
	}
    
    
    
    

    /**
     * This method initializes versionTf	
     * 	
     * @return javax.swing.JTextField	
     */    
    private JTextField getVersionTf() {
    	if (versionTf == null) {
    		versionTf = new JTextField();
    		versionTf.setBounds(502, 76, 115, 19);
    		versionTf.setText("NameRec1.1neu");
    	}
    	return versionTf;
    }
    /**
     * This method initializes baseTaggerPanel	
     * 	
     * @return namerec.gui.BaseTaggerPanel	
     */    
    private BaseTaggerPanel getBaseTaggerPanel() {
    	if (baseTaggerPanel == null) {
    		baseTaggerPanel = new BaseTaggerPanel();
    		baseTaggerPanel.setBounds(13, 227, 584, 25);
    	}
    	return baseTaggerPanel;
    }
    
    /**
     * This method initializes DBConfigPanelAkt	
     * 	
     * @return namerec.gui.DBConfigPanel	
     */    
    private DBConfigPanel getDBConfigPanelAkt() {
    	if (DBConfigPanelAkt == null) {
    		DBConfigPanelAkt = new DBConfigPanel();
    		DBConfigPanelAkt.setBounds(14, 267, 367, 195);
    		DBConfigPanelAkt.setTitle("DB new");
            DBConfigPanelAkt.showWriteBackCb(true);
    	}
    	return DBConfigPanelAkt;
    }
    /**
     * This method initializes DBConfigPanelWs	
     * 	
     * @return namerec.gui.DBConfigPanel	
     */    
    private DBConfigPanel getDBConfigPanelWs() {
    	if (DBConfigPanelWs == null) {
    		DBConfigPanelWs = new DBConfigPanel();
    		DBConfigPanelWs.setBounds(402, 269, 367, 195);
    		DBConfigPanelWs.setTitle("DB verification");
    	}
    	return DBConfigPanelWs;
    }
    /**
     * This method initializes startNoSpiner	
     * 	
     * @return javax.swing.JSpinner	
     */    
    private JSpinner getStartNoSpiner() {
    	if (startNoSpiner == null) {
    		startNoSpiner = new JSpinner();
    		startNoSpiner.setBounds(503, 111, 54, 23);
    		startNoSpiner.setValue(new Integer(1));
            ((SpinnerNumberModel)startNoSpiner.getModel()).setMinimum(new Integer(0));
    	}
    	return startNoSpiner;
    }
    /**
     * This method initializes endnoSpinner	
     * 	
     * @return javax.swing.JSpinner	
     */    
    private JSpinner getEndnoSpinner() {
    	if (endnoSpinner == null) {
    		endnoSpinner = new JSpinner();
    		endnoSpinner.setBounds(505, 144, 61, 22);
    		endnoSpinner.setValue(new Integer(-1));
    	}
    	return endnoSpinner;
    }
    /**
     * This method initializes samplesSpinner	
     * 	
     * @return javax.swing.JSpinner	
     */    
    private JSpinner getSamplesSpinner() {
    	if (samplesSpinner == null) {
    		samplesSpinner = new JSpinner();
    		samplesSpinner.setBounds(202, 191, 55, 25);
    	}
    	return samplesSpinner;
    }
    /**
     * This method initializes runnerPanel	
     * 	
     * @return javax.swing.JPanel	
     */    
    private JPanel getRunnerPanel() {
    	if (runnerPanel == null) {
    		jLabel5 = new JLabel();
    		jLabel5.setBounds(12, 62, 105, 26);
    		jLabel5.setText("Use fileinput:");
    		jLabel4 = new JLabel();
    		jLabel4.setBounds(11, 98, 105, 31);
    		jLabel4.setText("Use sentence:");
    		runnerPanel = new JPanel();
    		runnerPanel.setLayout(null);
    		runnerPanel.add(getScrollPane(), null);
    		runnerPanel.add(getStartButton(), null);
    		runnerPanel.add(jLabel4, null);
    		runnerPanel.add(getSentenceTf(), null);
    		runnerPanel.add(getSingleSentenceButton(), null);
    		runnerPanel.add(jLabel5, null);
    		runnerPanel.add(getFileSourceTf(), null);
    		runnerPanel.add(getFileSourceButton(), null);
    		runnerPanel.add(getRunFileDatasourceButton(), null);
    		runnerPanel.add(getRunNECb(), null);
    		runnerPanel.add(getJButton(), null);
    	}
    	return runnerPanel;
    }
    private JScrollPane getScrollPane() {
        if(scrollPane == null) {
            scrollPane=new JScrollPane(getTextArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);            
            scrollPane.setBounds(30, 166, 602, 329);
        }
        return scrollPane;
    }
    /**
     * This method initializes textArea	
     * 	
     * @return javax.swing.JTextArea	
     */    
    private JTextArea getTextArea() {
    	if (textArea == null) {
    		textArea = new JTextArea();
            textArea.setEditable(false);
    		//textArea.setPreferredSize(new java.awt.Dimension(500,400));
    		textArea.setBounds(17, 5, 625, 400);
            textArea.setDocument(new CyclicDocument(1000000));
    	}
    	return textArea;
    }
    /**
     * This method initializes startButton	
     * 	
     * @return javax.swing.JButton	
     */    
    private JButton getStartButton() {
    	if (startButton == null) {
    		startButton = new JButton();
    		startButton.setBounds(640, 17, 134, 26);
    		startButton.setText("Run DB");
            startButton.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {    
                    Config cfg=getConfigFromGui();
                    try {
                        redirectSysOut();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    doTheCalculation(cfg, null);
                }
            });
    	}
    	return startButton;
    }
    /**
     * This method initializes sentenceTf	
     * 	
     * @return javax.swing.JTextField	
     */    
    private JTextField getSentenceTf() {
    	if (sentenceTf == null) {
    		sentenceTf = new JTextField();
    		sentenceTf.setBounds(127, 98, 503, 31);
    	}
    	return sentenceTf;
    }
    /**
     * This method initializes singleSentenceButton	
     * 	
     * @return javax.swing.JButton	
     */    
    private JButton getSingleSentenceButton() {
    	if (singleSentenceButton == null) {
    		singleSentenceButton = new JButton();
    		singleSentenceButton.setBounds(640, 99, 136, 26);
    		singleSentenceButton.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
    			    Config cfg=getConfigFromGui();
                    try {
                        redirectSysOut();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
    			    cfg.set("OPTION.NUMOFTHREADS","1");
                    SatzDatasource ds=new SatzDatasource() {
                        boolean isDone=false;
                        public String getNextSentence() {
                            if(!isDone) {
                                isDone=true;
                                return sentenceTf.getText();
                            }
                            return "END";
                        }                        
                    };
                    doTheCalculation(cfg, ds);
                }
    		});
    		singleSentenceButton.setText("Run sentence");
    	}
    	return singleSentenceButton;
    }
    private void doTheCalculation(final Config cfg, final SatzDatasource ds) {
        RecognizerPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker sw=new SwingWorker() {
            public Object construct() {
                Recognizer rec=null;
                try {
                    rec = new Recognizer(cfg,ds);
                    enableStopButton(Thread.currentThread());
                    rec.doTheRecogBoogie();
                    if(cfg.getBoolean("OPTION.NERECOG",false))
                        rec.runNERecognition();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return rec;
            }
            public void finished() {
                RecognizerPanel.this.setCursor(Cursor.getDefaultCursor());
                getJButton().setEnabled(false);
            }
        };
        sw.start();
    }
    protected void enableStopButton(final Thread t) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getJButton().setEnabled(true);
                getJButton().addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        t.interrupt();
                        getJButton().setEnabled(false);
                        RecognizerPanel.this.setCursor(Cursor.getDefaultCursor());
                    }
                });
            }
        });
    }
    protected void redirectSysOut() throws IOException {
        final BufferedWriter bw=new BufferedWriter(new FileWriter(fileOutLogPane.getText(),true));
        bw.write("########### new start: "+new Date());
        PrintStream ps =  new PrintStream(System.out) {
            public void println(String x) {
                getTextArea().append(x);
                getTextArea().append("\n");
                try {
                    bw.write(x);
                    bw.write("\n");
                } catch (IOException e) {
                }
                getTextArea().setCaretPosition(getTextArea().getText().length() - 1);
            }
            public void print(String x) {
                getTextArea().append(x);
                try {
                    bw.write(x);
                } catch (IOException e) {
                }
                getTextArea().setCaretPosition(getTextArea().getText().length() - 1);
            }
        };
        System.setOut(ps);
        System.setErr(ps);
    }
    /**
     * This method initializes fileSourceTf	
     * 	
     * @return javax.swing.JTextField	
     */    
    private JTextField getFileSourceTf() {
    	if (fileSourceTf == null) {
    		fileSourceTf = new JTextField();
    		fileSourceTf.setBounds(183, 63, 444, 26);
    	}
    	return fileSourceTf;
    }
    /**
     * This method initializes fileSourceButton	
     * 	
     * @return javax.swing.JButton	
     */    
    private JButton getFileSourceButton() {
    	if (fileSourceButton == null) {
    		fileSourceButton = new JButton();
    		fileSourceButton.setBounds(125, 60, 47, 26);
    		fileSourceButton.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    File f=FileSelector.getUserSelectedFile(RecognizerPanel.this,"Find pattern file...", null,FileSelector.OPEN_DIALOG);
                    if(f != null) {
                        String filename=f.getAbsolutePath();
                        fileSourceTf.setText(filename);
                    }
                }
    		});
    		fileSourceButton.setText("...");
    	}
    	return fileSourceButton;
    }
    /**
     * This method initializes runFileDatasourceButton	
     * 	
     * @return javax.swing.JButton	
     */    
    private JButton getRunFileDatasourceButton() {
    	if (runFileDatasourceButton == null) {
    		runFileDatasourceButton = new JButton();
    		runFileDatasourceButton.setBounds(641, 62, 131, 26);
    		runFileDatasourceButton.addActionListener(new java.awt.event.ActionListener() { 
    			public void actionPerformed(java.awt.event.ActionEvent e) {    
                    Config cfg=getConfigFromGui();
                    try {
                        redirectSysOut();
                        SatzDatasource ds=new FileDataSource(fileSourceTf.getText());
                        doTheCalculation(cfg, ds);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
    		});
    		runFileDatasourceButton.setText("Run file");
    	}
    	return runFileDatasourceButton;
    }
    /**
     * This method initializes runNECb	
     * 	
     * @return javax.swing.JCheckBox	
     */    
    private JCheckBox getRunNECb() {
    	if (runNECb == null) {
    		runNECb = new JCheckBox();
    		runNECb.setBounds(122, 142, 251, 21);
    		runNECb.setText("run NE recognition afterwards");
    	}
    	return runNECb;
    }
    
    
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane() {
                public String getToolTipText() {
                    return getKlassTagPanel().getToolTipText();
                }         
            };
            jScrollPane.setViewportView(getKlassTagPanel());
			jScrollPane.setBounds(110, 423, 219, 35);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes klassTagPanel	
	 * 	
	 * @return namerec.gui.KlassTagPanel	
	 */    
	private KlassTagPanel getKlassTagPanel() {
		if (klassTagPanel == null) {
			klassTagPanel = new KlassTagPanel();
			klassTagPanel.setBounds(130, 400, 685, 40);
		}
		return klassTagPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("Stop");
            jButton.setEnabled(false);
			jButton.setBounds(639, 274, 95, 34);
		}
		return jButton;
	}
   }


