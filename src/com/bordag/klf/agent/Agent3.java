package com.bordag.klf.agent;

// standard imports
import java.util.*;
import javax.swing.tree.*;
import javax.swing.JPanel;

// app specific imports
import com.bordag.klf.ui.*;

/**
 * Sliding trigram algorithm
 */
public class Agent3 extends Agent implements Comparable
{
  // The actual information stored in this node
	protected String myToken = null;
  // The values and their frequencies
	protected Hashtable myDecisions = null;
  // trigram or more or less?
  protected int CHUNK_LENGTH = 3;
  // shift value ( by how many chars to shift, minimum is 1
  protected int SHIFT_VAL = 3;

  private Agent3()
  {
  }

  public Agent3(TreeNode parent, int chunkLength, int shiftVal)
  {
		if (parent!=null)
		{
			this.parent = parent;
		}
    this.CHUNK_LENGTH = chunkLength;
    this.SHIFT_VAL = shiftVal;
    init();
  }

  public Agent3(TreeNode parent)
  {
		if (parent!=null)
		{
			this.parent = parent;
		}
    init();
  }

  /**
   * Initializes all the fields
   */
  private void init()
  {
    this.IDString = "Sliding Trigram";
    this.myToken = "";
    this.myDecisions = new Hashtable();
    this.children = new Vector();
    this.schwellWert = 0;
  }

	/**
	 *  Method for the comparable interface, you can override it
	 *  and implement it as you wish
	 **/
	public int compareTo(Object object)
	{
		Agent3 agent = (Agent3)object;
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

  /**
   * (Recursively) Learns the given word with the given attribute
   */
  public void learn(String word, String value)
  {
    if ( word.length() <= this.CHUNK_LENGTH ) // then it's a chunk short enough to assign it
    {
      if ( isRoot() ) // if I am the root
      {
        Agent3 agent;
        if ( leavesHave(word) ) // my leaves have this item
        {
          agent = getLeave(word);
        }
        else // my leaves dont have
        {
          agent = new Agent3(this, this.CHUNK_LENGTH, this.SHIFT_VAL);
        }
        agent.learn(word, value); // add this leave with freq 1
        this.children.add(agent);
        addDecision(value);

        //Collections.sort(this.children);
      }
      else // I am a leave
      {
        if (this.myToken.equals("") )
        {
          this.myToken = word;
          this.IDString = word;
        }
        addDecision(value);
      }
    }
    else // we split
    {
      String curTrigram = word.substring(0, this.CHUNK_LENGTH);
      this.learn(curTrigram, value);
      this.learn(word.substring(this.SHIFT_VAL), value);
    }
  }

  /**
   * Adds the given value as a decision, if not there, else increases frequency
   */
  protected void addDecision(String value)
  {
    if ( this.myDecisions.containsKey(value) )
    {
      this.myDecisions.put(value, new Integer(((Integer)this.myDecisions.get(value)).intValue()+1) );
    }
    else
    {
      this.myDecisions.put(value, new Integer(1) );
    }
  }


  /**
   * Sets the disicion treshold for the non-decision
   */
  public void setSchwellWert(int newValue, boolean recursive)
  {
    this.schwellWert = newValue;
    if ( recursive )
    {
      for ( Enumeration e = this.children.elements() ; e.hasMoreElements() ; )
      {
        ((Agent)e.nextElement()).setSchwellWert(newValue, recursive);
      }
    }
  }

  /**
   * asks...
   */
  public AgentAnswer ask(String word, AgentAnswer a)
  {
    if ( word.length() <= this.CHUNK_LENGTH ) // then it's a leave
    {
      if ( isRoot() )
      {
        Vector one = new Vector();
        Agent3 agent = getLeave(word);
        if ( agent != null)
        {
          one.add(getLeave(word).ask(word, null));
        }
        return evaluateAnswers(one);
      }
      else
      {
        return getLeaveAnswer();
      }
    }
    else
    {
      Vector answers = new Vector();
      String tempWord = new String(word);
      for (int i = 0; i <= word.length() - this.CHUNK_LENGTH ; i++ )
      {
        String curTrigram = tempWord.substring(0, this.CHUNK_LENGTH);
        Agent3 agent = getLeave(curTrigram);
        if ( agent != null )
        {
          answers.add(agent.ask(curTrigram, null));
        }
        tempWord = tempWord.substring(1, tempWord.length());
      }
      return evaluateAnswers(answers);
    }
  }

  /**
   * Returns what this current leave thinks would be most appropriate if the whole
   * word would be just the item given and stored in this leave
   */
  protected AgentAnswer getLeaveAnswer()
  {
    AgentAnswer a = new AgentAnswer();
    a.setData(this.myDecisions);
    a.addReason(this.myToken);
    return a;
  }


  /**
   * Returns what this current leave thinks would be most appropriate if the whole
   * word would be just the item given and stored in this leave
   */
  protected AgentAnswer getStrongestAnswer(Hashtable decisions)
  {
    AgentAnswer a = new AgentAnswer();
		int i = 0;
		int allTogether = 0;
		String found = new String("error");
    if ( decisions.isEmpty() )
    {
      found = new String("no match");
    }
		for ( Enumeration e = decisions.keys() ; e.hasMoreElements() ; )
		{
			String key    = (String)e.nextElement();
			Integer value = (Integer)decisions.get(key);
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
    a.setAgentID("Sliding Trigram");
    return a;
  }

  /**
   * Evaluates all the answers from the different leaves and creates one final
   * answer for the MetaAgent
   */
  protected AgentAnswer evaluateAnswers(Vector answers)
  {
    Hashtable voting = new Hashtable();
    Vector reasons = new Vector();
    for ( Enumeration e = answers.elements() ; e.hasMoreElements() ; )
    {
      AgentAnswer curAnswer = (AgentAnswer)e.nextElement();
      Vector v = curAnswer.getData();
      Hashtable curDecisions = (Hashtable)v.firstElement();
      voting = addFrequency(voting, curDecisions);
      reasons.add(v.elementAt(1));
    }
    AgentAnswer answer = getStrongestAnswer(voting);
    answer.setReasons(reasons);
    return answer;
  }

  /**
   * Adds the frequency information from the second given Hashtable to the
   * first given
   */
  protected Hashtable addFrequency(Hashtable voting, Hashtable curDecisions)
  {
    for ( Enumeration e = curDecisions.keys() ; e.hasMoreElements() ; )
    {
      String curVal = (String)e.nextElement();
      if ( voting.containsKey(curVal) )
      {
        voting.put(curVal, new Integer( ((Integer)voting.get(curVal)).intValue()
         + ((Integer)curDecisions.get(curVal)).intValue() ) );
      }
      else
      {
        voting.put(curVal, curDecisions.get(curVal) );
      }
    }
    return voting;
  }

  /**
   * Returns the control element for this agent
   */
	public JPanel getUI()
	{
		return new UIAgent3(this);
	}

  /**
   * Returns true if the node is the root in comparison to MetaAgent. Actually
   * MetaAgent is root, but for the algorithm it is important which node is ITS
   * root and this is the next node after MetaAgent
   */
  protected boolean isRoot()
  {
    if ( this.getParent().getClass().getName().lastIndexOf("MetaAgent") >= 0 )
    {
      return true;
    }
    return false;
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
    if ( this.children == null )
    {
      return false;
    }
		for(Enumeration e = this.children.elements() ; e.hasMoreElements() ; )
		{
			if ( ((Agent3)e.nextElement()).getToken().equals(ch) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 *	Returns the node with the given token
	 **/
	protected Agent3 getLeave(String ch)
	{
    if ( this.children == null )
    {
      return null;
    }
		for( Enumeration e = this.children.elements() ; e.hasMoreElements() ; )
		{
			Agent3 n = (Agent3)e.nextElement();
			if ( n.getToken().equals(ch) )
			{
				return n;
			}
		}
		return null;
	}

  /**
   * Returns the trigram of this node
   */
  public String getToken()
  {
    return this.myToken;
  }

  public String toString()
  {
		if ( IDString == null )
		{
			return "[ "+myToken+" ] "+myDecisions;
		}
		else
		{
			return "[ "+IDString+" ] "+myDecisions;
		}
  }

}