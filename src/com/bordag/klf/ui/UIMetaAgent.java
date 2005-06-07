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
 *  This class is a graphical control mechanism for the Metagent where one
 *  can adjust all kinds of different "magic" numbers which play a role
 **/
public class UIMetaAgent extends JPanel
{
	// references
	protected AgentIF agent;
	
	// UI components
	protected JSlider schwellWertSlider;
	protected JSlider wortSlider;
	protected JSlider similaritySlider;

	protected JCheckBox WertRecursive = null;
	protected boolean recursive = false;

	protected JCheckBox caseSensitive = null;
	protected boolean cSensitive = false;

	/**
	 *  Default constructor, does nothing, is hidden away
	 **/
	protected UIMetaAgent()
	{
	}
	
	public UIMetaAgent(GridLayout layout)
	{
		super(layout);
	}
	
	/**
	 *  Standard constructor, takes a TreeNode, to which this control 
	 *  mechanism should be connected
	 **/
	public UIMetaAgent(TreeNode node)
	{
		super(new GridLayout(9,1));
		this.agent = (AgentIF)node;
		init();
	}
	
	/**
	 *  Component initialization
	 **/
	protected void init()
	{
		try
		{
			schwellWertSlider = new JSlider(0, 100, ((MetaAgent)agent).getSchwellWert());
		}
		catch(IllegalArgumentException e)
		{
			schwellWertSlider = new JSlider(0, 100, 50);
		}
		schwellWertSlider.addChangeListener((ChangeListener)new MyChangeListener());

		try
		{
			wortSlider = new JSlider(0, 30, ((MetaAgent)agent).getMaxWortLaenge());
		}
		catch(IllegalArgumentException e)
		{
			wortSlider = new JSlider(1, 30, 30);
		}
		wortSlider.addChangeListener((ChangeListener)new MyChangeListener());

		try
		{
			similaritySlider = new JSlider(0, 100, ((MetaAgent)agent).getSimilarity());
		}
		catch(IllegalArgumentException e)
		{
			similaritySlider = new JSlider(0, 100, 50);
		}
		similaritySlider.addChangeListener((ChangeListener)new MyChangeListener());

		Hashtable d1 = new Hashtable();
		d1.put(new Integer(0), new JLabel("0"));
		d1.put(new Integer(50), new JLabel("50"));
		d1.put(new Integer(100), new JLabel("100"));		

		this.caseSensitive = new JCheckBox("Gross- und Kleinschreibung unterscheiden");
		this.caseSensitive.setSelected(((MetaAgent)agent).getCaseSensitive());
		this.caseSensitive.addChangeListener((ChangeListener)new MyChangeListener());

		schwellWertSlider.setLabelTable(d1);
		schwellWertSlider.setPaintLabels(true);

		Hashtable d2 = new Hashtable();
		d2.put(new Integer(1),new JLabel("1"));
		d2.put(new Integer(15),new JLabel("15"));
		d2.put(new Integer(30),new JLabel("whole word"));
		wortSlider.setLabelTable(d2);
		wortSlider.setPaintLabels(true);

		Hashtable d3 = new Hashtable();
		d3.put(new Integer(0),new JLabel("0"));
		d3.put(new Integer(50),new JLabel("50"));
		d3.put(new Integer(100),new JLabel("100"));
		similaritySlider.setLabelTable(d3);
		similaritySlider.setPaintLabels(true);

		this.WertRecursive = new JCheckBox("Aenderung Rekursiv");
		this.WertRecursive.addChangeListener((ChangeListener)new MyChangeListener());
		
		this.add(new JLabel("                      "+agent.toString()));
		this.add(new JLabel("Schwellwert für weiss nicht Entscheidung:"));
		this.add(schwellWertSlider);
		this.add(new JLabel("Maximale Wortlänge:"));
		this.add(wortSlider);
		this.add(new JLabel("Similarity"));
		this.add(similaritySlider);
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
			if (e.getSource()==schwellWertSlider)
			{
				((MetaAgent)agent).setSchwellWert(schwellWertSlider.getValue(), recursive);
			}
			else if ( e.getSource() == similaritySlider )
			{
				((MetaAgent)agent).setSimilarity(similaritySlider.getValue());
			}
			else if (e.getSource() == wortSlider)
			{
				int val = wortSlider.getValue();
				if (val<=29)
				{
					((MetaAgent)agent).setMaxWortLaenge(val);
				}
				else
				{
					((MetaAgent)agent).setMaxWortLaenge(-1);
				}
			}
			else if ( e.getSource() == caseSensitive )
			{
				cSensitive = caseSensitive.isSelected();
				((MetaAgent)agent).setCaseSensitive(cSensitive, recursive);
			}
			else if ( e.getSource() == WertRecursive )
			{
				recursive = WertRecursive.isSelected();
			}
		}
	}
}
