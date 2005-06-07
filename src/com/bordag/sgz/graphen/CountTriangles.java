package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Title:        Automatische Sachgebietszuordnung
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      n/a
 * @author Stefan Bordag
 * @version 1.0
 */

public class CountTriangles
{
  /**
   * The source of information about the graph
   **/
  private DBConnection connection = null;

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private static String NEIGHBOURS_LIMIT_1 = "100";

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private static String NEIGHBOURS_LIMIT_2 = "100";

  /**
   * The query with which to retrieve the neighbours of a word
   **/
  private final static String NEIGHBOURS_QUERY = "select k.wort_nr1 from kollok_sig k where k.wort_nr2 = #ARG# limit #ARG#;";

  private DoubleList doubleList = null;

  private int nodesCount = 0;

  public CountTriangles()
  {
    this.doubleList = new DoubleList();
   // connect to DB
   // retrieve all neighbours of inputWord
   // retrieve all neighbours for each neighbour, which are also connected to inputWord
   // add all retrieved sets into doubleList, don't care for double entries, as they will be ignored


  }

  public static void testRandomGraph(int nodes, int connections)
  {
    CountTriangles ct = new CountTriangles();
    for ( int i = 0 ; i < (int)Math.sqrt((double)nodes) ; i++ )
    {
      ct.doubleList.addPair(new Integer(0), new Integer( (int) ( ((double)nodes)*Math.random() )));
    }
    for ( int i = (int)Math.sqrt((double)nodes)+1; i < connections ; i++ )
    {
      ct.doubleList.addPair( new Integer( (int) ( ((double)nodes)*Math.random() )), new Integer( (int) ( ((double)nodes)*Math.random() )));
    }
    System.out.println("Found "+ct.countTriangles()+" triangles.");
  }

  /**
   * Puts all connections to neighblours and their interconnections into table
   */
  public void loadWord(DBConnection connection, Integer wordNum)
  {
    this.connection = connection;
    // get neighbours, put into HashSet
    HashSet neighbours = getNeighbours(wordNum, this.NEIGHBOURS_LIMIT_1);
    this.nodesCount++;
    this.nodesCount+= neighbours.size();
    // get for each element from HashSet neighbours and addToTable only those
    // which are also in HashSet
    for ( Iterator it = neighbours.iterator() ; it.hasNext() ; )
    {
      Integer curNeighbour = (Integer)it.next();
      // put {HashSet},wordNum into table
      this.doubleList.addPair(wordNum, curNeighbour);
      HashSet curSet = getNeighbours(curNeighbour, this.NEIGHBOURS_LIMIT_2);
      for ( Iterator it2 = neighbours.iterator() ; it2.hasNext() ; )
      {
        Integer curNeighboursNeighbour = (Integer)it2.next();
        if ( neighbours.contains(curNeighboursNeighbour) )
        {
          this.doubleList.addPair(curNeighbour, curNeighboursNeighbour);
        }
      }
    }
  }

  /**
   * Counts triangles with following procedure:
   * 1. Take and remove an arbitrary pair x,y
   * 2. Count all those z which correspond to exactly ( (x,z) and (z,y) )
   */
  public int countTriangles()
  {
    int retVal = 0;
    while ( ! this.doubleList.isEmpty() )
    {
      Integer[] curPair = this.doubleList.getNext();
      this.doubleList.removePair(curPair[0], curPair[1]);
      this.doubleList.removePair(curPair[1], curPair[0]);
      if ( this.doubleList.isEmpty() ) { break; }

      retVal += searchTriangles( curPair[0], curPair[1] );
    }
    return retVal;
  }

  /**
   * Returns a count of all triangles with edges x and y using following procedure:
   * Take HashSet of x - these are all now potential z's
   * Now look in HashSet of each potential z whether y is in there
   */
  private int searchTriangles(Integer x, Integer y)
  {
    int retVal = 0;
    HashSet potentialZSet = this.doubleList.getSet(x);
    if ( potentialZSet == null ) { return 0; }
    for ( Iterator it = potentialZSet.iterator() ; it.hasNext() ; )
    {
      Integer curZ = (Integer)it.next();
      if ( this.doubleList.getSet(curZ).contains(y) )
      {
        //System.out.println("Triangle: ("+x.intValue()+","+curZ.intValue()+","+y.intValue()+")");
        retVal++;
      }
    }
    return retVal;
  }

  /**
   * Returns an array with the neighbours of the given node or null
   **/
  private HashSet getNeighbours(Integer node, String limit)
  {
    HashSet resultSet = null;
    String[] args = new String[2];
    args[0] = node.toString();
    args[1] = limit;
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.NEIGHBOURS_QUERY, args);
    if ( resultsOfQuery == null || resultsOfQuery.length < 1 )
    {
      System.out.println("Warning, execution of query ["+this.NEIGHBOURS_QUERY+"] with args : ["+args[0]+","+args[1]+"] yielded no results!");
      return null;
    }
    resultSet = new HashSet();

    for ( int i = 0 ; i < resultsOfQuery.length ; i++ )
    {
      resultSet.add( new Integer(resultsOfQuery[i][0].toString()));
    }
    return resultSet;
  }

  /**
   * c = trunc(sqrt( 2*V - sqrt(2*V) ) ) + 1
   */
  public static int maxTriangles(int nodes, int connections)
  {
    int V = connections - nodes + 1;
    int c = (int)Math.floor(Math.sqrt(2*V - Math.sqrt(2*V)))+1;
    int k = c+2;
    int d = V - (c*(c-1))/2;

    int t = ( (k-1)*(k-1)*(k-1) - 3*(k-1)*(k-1) + 2*(k-1) ) / 6 +
            (d*(d+1))/2;

    return t;
  }

  public static void main(String[] argv)
  {
    System.out.println("21,200: "+maxTriangles(10,15));
    System.out.println("21,200: "+maxTriangles(150000,8000000));
/*    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();
    DBConnection connection = null;
    try
    {
      connection = new DBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }

    CountTriangles ct = new CountTriangles();
    System.out.println("Loading word:");
    ct.loadWord(connection, new Integer(27976));
    System.out.println("Calculating results:");
    System.out.println("For word : 27976, there are "+ct.doubleList.countConnections()+", "+ct.nodesCount+" nodes and connections and "+ct.countTriangles()+" triangles.");
*/
//    System.out.println("Found "+ct.countTriangles()+" triangles");
//    CountTriangles.testRandomGraph(150000, 8000000);
  }


//----------------------Inner classes----------------------------
  /**
   * Container for pairs of Integers.
   */
  class DoubleList
  {
    Hashtable table = null;

    public DoubleList()
    {
      this.table = new Hashtable();
    }

    /**
     * Returns true if the doubleList is empty
     */
    public boolean isEmpty()
    {
      if ( this.table.size() < 1 )
      {
       return true;
      }
      return false;
    }

    /**
     * Gets a pair from the set.
     * Note: It is not defined which will be the next.
     */
    public Integer[] getNext()
    {
      Integer[] retVal = new Integer[2];
      synchronized ( DoubleList.class )
      {
        Enumeration enum = this.table.keys();
        Integer val_1 = (Integer)enum.nextElement();
        HashSet set = (HashSet)this.table.get(val_1);
        Iterator it = set.iterator();
        Integer val_2 = (Integer) it.next();
        retVal[0] = val_1;
        retVal[1] = val_2;
      }
      return retVal;
    }

    /**
     * Returns an enumeration of all possible nodes in this graph
     */
    public Enumeration enumeration()
    {
      return this.table.keys();
    }

    /**
     * Returns the set of nodes which are connected to x
     */
    public HashSet getSet(Integer x)
    {
      if ( this.table.containsKey(x) )
      {
        return (HashSet)this.table.get(x);
      }
      return null;
    }

    /**
     * Returns the number of connections present here (not times 2 !)
     */
    public int countConnections()
    {
      int counter = 0;
      for ( Enumeration enum = table.keys() ; enum.hasMoreElements() ; )
      {
        Integer key = (Integer) enum.nextElement();
        counter += ((HashSet)table.get(key)).size();
      }
      return counter;
    }

    /**
     * Adds a pair if it is not yet there
     */
    public void addPair(Integer x, Integer y)
    {
      if ( x.intValue() != y.intValue() )
      {
        myAddPair(x,y);
        myAddPair(y,x);
      }
    }

    /**
     * Adds a pair if it is not yet there
     */
    private void myAddPair(Integer x, Integer y)
    {
      if ( this.table.containsKey(x) )
      {
        HashSet set = (HashSet)this.table.get(x);
        if ( ! set.contains(y) )
        {
          set.add(y);
          this.table.put(x, set);
        }
        // else // then it contains it already
      }
      else
      {
        HashSet set = new HashSet();
        set.add(y);
        this.table.put(x,set);
      }
    }


    /**
     * Removes a pair if it should be there
     */
    public void removePair(Integer x, Integer y)
    {
      if ( this.table.containsKey(x) )
      {
        HashSet set = (HashSet)this.table.get(x);
        if ( set.contains(y) )
        {
          set.remove(y);
          if ( set.size() > 0 )
          {
            this.table.put(x, set);
          }
          else
          {
            this.table.remove(x);
          }
        }
        // else // then it already has been removed or never added.
      }
    }

    /**
     * Returns whether the given pair exists in the set or not
     */
    public boolean containsPair(Integer x, Integer y)
    {
      if ( this.table.containsKey(x) )
      {
        HashSet set = (HashSet)this.table.get(x);
        if ( set.contains(y) )
        {
          return true;
        }
      }
      return false;
    }
  }
}