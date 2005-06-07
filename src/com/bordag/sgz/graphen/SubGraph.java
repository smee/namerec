package com.bordag.sgz.graphen;

// app specific imports
import com.bordag.sgz.util.*;

// standard imports
import java.util.*;

/**
 * Construct a graph, retrieving for each word its collocationset, deleting
 * all not in the wordset contained words (number-words warning) and calling
 * addConnection on the graph with the curWord and each remaining in the set
 *
 * @author  Stefan Bordag
 * @date    23.04.2002
 */
public class SubGraph
{

  /**
   * Reference to DB
   */
  private DBConnection connection = null;

  /**
   * Stores the nodes in a Hashtable with the wordnumbers as keys and a HashSet
   * of connected wordnumbers as value.
   */
  protected Hashtable nodes = null;

  /**
   * As connections are created this variable just counts them
   **/
  protected int connectionsCount = 0;

  /**
   * The query to retrieve the collocations from the DB
   */
  public static final String COLLOCATIONS_QUERY =
  "SELECT k.wort_nr2,k.signifikanz FROM kollok_sig k WHERE k.signifikanz > 1 and k.wort_nr1 = "+DBConnection.ARGUMENT_PLACE+" order by k.signifikanz desc limit 300;";

  public SubGraph(DBConnection connection, Set wordsAsNumbers)
  {
    this.connection = connection;
    this.nodes = new Hashtable();
    buildConnectionsFromDB(wordsAsNumbers);
  }

  public SubGraph(DBConnection connection, List wordsAsNumbers)
  {
    this.connection = connection;
    this.nodes = new Hashtable();
    HashSet wordSet = new HashSet();
    wordSet.addAll(wordsAsNumbers);
    buildConnectionsFromDB(wordSet);
  }
  /**
   * construct a graph, retrieving for each word its collocationset, deleting
   * all not in the wordset contained words (number-words warning) and calling
   * addConnection on the graph with the curWord and each remaining in the set
   */
  protected void buildConnectionsFromDB(Set words)
  {
    for ( Iterator it = words.iterator() ; it.hasNext() ; )
    {
      ComparableStringBuffer wordNumber = (ComparableStringBuffer)it.next();
      HashSet collocSet = getCollocationsOf(wordNumber);
      HashSet iteratorSet = (HashSet)collocSet.clone();
      for ( Iterator it2 = iteratorSet.iterator() ; it2.hasNext() ; )
      {
        Object curObject = it2.next();
        if ( ! words.contains(curObject) )
        {
          collocSet.remove(curObject);
        }
      }
      for ( Iterator it2 = collocSet.iterator() ; it2.hasNext() ; )
      {
        addConnection(new Integer(wordNumber.toString()), new Integer( ((ComparableStringBuffer)it2.next()).toString()));
      }
    }
  }

  /**
   * Returns always a HashSet. If query was successfull it will contains
   * collocatoins of the given wordnumber
   */
  private HashSet getCollocationsOf(ComparableStringBuffer wordNumber)
  {
    HashSet retSet = new HashSet();
    String[] s = new String[1];
    s[0] = wordNumber.toString();
    ComparableStringBuffer[][] buffer = this.connection.getResultsOf(this.COLLOCATIONS_QUERY,s);
    if ( buffer == null || buffer.length < 1 )
    {
      return retSet;
    }
    for ( int i = 0 ; i < buffer.length ; i++ )
    {
      if ( buffer[i][0] != null )
      {
        retSet.add(buffer[i][0]);
      }
    }
    return retSet;
  }
  /**
   * Adds if not yet present both nodes to the hashtable and builds connections
   * between them (in the hashsets)
   */
  protected void addConnection(Object node1, Object node2)
  {
    if ( node1.equals(node2) )
    {
      return;
    }
    if ( ! this.nodes.containsKey(node1) )
    {
      this.nodes.put(node1, new HashSet());
    }
    if ( ! this.nodes.containsKey(node2) )
    {
      this.nodes.put(node2, new HashSet());
    }
    HashSet set = (HashSet)this.nodes.get(node1);
    if ( set.contains(node2) )
    {
      return;
    }
    set.add(node2);
    this.nodes.put(node1, set);

    HashSet set2 = (HashSet)this.nodes.get(node2);
    set2.add(node1);
    this.nodes.put(node2, set2);
    this.connectionsCount++;
  }

  /**
   * Calculates by how much the graph holds together
   */
  public double calculateConnectionsCoefficient()
  {
    double N = (double)this.nodes.size();
    double possibleConnectionsPerNode = N - 1.0;
    double meanValue = 0.0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      HashSet currentSet = (HashSet)this.nodes.get((Object)enum.nextElement());
      meanValue += (double)currentSet.size() / possibleConnectionsPerNode;
    }
    meanValue = meanValue / N;
    return meanValue;
  }

  /**
   * For each node a
   *   if this node knows a set of other nodes B, then for each node b there
   *   if b knows c which is not b or a then
   *   if a knows c
   **/
  public double calculateCliquishness()
  {
    double retVal = 0.0;
    double count = 0.0;
    for ( Enumeration enum = this.nodes.keys() ; enum.hasMoreElements() ; )
    {
      Integer curObjectA = (Integer)enum.nextElement();
      HashSet nodesB = (HashSet)this.nodes.get(curObjectA);
      for ( Iterator it = nodesB.iterator() ; it.hasNext() ; )
      {
        Integer curObjectB = (Integer)it.next();
        if ( curObjectB.equals(curObjectA) )
        {
          continue;
        }
        HashSet nodesC = (HashSet)this.nodes.get(curObjectB);
        for ( Iterator it2 = nodesC.iterator() ; it2.hasNext() ; )
        {
	        Integer curObjectC = (Integer)it2.next();
          if ( curObjectC.equals(curObjectA) || curObjectC.equals(curObjectB) )
          {
	          continue;
          }
          HashSet nodesD = (HashSet)this.nodes.get(curObjectB);
          count += 1.0;
          for ( Iterator it3 = nodesD.iterator() ; it3.hasNext() ; )
          {
            Integer curObject = (Integer)it3.next();
            if ( curObject.equals(curObjectA) || curObject.equals(curObjectB) )
            {
              continue;
            }
            if ( nodesB.contains(curObject) )
            {
              retVal += 1.0;
            }
          }
        }
      }
    }
    if ( count < 1.0 )
    {
      return 0.0;
    }
    double ratio = retVal/count;
    ratio = ratio / ((double) this.nodes.size() - 2.0);
    return ratio;
  }
}
