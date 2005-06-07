package com.bordag.paraexp.eval;

import java.util.*;

import com.bordag.paraexp.*;

/**
 * @author Stefan Bordag
 */
public class EvalTree
{
  // This map is the index over all wordforms onto the corresponding nodes
  protected Map PSMap = null;


  public EvalTree(Map psMap)
  {
    this.PSMap = psMap;
  }

  public Iterator getAllFathersWordForms(ParaSynWord word)
  {
    return new IterFatherWords(this.PSMap, word);
  }

  public Iterator getLeavesInnerNodes()
  {
    return new IterInnerNodes(this.PSMap);
  }

  /**
   * Returns true if possFather is an existing node higher up in the hierarchy
   * from the word.
   * Simply goes up in the hierarchy until hitting a root.
   * @param word
   * @param possFather
   * @return
   */
  public boolean isFatherOf(ParaSynWord word, ParaSynWord possFather)
  {
    EvalNode father = (EvalNode)this.PSMap.get(word);
    while ( father != null )
    {
      if ( father.containsWordForm(possFather) )
      {
        return true;
      }
      father = father.getFather();
    }
    return false;
  }

  /**
   * This class gives an iterator over all father wordForms
   * node
   */
  class IterFatherWords implements Iterator
  {
    Iterator myIt = null;

    public IterFatherWords(Map map, ParaSynWord word)
    {
      Map psMap = map;
      Vector elements = new Vector();
      EvalNode father = ((EvalNode)psMap.get(word)).getFather();
      while ( father != null )
      {
        elements.addAll(father.getWordForms());
        father = father.getFather();
      }
      System.out.println("Fathers are: "+elements.size());
      this.myIt = elements.iterator();
    }

    public boolean hasNext()
    {
      return this.myIt.hasNext();
    }

    public Object next()
    {
      return this.myIt.next();
    }

    public void remove()
    {
      System.err.println("This feature is not implemented: com.bordag.paraexp.eval.EvalTree.IterFatherWords.remove()");
    }
  }


  /**
   * This class gives an iterator over those nodes which have a non-null father
   * node
   */
  class IterInnerNodes implements Iterator
  {
    Iterator myIt = null;

    public IterInnerNodes(Map map)
    {
      Map psMap = map;
      Vector elements = new Vector();
      for ( Iterator it = psMap.keySet().iterator() ; it.hasNext() ; )
      {
        Object key = it.next();
/*        System.out.println("For key ["+key+"]");
        System.out.println("For key ["+key.getClass()+"]");
        System.out.println("we have ["+psMap.get(key)+"]");*/
        if ( ((EvalNode)psMap.get(key)).getFather() != null )
        {
          elements.add(key);
        }
      }
      this.myIt = elements.iterator();
    }

    public boolean hasNext()
    {
      return this.myIt.hasNext();
    }

    public Object next()
    {
      return this.myIt.next();
    }

    public void remove()
    {
      System.err.println("This feature is not implemented: com.bordag.paraexp.eval.EvalTree.IterInnerNodes.remove()");
    }
  }

}