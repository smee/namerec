package com.bordag.klf.agent;

import java.util.*;

/**
*	This class encapsulates the answer structure the different agents
*	can give
*	@author Stefan Bordag
**/
public class AgentAnswer
{
	private Vector reasons; // here some meaningful information can be stored
	private String answer; // here the actual answer is stored
	private String AgentID; // here the answering Agint identifies himself
	private double strength; // the decisionstrength

	public AgentAnswer()
	{
		reasons = new Vector();
		answer = "";
		AgentID = "";
		strength = 0.0;
	}

// ------ setter and getter methods

	public void addReason(String newReason)
	{
		reasons.add(newReason);
	}

  public void setReasons(Vector reasons)
  {
    this.reasons = reasons;
  }

  /**
   * If you wish the AgentAnswers to carry some data which you then use by
   * yourself to examine, us setData and getData.
   */
  public void setData(Object data)
  {
    reasons.add(data);
  }

  public Vector getData()
  {
    return reasons;
  }

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public void setAgentID(String ID)
	{
		this.AgentID = ID;
	}

	public void setStrength(double strength)
	{
		this.strength = strength;
	}

	public Vector getReasons()
	{
		return reasons;
	}

	public String getAnswer()
	{
		return answer;
	}

	public String getAgentID()
	{
		return AgentID;
	}

	public double getStrength()
	{
		return strength;
	}

	public String toString()
	{
		return AgentID+" "+reasons+" "+answer+" "+strength+"%";
	}
}// End of AgentAnswer.java
