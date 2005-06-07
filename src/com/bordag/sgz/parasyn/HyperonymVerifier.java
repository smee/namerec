package com.bordag.sgz.parasyn;

import com.bordag.sgz.util.*;

import java.util.*;

/**
 * This takes a word as argument,
 * calculates is ParaSynMap,
 * gets the resulting cohyponyms,
 * calculates their maps,
 * checks which words hit which element from 1hyperonymset most
 * and prints it all out
 *
 * <p>Description: Abteilungsinternes Entwicklungstool</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Stefan Bordag
 */
public class HyperonymVerifier
{
  public final static int MAX_PRUNED_SIZE = 15;
  protected CachedDBConnection connection = null;
  protected ParaSynMap myWordMap = null;

  public HyperonymVerifier(String word, CachedDBConnection connection)
  {
    this.connection = connection;
    // calculate the initial map
    System.out.println("Calculate the initial map of "+word);
    try
    {
      this.myWordMap = new ParaSynMap(word, connection);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }

System.out.println("Generating all other maps: ");
    // get all cohyponyms of the inputword and calculate their maps,
    // storing it all in a Hashtable where elements are accessible via
    // wordnumber
    HashSet cohyponyms = this.myWordMap.getCohyponyms();
    Hashtable cohypMaps = new Hashtable();
    for ( Iterator it = cohyponyms.iterator() ; it.hasNext() ; )
    {
      ParaSynInfo curP = (ParaSynInfo)it.next();
      System.out.println("creating map of "+curP);
      cohypMaps.put( curP.getWordNr(), new ParaSynMap(curP.getWordNr(), connection) );
    }
    cohypMaps.put( this.myWordMap.getMyWordNr(), this.myWordMap );

    // now create a hashtable where as keys there are wordnrs which we get from
    // initial map the hyperonyms and as values Vectors containing the inputword
    // as sole element

System.out.println("Creating hypotable");
    Hashtable hypoTable = new Hashtable();
    // now go through all cohypMaps and for each word in their Hyperonymmaps
    // check whether is's already in hypoTable and if not, add it with a vector
    // containing the wordNr of the cohyponym and if yes, add the wordNr of the
    // cohyponym to the vector which should be there
    for ( Enumeration enum = cohypMaps.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      ParaSynMap curMap = (ParaSynMap)cohypMaps.get(curKey);
      for ( Iterator it = curMap.getHyperonyms().iterator() ; it.hasNext() ; )
      {
        ParaSynInfo curHypon = (ParaSynInfo)it.next();// this is the current Hyperonym
        Vector t = null;
        Integer curHyponKey = curHypon.getWordNr();
        if ( hypoTable.containsKey(curHyponKey) )
        {
          t = (Vector) hypoTable.get(curHyponKey);
        }
        else
        {
          t = new Vector();
        }
        t.add(curKey); // das gerade aktuelle Wort hat zu diesem Hyperonym beigetragen
        hypoTable.put(curHyponKey, t);
      }
    }

    // Now try to verify results with collocations by comparing cohyponyms with
    // the collocations
    /**
     * das beste hat 20 Kohyp-Elemente
     * Wenn von diesen 20 18 in den Kollokationen enthalten sind, ist der Wert 0.9 = 18/20
     *
     * Der naechste hat, sagen wir, nur noch 18 Elemente, dessen Maximalwert ist nicht mehr 1 sondren 18/20. Von diesen 18 sind dann auch noch nur 16  in den Kollokationen, der Gesamtwert ist demnach 0.8
     */
    Hashtable prunedHypoTable = new Hashtable();
    int maxKohypCount = -1;

    System.out.println("Results: \n");
    while ( hypoTable.size() > 0 )
    {
      Integer curMaxKey = getMaxValueKey(hypoTable);
//      System.out.print(this.connection.getWordForNumber( new ComparableStringBuffer( curMaxKey.toString() ) )+" : ");
      Vector curVec = (Vector)hypoTable.get(curMaxKey);
      if ( maxKohypCount < 0 ) { maxKohypCount = curVec.size(); }  // preset maxCount
      if ( prunedHypoTable.size() < this.MAX_PRUNED_SIZE )
      {
        prunedHypoTable.put(curMaxKey, curVec);
      }
      else
      {
        break;
      }
/*      for ( Iterator it = curVec.iterator() ; it.hasNext() ; )
      {
        Integer curWord = (Integer)it.next();
        if ( it.hasNext() )
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + ", ");
        }
        else
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + " ");
        }
      }
      System.out.println("]");*/
      hypoTable.remove(curMaxKey);
    }

    compareKohypsWithColloc(prunedHypoTable,maxKohypCount);

  }

  /**
   * For each Key gets values which is a vector which can be compared to the
   * collocations of the key
   * @param kohypTable
   */
  public void compareKohypsWithColloc(Hashtable kohypTable,int maxCount)
  {
    for ( Enumeration enum = kohypTable.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      Vector kohyps = (Vector)kohypTable.get(curKey);
      Vector collocs = getCollocations(curKey);
      double matchCount = (double)ParaSynMap.countMatchingElements(kohyps, collocs);
      double curGrade = (matchCount / (double)kohyps.size()) * ((double)kohyps.size() / maxCount );
      System.out.print(this.connection.getWordForNumber( new ComparableStringBuffer( curKey.toString() ) )+" : "+curGrade+" : [ ");
      for ( Iterator it = kohyps.iterator() ; it.hasNext() ; )
      {
        Integer curWord = (Integer)it.next();
        if ( it.hasNext() )
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + ", ");
        }
        else
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + " ");
        }
      }
      System.out.println("]");
    }
  }

  /**
   * Retrieves the collocations of a given word(number) as a pure Integer vector
   */
  protected Vector getCollocations(Integer word)
  {
    Vector retVec = new Vector();
    String[] s = new String[4];
    s[0] = Options.getInstance().getParaMinSignifikanz();
    s[1] = Options.getInstance().getParaMinWordNr();
    s[2] = word.toString();
    s[3] = Options.getInstance().getParaMaxKollokationen();
    String query = Options.getInstance().getParaQueryKollokationen();
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(query,s);
    if ( buffer == null || buffer.length < 1 )
    {
      System.err.println("HyperonymVerifier: Wordnr ["+word+"]: had no collocations!");
      return retVec;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        Integer val = new Integer(buffer[i][0].toString());
        if ( ! word.equals(val) )
        {
          retVec.add(val);
        }
      }
    }
    return retVec;
  }

  /**
   * Assumes that the Hashtable has Vectors as values and returns the key
   * whose vector is the largest.
   * @param table
   * @return
   */
  protected Integer getMaxValueKey(Hashtable table)
  {
    Integer maxKey = null;
    int maxVec = 0;
    for ( Enumeration enum = table.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      Vector curVec = (Vector)table.get(curKey);
      if ( curVec.size() > maxVec )
      {
        maxKey = curKey;
        maxVec = curVec.size();
      }
    }
    return maxKey;
  }

    // now basically print the hypoTable
//    System.out.println("The hypoTable looks now like this: ["+hypoTable+"]\n\n");
/*    for ( Enumeration enum = hypoTable.keys() ; enum.hasMoreElements() ; )
    {
      Integer curKey = (Integer)enum.nextElement();
      Vector curVec = (Vector)hypoTable.get(curKey);

      System.out.print(this.connection.getWordForNumber( new ComparableStringBuffer( curKey.toString() ) )+" : [");
      for ( Iterator it = curVec.iterator() ; it.hasNext() ; )
      {
        Integer curWord = (Integer)it.next();
        if ( it.hasNext() )
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + ", ");
        }
        else
        {
          System.out.print(this.connection.getWordForNumber(new ComparableStringBuffer(curWord.toString())) + " ");
        }
      }
      System.out.println("]");
    }
  }*/

  public static void main(String[] args)
  {
    String word = "Zink";
    if ( args.length > 0 && args[0].length() > 0)
    {
      word = args[0];
    }
    else
    {
      System.out.println("Which word to calculate?");
      System.exit(0);
    }

    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    CachedDBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
      HyperonymVerifier ver = new HyperonymVerifier(word,connection);
//      System.out.println("After some processed: "+p);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }

  }

}