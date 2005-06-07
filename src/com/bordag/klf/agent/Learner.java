package com.bordag.klf.agent;

// standard imports
import java.util.*;
import javax.swing.tree.*;

/**
 *  An implementation of a language learner. Utilizes the backwards
 *  algorithm taking single letters from the end of the word.
 *  Needs SprachData to store possible values as well as all seen 
 *  words so far.
 *  Also needs Node which is used to build the learning tree.
 *  @author		Stefan Bordag
 **/
public class Learner
{
	public static final String UNKNOWN = "undecided"; // the value for unknown
	private MetaAgent root;
	
	/**
	 *  Default constructor, initializes the Arrays
	 **/
	public Learner()
	{
		root = new com.bordag.klf.agent.MetaAgent();
	}
		
	/**
	 *	Adds the new information to the data and if it wasn't there 
	 * 	already then 
	 **/
	public void learnToken(String word,String value)
	{
		if ( word != null && value != null )
		{
			root.learn(word,value);
		}
	}
	
	/**
	 *	Returns the root of the learning data set (to be 
	 *	serialized orreassigned or whatever)
	 **/
	public TreeNode getRoot()
	{
		return (TreeNode)root;
	}
	
	/**
	 *	Assigns the Learner a new root of the learning dataset
	 **/
	public void setRoot(TreeNode root)
	{
		this.root = (MetaAgent)root;
	}
	
}// End of Learner.java
