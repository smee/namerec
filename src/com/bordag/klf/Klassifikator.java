package com.bordag.klf;

import de.wortschatz.*;
import javax.swing.*;
import java.awt.*;


import com.bordag.klf.ui.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class Klassifikator extends WortschatzModul
{
  private GuiClient client = null;

  public Klassifikator(WortschatzTool wTool)
  {
    super(wTool);
    this.setLayout(null);
    this.client = new GuiClient();
    this.client.setBounds(0,0,800,523);
    this.add(client);
  }

  public void activated()
  {
    this.repaint();
    this.client.repaint();
  }

  public JPanel getModulePanel()
  {
    return this;
  }

  public char getMnemonic()
  {
    return 'K';
  }

  public String getName()
  {
    return "Klassifikator";
  }

  public Icon getIcon()
  {
    return this.createImageIcon("Klf.jpg");
  }

  public String getToolTip()
  {
    return "Classify words on letter-level.";
  }
}