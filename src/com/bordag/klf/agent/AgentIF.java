package com.bordag.klf.agent;

import java.util.*;
import javax.swing.*;

/**
*	This interface is ought to hide implementations of 
*	different agents
*	@author		Stefan Bordag
**/
public interface AgentIF
{
	// fields

	// methods
	public AgentAnswer ask(String word,AgentAnswer a);
	public void learn(String word,String value);
	
	public JPanel getUI();

}// End of LearnerIF.java
