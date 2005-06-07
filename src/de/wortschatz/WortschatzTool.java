package de.wortschatz;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

/**
 * Wortschatz Tool - eine Sammlung, computerlinguistischer Werkzeuge
 * @author Stefan Bordag
 * @version 0.2
 */

public class WortschatzTool extends JPanel
{
  private static final String[] modules = {
  //  "com.bordag.klf.Klassifikator",
    "com.bordag.wtool.WTool",
    "com.biemann.pendel.PendelPanel",
    "com.biemann.pretree.PretreePanel",
    "namerec.gui.RecognizerPanel"
  };
  private java.util.List modulesVector = null;

  private static JFrame frame = null;
  private JWindow splashScreen = null;
  private JMenuBar mainMenu = null;

  private ToggleButtonToolBar mainToolBar = null;
  private ButtonGroup chooserButtonGroup = null;

  private ModuleLoaderThread moduleLoader = null;
  private WortschatzModul currentModule = null;

  private JPanel modulePanel = null;

  protected final static Dimension preferredSize = new Dimension(800,600);
  protected final static Dimension moduleSize = new Dimension(800,550);
  protected final static Rectangle modulePanelBounds = new Rectangle(0,50,800,550);
  protected final static Rectangle toolBarBounds = new Rectangle(0, 0, 800, 50);

  Frame parentFrame = null;

  public WortschatzTool(Frame parentFrame)
  {
    this.parentFrame = parentFrame;
    createSplashScreen();

	  SwingUtilities.invokeLater(new Runnable()
    {
	    public void run() { splashScreen.show(); }
    });

    initProgram();
    preloadFirstModule();

    // Show the demo and take down the splash screen. Note that
    // we again must do this on the GUI thread using invokeLater.
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run() {showMainProgram();hideSplash();}
    });

    // Start loading the rest of the demo in the background
    ModuleLoaderThread moduleLoader = new ModuleLoaderThread(this);
    moduleLoader.start();

  }

  /**
   * Show the splash screen while the rest of the program loads
   */
  public void createSplashScreen()
  {
	  JLabel splashLabel = new JLabel(createImageIcon("Splash2.jpg"));

    splashScreen = new JWindow();
    splashScreen.getContentPane().add(splashLabel);
    splashScreen.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    splashScreen.setLocation(screenSize.width/2 - splashScreen.getSize().width/2,
    screenSize.height/2 - splashScreen.getSize().height/2);
  }
  /**
   * destroy splash screen
   */
  public void hideSplash()
  {
    this.splashScreen.setVisible(false);
    this.splashScreen = null;
  }

  /**
   * Creates an icon from an image
   */
  public ImageIcon createImageIcon(String filename)
  {
    String path = "" + filename;
    return new ImageIcon(filename);
  }

  /**
   * Initializes components of the main program
   */
  private void initProgram()
  {
    this.modulesVector = new Vector();

    this.mainToolBar = new ToggleButtonToolBar();
    this.chooserButtonGroup = new ButtonGroup();

    this.modulePanel = new JPanel();
    this.modulePanel.setBounds(this.modulePanelBounds);
    this.modulePanel.setPreferredSize(this.preferredSize);
    this.modulePanel.setLayout(null);
    this.modulePanel.setBackground(Color.white);

    this.frame.getContentPane().setLayout(null);
    this.mainToolBar.setToolTipText("Choose the module you wish to use");
    this.mainToolBar.setBounds(this.toolBarBounds);
    this.frame.getContentPane().add(mainToolBar);
    this.frame.getContentPane().add(this.modulePanel);
  }

  /**
   * Loads first module
   */
  protected void preloadFirstModule()
  {
    WortschatzModul modul = addModule(new com.bordag.klf.Klassifikator(this));
    //WortschatzModul modul = addModule(new com.bordag.wtool.WTool(this));
    setModule(modul);
  }

  private void loadModules()
  {
	  for(int i = 0; i < this.modules.length ; i++ )
    {
      loadModule(this.modules[i]);
    }
  }

  public Frame getParentFrame()
  {
    return this.frame;
  }

  /**
   * Loads a demo from a classname
   */
  void loadModule(String classname)
  {
    //  setStatus(getString("Status.loading") + getString(classname + ".name"));
  	WortschatzModul module = null;
  	try
    {
	    Class moduleClass = Class.forName(classname);
	    Constructor moduleConstructor = moduleClass.getConstructor(new Class[]{WortschatzTool.class});
	    module = (WortschatzModul) moduleConstructor.newInstance(new Object[]{this});
	    addModule(module);
	  }
    catch (Exception ex)
    {
      ex.printStackTrace();
	    System.out.println("Error occurred loading module: " + classname);
  	}
  }

  /**
   * Add a module to the toolbar
   */
  public WortschatzModul addModule(WortschatzModul module)
  {
	  modulesVector.add(module);
	  // do the following on the gui thread
	  SwingUtilities.invokeLater(new WortschatzRunnable(this, module)
    {
      public void run()
      {
	  	  SwitchToModuleAction action = new SwitchToModuleAction(wTool, (WortschatzModul) obj);
  	  	JToggleButton tb = wTool.mainToolBar.addToggleButton(action);
    		wTool.chooserButtonGroup.add(tb);
    		if(wTool.chooserButtonGroup.getSelection() == null)
        {
		      tb.setSelected(true);
    		}
    		tb.setText(null);
		    tb.setToolTipText(((WortschatzModul)obj).getToolTip());
	    }
  	});
	  return module;
  }

  /**
   * Sets the current module
   */
  public void setModule(WortschatzModul module)
  {
	  this.currentModule = module;

    this.modulePanel.removeAll();
    JPanel mod = module.getModulePanel();
    mod.setSize(this.moduleSize);
	  this.modulePanel.add(mod, null);
    module.activated();
  }

  /**
   * Bring up the SwingSet2 demo by showing the frame (only
   * applicable if coming up as an application, not an applet);
  */
  public void showMainProgram()
  {
    this.frame.setTitle("Wortschatz Tool v0.3");
	  this.frame.getContentPane().add(this, BorderLayout.CENTER);
    this.frame.setSize(this.preferredSize);
	  this.frame.pack();
	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.frame.setSize(this.preferredSize);
    this.frame.setLocation(
		screenSize.width/2 - this.frame.getSize().width/2,
		screenSize.height/2 - this.frame.getSize().height/2);
	  this.frame.show();
	}

  /**
   * Create a frame for SwingSet2 to reside in if brought up
   * as an application.
   */
  public static JFrame createFrame()
  {
	  JFrame frame = new JFrame();
    frame.setSize(preferredSize);
	  WindowListener l = new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}};
  	frame.addWindowListener(l);
	  return frame;
  }

  public static void main(String[] args)
  {
      try {
          UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
       } catch (Exception e) {}
    frame = createFrame();
    WortschatzTool wTool = new WortschatzTool(frame);
  }





// ------------------------- helper classes --------------------





  /**
   * Loads modules in a thread
   */
  class ModuleLoaderThread extends Thread
  {
  	WortschatzTool program;

  	public ModuleLoaderThread(WortschatzTool wTool)
    {
	    this.program = wTool;
	  }

	  public void run()
    {
	    program.loadModules();
	  }
  }

  /**
   * This Action switches modules of the main program
   */
  public class SwitchToModuleAction extends AbstractAction
  {
	  WortschatzTool wTool;
  	WortschatzModul module;

  	public SwitchToModuleAction(WortschatzTool wTool, WortschatzModul module)
    {
	    super(module.getName(), module.getIcon());
	    this.wTool = wTool;
	    this.module = module;
	  }

  	public void actionPerformed(ActionEvent e)
    {
      wTool.setModule(module);
  	}
  }

  /**
   */
  class WortschatzRunnable implements Runnable
  {
  	protected WortschatzTool wTool;
  	protected Object obj;

  	public WortschatzRunnable(WortschatzTool wTool, Object obj)
    {
	    this.wTool = wTool;
	    this.obj = obj;
  	}

  	public void run()
    {
  	}
  }

  protected class ToggleButtonToolBar extends JToolBar
  {
    private Insets zeroInsets = new Insets(1,1,1,1);
	  public ToggleButtonToolBar()
    {
	    super();
	  }

	  JToggleButton addToggleButton(Action a)
    {
	    JToggleButton tb = new JToggleButton((String)a.getValue(Action.NAME),(Icon)a.getValue(Action.SMALL_ICON));
	    tb.setMargin(zeroInsets);
	    tb.setText(null);
	    tb.setEnabled(a.isEnabled());
	    tb.setToolTipText((String)a.getValue(Action.SHORT_DESCRIPTION));
	    tb.setAction(a);
	    add(tb);
	    return tb;
	  }
  }
}