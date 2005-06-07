/*
 * Created on 30.05.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec.gui;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import de.wortschatz.WortschatzModul;
import de.wortschatz.WortschatzTool;

import namerec.Recognizer;
import namerec.util.Config;
import namerec.util.FileSelector;
import namerec.util.SwingWorker;

import java.awt.GridBagConstraints;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author sdienst
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RecognizerPanel extends WortschatzModul {

    private JTabbedPane jTabbedPane = null;

    private ConfigPanel configPanel = null;

    private FileConfigPanel fileConfigPanel = null;

    private JPanel jPanel = null;

    private JButton calcButton = null;

    private JPanel jPanel1 = null;

    private JPanel jPanel2 = null;

    private JButton loadButton = null;

    private JButton saveButton = null;

    private JPanel dbConfig = null;

    private DBConfigPanel aktDBPanel = null;

    private DBConfigPanel wsDBPanel = null;

    /**
     * This is the default constructor
     */
    public RecognizerPanel(WortschatzTool wTool) {
        super(wTool);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
        this.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.addTab("Preferences", null, getConfigPanel(), null);
            jTabbedPane.addTab("Files", null, getFileConfigPanel(), null);
            jTabbedPane.addTab("DB", null, getDbConfig(), null);
        }
        return jTabbedPane;
    }

    /**
     * This method initializes configPanel
     * 
     * @return namerec.gui.ConfigPanel
     */
    private ConfigPanel getConfigPanel() {
        if (configPanel == null) {
            configPanel = new ConfigPanel();
        }
        return configPanel;
    }

    /**
     * This method initializes fileConfigPanel
     * 
     * @return namerec.gui.FileConfigPanel
     */
    private FileConfigPanel getFileConfigPanel() {
        if (fileConfigPanel == null) {
            fileConfigPanel = new FileConfigPanel();
        }
        return fileConfigPanel;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            gridBagConstraints23.insets = new java.awt.Insets(10, 10, 10, 10);
            jPanel.add(getCalcButton(), gridBagConstraints23);
        }
        return jPanel;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCalcButton() {
        if (calcButton == null) {
            calcButton = new JButton();
            calcButton.setText("Run");
            calcButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    final JDialog dialog = new JDialog((JFrame) null,
                            "Running...", true);
                    dialog
                            .setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                    JProgressBar progress = new JProgressBar(0, 100);
                    progress.setIndeterminate(true);
                    dialog.getContentPane().add(progress);
                    SwingWorker sw = new SwingWorker() {
                        public Object construct() {
                            try {
                                Config cfg = getConfig();
                                Recognizer rec = new Recognizer(cfg);
                                rec.doTheRecogBoogie();
                                if (cfg.getBoolean("OPTION.NERECOG", false) == true)
                                    rec.runNERecognition();
                            } catch (Exception e) {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream(
                                        1000);
                                PrintStream ps = new PrintStream(bos);
                                e.printStackTrace(ps);
                                JOptionPane.showMessageDialog(
                                        RecognizerPanel.this, bos.toString(),
                                        "Error on running",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            return null;
                        }

                        public void finished() {
                            dialog.dispose();
                        }
                    };
                    sw.start();
                    dialog.pack();
                    dialog.setVisible(true);
                }
            });
        }
        return calcButton;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(new BorderLayout());
            jPanel1.add(getJPanel(), java.awt.BorderLayout.EAST);
            jPanel1.add(getJPanel2(), java.awt.BorderLayout.WEST);
        }
        return jPanel1;
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2() {
        if (jPanel2 == null) {
            jPanel2 = new JPanel();
            jPanel2.add(getLoadButton(), null);
            jPanel2.add(getSaveButton(), null);
        }
        return jPanel2;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load config");
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File f = FileSelector.getUserSelectedFile(
                            RecognizerPanel.this, "Load configuration",
                            getConfigFileFilter(), FileSelector.OPEN_DIALOG);
                    if (f != null) {
                        Config cfg;
                        try {
                            cfg = new Config(f);
                            setConfig(cfg);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(RecognizerPanel.this,
                                    "Error: " + e1.getMessage(),
                                    "Error on loading configfile",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        }
        return loadButton;
    }

    protected void setConfig(Config cfg) {
        getConfigPanel().loadFromConfig(cfg);
        getFileConfigPanel().loadFromConfig(cfg);
        getAktDBPanel().loadFromConfig(cfg, "AKT");
        getWSDBPanel().loadFromConfig(cfg, "WS");
    }

    private FileFilter getConfigFileFilter() {
        return new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory()
                        || (f.isFile() && f.getName().toLowerCase().endsWith(
                                ".cfg"));
            }

            public String getDescription() {
                return "configfiles (*.cfg)";
            }

        };
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setText("Save config");
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    File f = FileSelector.getUserSelectedFile(
                            RecognizerPanel.this, "Save configuration",
                            getConfigFileFilter(), FileSelector.SAVE_DIALOG);
                    if (f != null) {
                        try {
                            Config cfg = getConfig();
                            cfg.saveToFile(f);
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(RecognizerPanel.this,
                                    "Error: " + e1.getMessage(),
                                    "Error on saving configfile",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
            });
        }
        return saveButton;
    }

    protected Config getConfig() {
        Config c = new Config();
        getConfigPanel().saveToConfig(c);
        getFileConfigPanel().saveToConfig(c);
        getAktDBPanel().saveToConfig(c, "AKT");
        getAktDBPanel().saveToConfig(c, "AKT");
        return c;
    }

    /**
     * This method initializes jPanel3 dbConfig.add(getDBConfigPanel1(), null);
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDbConfig() {
        if (dbConfig == null) {
            dbConfig = new JPanel();
            dbConfig.setLayout(new BoxLayout(dbConfig, BoxLayout.Y_AXIS));
            dbConfig.add(getAktDBPanel());
            dbConfig.add(getWSDBPanel());
        }
        return dbConfig;
    }

    /**
     * This method initializes DBConfigPanel
     * 
     * @return namerec.gui.DBConfigPanel
     */
    private DBConfigPanel getAktDBPanel() {
        if (aktDBPanel == null) {
            aktDBPanel = new DBConfigPanel();
            aktDBPanel.setTitle("WDT_Aktuell");
        }
        return aktDBPanel;
    }

    /**
     * This method initializes DBConfigPanel1
     * 
     * @return namerec.gui.DBConfigPanel
     */
    private DBConfigPanel getWSDBPanel() {
        if (wsDBPanel == null) {
            wsDBPanel = new DBConfigPanel();
            wsDBPanel.setTitle("Wortschatz");
        }
        return wsDBPanel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (Exception e) {
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new RecognizerPanel(null));
        frame.pack();
        frame.setVisible(true);
    }

    public String getToolTip() {return "extract names and titles from sentences";};
    public void activated() {revalidate();repaint();};
    public JPanel getModulePanel() {return this;};
    public char getMnemonic() {return 'n';};
    public String getName() {return "Namerec";};
    public Icon getIcon() { return this.createImageIcon("Pen.jpg");}


}
