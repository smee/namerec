package com.bordag.klf.agent;

// standard imports
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

// app specific imports
import com.bordag.klf.ui.*;
import com.bordag.klf.util.*;

/**
*	A node of the learning tree
*	@author		Stefan Bordag
**/
public class Agent2 extends Agent1
{

	public Agent2(TreeNode parent)
	{
		super(parent);

		this.view = new UIAgent2(this);
		this.IDString = new String("Forward");
	}

	public Agent2(TreeNode parent, String x, String y)
	{
		super(parent, x, y);
	}

	/**
	 *  Method for the comparable interface, you can override it
	 *  and implement it as you wish
	 **/
	public int compareTo(Object object)
	{
		Agent2 agent = (Agent2)object;
		if ( myToken.compareTo(agent.getToken()) > 0 )
		{
			return 1;
		}
		else if ( myToken.equals(agent.getToken()) )
		{
			return 0;
		}
		return -1;
	}


	public JPanel getUI()
	{
		return new UIAgent2(this);
	}

	/**
	 *  passes down the given piece of word to the leaves and increases the
	 *  appropriate frequency
	 **/
	public void learn(String word, String value)
	{
		String ch = StringUtils.getFirstChar(word);
		String chopped = null;

		// checking case sensivity
		if ( this.caseSensitive )
		{
			chopped = StringUtils.chopFirstChar(word);
		}
		else
		{
			chopped = StringUtils.chopFirstChar(word.toLowerCase());
		}

		// increasing frequency or adding entry to decision table
		if ( myDecisions.get( new String(value)) == null )
		{
			myDecisions.put(new String(value), new Integer(1));
		}
		else
		{
			myDecisions.put(new String(value), new Integer(((Integer)myDecisions.get(new String(value))).intValue() + 1));
		}

		// end recursion
		if (chopped.equals(" "))
		{
			// end passing down (end of word)
			return;
		}

		// check if there are leaves like this
		if ( leavesHave(ch) )
		{// if so, then chop the last char and pass it to the existing
			getLeave(ch).learn(chopped, value);
		}
		else
		{// if not, then create a new leave like this
			Agent2 n = new Agent2((TreeNode)this, ch, value);
			n.learn(chopped, value);
			this.children.add(n);
			Collections.sort(this.children);
		}
	}

	/**
	 *	This method checks if there are leaves like the last char
	 *
	 **/
	public AgentAnswer ask(String word, AgentAnswer a)
	{
		String retval;
		if ( leavesHave(StringUtils.getFirstChar(word)) )
		{
			a = getLeave(StringUtils.getFirstChar(word)).ask(StringUtils.chopFirstChar(word),a);
			a.addReason("["+StringUtils.getFirstChar(word)+"]");
		}
		else
		{
			int i=0;
			int allTogether=0;
			String found = new String("error");
			for( Enumeration e = myDecisions.keys() ; e.hasMoreElements() ; )
			{
				String key    = (String)e.nextElement();
				Integer value = (Integer)myDecisions.get(key);
				allTogether   +=value.intValue();
				if ( i < value.intValue() )
				{
					i = value.intValue();
					found = key;
				}
			}
			double strength = 0; //determines how sure we are with our decision
			if (allTogether!=0){strength = 100*(double)i/(double)allTogether;}

			a.setStrength(strength);
			a.setAnswer(found);
			a = checkSchwellWert(a);
			//return the one with biggest frequency
		}
		if ( IDString != null )
		{
			a.setAgentID(IDString);
		}
		return a;
	}
}