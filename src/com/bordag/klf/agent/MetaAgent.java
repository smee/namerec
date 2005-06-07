package com.bordag.klf.agent;

// standard imports
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

// app specific imports
import com.bordag.klf.ui.*;

/**
 *	This agent manager holds the different references to the
 *	different agents and manages the passing down of learning data
 *	and asking requests and evalutation of their results.
 *	@author Stefan Bordag
 **/
public class MetaAgent extends Agent
{
	protected int similarity = 5;
	protected int maxWortLaenge  = -1;

	/**
	 *	This should be the only constructor, adding agents and stuff
	 *	can be done dynamically later or hardcoded right here
	 **/
	public MetaAgent()
	{

		this.children = new Vector();

		// here come the different agents, just add them
		this.children.add(new Agent1((TreeNode)this));
		this.children.add(new Agent2((TreeNode)this));
		this.children.add(new Agent3((TreeNode)this, 3, 3));
		this.children.add(new Agent3((TreeNode)this, 3, 1));

		this.view = new UIMetaAgent(this);
		this.IDString = "MetaAgent";
	}

	/**
	 *  Returns the view of this Agent
	 **/
	public JPanel getUI()
	{
		return new UIMetaAgent(this);
	}

	/**
	 *	This is the actually important method, it gets all the answers
	 *	from all the agents and can give it right back or calculate the
	 *	best one and give that back
	 **/
	 // TODOX Vergleich der Frequenzen Vla ast (Vlasta) fuer bessere Staerken plus vergleich dabei der gesamt frequenz
	public AgentAnswer ask(String word, AgentAnswer a)
	{
		double i = 0;
		a.setAgentID(this.IDString);

		/**
		 *  Get all answers
		 **/
		Vector answers = getAnswers(word, this.children);

		/**
		 *  Find the strongest answer which is not unknown
		 **/
		AgentAnswer myA = null;
		for(Enumeration e = answers.elements() ; e.hasMoreElements() ; )
		{
			myA = (AgentAnswer)e.nextElement();
      if ( myA != null )
      {
        if ( i < myA.getStrength() )//&& !myA.getAnswer().equalsIgnoreCase(Learner.UNKNOWN) ) // this finds the strongest one
        {
          i=myA.getStrength();
          a.setAnswer(myA.getAnswer());
          a.setStrength(myA.getStrength());
        }
        a.addReason('\n'+myA.toString());
      }
		}
		a.addReason("\n");

		/**
		 *  If all answers said the same, build the mean of their
		 *  strengthes and set our strength to that
		 **/
		a.setStrength(meanStrength(answers));

		/**
		 *  Decide to not decide if different answers with
		 *  approximately the same strength
		 **/
		if ( !sameAnswer(answers) && sameStrength(answers) )
		{
			a.setAnswer(Learner.UNKNOWN);
		}

		/**
		 *  Decide to reply with unknown if answer was too weak
		 **/
		if ( a.getStrength() < (double)schwellWert )
		{
			a.setAnswer(Learner.UNKNOWN);
		}

		return a;
	}

	/**
	 *  Returns true, if all Answers are approximately the same
	 **/
	protected boolean sameStrength(Vector answers)
	{
		if ( answers == null || answers.size() == 0 )
		{
			return false;
		}
		double strength = ((AgentAnswer)answers.firstElement()).getStrength();
		for ( Enumeration e = answers.elements() ; e.hasMoreElements() ; )
		{
      AgentAnswer answer = (AgentAnswer)e.nextElement();
      if ( answer == null )
      {
        continue;
      }
			double curStrength = answer.getStrength();
			if ( ( strength > ( curStrength + (double)this.similarity ) ) ||
			     ( strength < ( curStrength - (double)this.similarity ) ) )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 *	This method can also probably be tweaked, maybe not give all
	 *	information to all agents
	 **/
	public void learn(String word, String value)
	{
		// checking the length of the word
		if ( word.length() > maxWortLaenge && maxWortLaenge != -1 )
		{
			word = word.substring(0, maxWortLaenge);
		}

		// checking whether to ignore upper case
		if ( !this.caseSensitive )
		{
			word = word.toLowerCase();
		}

		// telling all Agents to learn this word
		for( Enumeration e = children.elements() ; e.hasMoreElements() ; )
		{
			((AgentIF)e.nextElement()).learn(word,value);
		}
		return;
	}

	/**
	 *  Returns all the answers from the Agents
	 **/
	private Vector getAnswers(String word, Vector children)
	{
		Vector answers = new Vector();
		AgentAnswer myA = null;
		for( Enumeration e = children.elements() ; e.hasMoreElements() ; )
		{
			myA = new AgentAnswer();
			myA = ((AgentIF)e.nextElement()).ask(word,myA);
			answers.add(myA);
		}
		return answers;
	}

	/**
	 *  Returns true if all the children had the same answer
	 **/
	private boolean sameAnswer(Vector answers)
	{
		if ( answers == null || answers.size() == 0 )
		{
			return false;
		}
		String answer = ((AgentAnswer)answers.elementAt(0)).getAnswer();
		for ( Enumeration e = answers.elements() ; e.hasMoreElements() ; )
		{
      try
      {
        if ( !((AgentAnswer)e.nextElement()).getAnswer().equalsIgnoreCase(answer) )
        {
          return false;
        }
      }
      catch (NullPointerException ne)
      {
        return false;
      }
		}
		return true;
	}

	/**
	 *  Returns the mean strength of all Answers
	 **/
	private double meanStrength(Vector answers)
	{
		double strength = 0;
		int i = 0;
		for ( Enumeration e = answers.elements() ; e.hasMoreElements() ; i++ )
		{
			AgentAnswer myA = (AgentAnswer)e.nextElement();
      if ( myA != null )
      {
  			strength = strength + myA.getStrength();
      }
		}
		return strength/(double)i;
	}

	public String toString()
	{
		return this.IDString;
	}

//----------- methods for UI -------------

	/**
	 *  Getter method for the similarity field
	 **/
	public int getSimilarity()
	{
		return this.similarity;
	}

	/**
	 *  Setter method for the similarity field
	 **/
	public void setSimilarity(int newVal)
	{
		this.similarity = newVal;
	}

	/**
	 *  This sets the treshold for the nodecision field and to all
	 *  children, if recursive is set to true
	 **/
	public void setSchwellWert(int newValue, boolean recursive)
	{
		this.schwellWert = newValue;

		if ( recursive && children != null )
		{
			for( Enumeration e = children.elements() ; e.hasMoreElements() ; )
			{
				((Agent)e.nextElement()).setSchwellWert(newValue, recursive);
			}
		}
	}

	public int getMaxWortLaenge()
	{
		return maxWortLaenge;
	}

	public void setMaxWortLaenge(int newValue)
	{
		this.maxWortLaenge = newValue;
	}

}// End of MetaAgent
