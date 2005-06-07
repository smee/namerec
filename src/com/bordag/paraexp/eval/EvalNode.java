package com.bordag.paraexp.eval;

import java.util.*;
import com.bordag.paraexp.*;

/**
 * A node represents a word by all of it's wordforms and has links to the
 * fathernode as well as to the sons
 *
 * @author Stefan Bordag
 */
public class EvalNode
{

  // This vector contains ParaSynWord instances which are the various wordforms
  // of this word
  public List wordForms = null;

  // This vector contains instances of EvalNode which are the sons of this node
  public List sons = null;

  // This is the link to the father of this node
  public EvalNode father = null;

  public EvalNode()
  {
    init();
  }

  public EvalNode(ParaSynWord word)
  {
    init();
    this.wordForms.add(word);
  }

  protected void init()
  {
    this.wordForms = new Vector();
    this.sons = new Vector();
  }

  public void addWordForm(ParaSynWord wordform)
  {
    this.wordForms.add(wordform);
  }

  public void addSon(EvalNode node)
  {
    this.sons.add(node);
  }

  public List getSons()
  {
    return this.sons;
  }

  public EvalNode getFather()
  {
    return this.father;
  }

  public List getWordForms()
  {
    return this.wordForms;
  }

  public boolean containsWordForm(ParaSynWord word)
  {
    if ( this.wordForms.contains(word) )
    {
      return true;
    }
    return false;
  }

  public String toString()
  {
    return " EvalNode: <"+wordForms+" : s"+this.sons+"> ";
  }
}