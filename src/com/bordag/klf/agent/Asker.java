package com.bordag.klf.agent;

import java.util.*;
import javax.swing.tree.TreeNode;

/**
*	This class is to refer to all the data it can and 
*	to get answers from them and analyse it with some
*	statistical methods and to return the information
*	it gathered
*	NOTE: Currently just an empty implementation
*	@author		Stefan Bordag
**/
public class Asker
{
	public Asker()
	{
	}
	
	public AgentAnswer ask(String word,TreeNode root)
	{
//		String answer = ((MetaAgent)root).ask(word,new AgentAnswer()).toString();
		return ((MetaAgent)root).ask(word,new AgentAnswer());
	}
}// End of Agent.java
