package com.bordag.klf.agent;

// standard imports
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.io.Serializable;

// app sepcific imports

/**
 *  This is the abstract class for all agents, they should extend this one,
 *  implement the abstract methods and by need override the given ones
 **/
public abstract class Agent implements TreeNode,Serializable,AgentIF
{

	/**
	 *  String with which the Agent identifies himself
	 **/
	protected String IDString = null;

	/**
	 *  This field contains the view for this Agent, i.e. the control
	 *  mechanism
	 **/
	protected transient JPanel view = null;

	/**
	 *  Because all agents are treated like TreeNodes, they must provide
	 *  their parent
	 **/
	protected TreeNode parent = null;

	/**
	 *  Because all agents are treated like TreeNodes, they must provide
	 *  their children
	 **/
	protected Vector children = null;

	/**
	 *  CaseSensivity field, if set to true, the learn method will distinguish
	 *  between upper case and lower case, else it will convert everything to
	 *  lower case.
	 **/
	protected boolean caseSensitive = true;

	/**
	 *  If the answer strength is less then this value, no decision will be done
	 **/
	protected int schwellWert = 75;

	//------------ AgentIF methods

	public abstract AgentAnswer ask(String word,AgentAnswer a);
	public abstract void learn(String word,String value);

	/**
	 *  Getter method for the view field
	 **/
	public JPanel getUI()
	{
		return this.view;
	}

    //------------ TreeNode methods

	public Enumeration children()
	{
		return children.elements();
	}

	public boolean getAllowsChildren()
	{
		return true;
	}

	public TreeNode getChildAt(int childIndex)
	{
		return (TreeNode)children.elementAt(childIndex);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public int getIndex(TreeNode node)
	{
		if (node!=null)
		{
			return children.indexOf(node);
		}
		else
		{
			return -1;
		}
	}

	public TreeNode getParent()
	{
		return parent;
	}

	public boolean isLeaf()
	{
    if ( this.children == null )
    {
      return true;
    }
		if (children.size()==0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// --------- UI methods ---------------------------
	/**
	 *  Allows the CaseSensivity to be set to a new value
	 **/
	public void setCaseSensitive(boolean caseSensitive, boolean recursive)
	{
		this.caseSensitive = caseSensitive;
		if ( recursive && children != null )
		{
			for ( Enumeration e = children.elements(); e.hasMoreElements() ; )
			{
				((Agent1)e.nextElement()).setCaseSensitive(caseSensitive, recursive);
			}
		}
	}

	/**
	 *  Returns the value of the caseSensivity field
	 **/
	public boolean getCaseSensitive()
	{
		return this.caseSensitive;
	}

	public int getSchwellWert()
	{
		return schwellWert;
	}

	public abstract void setSchwellWert(int newValue, boolean recursive);

}
