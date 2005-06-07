package com.bordag.parasyn;

import com.bordag.parasyn.util.*;

import java.util.*;

/**
 * Contains the word and it's collocations where for each collocation (key in
 * the map) there is a set of it's collocations.
 * All words here are instances of PSWord, only the keys in the map have the
 * Strings as well for printout.
 *
 */

public class PSData
{
  /**
   * The word, which is represented by this data object
   */
  protected PSWord word = null;

  /**
   * The collocations with their collocations in a set
   * {PSWord -> Vector(PSWord)}
   */
  protected Map collocations = null;

  protected Polygon pruneRegion = null;
  protected static final double[] pruneRegionEdges = {-1.0,-1.0, -1.0,0.4, 0.3,0.4, 0.4,0.3, 0.4,-1.0, -1.0,-1.0};

  public PSData(PSWord word)
  {
    init();
    this.word = word;
  }

  public PSData(PSWord word, Map collocations)
  {
    init();
    this.word = word;
    this.collocations.putAll(collocations);
  }

  private void init()
  {
    this.collocations = new HashMap();
  }

//-------- ADDS

  public void addCollocation(PSWord word, Set collocations2)
  {
    this.collocations.put(word, collocations2);
  }

  public void addCollocation(PSWord word)
  {
    this.collocations.put(word, null);
  }

  public void addString(PSWord word, String string)
  {
    if ( word == null || string == null )
    {
      System.out.println("Warning: in PSData, addString called with null! ("+word+","+string+")");
      return;
    }
    Object obj = this.collocations.get(word);
    word.word = new CHString(string);
    this.collocations.put(word, obj);
  }


//------GETS

  public ParaSynMap getParaSynMap()
  {
    HashSet newSet = new HashSet();
    for ( Iterator it = this.collocations.keySet().iterator() ; it.hasNext() ; )
    {
      newSet.add(it.next());
    }
    // since I cant change a keyset later on...
    ParaSynMap map = new ParaSynMap(newSet);
    map.setWord(this.word);
    return map;
  }

  public List getCollocationsList()
  {
    Vector retVec = new Vector();
    for ( Iterator it = this.collocations.keySet().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      retVec.add(curWord);
    }
    return retVec;
  }

  public Set getCollocationsSet()
  {
    return this.collocations.keySet();
  }

  public List getCollocationsOfCollocation(PSWord word)
  {
    Set set = (Set)this.collocations.get(word);
    List list = new Vector();
    if ( set == null )
    {
      return list;
    }
    for ( Iterator it = set.iterator() ; it.hasNext() ; )
    {
      list.add(it.next());
    }
    return list;
  }

  public void normalize()
  {
    calcNormalizedYValues();
    normalizeSignificances();
    removeCollsOfColls();
  }

  public void prune()
  {
    // create the pruning region
    this.pruneRegion = new Polygon(this.pruneRegionEdges);
    // now prune it
    // we approximate that pruning will delete half of the items
    HashMap newMap = new HashMap((int)((double)this.collocations.size()/2.0));
    for ( Iterator it = this.collocations.keySet().iterator() ; it.hasNext() ; )
    {
      PSWord curKey = (PSWord)it.next();
      Object curValue = this.collocations.get(curKey);
      if ( this.pruneRegion.outside(curKey.getX(), curKey.getY()) )
      {
        newMap.put(curKey, curValue);
      }
    }
    this.collocations = newMap;
  }


  /**
   * Gets a list of PSWords and returns a list of Integers
   * @param psWords List(Integer)
   * @return Vector(Integer)
   */
  protected static List getPSWordsAsIntegers(List psWords)
  {
    Vector retVec = new Vector();
    for (Iterator it = psWords.iterator(); it.hasNext(); )
    {
      PSWord psWord = (PSWord) it.next();
      retVec.add(psWord.wordNr);
    }
    return retVec;
  }

  protected void calcNormalizedYValues()
  {
    // need a vector of Integers which are the collocations
    // then
    List colls = PSData.getPSWordsAsIntegers(this.getCollocationsList());
    double maxValue = Double.MIN_VALUE;
    double minValue = Double.MAX_VALUE;
    // calculating the maximum values
    for ( Iterator it = this.getCollocationsList().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      List collsOfColl = PSData.getPSWordsAsIntegers(this.getCollocationsOfCollocation(curWord));
      double curYValue = PSDataFactory.compare(colls, collsOfColl);
      //System.out.println("New3: word: ["+curWord+"] having "+collsOfColl.size()+" collocations has count of "+curYValue+" ");
      if ( true )//curYValue <= 1.0 && curYValue >= 0.0 )
      {
        curWord.xValue = curYValue;
        if ( curYValue > maxValue )
        {
          maxValue = curYValue;
        }
        if ( curYValue < minValue )
        {
          minValue = curYValue;
        }
      }
    }
    // normalize it to be between 0 and 1
//System.out.println("-------->minValue="+minValue+" maxValue="+maxValue);
    maxValue -= minValue;
//System.out.println("-------->minValue="+minValue+" maxValue="+maxValue);
    for ( Iterator it = this.getCollocationsList().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = ( PSWord )it.next();
      double tt = curWord.xValue;
//System.out.print("New4: word: ["+curWord+"] gets normalized to ");
      curWord.xValue -= minValue;
      curWord.xValue = curWord.xValue / maxValue ;
//System.out.println(""+curWord);
//System.out.println("minValue="+minValue+" maxValue="+maxValue+" tt="+tt+" yValue="+curWord.yValue);
      if ( curWord.xValue < 0.0 )
      {
      System.out.println("Illegal negative value detected! minValue="+minValue+" maxValue="+maxValue+" tt="+tt+" yValue="+curWord.xValue);
          System.exit(0);
      }
    }

  }

  protected void normalizeSignificances()
  {
    // get maxSig
    double maxSig = Double.MIN_VALUE;
    double minSig = Double.MAX_VALUE;
    for ( Iterator it = this.getCollocationsList().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      ////System.out.print("New: word: ["+curWord+"] gets normalized to ");
      //curWord.significance = Math.log( curWord.significance );
      ////System.out.println("["+curWord+"]");
      if ( curWord.significance > maxSig )
      {
        maxSig = curWord.significance;
      }
      if ( curWord.significance < minSig )
      {
        minSig = curWord.significance;
      }
    }
    maxSig = maxSig - minSig;
    maxSig = Math.log(maxSig);
    ////System.out.println("------> maxSig = "+maxSig+" minSig = "+minSig);
    // then divide each one by maxSig, after taking log (of max as well)
    for ( Iterator it = this.getCollocationsList().iterator() ; it.hasNext() ; )
    {
      PSWord curWord = (PSWord)it.next();
      ////System.out.print("New2: word: ["+curWord+"] gets normalized to ");
      //curWord.significance = Math.log(curWord.significance) / maxSig;
      curWord.significance = curWord.significance - minSig;
      if ( curWord.significance > 0.0 ) { curWord.significance = Math.log (curWord.significance); }
      curWord.significance = curWord.significance / maxSig;
      ////System.out.println("["+curWord+"]");
    }
  }

  /**
   *
   * @param myData
   * @return
   */
  protected void removeCollsOfColls()
  {
    HashMap newMap = new HashMap();
    for ( Iterator it = this.collocations.keySet().iterator() ; it.hasNext() ; )
    {
      newMap.put(it.next(), null);
    }
    this.collocations = newMap;
  }


  public String toString()
  {
    return this.word+" : "+this.collocations;
  }

}