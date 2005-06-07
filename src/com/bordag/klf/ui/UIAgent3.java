package com.bordag.klf.ui;

// standard imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.tree.*;
//import java.util.Vector;
//import java.util.Enumeration;
import java.util.*;
import javax.swing.event.*;

// app specific imports
import com.bordag.klf.agent.*;


/**
 * Title:        Klassifikator
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class UIAgent3 extends UIMetaAgent
{

  public UIAgent3()
  {
  }

	/**
	 *  Creates a new instance of this view and connects it to the given
	 *  Agent
	 **/
	public UIAgent3(TreeNode node)
	{
		super(new GridLayout(8,1));
		this.agent = (AgentIF)node;
//		init();
	}

}