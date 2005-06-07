package com.bordag.klf.ui;

// standard imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.Vector;
import java.util.Enumeration;

// app specific imports
import com.bordag.klf.agent.*;
import com.bordag.klf.util.*;

/**
 *  This is just a five-minute GUI for the Learning program.
 *  @author Stefan Bordag
 *  @date	13.05.2001
 **/
final public class GuiClient extends JPanel
{

	JPanel          contentPane = null;
	JPanel          leftPane = null;
	JPanel          optionsPane = null;
	JMenuBar        jMenuBar1 = new JMenuBar();
	JMenu           jMenuFile = new JMenu();
// -
	JMenuItem       jMenuFileNew = new JMenuItem();
	JMenuItem       jMenuFileLoad = new JMenuItem();
	JMenuItem       jMenuFileSave = new JMenuItem();
	JMenuItem       jMenuFileExit = new JMenuItem();
	JMenu           jMenuHelp = new JMenu();
	JMenuItem       jMenuHelpAbout = new JMenuItem();
	BorderLayout    borderLayout1 = new BorderLayout();

	JPanel          buttonPanel = new JPanel(new GridLayout(4,3));
	JButton[]       buttons = new JButton[8];
	String[]        buttonsText = {"Learn","Ask","Learn File","AskFile","Import from File","Export to File","Clear","Quit"};
	String[]        toolTips = {"Learn the data from commandline","Ask to determine the value for the word in the command line",
			         "Learn values from a file","Measure how much we would have answered correctly in this file",
	                 "Import previously learned data from file","Export learned data to file","Clear learned data","Bail outta here"};
	char[]          mnemonics = {'L','A','E','K','I','X','C','Q'};
	JTextField      inputField = new JTextField(50);
	JTextArea       infoField = new JTextArea(5,5);
	Learner         l = new Learner();
	Asker           a = new Asker();

	JTree           console = new JTree(l.getRoot());
	JScrollPane     sPane1 = new JScrollPane(console);
	JScrollPane     sPane2 = new JScrollPane(infoField);

	File            lastFile = null;

  Vector          commands = new Vector();
  int             commandCount = 0;

	/**
	 *  Constructor of the default frame
	 **/
	public GuiClient()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);


    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for ( int i = 0; i < fonts.length ; i++ )
    {
//      System.out.println("Font "+i+" = "+fonts[i]);
    }

		try
		{
			init();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Main function, just creates an instance of itself
	 **/
	public static void main(String[] args)
	{
 	  JFrame frame = new JFrame();
		frame.setSize(new Dimension(800, 550));
		frame.setTitle("Language item classification");
	  WindowListener l = new WindowAdapter() {public void windowClosing(WindowEvent e) {System.exit(0);}};
  	frame.addWindowListener(l);

    GuiClient client = new GuiClient();
	  frame.getContentPane().add(client, null);
	  frame.pack();
	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	  frame.setLocation(
  	  screenSize.width/2 - frame.getSize().width/2,
	    screenSize.height/2 - frame.getSize().height/2);
	  frame.show();
	}

	/**
	 *  Component initialization
	 **/
	private void init() throws Exception
	{
		//contentPane = (JPanel) this.getContentPane();
		//this.setSize(new Dimension(800, 550));
    contentPane = this;
		contentPane.setLayout(borderLayout1);
		optionsPane = new JPanel(new BorderLayout());

		leftPane = new JPanel(new BorderLayout());

		jMenuFile.setText("File");
// -
		jMenuFileNew.setText("Learn from File");
		jMenuFileNew.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
			   	learnFile();
		   }
		});

		jMenuFileSave.setText("Export");
		jMenuFileSave.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
				exportFile();
		   }
		});

		jMenuFileLoad.setText("Import");
		jMenuFileLoad.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
				importFile();
		   }
		});

		jMenuFileExit.setText("Exit");
		jMenuFileExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jMenuFileExit_actionPerformed(e);
			}
		});
		jMenuHelp.setText("Help");
		jMenuHelpAbout.setText("About");
		jMenuHelpAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jMenuHelpAbout_actionPerformed(e);
			}
		});
		inputField.addActionListener(new InputFieldListener());
    inputField.addKeyListener(new InputFieldKeyListener());
// -
		jMenuFile.add(jMenuFileNew);
		jMenuFile.add(jMenuFileLoad);
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileExit);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuBar1.add(jMenuFile);
		jMenuBar1.add(jMenuHelp);

		for(int i=0;i<8;i++)
		{
			buttons[i] = new JButton(buttonsText[i]);
			buttons[i].setToolTipText(toolTips[i]);
			buttons[i].setMnemonic(mnemonics[i]);
			buttonPanel.add(buttons[i]);
		}

		buttons[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				learn();
			}
		});

		buttons[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ask();
			}
		});

		buttons[2].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				learnFile();
			}
		});

		buttons[3].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				askFile();
			}
		});

		buttons[4].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				importFile();
			}
		});

		buttons[5].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				exportFile();
			}
		});

		buttons[6].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearData();
			}
		});

		buttons[7].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
////    this.add(jMenuBar1);
//		this.setJMenuBar(jMenuBar1);

		infoField.setEditable(false);
		leftPane.add(sPane2,BorderLayout.CENTER);
		leftPane.add(buttonPanel,BorderLayout.SOUTH);


		optionsPane.add(((Agent)l.getRoot()).getUI());

		infoField.addMouseListener(new MyMouseListener());

		contentPane.add(optionsPane,BorderLayout.EAST);
		contentPane.add(sPane1,BorderLayout.CENTER);
		contentPane.add(inputField,BorderLayout.SOUTH);

		contentPane.add(leftPane,BorderLayout.WEST);

		console.addTreeSelectionListener(new STreeListener());

	}

   	/**
	 *  Asks the program to add the given information to the data
	 **/
	protected void learn()
	{
		String text = inputField.getText();
		inputField.setText("");
		if (StringUtils.checkForm(text))
		{
			String splitted[] = StringUtils.split(text);
			l.learnToken(splitted[0],splitted[1]);

			// FIXME to be replaced by some way to update the tree
			// without assigning it as a whole
			console.setModel(new DefaultTreeModel(l.getRoot()));
		}
	}

	/**
	 *  Asks for data about a given word
	 **/
	protected void ask()
	{
		String answer = a.ask(inputField.getText(),l.getRoot()).toString();
		sysMsg(inputField.getText()+" : "+answer);
		inputField.setText("");
	}

	/**
	 *  Clears all learned data by simply overwriting the reference to
	 *  the learner with a new, empty instance
	 **/
	protected void clearData()
	{
		l = new Learner();
		console.setModel(new DefaultTreeModel(l.getRoot()));
	}

	/**
	 *  Determines how much in a given data would have been answered wrong
	 **/
	protected void askFile()
	{
		int right = 0;
		int undec = 0;
		int wrong = 0;

		JFileChooser fc = new JFileChooser(lastFile);
		fc.setApproveButtonText("ASK");
		SprachFileFilter myFilter = new SprachFileFilter();
		myFilter.addExtension("txt");
		myFilter.addExtension("dat");
		myFilter.addExtension("inp");
		myFilter.setDescription("Ascii input files");
		fc.addChoosableFileFilter(myFilter);
		int retVal = fc.showOpenDialog(this);
		if ( retVal == JFileChooser.APPROVE_OPTION )
		{
			Vector data = FileUtils.getDataFromFile(fc.getSelectedFile());
			lastFile = fc.getSelectedFile();
			if (data.size()==0)
			{
				sysMsg("File contained no data or was not readable.");
			}
			for ( Enumeration e = data.elements() ; e.hasMoreElements() ; )
			{
				String splitted[] = StringUtils.split((String)e.nextElement());
				AgentAnswer answer = a.ask(splitted[0],l.getRoot());
				if (answer.getAnswer().equals(splitted[1]))
				{
					right++;
				}
				else if (answer.getAnswer().equals(Learner.UNKNOWN))
				{
					undec++;
				}
				else
				{
					wrong++;
				}
			}
			double percent = (double)wrong/((double)(right+wrong+undec)/100);
			sysMsg("I answered "+right+" right, "+undec+" undecided and "+wrong+" wrong. "+'\n'+"-> "+percent+"% wrong");
		}
	}

	/**
	 *  Opens up a filechooser, which eventually pics a valid file and
	 *  feeds it to the learning funtion of Learner, line by line
	 *  to prevent loading big files completely into memory
	 **/
	protected void learnFile()
	{
		JFileChooser fc = new JFileChooser(lastFile);
		fc.setApproveButtonText("LEARN");
		SprachFileFilter myFilter = new SprachFileFilter();
		myFilter.addExtension("txt");
		myFilter.addExtension("dat");
		myFilter.addExtension("inp");
		myFilter.setDescription("Ascii input files");
		fc.addChoosableFileFilter(myFilter);
		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			Vector data = FileUtils.getDataFromFile(fc.getSelectedFile());
			lastFile = fc.getSelectedFile();
			if (data.size()==0)
			{
				sysMsg("File contained no data or was not readable.");
			}
			for(Enumeration e = data.elements();e.hasMoreElements();)
			{
				String splitted[] = StringUtils.split((String)e.nextElement());
				l.learnToken(splitted[0],splitted[1]);
			}
			console.setModel(new DefaultTreeModel(l.getRoot()));
		}
	}

	/**
	 *  Exports learned data with serializing to a binary file
	 **/
	private void exportFile()
	{
		JFileChooser fc = new JFileChooser(lastFile);
	//	fc.setApproveButtonToolTipText("Save the learned data to this file");
		fc.setApproveButtonText("SAVE");
		SprachFileFilter myFilter = new SprachFileFilter();
		myFilter.addExtension("bin");
		myFilter.setDescription("Serialized binary files");
		fc.addChoosableFileFilter(myFilter);

		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION)
		{

			File f = fc.getSelectedFile();
			lastFile = f;
			try
			{
				FileOutputStream fo = new FileOutputStream(f);
				ObjectOutputStream wc = new ObjectOutputStream(fo);
				wc.writeObject(l.getRoot());
				wc.flush();
				wc.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			sysMsg("export successful");
		}
	}

	/**
	 *  Reads previously learned data from a serialized file and shows it in the tree
	 **/
	private void importFile()
	{
		JFileChooser fc = new JFileChooser(lastFile);
	//	fc.setApproveButtonToolTipText("Load the learned data from this file");
		fc.setApproveButtonText("LOAD");
		SprachFileFilter myFilter = new SprachFileFilter();
		myFilter.addExtension("bin");
		myFilter.setDescription("Serialized binary files");
		fc.addChoosableFileFilter(myFilter);

		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION)
		{

			File f = fc.getSelectedFile();
			lastFile = f;
			try
			{
				FileInputStream fi = new FileInputStream(f);
				ObjectInputStream si = new ObjectInputStream(fi);
				TreeNode root = (TreeNode) si.readObject();
				l.setRoot(root);
			    si.close();
			}
			catch (Exception e)
			{
				sysMsg("import failed");
				return;
			}
			console.setModel(new DefaultTreeModel(l.getRoot()));
			sysMsg("import successful");
		}
	}

	/**
	 *  Adds a system message to the infoField
	 **/
	private void sysMsg(String mesg)
	{
		infoField.append("% "+mesg+'\n');
	}

	/**
	 *  File | Exit action performed
	 **/
	public void jMenuFileExit_actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

	/**
	 *  Opens up the about box for convenience
	 **/
	public void jMenuHelpAbout_actionPerformed(ActionEvent e)
	{
		AboutBox dlg = new AboutBox((JFrame)this.getParent().getParent());
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true); // only here input
		dlg.show();
	}

	/**
	 *  Overridden so we can exit when window is closed
	 **/
	protected void processWindowEvent(WindowEvent e)
	{
		//super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			jMenuFileExit_actionPerformed(null);
		}
	}

	/**
	 *  Saves the text from the logwindow into a file and
	 *  clears the logwindow
	 **/
	protected void saveLog()
	{
		JFileChooser fc = new JFileChooser(lastFile);
		fc.setApproveButtonText("SAVE");
		SprachFileFilter myFilter = new SprachFileFilter();
		myFilter.addExtension("txt");
		myFilter.addExtension("log");
		myFilter.setDescription("Ascii log files");
		fc.addChoosableFileFilter(myFilter);
		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			lastFile = fc.getSelectedFile();
			String text = infoField.getText();
			PrintWriter writer = null;
			if ( lastFile != null)
			{
				try
				{
					writer = new PrintWriter(new BufferedWriter(new FileWriter(lastFile.getPath(), true)));
					writer.println("Log entry : " + java.util.Calendar.getInstance().getTime());
					writer.println(text);
					writer.close();
					infoField.setText("");
				}
				catch(Exception ex)
				{
					infoField.setText("Exception : "+ex);
				}
			}

		}
	}

	class MyMouseListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e)
		{
			if ( e.getClickCount() > 1 ) // doubleclick occured
			{
				saveLog();
			}
		}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
	}

  class InputFieldKeyListener implements KeyListener
  {
    public void keyReleased(KeyEvent ke)
    {

      if ( ke.getKeyCode() == KeyEvent.VK_UP )
      {
        if ( commandCount > 0 )
        {
          synchronized ( GuiClient.class )
          {
            commandCount--;
            try
            {
              inputField.setText((String)commands.elementAt(commandCount));
            }
            catch ( Exception e){}
          }
        }
      }
      else if ( ke.getKeyCode() == KeyEvent.VK_DOWN )
      {
        if ( commandCount <= commands.size() )
        {
          synchronized ( GuiClient.class )
          {
            commandCount++;
            if ( commandCount >= commands.size() )
            {
              inputField.setText("");
            }
            else
            {
              inputField.setText((String)commands.elementAt(commandCount));
            }
          }
        }
      }
    }
    public void keyPressed(KeyEvent ke){}
    public void keyTyped(KeyEvent ke){}
  }

	/**
	 *  Listens for Enter action on the input field
	 **/
	class InputFieldListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String what = inputField.getText().trim();
			if (!what.equals(""))
			{
				if ( what.indexOf(" ") > 0 )
				{
					learn();
				}
				else
				{
					ask();
				}
        synchronized ( GuiClient.class )
        {
          commands.add(what);
          commandCount++;
        }
			}
			inputField.requestFocus();
		}
	}

	/**
	 *  Listens for changes in the Tree which shows the Agents
	 **/
	class STreeListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			try
			{
				JPanel temp = ((Agent)console.getSelectionPath().getLastPathComponent()).getUI();
				if ( temp == null )
				{
					return;
				}
			}
			catch ( NullPointerException ne )
			{
				return;
			}

			contentPane.remove(optionsPane);
			optionsPane = new JPanel();
			optionsPane.add(((Agent)console.getSelectionPath().getLastPathComponent()).getUI());

			contentPane.add(optionsPane,BorderLayout.EAST);
			contentPane.validate();
		}
	}
}
// End of GuiClient.java