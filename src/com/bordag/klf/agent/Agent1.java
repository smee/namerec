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
public class Agent1 extends Agent implements Comparable
{
	protected String myToken;	// The actual information stored in this node
	protected Hashtable myDecisions; // The values and their frequencies


	/**
	 *  To prevent creating non-functional nodes
	 **/
	private Agent1()
	{
	}

	/**
	 *	This consgtructor is to be used by the metaAgent to create
	 *	the root of this agent
	 **/
	public Agent1(TreeNode parent)
	{
		if (parent!=null)
		{
			this.parent = parent;
		}
		this.myToken = " ";

		this.myDecisions = new Hashtable();
		this.IDString = this.getClass().getName();

		this.children = new Vector();
		//this.view = new UIAgent1(this);
		this.IDString = new String("Backward");
	}

	/**
	 *  Method for the comparable interface, you can override it
	 *  and implement it as you wish
	 **/
	public int compareTo(Object object)
	{
		Agent1 agent = (Agent1)object;
		if ( this.myToken.compareTo(agent.getToken()) > 0 )
		{
			return 1;
		}
		else if ( this.myToken.equals(agent.getToken()) )
		{
			return 0;
		}
		return -1;
	}

	public JPanel getUI()
	{
		return new UIAgent1(this);
	}

	/**
	 *	The second constructor
	 **/
	public Agent1(TreeNode parent, String x, String y)
	{
		if ( parent != null )
		{
			this.parent = parent;
		}
		this.myToken = x;

		this.myDecisions = new Hashtable();
		this.myDecisions.put(new String(y), new Integer(0));

		this.children = new Vector();
		//this.view = new UIAgent1(this);
	}

	/**
	 *  Passes down the given piece of word to the leaves and increases the
	 *  appropriate frequency
	 **/
	public void learn(String word, String value)
	{
		String ch = StringUtils.getLastChar(word);

		String chopped = null;

		// checking for case sensivity
		if ( this.caseSensitive )
		{
			chopped = StringUtils.chopLastChar(word);
		}
		else
		{
			chopped = StringUtils.chopLastChar(word.toLowerCase());
		}

		// increasing frequency or adding entry to decision table
		if (myDecisions.get(new String(value))==null)
		{
			myDecisions.put(new String(value), new Integer(1));
		}
		else
		{
			myDecisions.put(new String(value), new Integer(((Integer)myDecisions.get(new String(value))).intValue() + 1));
		}

		// end recursion
		if ( chopped.equals(" ") )
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
			Agent1 n = new Agent1((TreeNode)this, ch, value);
			n.learn(chopped, value);
			this.children.add(n);
			Collections.sort(this.children);
		}
	}

	/**
	 *	This method checks if there are leaves like the last char
	 *
	 **/
	public AgentAnswer ask(String word,AgentAnswer a)
	{

		// this continues the recursion
		if( leavesHave(StringUtils.getLastChar(word)) )
		{
			a = getLeave(StringUtils.getLastChar(word)).ask(StringUtils.chopLastChar(word), a);
			a.addReason("["+StringUtils.getLastChar(word)+"]");
		}
		else // this ends it
		{
			int i = 0;
			int allTogether = 0;
			String found = new String("error");
			for ( Enumeration e = myDecisions.keys() ; e.hasMoreElements() ; )
			{
				String key    = (String)e.nextElement();
				Integer value = (Integer)myDecisions.get(key);
				allTogether   += value.intValue();
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

	/**
	 *  Checks the answer against the treshold and sets answer to unknown if
	 *  treshold was higher then strength of answer
	 **/
	protected AgentAnswer checkSchwellWert(AgentAnswer answer)
	{
		if ( answer.getStrength() < this.schwellWert )
		{
			answer.addReason("(" + answer.getAnswer() + ")");
			answer.setAnswer(Learner.UNKNOWN);
		}
		return answer;
	}

	/**
	 *	Returns true, if one of the leaves has the specified token saved
	 **/
	protected boolean leavesHave(String ch)
	{
		for(Enumeration e = this.children.elements() ; e.hasMoreElements() ; )
		{
			if ( ((Agent1)e.nextElement()).getToken().equals(ch) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *	Returns the node with the given token
	 **/
	protected Agent1 getLeave(String ch)
	{
		for( Enumeration e = this.children.elements() ; e.hasMoreElements() ; )
		{
			Agent1 n = (Agent1)e.nextElement();
			if ( n.getToken().equals(ch) )
			{
				return n;
			}
		}
		return null;
	}

	/**
	 *  returns the token of this leaves
	 **/
	public String getToken()
	{
		return myToken;
	}

	/**
	 *  returns true if this node has leaves
	 **/
	public boolean hasLeaves()
	{
		if ( this.children.size() == 0 )
		{
			return true;
		}
		return false;
	}

	//----------- methods for UI -------------
	public void setSchwellWert(int newValue, boolean recursive)
	{
		this.schwellWert = newValue;
		if ( recursive && children != null )
		{
			for ( Enumeration e = children.elements() ; e.hasMoreElements() ; )
			{
				((Agent1)e.nextElement()).setSchwellWert(newValue, recursive);
			}
		}
	}

	/**
	 *  Returns the name of this node
	 **/
	public String toString()
	{
		if (IDString==null)
		{
			return "[ "+myToken+" ] "+myDecisions;
		}
		else
		{
			return "[ "+IDString+" ] "+myDecisions;
		}
	}
}// End of Node.java
