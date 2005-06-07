package com.bordag.sgz.graphen;

// standard imports
import java.util.*;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Determines the average path length between words in the DB
 */
public class AvgLength2
{

  /**
   * The table where iteratively all neighbours of the sourceNode and then
   * their neighbours and so on will be put in with growing range
   */
  private HashMap srcTable = null;

  /**
   * The same as for srcTable, just from dstNode out
   */
  private HashMap dstTable = null;

  /**
   * The source node
   **/
  private Integer node1 = null;

  /**
   * The dest. node
   **/
  private Integer node2 = null;

  /**
   * The source of information about the graph
   **/
  private DBConnection connection = null;

  /**
   * Look always at that many neighbours, ordered descending by frequency
   **/
  private final static String NEIGHBOURS_LIMIT = "10";

  /**
   * The limit when to stop adding new neighbours
   */
  private final static int SEARCH_DEPTH = 10;

  /**
   * The query with which to retrieve the neighbours of a word
   **/
  // oder by ver. :: private final static String NEIGHBOURS_QUERY = "select k.wort_nr1 from kollok_sig k where k.wort_nr2 = #ARG# order by k.wort_nr1 asc limit #ARG#;";
  private final static String NEIGHBOURS_QUERY = "select k.wort_nr1 from kollok_sig k where k.wort_nr2 = #ARG# limit #ARG#;";

  String hitWord = null;

  /**
   * Constructor
   **/
  public AvgLength2(Integer node1, Integer node2, DBConnection connection)
  {
    this.srcTable =  new HashMap();
    this.srcTable.put(node1, new Integer(0));
    this.dstTable =  new HashMap();
    this.dstTable.put(node2, new Integer(0));
    this.node1 = node1;
    this.node2 = node2;
    this.connection = connection;
  }

    /**
     * put srcNode into srcTable
     * put dstNode into dstTable
     * i=0;
     * while(true)
     * {
     *   put neighbours of all elements from srcTable into srcTable with range i, ignoring those already there
     *   check whether both tables src and dst have a matching element
     *   put neighbours of all elements from dstTable into dstTable with range i, ignoring those already there
     *   check whether both tables src and dst have a matching element
     *   i++;
     * }
    **/
  public int getLength() throws Exception
  {
    int i = 1;
    int length = -1;
    while ( true )
    {
//System.out.println("Putting neighbours in 1. table.");
      this.srcTable = putNeighbours(this.srcTable, i);
//System.out.println("Put neighbours in table 1 (1): "+this.srcTable);
//System.out.println("Put neighbours in table 1 (2): "+this.dstTable);

      length = checkMatchingElements();
//System.out.println("Checking for matching elements.");
      if ( length >= 0 )
      {
        return length;
      }

//System.out.println("Putting neighbours in 2. table.");
      this.dstTable = putNeighbours(this.dstTable, i);
//System.out.println("2nd. Checking for matching elements.");
      length = checkMatchingElements();
      if ( length >= 0 )
      {
        return length;
      }
      i++;
    }
  }

  /**
   * Puts the neighbours of all elements in the given table into that table and
   * returns it
   */
  private HashMap putNeighbours(HashMap table, int i) throws Exception
  {
    if ( i > 10 )
    {
      throw new Exception("Search depth limit reached.");
    }
    HashMap tempMap = new HashMap();
    for ( Iterator it = table.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNode = (Integer)it.next();
      Integer[] neighbours = getNeighbours(curNode);
      if ( neighbours == null )
      {
        throw new Exception("Can't find neighbours for word "+curNode);
      }
      for ( int j = 0 ; j < neighbours.length ; j++ )
      {
        tempMap.put(neighbours[j], new Integer(i));
      }
    }
    table.putAll(tempMap);
    return table;
  }

  /**
   * Checks whether the two tables contain some matching key. If so, it returns
   * the sum of the according values from both tables.
   * If not, it returns -1
   */
  private int checkMatchingElements()
  {
    for ( Iterator it = this.srcTable.keySet().iterator() ; it.hasNext() ; )
    {
      Integer curNode = (Integer)it.next();
      if ( this.dstTable.containsKey(curNode) )
      {
        this.hitWord = curNode.toString();
        return ((Integer)this.dstTable.get(curNode)).intValue() +
               ((Integer)this.srcTable.get(curNode)).intValue();
      }
      else
      {
      }
    }
    return -1;
  }

  /**
   * Returns an array with the neighbours of the given node or null
   **/
  private Integer[] getNeighbours(Integer node)
  {
    Integer[] resultSet = null;
    String[] args = new String[2];
    args[0] = node.toString();
    args[1] = this.NEIGHBOURS_LIMIT;
    ComparableStringBuffer[][] resultsOfQuery = connection.getResultsOf(this.NEIGHBOURS_QUERY, args);
    if ( resultsOfQuery == null || resultsOfQuery.length < 1 )
    {
      System.out.println("Warning, execution of query ["+this.NEIGHBOURS_QUERY+"] with args : ["+args[0]+","+args[1]+"] yielded no results!");
      return null;
    }
    resultSet = new Integer[resultsOfQuery.length];

    for ( int i = 0 ; i < resultsOfQuery.length ; i++ )
    {
      resultSet[i] = new Integer(resultsOfQuery[i][0].toString());
    }
    return resultSet;
  }

  /**
   * This tests the functionality of this class
   **/
  public static void main(String[] args)
  {
    String url = Options.getInstance().getConUrl();
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

    if ( args.length < 2 )
    {
      for ( int i = 0 ; i < 100000 ; i++ )
      {
        Integer node1 = new Integer(1 + (int)(150000.0*Math.random()));
        Integer node2 = new Integer(2 + (int)(150000.0*Math.random()));
        String word1 = connection.getWordForNumber(new ComparableStringBuffer(node1.toString())).toString();
        String word2 = connection.getWordForNumber(new ComparableStringBuffer(node2.toString())).toString();
        AvgLength2 length = new AvgLength2(node1, node2, connection);
        try
        {
          int l = length.getLength();
          System.out.println("("+node1.intValue()+","+node2.intValue()+")->"+length.hitWord+"\t=\t"+l+
                           "\t("+word1+","+word2+")->"+connection.getWordForNumber(new ComparableStringBuffer(length.hitWord.toString())));
        }
        catch(Exception ex)
        {
          System.out.println("Could not find a path for wordpair ("+node1+","+node2+") ("+word1+","+word2+"), reason: "+ex.getMessage());
        }
      }
    }
    else
    {
      Integer node1 = new Integer(args[0]);
      Integer node2 = new Integer(args[1]);
      String word1 = connection.getWordForNumber(new ComparableStringBuffer(node1.toString())).toString();
      String word2 = connection.getWordForNumber(new ComparableStringBuffer(node2.toString())).toString();
      AvgLength2 length = new AvgLength2(node1, node2, connection);
      try
      {
        int l = length.getLength();
        System.out.println("("+node1.intValue()+","+node2.intValue()+")->"+length.hitWord+"\t=\t"+l+
                         "\t("+word1+","+word2+")->"+connection.getWordForNumber(new ComparableStringBuffer(length.hitWord.toString())));
      }
      catch(Exception ex)
      {
        System.out.println("Could not find a path for wordpair ("+node1+","+node2+") ("+word1+","+word2+"), reason: "+ex.getMessage());
      }
    }
  }
}
