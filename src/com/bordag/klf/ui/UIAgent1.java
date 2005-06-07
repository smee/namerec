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
 *  This class is a graphical control mechanism for the Agent1 where one
 *  can adjust all kinds of different "magic" numbers which play a role
 **/
public class UIAgent1 extends UIMetaAgent
{

	/**
	 *  Creates a new instance of this view and connects it to the given
	 *  Agent
	 **/
	public UIAgent1(TreeNode node)
	{
		super(new GridLayout(8,1));
		this.agent = (AgentIF)node;
		init();
	}
	
	/**
	 *  Initializes the componenets and Listeners and adds them
	 **/
	protected void init()
	{
		try
		{
			this.schwellWertSlider = new JSlider(0, 100, ((Agent1)this.agent).getSchwellWert());
		}
		catch(IllegalArgumentException e)
		{
			this.schwellWertSlider = new JSlider(0, 100, 50);
		}
		this.schwellWertSlider.addChangeListener((ChangeListener)new MyChangeListener());

		Hashtable d1 = new Hashtable();
		d1.put(new Integer(0), new JLabel("0"));
		d1.put(new Integer(50), new JLabel("50"));
		d1.put(new Integer(100), new JLabel("100"));		
		this.schwellWertSlider.setLabelTable(d1);
		this.schwellWertSlider.setPaintLabels(true);

		this.caseSensitive = new JCheckBox("Gross- und Kleinschreibung unterscheiden");
		this.caseSensitive.setSelected(((Agent1)agent).getCaseSensitive());
		this.caseSensitive.addChangeListener((ChangeListener)new MyChangeListener());

		this.WertRecursive = new JCheckBox("Aenderung Rekursiv");
		this.WertRecursive.addChangeListener((ChangeListener)new MyChangeListener());

		this.add(new JLabel("                      "+this.agent.toString()));
		this.add(new JLabel("Schwellwert für Weiss nicht Entscheidung:"));
		this.add(schwellWertSlider);
		this.add(caseSensitive);
		this.add(WertRecursive);
	}
	
	/**
	 *  Inner class Changelistener to perform the changes on the data which 
	 *  had been performed in the UI.
	 **/
	protected class MyChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if ( e.getSource() == schwellWertSlider )
			{
				((Agent1)agent).setSchwellWert(schwellWertSlider.getValue(), recursive);
			}
			else if ( e.getSource() == caseSensitive )
			{
				cSensitive = caseSensitive.isSelected();
				((Agent1)agent).setCaseSensitive(cSensitive, recursive);
			}
			else if ( e.getSource() == WertRecursive )
			{
				recursive = WertRecursive.isSelected();
			}
		}
	}

}