/*
 * Created on 19.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package namerec.gui;

import javax.swing.JPanel;

import javax.swing.JLabel;
/**
 * @author sdienst
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WelcomePanel extends JPanel {

	private JLabel jLabel = null;
    private static final String welcometext = "<html>" +
            "<h2>Welcome to Namerec</h2><br><br>" +
            "This tool tries to recognize names and titles from sentences. It needs some initial known<br>" +
            "names and titles and some rules for learning new names.<br>" +
            "The graphical user interface you admire at the moment tries to help you in <br>" +
            "customizing your rules and settings.<br></html>";
	/**
	 * This method initializes 
	 * 
	 */
	public WelcomePanel() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
        jLabel = new JLabel(welcometext);
        this.setLayout(null);
        this.setSize(478, 299);
        jLabel.setBounds(113, 72, 339, 242);
        this.add(jLabel, null);
			
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
