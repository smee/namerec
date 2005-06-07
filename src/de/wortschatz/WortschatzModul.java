package de.wortschatz;

import javax.swing.*;
import java.awt.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

abstract public class WortschatzModul extends JPanel
{

  private final static Dimension preferredSize = new Dimension(800,550);

  private WortschatzTool wTool = null;

  public WortschatzModul()
  {
  }

  public WortschatzModul(WortschatzTool wTool)
  {
  	this.setLayout(null);

    this.wTool = wTool;
  }

  public abstract void activated();

  public abstract JPanel getModulePanel();

  public abstract char getMnemonic();

  public abstract String getName();

  public abstract Icon getIcon();

  public abstract String getToolTip();

  public WortschatzTool getWTool()
  {
	  return this.wTool;
  }

  public ImageIcon createImageIcon(String filename)
  {
	  if( this.wTool != null )
    {
	    return this.wTool.createImageIcon(filename);
    }
    return null;
  }


}