package com.bordag.sgz.algorithms;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * This class calculates the triple approximation.
 *
 * @author  Stefan Bordag
 * @date    28.12.2001
 */
public class NonThreadTriangles
{
  /**
   * The reference to the object which gives us categories
   */
  private HashSet input = null;

  /**
   * The reference to the DB
   */
  private DBConnection connection = null;

  /**
   * The reference to the container which holds our calculated results
   */
  private HashSet output = null;

  /**
   * The maxRecursion constant tells how deep we allow the algorithm to look
   * recursively
   */
  private int maxRecursion = 0;

  /**
   * The minRecursion constant tells how deep the algorithm should look at least
   */
  private int minRecursion = 0;

  public NonThreadTriangles()
  {
  }

  /**
   * Constructer, does not start the algorithm
   */
  public NonThreadTriangles(DBConnection connection, Set input) throws IllegalArgumentException
  {
    if ( connection == null || input == null )
    {
      throw new IllegalArgumentException("In SumCounter.SumCounter(DBConnection, PropertySet) null connection invalid");
    }

    this.input = new HashSet();
    this.connection = connection;
    for ( Iterator it = input.iterator() ; it.hasNext() ; )
    {
      this.input.add(it.next());
    }
    init();
  }

  /**
   * same constructor, just for convenience
   */
  public NonThreadTriangles(DBConnection connection, List input) throws IllegalArgumentException
  {
    if ( connection == null || input == null )
    {
      throw new IllegalArgumentException("In SumCounter.SumCounter(DBConnection, PropertySet) null connection invalid");
    }

    this.input = new HashSet();
    this.connection = connection;
    for ( Iterator it = input.iterator() ; it.hasNext() ; )
    {
      this.input.add(it.next());
    }
    init();
  }

  private void init()
  {
    this.output = new HashSet();
    this.maxRecursion = new Integer(Options.getInstance().getTriMaxRecursion()).intValue();
    this.minRecursion = new Integer(Options.getInstance().getTriMinRecursion()).intValue();
  }

  /**
   * Runs the algorithm and returns the results
   */
  public HashSet getResults()
  {
    try
    {
      runAlgorithm();
    }
    catch ( Exception ae )
    {
      ae.printStackTrace();
    }
    return output;
  }

  /**
   * Performs error checking and runs the algorithm on choosed sets
   */
  private void runAlgorithm()
  {
    if ( this.input == null || this.input.size() < 3 )
    {
      Debugger.getInstance().println("WARNING: Not enough words to process Triangles! ",1);
      return;
    }
    else
    {
      Vector newFoundWords = new Vector();
      Permutator perm = new Permutator(this.input,3);

      while ( perm.hasMore() )
      {
        Object[] permutation = perm.getNext();
        Debugger.getInstance().println("Calculating next triangle : "+permutation[0]+" "+permutation[1]+" "+permutation[2],2);
        Vector temp = null;
        try
        {
          temp = triangles(0, (ComparableStringBuffer)permutation[0],(ComparableStringBuffer)permutation[1],(ComparableStringBuffer)permutation[2]);
        }
        catch ( Exception ex )
        {
    	    System.err.println("null caught in runAlgorithm.");
	        ex.printStackTrace();
          continue;
        }
        if ( temp == null || temp.size() < 1 )
        {
        }
        else
        {
          this.output.addAll(temp);
        }
      }
    }
  }

  /**
   * This is the actual triangle algorithm, the first implementation.
   */
  private Vector triangles(int recursionCount, ComparableStringBuffer word1, ComparableStringBuffer word2, ComparableStringBuffer word3 )
  {
    if ( word1 == null || word1.toString().length() < 1
      || word2 == null || word2.toString().length() < 1
      || word3 == null || word3.toString().length() < 1 )
    {
      String s = "word1 = "+word1+" word2 = "+word2+" word3 = "+word3+" ";
      throw new IllegalArgumentException("In NonThreadTriangles.triangles(ComparableStringBuffer,ComparableStringBuffer,ComparableStringBuffer) invalid argument! \n"+s);
    }

    Vector mc1 = getMatchingCollocations(word1,word2);
    Vector mc2 = getMatchingCollocations(word1,word3);
    Vector mc3 = getMatchingCollocations(word2,word3);

    Vector mcA = getMatchingElements(mc1, mc2, mc3);
    // abort pattern #1 when we found some good candidates
    if ( mcA != null && mcA.size() > 0 && recursionCount >= this.minRecursion )
    {
      return mcA;
    }

    // recursion pattern #1 when there are clues about possible candidates
    if ( mc1 != null && mc1.size() > 0
      && mc2 != null && mc2.size() > 0
      && mc3 != null && mc3.size() > 0 )
    {
      Vector retVal = new Vector();
      for ( int i = 0 ; i < mc1.size() && i < 5 ; i++ )
      {
        for ( int j = 0 ; j < mc2.size() && j < 5 ; j++ )
        {
          for ( int k = 0 ; k < mc3.size() && k < 5 ; k++ )
          {
            Vector v = null;
      	    if ( recursionCount < this.maxRecursion )
	          {
              Debugger.getInstance().println("Calling triangles with rec = "+(recursionCount+1),2);
              v = triangles(recursionCount+1,
	                          (ComparableStringBuffer)mc1.elementAt(i),
                            (ComparableStringBuffer)mc2.elementAt(j),
                            (ComparableStringBuffer)mc3.elementAt(k));
	          }
            else
            {
            //Output.println("not descending into recursion : rC = "+recursionCount+" mR= "+this.maxRecursion+" ");
            }
            if ( v != null && v.size() > 0 )
            {
              HashSet set = new HashSet();
              set.addAll(v);
            Output.println("Found following new words: "+this.connection.getWordsForNumbers(set));

              retVal.addAll(v);
            }
          }
        }
      }
      return retVal;
    }
    else // abort pattern #2 Not enough clues to continue
    {    // ( as we won't get a full triangle again at this point )
      return null;
    }
  }

  /**
   * Directly prints the set of words divided by spaces
   */
  private void printSetResolving(Vector set, String divider)
  {
    if ( set == null )
    {
      Output.print("null-set ");
      return;
    }
    for ( Enumeration enum = set.elements() ; enum.hasMoreElements() ; )
    {
      Output.print(this.connection.getWordForNumber((ComparableStringBuffer)enum.nextElement())+divider);
    }
  }

  /**
   * Returns the set of collocations which both words have in common
   */
  private Vector getMatchingCollocations(ComparableStringBuffer word1, ComparableStringBuffer word2)
  {
    if ( word1 == null || word1.toString().length() < 1
      || word2 == null || word2.toString().length() < 1 )
    {
      String s = "word1 = "+word1+" word2 = "+word2+" ";
      throw new IllegalArgumentException("In Triangles.getMatchingCollocations(ComparableStringBuffer,ComparableStringBuffer) invalid argument! \n"+s);
    }
    Vector retVal = new Vector();

    ComparableStringBuffer[][] kollokOfWord1 = this.connection.getTriAssociatedOf(word1);
    ComparableStringBuffer[][] kollokOfWord2 = this.connection.getTriAssociatedOf(word2);

    ComparableStringBuffer[][] toHash = null;
    ComparableStringBuffer[][] toSearch = null;
    if ( kollokOfWord1.length < kollokOfWord2.length )
    {
      toHash = kollokOfWord2;
      toSearch = kollokOfWord1;
    }
    else
    {
      toHash = kollokOfWord1;
      toSearch = kollokOfWord2;
    }

    HashMap hash = new HashMap();
    for ( int i = 0 ; i < toHash.length ; i++ )
    {
      hash.put(toHash[i][0],null);
    }

    for ( int i = 0 ; i < toSearch.length ; i++ )
    {
      if ( toSearch[i][0].equals(word1) || toSearch[i][0].equals(word2) )
      {
        continue;
      }
      if ( hash.containsKey(toSearch[i][0]) )
      {
        retVal.add(toSearch[i][0]);
      }
    }

//    Debugger.getInstance().println("calculated matching: "+retVal.size(),2);
    return retVal;
  }

  /**
   * Returns the set of Elements which are equal in all the given Vectors or null
   */
  private Vector getMatchingElements(Vector v1, Vector v2, Vector v3)
  {
    if ( v1 == null
      || v2 == null
      || v3 == null )
    {
      String s = "v1 = ["+v1+"] v2 = ["+v2+"] v3 = ["+v3+"] ";
      throw new IllegalArgumentException("In NonThreadTriangles.getMatchingElements(Vector,Vector,Vector) invalid argument! \n"+s);
    }
    Vector retVal = null;
    retVal = getMatchingElements(v1,v2);
    if ( retVal != null && retVal.size() < 1 )
    {
      return getMatchingElements(retVal,v3);
    }
    return retVal;
  }

  /**
   * Returns the set of Elements which are equal in the two given Vectors
   *
   * WARNING : This method has no errorchecking at all for performance reasons
   * it is suposed to be called by getMatchingElements(Vector,Vector,Vector)
   * which does the neccessary errorchecking.
   */
  private Vector getMatchingElements(Vector v1, Vector v2)
  {
    Vector toHash = null;
    Vector toSearch = null;
    if ( v1.size() < v2.size() )
    {
      toHash = v2;
      toSearch = v1;
    }
    else
    {
      toHash = v1;
      toSearch = v2;
    }

    Vector retVal = new Vector();
    HashMap hash = new HashMap();
    for ( int i = 0 ; i < toHash.size() ; i++ )
    {
      hash.put(toHash.elementAt(i),null);
    }

    for ( int i = 0 ; i < toSearch.size() ; i++ )
    {
      if ( hash.containsKey(toSearch.elementAt(i)) )
      {
        retVal.add(toSearch.elementAt(i));
      }
    }
    return retVal;
  }

}
